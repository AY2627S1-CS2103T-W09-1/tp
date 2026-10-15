package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.MatchCommand;

public class MatchCommandParserTest {
    private final MatchCommandParser parser = new MatchCommandParser();

    @Test
    public void parse_validIndexes_returnsMatchCommand() throws Exception {
        MatchCommand expected = new MatchCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);
        assertParseSuccess(parser, "1 2", expected);
        assertParseSuccess(parser, " \t1   2 \t", expected);
        assertParseSuccess(parser, "2 1", new MatchCommand(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON));
        assertEquals(expected, new AddressBookParser().parseCommand("match 1 2"));
    }

    @Test
    public void parse_invalidArguments_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MatchCommand.MESSAGE_USAGE);
        for (String arguments : new String[] {"", "1", "1 2 3", "0 2", "1 0", "-1 2", "1 -2",
            "a 2", "1 b", "1.5 2", "1 2147483648", "2147483648 1", "1 2 {s/no}", "1,2"}) {
            assertParseFailure(parser, arguments, message);
        }
    }

    @Test
    public void parse_sameIndex_throwsParseException() {
        assertParseFailure(parser, "1 1", MatchCommand.MESSAGE_SAME_CLIENT);
    }
}
