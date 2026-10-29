package seedu.address.logic;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.DatingAttribute;
import seedu.address.model.person.Person;

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
                .append("; Age: ")
                .append(person.getAge())
                .append("; Gender: ")
                .append(person.getGender())
                .append("; Smoking: ")
                .append(person.getSmokingStatus())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        builder.append("; Religion: ")
                .append(person.getReligion().map(Object::toString).orElse("Not specified"));
        appendCriteria(builder, "Preferences", person.getDatingCriteria().getPreferences());
        appendCriteria(builder, "Dealbreakers", person.getDatingCriteria().getDealbreakers());
        return builder.toString();
    }

    private static void appendCriteria(StringBuilder builder, String label,
            Map<DatingAttribute, Set<String>> criteria) {
        if (!criteria.isEmpty()) {
            builder.append("; ").append(label).append(": ")
                    .append(criteria.entrySet().stream()
                            .map(entry -> entry.getKey().getDisplayName() + " = "
                                    + entry.getValue().stream().sorted().collect(Collectors.joining(", ")))
                            .collect(Collectors.joining(", ")));
        }
    }

}
