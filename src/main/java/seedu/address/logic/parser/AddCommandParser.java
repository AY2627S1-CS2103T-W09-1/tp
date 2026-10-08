package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXCLUDED_RELIGION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PREFERRED_RELIGION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RELATIONSHIP_GOAL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RELIGION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REQUIRED_RELIGION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SMOKING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Age;
import seedu.address.model.person.Email;
import seedu.address.model.person.Gender;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.RelationshipGoal;
import seedu.address.model.person.Religion;
import seedu.address.model.person.SmokingStatus;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG,
                        PREFIX_GENDER, PREFIX_AGE, PREFIX_SMOKING,
                        PREFIX_RELIGION, PREFIX_PREFERRED_RELIGION, PREFIX_REQUIRED_RELIGION,
                        PREFIX_EXCLUDED_RELIGION, PREFIX_RELATIONSHIP_GOAL);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_AGE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_GENDER, PREFIX_SMOKING, PREFIX_AGE, PREFIX_RELIGION, PREFIX_PREFERRED_RELIGION,
                PREFIX_REQUIRED_RELIGION, PREFIX_RELATIONSHIP_GOAL);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        Age age = ParserUtil.parseAge(argMultimap.getValue(PREFIX_AGE).get());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        SmokingStatus smokingStatus = argMultimap.getValue(PREFIX_SMOKING).isPresent()
                ? ParserUtil.parseSmokingStatus(argMultimap.getValue(PREFIX_SMOKING).get()) : SmokingStatus.UNSPECIFIED;
        Gender gender = ParserUtil.parseGender(argMultimap.getValue(PREFIX_GENDER).orElse(""));

        Religion religion = parseOptionalReligion(argMultimap, PREFIX_RELIGION);
        Religion preferred = parseOptionalReligion(argMultimap, PREFIX_PREFERRED_RELIGION);
        Religion required = parseOptionalReligion(argMultimap, PREFIX_REQUIRED_RELIGION);
        Set<Religion> excluded = ParserUtil.parseExcludedReligions(
                argMultimap.getAllValues(PREFIX_EXCLUDED_RELIGION));
        RelationshipGoal relationshipGoal = parseOptionalRelationshipGoal(argMultimap);

        try {
            Person person = new Person(name, phone, email, address, age, tagList, smokingStatus, gender,
                    religion, preferred, required, excluded, relationshipGoal);
            return new AddCommand(person);
        } catch (IllegalArgumentException exception) {
            throw new ParseException(exception.getMessage());
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /** Parses a single optional religion field, leaving an absent field unknown. */
    private static Religion parseOptionalReligion(ArgumentMultimap arguments, Prefix prefix) throws ParseException {
        if (arguments.getValue(prefix).isEmpty()) {
            return null;
        }
        return ParserUtil.parseReligion(arguments.getValue(prefix).get());
    }

    /** Parses the optional relationship goal field, leaving an absent field unknown. */
    private static RelationshipGoal parseOptionalRelationshipGoal(ArgumentMultimap arguments) throws ParseException {
        if (arguments.getValue(PREFIX_RELATIONSHIP_GOAL).isEmpty()) {
            return null;
        }
        return ParserUtil.parseRelationshipGoal(arguments.getValue(PREFIX_RELATIONSHIP_GOAL).get());
    }

}
