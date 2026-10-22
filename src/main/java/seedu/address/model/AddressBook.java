package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.group.ClientGroup;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();
    private final ObservableList<ClientGroup> groups = FXCollections.observableArrayList();
    private final ObservableList<ClientGroup> unmodifiableGroups = FXCollections.unmodifiableObservableList(groups);

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
        groups.setAll(groups.stream().map(group -> group.withMembers(group.getMembers().stream()
                .filter(persons::contains).toList())).toList());
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        // Validate a complete snapshot before changing either observable collection.
        UniquePersonList replacement = new UniquePersonList();
        replacement.setPersons(newData.getPersonList());
        List<ClientGroup> replacementGroups = List.copyOf(newData.getGroupList());
        validateGroups(replacementGroups, replacement.asUnmodifiableObservableList());
        persons.setPersons(replacement);
        groups.setAll(replacementGroups);
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
        groups.setAll(groups.stream().map(group -> group.withMembers(group.getMembers().stream()
                .map(member -> member.equals(target) ? editedPerson : member).toList())).toList());
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
        groups.setAll(groups.stream().map(group -> group.withMembers(group.getMembers().stream()
                .filter(member -> !member.equals(key)).toList())).toList());
    }

    /** Replaces groups only after validating names and membership against this address book. */
    public void setGroups(List<ClientGroup> replacement) {
        List<ClientGroup> snapshot = List.copyOf(replacement);
        validateGroups(snapshot, getPersonList());
        groups.setAll(snapshot);
    }

    private static void validateGroups(List<ClientGroup> replacement, List<Person> persons) {
        HashSet<GroupName> names = new HashSet<>();
        for (ClientGroup group : replacement) {
            if (!names.add(group.getName())) {
                throw new IllegalArgumentException("Duplicate group name: " + group.getName());
            }
            if (!persons.containsAll(group.getMembers())) {
                throw new IllegalArgumentException("Group contains a client absent from the address book.");
            }
        }
    }

    @Override
    public ObservableList<ClientGroup> getGroupList() {
        return unmodifiableGroups;
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons) && groups.equals(otherAddressBook.groups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, groups);
    }
}
