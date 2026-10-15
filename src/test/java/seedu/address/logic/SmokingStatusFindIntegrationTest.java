package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.SmokingStatus;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/** Tests smoking searches through command parsing, filtering and file-backed application logic. */
public class SmokingStatusFindIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json"));
        initializeLogic();
    }

    @Test
    public void execute_searchesFullBookByOwnStatus_preservesRecordsAndSupportsList() throws Exception {
        addClients();
        AddressBook before = new AddressBook(model.getAddressBook());
        String saved = Files.readString(storage.getAddressBookFilePath());
        assertSearch("find s/YES", "Alice", "Dana");
        assertSearch("find s/ no ", "Bob");
        assertSearch("find Cara", "Cara");
        assertSearch("find s/yes", "Alice", "Dana");
        logic.execute("list");
        assertEquals(before.getPersonList(), model.getFilteredPersonList());
        assertEquals(before, model.getAddressBook());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_invalidSearch_preservesPreviousResultsAndSavedData() throws Exception {
        addClients();
        assertSearch("find s/no", "Bob");
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Person> displayed = List.copyOf(model.getFilteredPersonList());
        String saved = Files.readString(storage.getAddressBookFilePath());
        for (String command : new String[] {"find s/", "find s/maybe", "find s/yes s/no",
            "find s/yes age/25", "find g/m s/no", "find s/no r/Jainism", "find rg/long s/yes",
            "find Alice s/yes", "find s/no Alice", "find {s/yes}", "find <s/no>"}) {
            assertThrows(ParseException.class, () -> logic.execute(command));
            assertEquals(displayed, model.getFilteredPersonList());
            assertEquals(before, model.getAddressBook());
            assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        }
    }

    @Test
    public void execute_afterReload_searchesRecordedStatusAndExcludesUnspecified() throws Exception {
        addClients();
        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        initializeLogic();
        assertSearch("find s/yes", "Alice", "Dana");
        assertSearch("find s/no", "Bob");
    }

    @Test
    public void execute_emptyBookOrNoMatchingStatus_reportsZero() throws Exception {
        assertSearch("find s/yes");
        assertSearch("find s/no");
        assertTrue(storage.readAddressBook().orElseThrow().getPersonList().isEmpty());
        logic.execute("add n/Cara p/33333333 e/cara@example.com a/Home age/25");
        logic.execute("add n/Bob p/22222222 e/bob@example.com a/Home age/25 s/no");
        assertSearch("find s/yes");
        assertSearch("find s/no", "Bob");
    }

    @Test
    public void execute_editAndDeleteAfterSearch_targetDisplayedClient() throws Exception {
        addClients();
        Person alice = model.getAddressBook().getPersonList().get(0);
        assertSearch("find s/no", "Bob");
        logic.execute("edit 1 s/yes");
        assertEquals(alice, model.getAddressBook().getPersonList().get(0));
        assertEquals(new SmokingStatus("yes"), model.getAddressBook().getPersonList().get(1).getSmokingStatus());
        assertSearch("find s/no");
        assertSearch("find s/yes", "Alice", "Bob", "Dana");
        logic.execute("delete 2");
        assertSearch("find s/yes", "Alice", "Dana");
        assertEquals(List.of("Alice", "Cara", "Dana"), model.getAddressBook().getPersonList().stream()
                .map(person -> person.getName().fullName).toList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    private void initializeLogic() {
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    private void addClients() throws Exception {
        // Opposing partner criteria must not determine a client's own-status search result.
        logic.execute("add n/Alice p/11111111 e/alice@example.com a/Home age/25 s/yes {s/no} <s/yes>");
        logic.execute("add n/Bob p/22222222 e/bob@example.com a/Home age/25 s/no {s/yes} <s/no>");
        logic.execute("add n/Cara p/33333333 e/cara@example.com a/Home age/25 {s/yes} <s/no>");
        logic.execute("add n/Dana p/44444444 e/dana@example.com a/Home age/25 s/yes");
    }

    private void assertSearch(String command, String... expectedNames) throws Exception {
        String feedback = logic.execute(command).getFeedbackToUser();
        assertEquals(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, expectedNames.length), feedback);
        assertEquals(List.of(expectedNames), model.getFilteredPersonList().stream()
                .map(person -> person.getName().fullName).toList());
    }
}
