package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;

/**
 * Replaces or clears the remark of a displayed person for Tutorial 2.
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Replaces the remark of the person identified by the displayed index.\n"
            + "Parameters: INDEX (must be a positive integer) r/[REMARK]\n"
            + "Example: " + COMMAND_WORD + " 1 r/Likes to swim";
    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to %1$s: %2$s";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from %1$s";

    private final Index index;
    private final Remark remark;

    /**
     * Creates a command to replace the indexed person's remark.
     */
    public RemarkCommand(Index index, Remark remark) {
        requireAllNonNull(index, remark);
        this.index = index;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedPersons = model.getFilteredPersonList();
        if (index.getZeroBased() >= displayedPersons.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person original = displayedPersons.get(index.getZeroBased());
        Person edited = new Person(original, remark);
        model.setPerson(original, edited);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        String feedback = remark.value.isEmpty()
                ? String.format(MESSAGE_DELETE_REMARK_SUCCESS, edited.getName())
                : String.format(MESSAGE_ADD_REMARK_SUCCESS, edited.getName(), remark);
        return new CommandResult(feedback);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof RemarkCommand otherCommand
                && index.equals(otherCommand.index) && remark.equals(otherCommand.remark);
    }
}
