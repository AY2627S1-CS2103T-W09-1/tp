package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.ClientReference;
import seedu.address.logic.commands.MatchCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses exactly two distinct clients for a match command. Each client is given by displayed index or by
 * {@code n/NAME}, with any indexes before the names.
 */
public class MatchCommandParser implements Parser<MatchCommand> {

    @Override
    public MatchCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MatchCommand.MESSAGE_USAGE);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME);
        String preamble = argMultimap.getPreamble();
        List<String> indexes = preamble.isEmpty() ? List.of() : List.of(preamble.split("\\s+"));
        List<String> names = argMultimap.getAllValues(PREFIX_NAME);
        if (indexes.size() + names.size() != 2) {
            throw new ParseException(usage);
        }

        List<ClientReference> clients = new ArrayList<>();
        try {
            for (String index : indexes) {
                clients.add(new ClientReference.ByIndex(ParserUtil.parseIndex(index)));
            }
        } catch (ParseException exception) {
            throw new ParseException(usage, exception);
        }
        for (String name : names) {
            clients.add(new ClientReference.ByName(ParserUtil.parseName(name)));
        }
        if (clients.get(0).equals(clients.get(1))) {
            throw new ParseException(MatchCommand.MESSAGE_SAME_CLIENT);
        }
        return new MatchCommand(clients.get(0), clients.get(1));
    }
}
