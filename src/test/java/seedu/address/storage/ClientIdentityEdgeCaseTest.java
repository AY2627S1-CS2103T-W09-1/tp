package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.group.ClientGroup;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Checks that valid, similar-looking client identities are not merged or reassigned during persistence.
 */
public class ClientIdentityEdgeCaseTest {
    @TempDir
    public Path folder;

    private final ObjectMapper mapper = new ObjectMapper();
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() {
        storage = new JsonAddressBookStorage(folder.resolve("book.json"));
    }

    @Test
    public void saveAndReload_caseAndSpaceDistinctNames_preservesExactMemberReferences() throws Exception {
        AddressBook book = new AddressBook();
        for (String name : List.of("Alice", "alice", "Alice  Alpha", "Alice ")) {
            book.addPerson(new PersonBuilder().withName(name).build());
        }
        List<Person> persons = book.getPersonList();
        book.setGroups(List.of(new ClientGroup(new GroupName("Cases"), List.of(persons.get(0), persons.get(1))),
                new ClientGroup(new GroupName("Spaces"), List.of(persons.get(2), persons.get(3)))));

        storage.saveAddressBook(book);
        ReadOnlyAddressBook restored = storage.readAddressBook().orElseThrow();

        assertEquals(book, restored);
        for (int groupIndex = 0; groupIndex < 2; groupIndex++) {
            List<Person> members = restored.getGroupList().get(groupIndex).getMembers();
            for (int memberIndex = 0; memberIndex < 2; memberIndex++) {
                assertSame(restored.getPersonList().get(groupIndex * 2 + memberIndex), members.get(memberIndex));
            }
        }
    }

    @Test
    public void saveAndReload_caseDistinctAndNumericTags_preservesSetsAndBoundaryGroupName() throws Exception {
        AddressBook book = new AddressBook();
        Person person = new PersonBuilder().withTags("VIP", "vip", "001").build();
        book.addPerson(person);
        String boundaryName = "A" + "1".repeat(29);
        book.setGroups(List.of(new ClientGroup(new GroupName(boundaryName), List.of(person))));

        storage.saveAddressBook(book);
        ReadOnlyAddressBook restored = storage.readAddressBook().orElseThrow();

        assertEquals(Set.of("VIP", "vip", "001"), restored.getPersonList().getFirst().getTags().stream()
                .map(tag -> tag.tagName).collect(Collectors.toSet()));
        assertEquals(boundaryName, restored.getGroupList().getFirst().getName().toString());
        assertEquals(book, restored);
    }

    @Test
    public void read_omittedAndNullOptionalFields_remainUnknownAcrossSecondSave() throws Exception {
        AddressBook book = new AddressBook();
        book.addPerson(new PersonBuilder().withName("Omitted").withReligion("No religion")
                .withRelationshipGoal("unsure").build());
        book.addPerson(new PersonBuilder().withName("Explicit Null").withReligion("Buddhism")
                .withRelationshipGoal("long").build());
        storage.saveAddressBook(book);
        ObjectNode json = readFixture();
        ObjectNode omitted = (ObjectNode) json.get("persons").get(0);
        ObjectNode explicitNull = (ObjectNode) json.get("persons").get(1);
        for (String field : List.of("religion", "relationshipGoal", "preferences", "dealbreakers")) {
            omitted.remove(field);
            explicitNull.putNull(field);
        }
        writeFixture(json);

        ReadOnlyAddressBook restored = storage.readAddressBook().orElseThrow();

        for (Person person : restored.getPersonList()) {
            assertTrue(person.getReligion().isEmpty());
            assertTrue(person.getRelationshipGoal().isEmpty());
            assertTrue(person.getDatingCriteria().getPreferences().isEmpty());
            assertTrue(person.getDatingCriteria().getDealbreakers().isEmpty());
        }
        storage.saveAddressBook(restored);
        assertEquals(restored, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void read_manuallyRenamedAndReorderedClients_resolvesUpdatedExactReference() throws Exception {
        AddressBook book = new AddressBook();
        Person first = new PersonBuilder().withName("Alice").build();
        Person second = new PersonBuilder().withName("alice").build();
        book.addPerson(first);
        book.addPerson(second);
        book.setGroups(List.of(new ClientGroup(new GroupName("Chosen"), List.of(first))));
        storage.saveAddressBook(book);
        ObjectNode json = readFixture();
        ArrayNode persons = (ArrayNode) json.get("persons");
        ObjectNode renamed = (ObjectNode) persons.remove(0);
        renamed.put("name", "Alice  Renamed");
        persons.add(renamed);
        ArrayNode members = (ArrayNode) json.get("groups").get(0).get("memberNames");
        members.removeAll();
        members.add("Alice  Renamed");
        writeFixture(json);

        ReadOnlyAddressBook restored = storage.readAddressBook().orElseThrow();

        assertEquals(List.of("alice", "Alice  Renamed"), restored.getPersonList().stream()
                .map(person -> person.getName().fullName).toList());
        assertSame(restored.getPersonList().get(1), restored.getGroupList().getFirst().getMembers().getFirst());
        storage.saveAddressBook(restored);
        assertEquals(restored, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void read_wrongCaseMemberReference_rejectsWithoutReassigningOrChangingFile() throws Exception {
        AddressBook book = new AddressBook();
        Person person = new PersonBuilder().withName("Alice").build();
        book.addPerson(person);
        book.setGroups(List.of(new ClientGroup(new GroupName("Chosen"), List.of(person))));
        storage.saveAddressBook(book);
        ObjectNode json = readFixture();
        ArrayNode members = (ArrayNode) json.get("groups").get(0).get("memberNames");
        members.removeAll();
        members.add("ALICE");
        writeFixture(json);
        String saved = Files.readString(storage.getAddressBookFilePath());

        assertThrows(DataLoadingException.class, storage::readAddressBook);

        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
    }

    private ObjectNode readFixture() throws Exception {
        return (ObjectNode) mapper.readTree(Files.readString(storage.getAddressBookFilePath()));
    }

    private void writeFixture(ObjectNode json) throws Exception {
        Files.writeString(storage.getAddressBookFilePath(), json.toString());
    }
}
