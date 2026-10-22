package seedu.address.model.group;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import seedu.address.model.person.Person;

/** An immutable named collection of existing clients, independent of ordinary tags. */
public final class ClientGroup {
    private final GroupName name;
    private final List<Person> members;

    /** Creates a group, rejecting duplicate or null members. Empty groups are valid. */
    public ClientGroup(GroupName name, List<Person> members) {
        this.name = requireNonNull(name);
        this.members = List.copyOf(members);
        checkArgument(new HashSet<>(this.members).size() == this.members.size(), "Duplicate group members.");
    }

    public GroupName getName() {
        return name;
    }

    /** Returns an immutable membership snapshot. */
    public List<Person> getMembers() {
        return members;
    }

    public boolean contains(Person person) {
        return members.contains(requireNonNull(person));
    }

    /** Returns a copy with a new membership snapshot and the original display name. */
    public ClientGroup withMembers(List<Person> replacement) {
        return new ClientGroup(name, replacement);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ClientGroup group
                && name.equals(group.name) && members.equals(group.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, members);
    }
}
