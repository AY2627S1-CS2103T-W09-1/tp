package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SMOKING;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;
import seedu.address.model.person.SmokingStatus;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class SmokingStatusParserTest {

    private final AddCommandParser addParser = new AddCommandParser();
    private final EditCommandParser editParser = new EditCommandParser();
    private final String personDetails = " " + PersonUtil.getPersonDetails(new PersonBuilder().build());

    @Test
    public void parseSmokingStatus_validInput_trimsAndNormalizes() throws Exception {
        assertEquals(new SmokingStatus("yes"), ParserUtil.parseSmokingStatus("  YeS  "));
        assertEquals(new SmokingStatus("no"), ParserUtil.parseSmokingStatus(" NO "));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseSmokingStatus(null));
    }

    @Test
    public void parseSmokingStatus_invalidInput_throwsParseException() {
        for (String value : new String[] {"", " ", "sometimes", "unknown", "yes no"}) {
            assertThrows(ParseException.class,
                    SmokingStatus.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseSmokingStatus(value));
        }
    }

    @Test
    public void parse_addSmokingStatus_success() {
        for (String value : new String[] {"yes", "NO"}) {
            AddCommand expected = new AddCommand(new PersonBuilder().withSmokingStatus(value).build());
            assertParseSuccess(addParser, personDetails + " s/" + value, expected);
            assertParseSuccess(addParser, " s/" + value + personDetails, expected);
        }
        assertParseSuccess(addParser, personDetails, new AddCommand(new PersonBuilder().build()));
    }

    @Test
    public void parse_editSmokingStatus_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setSmokingStatus(new SmokingStatus("no"));
        assertParseSuccess(editParser, "1 s/ NO ", new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_invalidSmokingStatus_rejectsCommand() {
        for (String value : new String[] {"", "sometimes", "no extra text"}) {
            assertParseFailure(addParser, personDetails + " s/" + value, SmokingStatus.MESSAGE_CONSTRAINTS);
            assertParseFailure(editParser, "1 s/" + value, SmokingStatus.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_repeatedSmokingStatus_rejectsEvenIdenticalValues() {
        for (String repeated : new String[] {" s/yes s/no", " s/no s/no"}) {
            String expected = Messages.getErrorMessageForDuplicatePrefixes(PREFIX_SMOKING);
            assertParseFailure(addParser, personDetails + repeated, expected);
            assertParseFailure(editParser, "1" + repeated, expected);
        }
    }

    @Test
    public void parse_smokingStatusInAnyPosition_acceptsEveryCaseCombination() {
        String[] fields = {"n/Amy Bee", "p/85355255", "e/amy@gmail.com", "a/123, Jurong West Ave 6, #08-111"};
        for (String value : new String[] {"yes", "yeS", "yEs", "yES", "Yes", "YeS", "YEs", "YES",
            "no", "nO", "No", "NO"}) {
            for (int position = 0; position <= fields.length; position++) {
                StringBuilder args = new StringBuilder();
                for (int index = 0; index <= fields.length; index++) {
                    if (index == position) {
                        args.append(" s/ ").append(value).append("  ");
                    }
                    if (index < fields.length) {
                        args.append(" ").append(fields[index]);
                    }
                }
                AddCommand expected = new AddCommand(new PersonBuilder().withSmokingStatus(value).build());
                assertParseSuccess(addParser, args.toString(), expected);
            }
        }
    }

    @Test
    public void parse_generatedCommands_preserveSmokingStatus() throws Exception {
        for (String value : new String[] {"yes", "no"}) {
            Person person = new PersonBuilder().withSmokingStatus(value).build();
            assertEquals(new AddCommand(person),
                    new AddressBookParser().parseCommand(PersonUtil.getAddCommand(person)));
            EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
            assertEquals(person.getSmokingStatus(), descriptor.getSmokingStatus().orElseThrow());
            assertParseSuccess(editParser, "1 " + PersonUtil.getEditPersonDescriptorDetails(descriptor),
                    new EditCommand(INDEX_FIRST_PERSON, descriptor));
        }
    }
}
