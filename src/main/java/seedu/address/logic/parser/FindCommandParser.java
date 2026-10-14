package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATCH;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RELATIONSHIP_GOAL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RELIGION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SMOKING;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.FindMatchCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Age;
import seedu.address.model.person.AgeMatchesPredicate;
import seedu.address.model.person.Gender;
import seedu.address.model.person.GenderMatchesPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.RelationshipGoalMatchesPredicate;
import seedu.address.model.person.ReligionMatchesPredicate;
import seedu.address.model.person.SmokingStatusMatchesPredicate;

/**
 * Parses input arguments and creates a new FindCommand object.
 */
public class FindCommandParser implements Parser<Command> {

    public static final String MESSAGE_INVALID_GENDER_LIST =
            "Use distinct genders separated by commas (m, w, nb), or leave g/ empty for any specified gender.";

    /**
     * Parses name keywords or a single age, gender, religion, relationship-goal, smoking or match criterion.
     *
     * @throws ParseException if the input is invalid or combines search modes.
     */
    public Command parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + trimmedArgs,
                PREFIX_AGE, PREFIX_GENDER, PREFIX_RELIGION, PREFIX_RELATIONSHIP_GOAL, PREFIX_SMOKING, PREFIX_MATCH);
        boolean hasAge = argMultimap.getValue(PREFIX_AGE).isPresent();
        boolean hasGender = argMultimap.getValue(PREFIX_GENDER).isPresent();
        boolean hasReligion = argMultimap.getValue(PREFIX_RELIGION).isPresent();
        boolean hasGoal = argMultimap.getValue(PREFIX_RELATIONSHIP_GOAL).isPresent();
        boolean hasSmoking = argMultimap.getValue(PREFIX_SMOKING).isPresent();
        boolean hasMatch = argMultimap.getValue(PREFIX_MATCH).isPresent();
        int searchModes = (hasAge ? 1 : 0) + (hasGender ? 1 : 0) + (hasReligion ? 1 : 0)
                + (hasGoal ? 1 : 0) + (hasSmoking ? 1 : 0) + (hasMatch ? 1 : 0);
        if (trimmedArgs.isEmpty() || searchModes > 1
                || (searchModes == 1 && !argMultimap.getPreamble().isEmpty())) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_AGE, PREFIX_GENDER, PREFIX_RELIGION,
                PREFIX_RELATIONSHIP_GOAL, PREFIX_SMOKING, PREFIX_MATCH);
        if (hasSmoking) {
            return new FindCommand(new SmokingStatusMatchesPredicate(
                    ParserUtil.parseSmokingStatus(argMultimap.getValue(PREFIX_SMOKING).get())));
        }
        if (hasMatch) {
            return new FindMatchCommand(ParserUtil.parseIndex(argMultimap.getValue(PREFIX_MATCH).get()));
        }
        if (hasGoal) {
            return new FindCommand(new RelationshipGoalMatchesPredicate(
                    ParserUtil.parseRelationshipGoal(argMultimap.getValue(PREFIX_RELATIONSHIP_GOAL).get())));
        }
        if (hasReligion) {
            return new FindCommand(new ReligionMatchesPredicate(
                    ParserUtil.parseReligion(argMultimap.getValue(PREFIX_RELIGION).get())));
        }
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
