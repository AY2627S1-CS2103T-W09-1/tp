package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.FileUtil;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class RemarkIntegrationTest {

    private static final String ADD_CLIENT = "add n/Amy Demo p/91234567 e/amy@example.com a/Test Lane age/25"
            + " s/no g/nb rg/long r/Jainism t/friend {r/Buddhism} <r/Islam>";

    @TempDir
    Path temporaryFolder;

    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        logic.execute(ADD_CLIENT);
    }

    @Test
    public void remark_replace_preservesAllOtherFieldsAndPersists() throws Exception {
        Person original = first();
        assertTrue(logic.execute("remark 1 r/Likes to swim").getFeedbackToUser().contains("Likes to swim"));
        assertEquals(new Person(original, new Remark("Likes to swim")), first());
        assertEquals(original, new Person(first(), Remark.EMPTY));
        logic.execute("remark 1 r/Likes baseball");
        assertEquals(new Person(original, new Remark("Likes baseball")), first());
        Model restored = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(model.getAddressBook(), restored.getAddressBook());
    }

    @Test
    public void remark_clearWithEmptyOrOmittedPrefix_preservesOtherFields() throws Exception {
        Person original = first();
        for (String command : List.of("remark 1 r/", "remark 1")) {
            logic.execute("remark 1 r/A note");
            assertTrue(logic.execute(command).getFeedbackToUser().contains("Removed remark"));
            assertEquals(original, first());
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        }
    }

    @Test
    public void remark_filteredIndex_updatesOnlyDisplayedClient() throws Exception {
        Person amy = first();
        logic.execute(ADD_CLIENT.replace("Amy Demo", "Bob Demo"));
        Person bob = model.getAddressBook().getPersonList().get(1);
        logic.execute("find n/Bob");
        assertEquals(List.of(bob), model.getFilteredPersonList());
        logic.execute("remark 1 r/Filtered note");
        assertEquals(List.of(amy, new Person(bob, new Remark("Filtered note"))),
                model.getAddressBook().getPersonList());
        assertEquals(2, model.getFilteredPersonList().size());
    }

    @Test
    public void remark_invalidInput_doesNotChangeSavedData() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        for (String command : List.of("remark", "remark 0 r/note", "remark -1 r/note", "remark text r/note",
                "remark 1 r/first r/second")) {
            assertThrows(ParseException.class, () -> logic.execute(command));
            assertEquals(before, model.getAddressBook());
        }
        assertThrows(CommandException.class, () -> logic.execute("remark 2 r/note"));
        logic.execute("find n/Nobody");
        assertThrows(CommandException.class, () -> logic.execute("remark 1 r/note"));
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals(before, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void edit_otherFields_retainsRemarkAndCriteria() throws Exception {
        logic.execute("remark 1 r/Keep this note");
        Person before = first();
        logic.execute("edit 1 p/93456789 r/Christianity");
        assertEquals(before.getRemark(), first().getRemark());
        assertEquals(before.getDatingCriteria(), first().getDatingCriteria());
        assertEquals("Christianity", first().getReligion().orElseThrow().toString());
        assertEquals("93456789", first().getPhone().value);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void read_legacyJsonWithoutRemark_loadsEmptyRemark() throws Exception {
        String saved = FileUtil.readFromFile(storage.getAddressBookFilePath());
        String legacy = saved.replaceAll("(?m)^\\s*\"remark\"\\s*:\\s*\"\"\\s*,?\\s*\\n", "")
                .replaceAll(",\\s*}", "}");
        assertFalse(legacy.contains("\"remark\""));
        FileUtil.writeToFile(storage.getAddressBookFilePath(), legacy);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertEquals(Remark.EMPTY, storage.readAddressBook().orElseThrow().getPersonList().getFirst().getRemark());
    }

    @Test
    public void parser_dispatchesRemarkWithoutChangingReligionParsing() throws Exception {
        assertEquals(new RemarkCommand(Index.fromOneBased(1), new Remark("Likes swimming")),
                new AddressBookParser().parseCommand("remark 1 r/Likes swimming"));
        logic.execute("remark 1 r/Islam");
        assertEquals("Jainism", first().getReligion().orElseThrow().toString());
        assertEquals(new Remark("Islam"), first().getRemark());
    }

    @Test
    public void person_remarkAffectsEqualityButNotIdentity() {
        Person original = first();
        Person withRemark = new Person(original, new Remark("A note"));
        assertNotEquals(original, withRemark);
        assertTrue(original.isSamePerson(withRemark));
        assertEquals(withRemark, new Person(original, new Remark("A note")));
        assertEquals(withRemark.hashCode(), new Person(original, new Remark("A note")).hashCode());
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    private Person first() {
        return model.getFilteredPersonList().getFirst();
    }
}
