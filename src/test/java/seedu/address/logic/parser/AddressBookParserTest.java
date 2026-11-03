package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.GroupCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.MatchCommand;
import seedu.address.logic.commands.ShowGroupCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Age;
import seedu.address.model.person.AgeMatchesPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsPhrasePredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.Religion;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " x/what") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " x/what") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " age/18-35");
        assertEquals(new FindCommand(new AgeMatchesPredicate(18, 35)), command);
    }

    @Test
    public void parseCommand_findName() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_NAME + String.join(" ", keywords));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " x/what") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " x/what") instanceof ListCommand);
    }

    @Test
    public void parseCommand_invalidSyntax_identifiesCommandAndRetainsUsage() {
        Map<String, String> usages = Map.of("add", AddCommand.MESSAGE_USAGE, "edit", EditCommand.MESSAGE_USAGE,
                "delete", DeleteCommand.MESSAGE_USAGE, "find", FindCommand.MESSAGE_USAGE,
                "match", MatchCommand.MESSAGE_USAGE, "grp", GroupCommand.MESSAGE_USAGE,
                "show", ShowGroupCommand.MESSAGE_USAGE);
        usages.forEach((command, usage) -> assertThrows(ParseException.class,
                "Invalid " + command + " syntax. Type help for usage.\n" + usage, () -> parser.parseCommand(command)));
    }

    @Test
    public void parseCommand_specificErrors_preservesParameterGuidance() {
        assertThrows(ParseException.class, Age.MESSAGE_CONSTRAINTS, () -> parser.parseCommand("find age/17"));
        assertThrows(ParseException.class, Religion.MESSAGE_CONSTRAINTS, () -> parser.parseCommand("find r/Other"));
        assertThrows(ParseException.class, EditCommand.MESSAGE_NOT_EDITED, () -> parser.parseCommand("edit 1"));
    }

    @Test
    public void parseCommand_unknownPrefixAnywhere_reportsUnknownPrefix() {
        String details = "n/Extra p/81231235 e/ex@x.com a/x age/25";
        List<String> commands = List.of(
                "add " + details + " x/what",
                "add x/what " + details,
                "add n/Extra x/what p/81231235 e/ex@x.com a/x age/25",
                "edit 1 age/25 x/what",
                "edit 1 x/what n/Extra",
                "find x/what n/Extra",
                "find n/Extra x/what",
                "find age/25 x/what",
                "find g/m x/what",
                "find s/no x/what",
                "find r/Jainism x/what",
                "find rg/long x/what",
                "find match/1 x/what",
                "match 1 2 x/what",
                "match n/Alice x/what n/Bob",
                "delete 1 x/what",
                "grp VIP add 1 x/what",
                "show VIP x/what");
        for (String command : commands) {
            assertThrows(ParseException.class, String.format(ArgumentTokenizer.MESSAGE_UNKNOWN_PREFIX, "x/"), () ->
                    parser.parseCommand(command));
        }
    }

    @Test
    public void parseCommand_unknownPrefixSpellings_identifiesOffendingPrefix() {
        for (String prefix : List.of("unknown/", "X/", "agee/", "custom_1/", "custom-field/")) {
            assertThrows(ParseException.class, String.format(ArgumentTokenizer.MESSAGE_UNKNOWN_PREFIX, prefix), () ->
                    parser.parseCommand("edit 1 age/25 " + prefix + "what"));
        }
        assertThrows(ParseException.class, String.format(ArgumentTokenizer.MESSAGE_UNKNOWN_PREFIX, "x/"), () ->
                parser.parseCommand("edit 1 age/25 x/what y/other"));
    }

    @Test
    public void parseCommand_unsupportedCriteriaPrefix_listsAllowedCriteriaPrefixes() {
        String add = "add n/Extra p/81231235 e/ex@x.com a/x age/25 ";
        for (String block : List.of("{t/friend}", "<n/Alice>", "{x/what}", "{age/25 x/what}",
                "<r/Islam x/what>", "{r/ x/what}", "<x/what>")) {
            for (String command : List.of(add + block, "edit 1 " + block)) {
                assertThrows(ParseException.class, DatingCriteriaParser.MESSAGE_INVALID_CRITERION_PREFIX, () ->
                        parser.parseCommand(command));
            }
        }
    }

    @Test
    public void parseCommand_invalidCriteriaQuotes_preservesQuoteErrors() {
        for (String command : List.of("edit 1 {r/\"Buddhism}", "edit 1 <r/\"Islam>")) {
            assertThrows(ParseException.class, ArgumentTokenizer.MESSAGE_UNCLOSED_QUOTE, () ->
                    parser.parseCommand(command));
        }
        for (String command : List.of("edit 1 {r/\"Buddhism\" text}", "edit 1 <r/\"Islam\" text>")) {
            assertThrows(ParseException.class, ArgumentTokenizer.MESSAGE_TEXT_AFTER_QUOTE, () ->
                    parser.parseCommand(command));
        }
    }

    @Test
    public void parseCommand_quotedUnknownPrefixAndEmbeddedSlashes_preservesLiteralValues() throws Exception {
        Person person = new PersonBuilder().withAddress("Block x/what https://example.com/path 12/34").build();
        String command = PersonUtil.getAddCommand(person).replace("a/" + person.getAddress().value,
                "a/\"" + person.getAddress().value + "\"");
        assertEquals(new AddCommand(person), parser.parseCommand(command));
        Person unquoted = new PersonBuilder().withAddress("https://example.com/path 12/34").build();
        assertEquals(new AddCommand(unquoted), parser.parseCommand(PersonUtil.getAddCommand(unquoted)));
        assertThrows(ParseException.class, String.format(ArgumentTokenizer.MESSAGE_UNKNOWN_PREFIX, "y/"), () ->
                parser.parseCommand(command + " y/other"));
    }

    @Test
    public void parseCommand_quotedPrefixLikeNames_preservesNamesInAddEditAndFind() throws Exception {
        for (String name : List.of("Siti d/o Ahmad", "Ravi s/o Kumar", "Ravi xy/o")) {
            Person person = new PersonBuilder().withName(name).build();
            String add = PersonUtil.getAddCommand(person).replace("n/" + name, "n/\"" + name + "\"");
            assertEquals(new AddCommand(person), parser.parseCommand(add));
            EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(name).build();
            assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor),
                    parser.parseCommand("edit 1 n/\"" + name + "\""));
            assertEquals(new FindCommand(new NameContainsPhrasePredicate(name)),
                    parser.parseCommand("find n/\"" + name + "\""));
        }
    }

    @Test
    public void parseCommand_quotedCareOfAddress_preservesAddressInAddAndEdit() throws Exception {
        String address = "Blk 1 c/o John";
        Person person = new PersonBuilder().withAddress(address).build();
        String add = PersonUtil.getAddCommand(person).replace("a/" + address, "a/\"" + address + "\"");
        assertEquals(new AddCommand(person), parser.parseCommand(add));
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withAddress(address).build();
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor),
                parser.parseCommand("edit 1 a/\"" + address + "\""));
    }

    @Test
    public void parseCommand_unquotedPrefixLikeValues_suggestsQuotingWholeValue() {
        Map<String, String> commands = Map.of(
                "add n/Siti d/o Ahmad p/91111111 e/s@x.com a/x age/25", "d/",
                "add n/Bob p/92222222 e/b@x.com a/Blk 1 c/o John age/25", "c/",
                "edit 1 n/Siti d/o Ahmad", "d/",
                "edit 1 a/Blk 1 c/o John", "c/",
                "find n/Ravi xy/o", "xy/");
        commands.forEach((command, prefix) -> assertThrows(ParseException.class,
                "Unknown prefix: " + prefix + ". To use / inside a value, "
                        + "put the whole value in quotes, e.g. n/\"Siti d/o Ahmad\".", () ->
                        parser.parseCommand(command)));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        for (String input : List.of("", " ", "\t\n")) {
            assertThrows(ParseException.class,
                    "Enter a command. Type help for usage.", () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class,
                "Unknown command. Type help for usage.", () -> parser.parseCommand("unknownCommand"));
    }
}
