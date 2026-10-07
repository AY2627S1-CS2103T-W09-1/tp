package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Age;
import seedu.address.model.person.AgeMatchesPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + args.trim(), PREFIX_AGE);
        if (!argMultimap.getPreamble().isEmpty() || argMultimap.getValue(PREFIX_AGE).isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_AGE);

        String[] bounds = argMultimap.getValue(PREFIX_AGE).get().trim().split("-", -1);
        if (bounds.length == 0 || bounds.length > 2 || !Age.isValidAge(bounds[0])
                || (bounds.length == 2 && !Age.isValidAge(bounds[1]))) {
            throw new ParseException(Age.MESSAGE_CONSTRAINTS);
        }

        int lowerBound = Integer.parseInt(bounds[0]);
        int upperBound = bounds.length == 2 ? Integer.parseInt(bounds[1]) : lowerBound;
        if (lowerBound > upperBound) {
            throw new ParseException("The lower age bound cannot be greater than the upper age bound.");
        }

        return new FindCommand(new AgeMatchesPredicate(lowerBound, upperBound));
    }

}
