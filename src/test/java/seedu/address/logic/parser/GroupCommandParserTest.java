package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.GroupCommand;
import seedu.address.logic.commands.GroupCommand.Operation;
import seedu.address.logic.commands.ShowGroupCommand;
import seedu.address.model.group.GroupName;

public class GroupCommandParserTest {

    private static final String INVALID_GROUP_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, GroupCommand.MESSAGE_USAGE);
    private static final String INVALID_SHOW_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ShowGroupCommand.MESSAGE_USAGE);

    private final GroupCommandParser parser = new GroupCommandParser();
    private final ShowGroupCommandParser showParser = new ShowGroupCommandParser();
    private final GroupName vip = new GroupName("VIP");

    @Test
    public void parse_validOperations_returnsGroupCommand() {
        assertParseSuccess(parser, " VIP add 1 3", new GroupCommand(vip, Operation.ADD,
                List.of(INDEX_FIRST_PERSON, INDEX_THIRD_PERSON)));
        assertParseSuccess(parser, "  vip   remove  2 ", new GroupCommand(vip, Operation.REMOVE,
                List.of(INDEX_SECOND_PERSON)));
        assertParseSuccess(parser, " VIP matches 1", new GroupCommand(vip, Operation.MATCHES,
                List.of(INDEX_FIRST_PERSON)));
    }

    @Test
    public void parse_invalidFormat_throwsParseException() {
        for (String args : new String[] {"", " VIP", " VIP add", " VIP ADD 1", " VIP rename 1",
            " VIP matches", " VIP matches 1 2"}) {
            assertParseFailure(parser, args, INVALID_GROUP_FORMAT);
        }
    }

    @Test
    public void parse_invalidIndexes_throwsParseException() {
        assertParseFailure(parser, " VIP add 1 1", GroupCommand.MESSAGE_DUPLICATE_INDEX);
        for (String index : new String[] {"0", "-1", "a", "1,2", "2147483648"}) {
            assertParseFailure(parser, " VIP add " + index, ParserUtil.MESSAGE_INVALID_INDEX);
        }
    }

    @Test
    public void parse_invalidGroupName_throwsParseException() {
        for (String name : new String[] {"-VIP", "_VIP", "V/IP", "g/VIP", "a".repeat(31)}) {
            assertParseFailure(parser, " " + name + " add 1", GroupName.MESSAGE_CONSTRAINTS);
            assertParseFailure(showParser, " " + name, GroupName.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_show_returnsShowGroupCommand() {
        assertParseSuccess(showParser, "  vip ", new ShowGroupCommand(vip));
        assertParseFailure(showParser, "", INVALID_SHOW_FORMAT);
        assertParseFailure(showParser, " VIP extra", INVALID_SHOW_FORMAT);
    }
}
