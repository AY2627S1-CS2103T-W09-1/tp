package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GENDER;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Age;
import seedu.address.model.person.AgeMatchesPredicate;
import seedu.address.model.person.Gender;
import seedu.address.model.person.GenderMatchesPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object.
 */
public class FindCommandParser implements Parser<FindCommand> {

    public static final String MESSAGE_INVALID_GENDER_LIST =
            "Use distinct genders separated by commas (m, w, nb), or leave g/ empty for any specified gender.";

    /**
     * Parses name keywords, an age criterion, or a gender criterion into a FindCommand.
     *
     * @throws ParseException if the input is invalid or combines search modes.
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + trimmedArgs, PREFIX_AGE, PREFIX_GENDER);
        boolean hasAge = argMultimap.getValue(PREFIX_AGE).isPresent();
        boolean hasGender = argMultimap.getValue(PREFIX_GENDER).isPresent();
        if (trimmedArgs.isEmpty() || (hasAge && hasGender)
                || ((hasAge || hasGender) && !argMultimap.getPreamble().isEmpty())) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_AGE, PREFIX_GENDER);
        if (hasGender) {
            return new FindCommand(new GenderMatchesPredicate(parseGenders(argMultimap.getValue(PREFIX_GENDER).get())));
        }
        if (hasAge) {
            return parseAgeSearch(argMultimap.getValue(PREFIX_AGE).get());
        }

        // Prefix-like input must not silently become a name search.
        if (trimmedArgs.contains("/")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        return new FindCommand(new NameContainsKeywordsPredicate(List.of(trimmedArgs.split("\\s+"))));
    }

    /**
     * Parses an exact age or inclusive age range using the existing age search rules.
     */
    private FindCommand parseAgeSearch(String value) throws ParseException {
        String[] bounds = value.trim().split("-", -1);
        if (bounds.length > 2 || !Age.isValidAge(bounds[0])
                || (bounds.length == 2 && !Age.isValidAge(bounds[1]))) {
            throw new ParseException(Age.MESSAGE_CONSTRAINTS);
        }
        int lowerBound = Integer.parseInt(bounds[0]);
        int upperBound = bounds.length == 2 ? Integer.parseInt(bounds[1]) : lowerBound;
        if (lowerBound > upperBound) {
            throw new ParseException("The lower age bound cannot be greater than the upper age bound.");
        }
        return new FindCommand(new AgeMatchesPredicate(lowerBound, upperBound));
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
