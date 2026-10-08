package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Gender;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class GenderCommandParserTest {
    private static final String CONTACT_DETAILS =
            " n/Amy Bee p/85355255 e/amy@gmail.com a/123, Jurong West Ave 6, #08-111";
    private final AddCommandParser addParser = new AddCommandParser();
    private final EditCommandParser editParser = new EditCommandParser();

    @Test
    public void parse_genderAtEitherEnd_returnsAddAndEditCommands() {
        for (String value : new String[] {"m", " W ", "Nb", ""}) {
            Person person = new PersonBuilder().withGender(value).build();
            assertParseSuccess(addParser, " g/" + value + CONTACT_DETAILS, new AddCommand(person));
            assertParseSuccess(addParser, CONTACT_DETAILS + " g/" + value, new AddCommand(person));

            EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withGender(value).build();
            assertParseSuccess(editParser, "1 g/" + value, new EditCommand(INDEX_FIRST_PERSON, descriptor));
        }
    }

    @Test
    public void parse_genderBetweenOtherFields_preservesAllFields() {
        Person person = new PersonBuilder().withGender("nb").withTags("friend").build();
        assertParseSuccess(addParser, CONTACT_DETAILS + " g/nb t/friend", new AddCommand(person));
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone("98765432")
                .withGender("w").withTags("friend").build();
        assertParseSuccess(editParser, "1 p/98765432 g/w t/friend", new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_omittedGender_defaultsForAddAndPreservesForEdit() {
        assertParseSuccess(addParser, CONTACT_DETAILS, new AddCommand(new PersonBuilder().build()));
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone("98765432").build();
        assertParseSuccess(editParser, "1 p/98765432", new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_invalidGender_rejectsAddAndEdit() {
        for (String value : new String[] {"x", "m,w"}) {
            assertParseFailure(addParser, CONTACT_DETAILS + " g/" + value, Gender.MESSAGE_CONSTRAINTS);
            assertParseFailure(editParser, "1 g/" + value, Gender.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_repeatedGenderPrefix_rejectsAddAndEdit() {
        String message = Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_GENDER);
        for (String arguments : new String[] {" g/m g/w", " g/m g/m", " g/ g/"}) {
            assertParseFailure(addParser, CONTACT_DETAILS + arguments, message);
            assertParseFailure(editParser, "1" + arguments, message);
        }
    }

    @Test
    public void commandHelpers_includeGenderAndExplicitClear() {
        for (String value : new String[] {"m", ""}) {
            Person person = new PersonBuilder().withGender(value).withTags("friend").build();
            assertParseSuccess(addParser, " " + PersonUtil.getPersonDetails(person), new AddCommand(person));
            EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
            assertParseSuccess(editParser, "1 " + PersonUtil.getEditPersonDescriptorDetails(descriptor),
                    new EditCommand(INDEX_FIRST_PERSON, descriptor));
        }
    }
}
