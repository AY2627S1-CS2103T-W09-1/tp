package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MatchCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses exactly two distinct displayed client indexes for a match command.
 */
public class MatchCommandParser implements Parser<MatchCommand> {

    @Override
    public MatchCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] indexes = args.trim().split("\\s+");
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MatchCommand.MESSAGE_USAGE);
        if (indexes.length != 2) {
            throw new ParseException(usage);
        }
        Index first;
        Index second;
        try {
            first = ParserUtil.parseIndex(indexes[0]);
            second = ParserUtil.parseIndex(indexes[1]);
        } catch (ParseException exception) {
            throw new ParseException(usage, exception);
        }
        if (first.equals(second)) {
            throw new ParseException(MatchCommand.MESSAGE_SAME_CLIENT);
        }
        return new MatchCommand(first, second);
    }
}
