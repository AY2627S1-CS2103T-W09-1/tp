package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists all persons in the address book matching the supplied predicate.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose names contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: n/KEYWORD [MORE_KEYWORDS]... or n/\"PHRASE\" for a phrase that may contain /\n"
            + "Example: " + COMMAND_WORD + " n/alice bob charlie\n"
            + "Or search by gender: find g/m,w (m, w, nb; comma-separated without duplicates).\n"
            + "Use find g/ to list all persons with a specified gender.\n"
            + "Or search by age: find age/25 or find age/25-35 (18 to 99, inclusive).\n"
            + "Or search by own religion: find r/Jainism (one supported category, case-insensitive).\n"
            + "Or search by relationship goal: find rg/GOAL[,GOAL]... (e.g. find rg/long,long-open).\n"
            + "Or search by own smoking status: find s/yes or find s/no (case-insensitive).\n"
            + "Clients with unspecified smoking status are excluded from smoking searches.\n"
            + "Or find compatible clients for a person: find match/INDEX (best matches first).\n"
            + "Use one search mode at a time: name, age, gender, religion, relationship goal, smoking status "
            + "or match.";

    private final Predicate<Person> predicate;

    /**
     * Creates a search using the supplied matching criterion.
     */
    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
