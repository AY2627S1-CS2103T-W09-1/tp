package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.group.ClientGroup;
import seedu.address.model.group.GroupName;

public class GroupStorageTest {
    @TempDir
    public Path folder;

    @Test
    public void saveAndReload_preservesMultipleAndEmptyGroupsAndPreservesReferences() throws Exception {
        AddressBook book = new AddressBook();
        book.addPerson(ALICE);
        book.addPerson(BOB);
        book.setGroups(List.of(new ClientGroup(new GroupName("VIP"), List.of(BOB, ALICE)),
                new ClientGroup(new GroupName("Other"), List.of(BOB)),
                new ClientGroup(new GroupName("Empty"), List.of())));
        JsonAddressBookStorage storage = new JsonAddressBookStorage(folder.resolve("book.json"));
        storage.saveAddressBook(book);
        assertEquals(book, storage.readAddressBook().orElseThrow());
        book.removePerson(ALICE);
        storage.saveAddressBook(book);
        assertEquals(book, storage.readAddressBook().orElseThrow());
        assertEquals(List.of(BOB), storage.readAddressBook().orElseThrow().getGroupList().getFirst().getMembers());
    }

    @Test
    public void read_reorderedPersons_preservesGroupMembership() throws Exception {
        AddressBook book = new AddressBook();
        book.addPerson(ALICE);
        book.addPerson(BOB);
        book.setGroups(List.of(new ClientGroup(new GroupName("VIP"), List.of(BOB))));
        JsonAddressBookStorage storage = new JsonAddressBookStorage(folder.resolve("book.json"));
        storage.saveAddressBook(book);
        ObjectNode json = (ObjectNode) new ObjectMapper().readTree(Files.readString(storage.getAddressBookFilePath()));
        ArrayNode persons = (ArrayNode) json.get("persons");
        JsonNode first = persons.remove(0);
        persons.add(first);
        Files.writeString(storage.getAddressBookFilePath(), json.toString());
        assertEquals(List.of(BOB), storage.readAddressBook().orElseThrow().getGroupList().getFirst().getMembers());
    }

    @Test
    public void read_legacyFileWithoutGroups_defaultsToEmpty() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(folder.resolve("book.json"));
        AddressBook book = new AddressBook();
        book.addPerson(ALICE);
        storage.saveAddressBook(book);
        ObjectNode json = (ObjectNode) new ObjectMapper().readTree(Files.readString(storage.getAddressBookFilePath()));
        json.remove("groups");
        Files.writeString(storage.getAddressBookFilePath(), json.toString());
        assertEquals(book, storage.readAddressBook().orElseThrow());
        assertTrue(storage.readAddressBook().orElseThrow().getGroupList().isEmpty());
    }

    @Test
    public void save_withoutGroups_preservesEmptyGroupsField() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(folder.resolve("book.json"));
        AddressBook book = new AddressBook();
        book.addPerson(ALICE);

        storage.saveAddressBook(book);

        JsonNode json = new ObjectMapper().readTree(Files.readString(storage.getAddressBookFilePath()));
        assertTrue(json.get("groups").isArray());
        assertEquals(0, json.get("groups").size());
        assertEquals(book, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void read_malformedGroups_reportsDataLoadingErrorWithoutChangingFile() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(folder.resolve("book.json"));
        AddressBook book = new AddressBook();
        book.addPerson(ALICE);
        storage.saveAddressBook(book);
        ObjectNode json = (ObjectNode) new ObjectMapper().readTree(Files.readString(storage.getAddressBookFilePath()));
        for (String groups : new String[] {
            "null", "[null]", "[{\"name\":\"VIP\"}]", "[{\"memberNames\":[]}]",
            "[{\"name\":123,\"memberNames\":[]}]",
            "[{\"name\":true,\"memberNames\":[]}]",
            "[{\"name\":null,\"memberNames\":[]}]",
            "[{\"name\":\"VIP\",\"memberNames\":null}]",
            "[{\"name\":\"VIP\",\"memberNames\":\"Alice Pauline\"}]",
            "[{\"name\":\"VIP\",\"memberNames\":{}}]",
            "[{\"name\":\"bad name\",\"memberNames\":[]}]",
            "[{\"name\":\"VIP\",\"memberNames\":[0]}]",
            "[{\"name\":\"VIP\",\"memberNames\":[2]}]",
            "[{\"name\":\"VIP\",\"memberNames\":[-1]}]",
            "[{\"name\":\"VIP\",\"memberNames\":[\"Alice Pauline\",\"Alice Pauline\"]}]",
            "[{\"name\":\"VIP\",\"memberNames\":[1.5]}]",
            "[{\"name\":\"VIP\",\"memberNames\":[\"Missing client\"]}]",
            "[{\"name\":\"VIP\",\"memberNames\":[null]}]",
            "[{\"name\":\"VIP\",\"memberNames\":[2147483648]}]",
            "[{\"name\":\"VIP\",\"memberNames\":[]},{\"name\":\"vip\",\"memberNames\":[]}]"}) {
            json.set("groups", new ObjectMapper().readTree(groups));
            String invalid = json.toString();
            Files.writeString(storage.getAddressBookFilePath(), invalid);
            assertThrows(DataLoadingException.class, storage::readAddressBook);
            assertEquals(invalid, Files.readString(storage.getAddressBookFilePath()));
        }
    }
}
