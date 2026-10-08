package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
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

public class SmokingStatusIntegrationTest {

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    @Test
    public void execute_addAndEditSmokingStatus_persistsAndPreservesOtherDetails() throws Exception {
        String feedback = logic.execute("add n/Alice p/12345678 e/alice@example.com a/Home age/25 t/friend s/YES")
                .getFeedbackToUser();
        assertTrue(feedback.contains("Smoking: yes"));
        assertEquals(new SmokingStatus("yes"), firstPerson().getSmokingStatus());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());

        Person beforeEdit = firstPerson();
        logic.execute("edit 1 s/no");
        Person afterEdit = firstPerson();
        assertEquals(new SmokingStatus("no"), afterEdit.getSmokingStatus());
        assertEquals(beforeEdit.getName(), afterEdit.getName());
        assertEquals(beforeEdit.getPhone(), afterEdit.getPhone());
        assertEquals(beforeEdit.getEmail(), afterEdit.getEmail());
        assertEquals(beforeEdit.getAddress(), afterEdit.getAddress());
        assertEquals(beforeEdit.getTags(), afterEdit.getTags());

        logic.execute("edit 1 p/87654321 t/colleague");
        assertEquals(new SmokingStatus("no"), firstPerson().getSmokingStatus());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_filteredList_editsDisplayedPerson() throws Exception {
        logic.execute("add n/Alice p/12345678 e/alice@example.com a/Home age/20 s/yes");
        logic.execute("add n/Bob p/87654321 e/bob@example.com a/Office age/21");
        logic.execute("find age/21");
        logic.execute("edit 1 s/no");
        assertEquals(new SmokingStatus("yes"), firstPerson().getSmokingStatus());
        assertEquals(new SmokingStatus("no"), model.getAddressBook().getPersonList().get(1).getSmokingStatus());
    }

    @Test
    public void execute_afterReload_preservesStatusAcrossAllOtherFieldEdits() throws Exception {
        logic.execute("add n/Alice p/12345678 e/alice@example.com a/Home age/25 s/yes");
        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        for (String edit : new String[] {"n/Alicia", "p/87654321", "e/new@example.com", "a/Office", "t/friend", "t/"}) {
            logic.execute("edit 1 " + edit);
            assertEquals(new SmokingStatus("yes"), firstPerson().getSmokingStatus());
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        }
    }

    @Test
    public void execute_invalidEditInFilteredList_preservesFilterAndSavedData() throws Exception {
        logic.execute("add n/Alice p/12345678 e/alice@example.com a/Home age/20 s/yes");
        logic.execute("add n/Bob p/87654321 e/bob@example.com a/Office age/21 s/no");
        logic.execute("find age/21");
        Person bob = model.getFilteredPersonList().get(0);
        String saved = Files.readString(storage.getAddressBookFilePath());
        for (String index : new String[] {"0", "-1", "1.5", "2147483648", "999999999999999999999999"}) {
            assertThrows(ParseException.class, () -> logic.execute("edit " + index + " s/yes"));
        }
        assertThrows(CommandException.class, () -> logic.execute("edit 2 s/yes"));
        assertThrows(ParseException.class, () -> logic.execute("edit 1 s/yes s/yes"));
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(bob, model.getFilteredPersonList().get(0));
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_editOnEmptyList_rejectsWithoutCreatingDataFile() {
        assertThrows(CommandException.class, () -> logic.execute("edit 1 s/no"));
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
        assertTrue(Files.notExists(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_invalidCommands_leaveModelAndFileUnchanged() throws Exception {
        logic.execute("add n/Alice p/12345678 e/alice@example.com a/Home age/25");
        assertEquals(SmokingStatus.UNSPECIFIED, firstPerson().getSmokingStatus());
        AddressBook before = new AddressBook(model.getAddressBook());
        String saved = Files.readString(storage.getAddressBookFilePath());
        for (String command : new String[] {"edit 1 s/", "edit 1 s/sometimes", "edit 1 s/no s/yes",
            "add n/Bob p/87654321 e/bob@example.com a/Office age/25 s/unknown"}) {
            assertThrows(ParseException.class, () -> logic.execute(command));
        }
        assertThrows(CommandException.class, () -> logic.execute("edit 2 s/no"));
        // Changing smoking status does not bypass the existing duplicate-person rule.
        String duplicateCommand = "add n/Alice p/12345678 e/alice@example.com a/Home age/25 s/yes";
        assertThrows(CommandException.class, () -> logic.execute(duplicateCommand));
        assertEquals(before, model.getAddressBook());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
    }

    private Person firstPerson() {
        return model.getAddressBook().getPersonList().get(0);
    }
}
