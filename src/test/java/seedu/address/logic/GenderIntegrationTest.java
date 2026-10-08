package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class GenderIntegrationTest {
    private static final Person MAN = new PersonBuilder().withName("John").withGender("m").build();
    private static final Person WOMAN = new PersonBuilder().withName("Mary").withGender("w").build();
    private static final Person NON_BINARY = new PersonBuilder().withName("Alex").withGender("nb").build();
    private static final Person UNSPECIFIED = new PersonBuilder().withName("Sam").build();

    @TempDir
    Path temporaryFolder;

    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;
    private Path dataPath;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        dataPath = temporaryFolder.resolve("addressbook.json");
        storage = new JsonAddressBookStorage(dataPath);
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        List.of(MAN, WOMAN, NON_BINARY, UNSPECIFIED).forEach(model::addPerson);
    }

    @Test
    public void find_allGenderSelections_searchesFullListWithoutUnspecifiedClients() throws Exception {
        String[] queries = {"m", "w", "nb", "m,w", "m,nb", "w,nb", "m,w,nb", ""};
        List<List<Person>> results = List.of(List.of(MAN), List.of(WOMAN), List.of(NON_BINARY),
                List.of(MAN, WOMAN), List.of(MAN, NON_BINARY), List.of(WOMAN, NON_BINARY),
                List.of(MAN, WOMAN, NON_BINARY), List.of(MAN, WOMAN, NON_BINARY));
        for (int i = 0; i < queries.length; i++) {
            String feedback = logic.execute("find g/" + queries[i]).getFeedbackToUser();
            assertEquals(results.get(i), model.getFilteredPersonList(), queries[i]);
            assertEquals(results.get(i).size() + " person(s) listed!", feedback);
        }
    }

    @Test
    public void find_nameSearchRemainsAvailableAndGenderSearchCanReturnNoMatches() throws Exception {
        logic.execute("find g/w");
        logic.execute("find aLeX Sam");
        assertEquals(List.of(NON_BINARY, UNSPECIFIED), model.getFilteredPersonList());
        model.deletePerson(MAN);
        logic.execute("find g/m");
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void edit_filteredIndexChangesAndClearsGenderWithoutChangingOtherFields() throws Exception {
        logic.execute("find g/nb");
        Person changed = new PersonBuilder(NON_BINARY).withGender("w").build();
        String feedback = logic.execute("edit 1 g/w").getFeedbackToUser();
        assertTrue(feedback.contains("Gender: Woman"));
        assertEquals(List.of(MAN, WOMAN, changed, UNSPECIFIED), model.getAddressBook().getPersonList());

        logic.execute("edit 3 n/Alex New p/98765432 e/new@example.com a/New Road t/friend");
        Person otherFieldsEdited = new PersonBuilder(changed).withName("Alex New").withPhone("98765432")
                .withEmail("new@example.com").withAddress("New Road").withTags("friend").build();
        assertEquals(otherFieldsEdited, model.getAddressBook().getPersonList().get(2));

        logic.execute("edit 3 g/");
        assertEquals(new PersonBuilder(otherFieldsEdited).withGender("").build(),
                model.getAddressBook().getPersonList().get(2));
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void add_genderPersistsTogetherWithAllExistingRecords() throws Exception {
        Person added = new PersonBuilder().withName("Taylor").withGender("nb").withTags("friend").build();
        String feedback = logic.execute(PersonUtil.getAddCommand(added)).getFeedbackToUser();
        assertTrue(feedback.contains("Gender: Non-binary"));
        assertEquals(added, model.getAddressBook().getPersonList().getLast());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void invalidGenderCommands_leaveDataResultsAndSavedFileUnchanged() throws Exception {
        logic.execute("find g/w");
        AddressBook before = new AddressBook(model.getAddressBook());
        String savedBefore = Files.readString(dataPath);
        for (String command : List.of("find g/m,M", "edit 1 g/m,w",
                "add n/New Client p/98765432 e/new@example.com a/New Road g/x")) {
            assertThrows(ParseException.class, () -> logic.execute(command), command);
            assertEquals(before, model.getAddressBook());
            assertEquals(List.of(WOMAN), model.getFilteredPersonList());
            assertEquals(savedBefore, Files.readString(dataPath));
        }
    }

    @Test
    public void add_sameClientWithDifferentGender_isStillDuplicate() {
        Person duplicate = new PersonBuilder(MAN).withGender("nb").build();
        assertThrows(CommandException.class, () -> logic.execute(PersonUtil.getAddCommand(duplicate)));
        assertEquals(List.of(MAN, WOMAN, NON_BINARY, UNSPECIFIED), model.getAddressBook().getPersonList());
    }
}
