package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.ClientGroup;
import seedu.address.model.group.GroupName;

/** Displays a group's current members in address-book order. */
public class ShowGroupCommand extends Command {
    public static final String COMMAND_WORD = "show";
    public static final String MESSAGE_USAGE = "show NAME: Display a named group's clients. Example: show VIP";
    private final GroupName name;

    /** Creates a command that displays the group with the given (case-insensitive) name. */
    public ShowGroupCommand(GroupName name) {
        this.name = requireNonNull(name);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        ClientGroup group = model.getAddressBook().getGroupList().stream()
                .filter(candidate -> candidate.getName().equals(name)).findFirst()
                .orElseThrow(() -> new CommandException("Unknown group: " + name));
        model.showGroup(name);
        return new CommandResult(String.format("Group %s: %d client(s) listed.", group.getName(),
                model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ShowGroupCommand command && name.equals(command.name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("name", name).toString();
    }
}
