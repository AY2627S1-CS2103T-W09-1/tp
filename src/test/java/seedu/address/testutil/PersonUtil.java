package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RELATIONSHIP_GOAL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RELIGION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SMOKING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.DatingAttribute;
import seedu.address.model.person.Person;
import seedu.address.model.person.RelationshipGoal;
import seedu.address.model.tag.Tag;

/**
 * A utility class for Person.
 */
public class PersonUtil {

    /**
     * Returns an add command string for adding the {@code person}.
     */
    public static String getAddCommand(Person person) {
        return AddCommand.COMMAND_WORD + " " + getPersonDetails(person);
    }

    /**
     * Returns the part of command string for the given {@code person}'s details.
     */
    public static String getPersonDetails(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + person.getName().fullName + " ");
        sb.append(PREFIX_PHONE + person.getPhone().value + " ");
        sb.append(PREFIX_EMAIL + person.getEmail().value + " ");
        sb.append(PREFIX_ADDRESS + person.getAddress().value + " ");
        sb.append(PREFIX_GENDER).append(person.getGender().getValue()).append(" ");
        if (!person.getSmokingStatus().value.isEmpty()) {
            sb.append(PREFIX_SMOKING).append(person.getSmokingStatus().value).append(" ");
        }
        sb.append(PREFIX_AGE + String.valueOf(person.getAge().value) + " ");
        person.getTags().stream().forEach(
            s -> sb.append(PREFIX_TAG + s.tagName + " ")
        );
        person.getReligion().ifPresent(religion -> sb.append(PREFIX_RELIGION).append(religion).append(" "));
        appendCriteria(sb, person.getDatingCriteria().getPreferences(), '{', '}');
        appendCriteria(sb, person.getDatingCriteria().getDealbreakers(), '<', '>');
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditPersonDescriptor}'s details.
     */
    public static String getEditPersonDescriptorDetails(EditPersonDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(name.fullName).append(" "));
        descriptor.getPhone().ifPresent(phone -> sb.append(PREFIX_PHONE).append(phone.value).append(" "));
        descriptor.getEmail().ifPresent(email -> sb.append(PREFIX_EMAIL).append(email.value).append(" "));
        descriptor.getAddress().ifPresent(address -> sb.append(PREFIX_ADDRESS).append(address.value).append(" "));
        descriptor.getGender().ifPresent(gender -> sb.append(PREFIX_GENDER).append(gender.getValue()).append(" "));
        descriptor.getSmokingStatus().ifPresent(status -> sb.append(PREFIX_SMOKING).append(status.value).append(" "));
        descriptor.getAge().ifPresent(age -> sb.append(PREFIX_AGE).append(age.value).append(" "));
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (tags.isEmpty()) {
                sb.append(PREFIX_TAG).append(" ");
            } else {
                tags.forEach(s -> sb.append(PREFIX_TAG).append(s.tagName).append(" "));
            }
        }
        if (descriptor.isReligionEdited()) {
            sb.append(PREFIX_RELIGION);
            descriptor.getReligion().ifPresent(sb::append);
            sb.append(" ");
        }
        descriptor.getPreferences().ifPresent(criteria -> appendCriteria(sb, criteria, '{', '}'));
        descriptor.getDealbreakers().ifPresent(criteria -> appendCriteria(sb, criteria, '<', '>'));
        return sb.toString();
    }

    private static void appendCriteria(StringBuilder builder, Map<DatingAttribute, Set<String>> criteria,
            char openingDelimiter, char closingDelimiter) {
        builder.append(openingDelimiter);
        criteria.forEach((attribute, values) -> builder.append(getPrefix(attribute))
                .append(toCommandValue(attribute, values)).append(" "));
        builder.append(closingDelimiter).append(" ");
    }

    private static String toCommandValue(DatingAttribute attribute, Set<String> values) {
        if (attribute != DatingAttribute.RELATIONSHIP_GOAL) {
            return String.join(", ", values);
        }
        return values.stream()
                .map(value -> RelationshipGoal.fromStoredValue(value).getKeyword())
                .collect(Collectors.joining(","));
    }

    private static Prefix getPrefix(DatingAttribute attribute) {
        return switch (attribute) {
            case AGE -> PREFIX_AGE;
            case GENDER -> PREFIX_GENDER;
            case SMOKING -> PREFIX_SMOKING;
            case RELIGION -> PREFIX_RELIGION;
            case RELATIONSHIP_GOAL -> PREFIX_RELATIONSHIP_GOAL;
        };
    }
}
