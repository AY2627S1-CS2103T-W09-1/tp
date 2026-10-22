package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.ClientGroup;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.MatchAssessment;
import seedu.address.model.person.Person;

/** Changes named group membership without changing client records or ordinary tags. */
public class GroupCommand extends Command {
    public static final String COMMAND_WORD = "grp";
    public static final String MESSAGE_USAGE = "grp NAME add INDEX [MORE_INDEXES] or grp NAME remove INDEX "
            + "[MORE_INDEXES] or grp NAME matches INDEX. Use displayed indexes separated by spaces.";
    public static final String MESSAGE_DUPLICATE_INDEX = "Specify each displayed index only once.";

    /** Supported membership operations; matches creates a new snapshot group. */
    public enum Operation { ADD, REMOVE, MATCHES }

    private final GroupName name;
    private final Operation operation;
    private final List<Index> indexes;

    /** Creates an operation with distinct, non-empty displayed indexes. */
    public GroupCommand(GroupName name, Operation operation, List<Index> indexes) {
        this.name = requireNonNull(name);
        this.operation = requireNonNull(operation);
        this.indexes = List.copyOf(indexes);
        if (indexes.isEmpty() || indexes.stream().map(Index::getZeroBased).distinct().count() != indexes.size()
                || operation == Operation.MATCHES && indexes.size() != 1) {
            throw new IllegalArgumentException(MESSAGE_USAGE);
        }
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        // Resolve every index before mutating anything; a group view can change during removal.
        List<Person> selected = resolveMembers(model.getFilteredPersonList());
        List<ClientGroup> groups = new ArrayList<>(model.getAddressBook().getGroupList());
        ClientGroup existing = groups.stream().filter(group -> group.getName().equals(name)).findFirst().orElse(null);
        ClientGroup replacement;
        int changed;
        if (operation == Operation.MATCHES) {
            replacement = createMatchGroup(model, existing, selected);
            changed = replacement.getMembers().size();
        } else {
            replacement = changeMembership(existing, selected);
            changed = selected.size();
        }
        if (existing == null) {
            groups.add(replacement);
        } else {
            groups.set(groups.indexOf(existing), replacement);
        }
        model.setGroups(groups);
        return new CommandResult(String.format("Group %s: %d client(s) %s. Total members: %d.",
                replacement.getName(), changed, operation == Operation.REMOVE ? "removed" : "added",
                replacement.getMembers().size()));
    }

    private ClientGroup createMatchGroup(Model model, ClientGroup existing, List<Person> selected)
            throws CommandException {
        if (existing != null) {
            throw new CommandException("Group already exists: " + existing.getName());
        }

        Person target = selected.getFirst();
        List<Person> matches = model.getAddressBook().getPersonList().stream()
                .filter(candidate -> !candidate.isSamePerson(target))
                .filter(candidate -> new MatchAssessment(target, candidate).getOutcome()
                        != MatchAssessment.Outcome.INCOMPATIBLE).toList();
        if (matches.isEmpty()) {
            throw new CommandException("No matching candidates; no group was created.");
        }
        return new ClientGroup(name, matches);
    }

    private List<Person> resolveMembers(List<Person> displayed) throws CommandException {
        List<Person> selected = new ArrayList<>();
        for (Index index : indexes) {
            if (index.getZeroBased() >= displayed.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            selected.add(displayed.get(index.getZeroBased()));
        }
        return selected;
    }

    private ClientGroup changeMembership(ClientGroup existing, List<Person> selected) throws CommandException {
        if (operation == Operation.REMOVE && existing == null) {
            throw new CommandException("Unknown group: " + name);
        }
        List<Person> members = existing == null ? new ArrayList<>() : new ArrayList<>(existing.getMembers());
        for (Person person : selected) {
            if (operation == Operation.ADD && members.contains(person)) {
                throw new CommandException("A selected client already belongs to group " + existing.getName() + ".");
            }
            if (operation == Operation.REMOVE && !members.contains(person)) {
                throw new CommandException("A selected client does not belong to group " + existing.getName() + ".");
            }
        }
        if (operation == Operation.ADD) {
            members.addAll(selected);
        } else {
            members.removeAll(selected);
        }
        return new ClientGroup(existing == null ? name : existing.getName(), members);
    }
}
