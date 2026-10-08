package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GENDER;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Gender;
import seedu.address.model.person.GenderMatchesPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;

import seedu.address.model.person.Age;
import seedu.address.model.person.AgeMatchesPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    public static final String MESSAGE_INVALID_GENDER_LIST =
            "Use distinct genders separated by commas (m, w, nb), or leave g/ empty for any specified gender.";

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + args.trim(), PREFIX_AGE);
        if (!argMultimap.getPreamble().isEmpty() || argMultimap.getValue(PREFIX_AGE).isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_AGE);

        String[] bounds = argMultimap.getValue(PREFIX_AGE).get().trim().split("-", -1);
        if (bounds.length == 0 || bounds.length > 2 || !Age.isValidAge(bounds[0])
                || (bounds.length == 2 && !Age.isValidAge(bounds[1]))) {
            throw new ParseException(Age.MESSAGE_CONSTRAINTS);
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + trimmedArgs, PREFIX_GENDER);
        if (argMultimap.getValue(PREFIX_GENDER).isPresent()) {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GENDER);
            if (!argMultimap.getPreamble().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
            }
            return new FindCommand(new GenderMatchesPredicate(parseGenders(argMultimap.getValue(PREFIX_GENDER).get())));
        }

        // Prefix-like input must not silently become a name search.
        if (trimmedArgs.contains("/")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");
        return new FindCommand(new NameContainsKeywordsPredicate(List.of(nameKeywords)));
    }

    /**
     * Parses a non-repeating gender list. Empty input selects every specified gender.
     */
    private Set<Gender> parseGenders(String value) throws ParseException {
        if (value.isEmpty()) {
            return EnumSet.of(Gender.MAN, Gender.WOMAN, Gender.NON_BINARY);
        }
        Set<Gender> genders = EnumSet.noneOf(Gender.class);
        for (String token : value.split(",", -1)) {
            Gender gender = ParserUtil.parseGender(token);
            if (gender == Gender.UNSPECIFIED || !genders.add(gender)) {
                throw new ParseException(MESSAGE_INVALID_GENDER_LIST);
            }
        }
        return genders;
    }

}
