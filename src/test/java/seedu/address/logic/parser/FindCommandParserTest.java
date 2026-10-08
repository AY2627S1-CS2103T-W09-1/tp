package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.Age;
import seedu.address.model.person.AgeMatchesPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        assertParseSuccess(parser, "age/18", new FindCommand(new AgeMatchesPredicate(18, 18)));
        assertParseSuccess(parser, " age/18-35 ", new FindCommand(new AgeMatchesPredicate(18, 35)));
    }

    @Test
    public void parse_invalidAge_throwsParseException() {
        assertParseFailure(parser, "age/17", Age.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "age/100", Age.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "age/18-100", Age.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "age/35-18", "The lower age bound cannot be greater than the upper age bound.");
    }

}
