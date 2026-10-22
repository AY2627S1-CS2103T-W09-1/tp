package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ShowGroupCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses one group name; additional arguments are not silently ignored. */
public class ShowGroupCommandParser implements Parser<ShowGroupCommand> {
    @Override
    public ShowGroupCommand parse(String args) throws ParseException {
        String name = args.trim();
        if (name.isEmpty() || name.split("\\s+").length != 1) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ShowGroupCommand.MESSAGE_USAGE));
        }
        return new ShowGroupCommand(GroupCommandParser.parseGroupName(name));
    }
}
