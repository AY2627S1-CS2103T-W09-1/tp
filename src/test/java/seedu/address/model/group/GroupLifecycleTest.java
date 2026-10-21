package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.testutil.PersonBuilder;

public class GroupLifecycleTest {
    private final GroupName name = new GroupName("VIP");

    @Test
    public void setGroups_rejectsDuplicatesAndMissingClientsAtomically() {
        AddressBook book = bookWithGroup();
        AddressBook before = new AddressBook(book);
        assertThrows(IllegalArgumentException.class, () -> book.setGroups(List.of(
                new ClientGroup(name, List.of(ALICE)), new ClientGroup(new GroupName("vip"), List.of(BOB)))));
        Person stranger = new PersonBuilder().withName("Stranger").build();
        assertThrows(IllegalArgumentException.class, () ->
                book.setGroups(List.of(new ClientGroup(name, List.of(stranger)))));
        assertEquals(before, book);
        assertThrows(UnsupportedOperationException.class, () -> book.getGroupList().clear());
    }

    @Test
    public void editDeleteAndClear_updateMembershipAndShownGroup() {
        ModelManager model = new ModelManager(bookWithGroup(), new UserPrefs());
        model.showGroup(new GroupName("vip"));
        assertEquals(List.of(BOB), model.getFilteredPersonList());
        Person renamedBob = new PersonBuilder(BOB).withName("Renamed Bob").build();
        model.setPerson(BOB, renamedBob);
        assertEquals(List.of(renamedBob), model.getFilteredPersonList());
        assertEquals(List.of(renamedBob), model.getAddressBook().getGroupList().getFirst().getMembers());
        model.deletePerson(renamedBob);
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals(1, model.getAddressBook().getGroupList().size());
        model.setAddressBook(new AddressBook());
        assertTrue(model.getAddressBook().getGroupList().isEmpty());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void invalidEditAndUnknownGroup_preserveState() {
        ModelManager model = new ModelManager(bookWithGroup(), new UserPrefs());
        model.showGroup(name);
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(DuplicatePersonException.class, () -> model.setPerson(BOB, ALICE));
        assertThrows(IllegalArgumentException.class, () -> model.showGroup(new GroupName("missing")));
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(BOB), model.getFilteredPersonList());
    }

    @Test
    public void changingMembership_refreshesGroupButDoesNotOverrideOtherSearches() {
        ModelManager model = new ModelManager(bookWithGroup(), new UserPrefs());
        model.showGroup(name);
        model.setGroups(List.of(new ClientGroup(name, List.of(ALICE))));
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
        model.updateFilteredPersonList(BOB::equals);
        model.setGroups(List.of(new ClientGroup(name, List.of())));
        assertEquals(List.of(BOB), model.getFilteredPersonList());
    }

    @Test
    public void resetData_selfAndInvalidReplacement_preserveMembership() {
        AddressBook book = bookWithGroup();
        AddressBook before = new AddressBook(book);
        book.resetData(book);
        assertEquals(before, book);
        ReadOnlyAddressBook invalid = new ReadOnlyAddressBook() {
            @Override
            public ObservableList<Person> getPersonList() {
                return FXCollections.observableArrayList(ALICE);
            }

            @Override
            public ObservableList<ClientGroup> getGroupList() {
                return FXCollections.observableArrayList(new ClientGroup(name, List.of(BOB)));
            }
        };
        assertThrows(IllegalArgumentException.class, () -> book.resetData(invalid));
        assertEquals(before, book);
    }

    @Test
    public void setPersons_prunesAbsentMembersAndRejectsInvalidReplacementAtomically() {
        AddressBook book = bookWithGroup();
        AddressBook before = new AddressBook(book);
        assertThrows(DuplicatePersonException.class, () -> book.setPersons(List.of(ALICE, ALICE)));
        assertEquals(before, book);
        book.setPersons(List.of(ALICE));
        assertEquals(List.of(new ClientGroup(name, List.of())), book.getGroupList());
        book.addPerson(BOB);
        assertTrue(book.getGroupList().getFirst().getMembers().isEmpty());
    }

    private AddressBook bookWithGroup() {
        AddressBook book = new AddressBook();
        book.addPerson(ALICE);
        book.addPerson(BOB);
        book.setGroups(List.of(new ClientGroup(name, List.of(BOB))));
        return book;
    }
}
