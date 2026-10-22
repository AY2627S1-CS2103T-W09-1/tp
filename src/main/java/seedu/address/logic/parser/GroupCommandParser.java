package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GroupCommand;
import seedu.address.logic.commands.GroupCommand.Operation;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;

/** Parses explicit add/remove/matches operations, keeping single-member groups unambiguous. */
public class GroupCommandParser implements Parser<GroupCommand> {
    @Override
    public GroupCommand parse(String args) throws ParseException {
        String[] tokens = args.trim().split("\\s+");
        if (tokens.length < 3) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GroupCommand.MESSAGE_USAGE));
        }
        GroupName name = parseGroupName(tokens[0]);
        Operation operation = switch (tokens[1]) {
            case "add" -> Operation.ADD;
            case "remove" -> Operation.REMOVE;
            case "matches" -> Operation.MATCHES;
            default -> throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, GroupCommand.MESSAGE_USAGE));
        };
        if (operation == Operation.MATCHES && tokens.length != 3) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GroupCommand.MESSAGE_USAGE));
        }
        List<Index> indexes = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        for (int i = 2; i < tokens.length; i++) {
            Index index = ParserUtil.parseIndex(tokens[i]);
            if (!seen.add(index.getZeroBased())) {
                throw new ParseException(GroupCommand.MESSAGE_DUPLICATE_INDEX);
            }
            indexes.add(index);
        }
        return new GroupCommand(name, operation, indexes);
    }

    /** Shares group-name validation with show, translating model validation into user feedback. */
    public static GroupName parseGroupName(String value) throws ParseException {
        try {
            return new GroupName(value);
        } catch (IllegalArgumentException exception) {
            throw new ParseException(exception.getMessage());
        }
    }
}
