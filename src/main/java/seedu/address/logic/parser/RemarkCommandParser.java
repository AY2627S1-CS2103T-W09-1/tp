package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Remark;

/**
 * Parses a displayed index and an optional remark for Tutorial 2.
 */
public class RemarkCommandParser implements Parser<RemarkCommand> {

    @Override
    public RemarkCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_REMARK);
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_REMARK);
        Index index;
        try {
            index = ParserUtil.parseIndex(arguments.getPreamble());
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE),
                    exception);
        }
        // As in the tutorial, omitting r/ or supplying an empty r/ clears the remark.
        return new RemarkCommand(index, new Remark(arguments.getValue(PREFIX_REMARK).orElse("")));
    }
}
