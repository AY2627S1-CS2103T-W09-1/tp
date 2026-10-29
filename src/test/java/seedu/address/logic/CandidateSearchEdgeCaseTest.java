package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

/**
 * Exercises candidate ranking together with displayed indexes, persistence and observable lists.
 */
public class CandidateSearchEdgeCaseTest {
    @TempDir
    public Path folder;

    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(folder.resolve("book.json"));
        initializeLogic();
    }

    @Test
    public void findMatch_reciprocalPreferences_bothDirectionsContributeToRank() throws Exception {
        addClient("Target", 25, "g/w {r/Buddhism}");
        addClient("One Direction", 30, "r/Buddhism");
        addClient("Both Directions", 30, "r/Buddhism {g/w}");
        addClient("Reverse Only", 30, "r/Jainism {age/25}");

        logic.execute("find match/1");

        assertDisplayed("Both Directions", "One Direction", "Reverse Only");
    }

    @Test
    public void findMatch_respectedDealbreakers_doNotIncreasePreferenceScore() throws Exception {
        addClient("Target", 25, "g/w s/no r/Jainism {r/Buddhism rg/long}");
        addClient("Many Respected Exclusions", 30, "r/Islam <age/99 g/m s/yes r/Islam rg/short>");
        addClient("One Met Preference", 30, "r/Buddhism");

        logic.execute("find match/1");

        assertDisplayed("One Met Preference", "Many Respected Exclusions");
    }

    @Test
    public void findMatch_unknownAndUnmetPreferences_remainVisibleWithoutScoring() throws Exception {
        addClient("Target", 25, "{r/Buddhism} <s/yes>");
        addClient("Unknown", 30, "");
        addClient("Unmet", 30, "r/Jainism s/no");
        addClient("Met", 30, "r/Buddhism s/no");
        addClient("Excluded DespitePreference", 30, "r/Buddhism s/yes {age/25}");

        logic.execute("find match/1");

        assertDisplayed("Met", "Unknown", "Unmet");
    }

    @Test
    public void findMatch_equalScores_preservesAddressBookOrderAfterReverseGroupInsertion() throws Exception {
        addClient("Target", 25, "");
        addClient("Alpha", 30, "");
        addClient("Beta", 30, "");
        logic.execute("grp Reverse add 3 2 1");
        logic.execute("show Reverse");

        logic.execute("find match/1");

        assertDisplayed("Alpha", "Beta");
    }

    @Test
    public void findMatch_filteredTarget_searchesFullBookWithoutChangingStoredRecords() throws Exception {
        addClient("Target", 25, "{r/Buddhism}");
        addClient("Low", 30, "r/Jainism");
        addClient("High", 30, "r/Buddhism");
        logic.execute("find n/Target");
        AddressBook original = new AddressBook(model.getAddressBook());
        String saved = Files.readString(storage.getAddressBookFilePath());

        assertEquals("2 person(s) listed!", logic.execute("find match/1").getFeedbackToUser());

        assertDisplayed("High", "Low");
        assertEquals(original, model.getAddressBook());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void findMatch_chainedSearch_resolvesNewDisplayedTarget() throws Exception {
        addClient("First Target", 25, "{r/Buddhism}");
        addClient("Low", 30, "r/Jainism");
        addClient("High", 30, "r/Buddhism {age/25}");
        logic.execute("find match/1");
        assertDisplayed("High", "Low");

        logic.execute("find match/1");

        assertDisplayed("First Target", "Low");
    }

    @Test
    public void findMatch_nonOverlappingAgePreferenceAndExclusion_evaluatesEverySupportedAge() throws Exception {
        addClient("Target", 25, "{age/25-29} <age/30-32>");
        List<String> preferred = new ArrayList<>();
        List<String> other = new ArrayList<>();
        for (int age = 18; age <= 99; age++) {
            String name = "Candidate " + age;
            addClient(name, age, "");
            if (age >= 30 && age <= 32) {
                continue;
            }
            if (age >= 25 && age <= 29) {
                preferred.add(name);
            } else {
                other.add(name);
            }
        }
        preferred.addAll(other);

        logic.execute("find match/1");

        assertEquals(preferred, displayedNames());
    }

    @Test
    public void findMatch_invalidCommands_preserveRankedResultsRecordsAndFile() throws Exception {
        addClient("Target", 25, "{r/Buddhism}");
        addClient("Low", 30, "r/Jainism");
        addClient("High", 30, "r/Buddhism");
        logic.execute("find match/1");
        AddressBook original = new AddressBook(model.getAddressBook());
        List<Person> displayed = List.copyOf(model.getFilteredPersonList());
        String saved = Files.readString(storage.getAddressBookFilePath());

        for (String command : List.of("find match/", "find match/0", "find match/-1",
                "find match/2147483648", "find match/1 match/2", "find match/1 r/Buddhism")) {
            assertThrows(ParseException.class, () -> logic.execute(command));
            assertUnchanged(original, displayed, saved);
        }
        assertThrows(CommandException.class, () -> logic.execute("find match/3"));
        assertUnchanged(original, displayed, saved);
    }

    @Test
    public void findMatch_noCandidates_emptyResultAndSubsequentIndexFailureAreSafe() throws Exception {
        addClient("Target", 25, "<r/Buddhism>");
        addClient("Excluded", 30, "r/Buddhism");

        assertEquals("0 person(s) listed!", logic.execute("find match/1").getFeedbackToUser());
        assertDisplayed();
        String saved = Files.readString(storage.getAddressBookFilePath());
        assertThrows(CommandException.class, () -> logic.execute("find match/1"));
        assertDisplayed();
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        logic.execute("list");
        assertDisplayed("Target", "Excluded");
    }

    @Test
    public void deleteAndClear_rankedResults_updateObserversAndSurviveReload() throws Exception {
        addClient("Target", 25, "{r/Buddhism}");
        addClient("Low", 30, "r/Jainism");
        addClient("High", 30, "r/Buddhism");
        logic.execute("grp All add 1 2 3");
        logic.execute("find match/1");
        ObservableList<Person> observed = FXCollections.observableArrayList();
        Bindings.bindContent(observed, model.getFilteredPersonList());
        List<Throwable> listenerExceptions = new ArrayList<>();
        Thread thread = Thread.currentThread();
        Thread.UncaughtExceptionHandler previousHandler = thread.getUncaughtExceptionHandler();
        thread.setUncaughtExceptionHandler((unused, exception) -> listenerExceptions.add(exception));
        try {
            logic.execute("delete 1");
            assertDisplayed("Low");
            assertEquals(model.getFilteredPersonList(), observed);
            logic.execute("clear");
            assertDisplayed();
            assertTrue(observed.isEmpty());
            assertTrue(model.getAddressBook().getGroupList().isEmpty());
            assertEquals(List.of(), listenerExceptions);
        } finally {
            thread.setUncaughtExceptionHandler(previousHandler);
            Bindings.unbindContent(observed, model.getFilteredPersonList());
        }
        reload();
        assertDisplayed();
        assertTrue(model.getAddressBook().getGroupList().isEmpty());
    }

    @Test
    public void findMatch_reload_preservesRankingInputsWithoutPersistingDisplayOrder() throws Exception {
        addClient("Target", 25, "{r/Buddhism}");
        addClient("Low", 30, "r/Jainism t/Buddhism");
        addClient("High", 30, "r/Buddhism");
        logic.execute("find match/1");
        assertDisplayed("High", "Low");

        reload();

        assertDisplayed("Target", "Low", "High");
        logic.execute("find match/1");
        assertDisplayed("High", "Low");
    }

    private void addClient(String name, int age, String fields) throws Exception {
        logic.execute("add n/" + name + " p/91234567 e/test@example.com a/Test Lane age/" + age + " " + fields);
    }

    private void assertDisplayed(String... names) {
        assertEquals(List.of(names), displayedNames());
    }

    private List<String> displayedNames() {
        return model.getFilteredPersonList().stream().map(person -> person.getName().fullName).toList();
    }

    private void assertUnchanged(AddressBook original, List<Person> displayed, String saved) throws Exception {
        assertEquals(original, model.getAddressBook());
        assertEquals(displayed, model.getFilteredPersonList());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
    }

    private void reload() throws Exception {
        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        initializeLogic();
    }

    private void initializeLogic() {
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(folder.resolve("prefs.json"))));
    }
}
