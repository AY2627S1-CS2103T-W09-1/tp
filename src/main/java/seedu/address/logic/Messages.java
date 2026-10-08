package seedu.address.logic;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;
import seedu.address.model.person.RelationshipGoal;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d person(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Gender: ")
                .append(person.getGender())
                .append("; Smoking: ")
                .append(person.getSmokingStatus())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        builder.append("; Religion: ").append(person.getReligion().map(Object::toString).orElse("Not specified"));
        person.getPreferredReligion().ifPresent(value -> builder.append("; Preferred religion: ").append(value));
        person.getRequiredReligion().ifPresent(value -> builder.append("; Required religion: ").append(value));
        if (!person.getExcludedReligions().isEmpty()) {
            builder.append("; Excluded religions: ");
            builder.append(person.getExcludedReligions().stream().map(Object::toString)
                    .sorted(Comparator.naturalOrder()).collect(Collectors.joining(", ")));
        }
        String relationshipGoal = person.getRelationshipGoal().map(Object::toString)
                .orElse(RelationshipGoal.NOT_SPECIFIED_LABEL);
        builder.append("; Relationship goal: ").append(relationshipGoal);
        return builder.toString();
    }

}
