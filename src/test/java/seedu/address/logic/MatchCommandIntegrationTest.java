package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MatchCommand;
import seedu.address.logic.commands.MatchCommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class MatchCommandIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json"));
        initializeLogic();
        logic.execute("add n/Hidden p/99999999 e/hidden@example.com a/Home age/50");
        logic.execute("add n/Alice p/11111111 e/alice@example.com a/Home age/28 s/no rg/long"
                + " {age/25-35} <s/yes>");
        logic.execute("add n/Bob p/22222222 e/bob@example.com a/Home age/30 s/yes"
                + " {r/Buddhism rg/long}");
    }

    @Test
    public void execute_filteredIndexes_displaysBothDirectionsWithoutChangingData() throws Exception {
        logic.execute("find age/25-35");
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Person> displayed = List.copyOf(model.getFilteredPersonList());
        String saved = Files.readString(storage.getAddressBookFilePath());

        MatchCommandResult result = assertInstanceOf(MatchCommandResult.class, logic.execute("match 1 2"));
        assertEquals("Alice", result.getFirstName());
        assertEquals("Bob", result.getSecondName());
        assertEquals(2, result.getAssessment().getFirstComparisons().size());
        String feedback = result.getFeedbackToUser();
        assertEquals("Alice <-> Bob\nResult: Incompatible"
                + "\n\nAlice's criteria -> Bob"
                + "\nMet - Preference: Age 25-35; Actual: 30"
                + "\nViolated - Dealbreaker: Smoking Exclude: Yes; Actual: Yes"
                + "\n\nBob's criteria -> Alice"
                + "\nUnknown - Preference: Religion Buddhism; Actual: Not specified"
                + "\nMet - Preference: Relationship goal Long-term relationship; Actual: Long-term relationship",
                feedback);
        assertEquals(before, model.getAddressBook());
        assertEquals(displayed, model.getFilteredPersonList());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_invalidIndexes_preservesDisplayedListAndData() throws Exception {
        logic.execute("find age/25-35");
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Person> displayed = List.copyOf(model.getFilteredPersonList());
        String saved = Files.readString(storage.getAddressBookFilePath());
        for (String command : new String[] {"match 1 3", "match 3 1"}) {
            assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, ()
                -> logic.execute(command));
        }
        assertThrows(ParseException.class, MatchCommand.MESSAGE_SAME_CLIENT, () -> logic.execute("match 1 1"));
        assertThrows(CommandException.class, MatchCommand.MESSAGE_SAME_CLIENT, ()
            -> new MatchCommand(Index.fromOneBased(1), Index.fromOneBased(1)).execute(model));
        assertEquals(before, model.getAddressBook());
        assertEquals(displayed, model.getFilteredPersonList());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_afterReload_usesSavedCriteria() throws Exception {
        String expected = logic.execute("match 2 3").getFeedbackToUser();
        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        initializeLogic();
        assertEquals(expected, logic.execute("match 2 3").getFeedbackToUser());
    }

    @Test
    public void execute_editedCriteria_reportsUnknownCompatibleAndEmpty() throws Exception {
        logic.execute("edit 2 <>");
        assertTrue(logic.execute("match 2 3").getFeedbackToUser().contains("Result: Insufficient information"));
        logic.execute("edit 2 r/Islam");
        String feedback = logic.execute("match 2 3").getFeedbackToUser();
        assertTrue(feedback.contains("Result: Compatible with recorded dealbreakers"));
        assertTrue(feedback.contains("Unmet - Preference: Religion Buddhism; Actual: Islam"));
        logic.execute("edit 2 {}");
        logic.execute("edit 3 {}");
        feedback = logic.execute("match 2 3").getFeedbackToUser();
        assertTrue(feedback.contains("Result: Insufficient information"));
        assertTrue(feedback.contains("No criteria recorded."));
        assertFalse(feedback.contains("Preference:"));
    }

    @Test
    public void execute_emptyDisplayedList_rejectsIndexes() throws Exception {
        logic.execute("find Nobody");
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, ()
            -> logic.execute("match 1 2"));
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    private void initializeLogic() {
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }
}
