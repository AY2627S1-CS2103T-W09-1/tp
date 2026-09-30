package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.commands.exceptions.CommandException;
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
import seedu.address.testutil.PersonBuilder;

/**
 * Checks the remark command through parsing, model updates and JSON persistence.
 */
public class RemarkIntegrationTest {
    @TempDir
    public Path testFolder;

    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        storage = new JsonAddressBookStorage(testFolder.resolve("addressbook.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json"))));
    }

    @Test
    public void execute_addReplaceClearRemark_persistsChanges() throws Exception {
        Person expected = new PersonBuilder(ALICE).withRemark("Likes swimming").build();
        assertEquals(String.format(RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS, Messages.format(expected)),
                logic.execute("remark 1 r/Likes swimming").getFeedbackToUser());
        assertEquals(expected, model.getFilteredPersonList().get(0));
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());

        logic.execute("remark 1 r/Likes reading");
        assertEquals(new Remark("Likes reading"), model.getFilteredPersonList().get(0).getRemark());
        Model reloaded = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(model, reloaded);

        assertEquals(String.format(RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS, Messages.format(ALICE)),
                logic.execute("remark 1 r/").getFeedbackToUser());
        assertEquals(ALICE, model.getFilteredPersonList().get(0));
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_filteredList_updatesDisplayedPerson() throws Exception {
        logic.execute("find Benson");
        assertEquals(1, model.getFilteredPersonList().size());
        logic.execute("remark 1 r/Met at school");
        assertEquals(ALICE, model.getFilteredPersonList().get(0));
        assertEquals(new PersonBuilder(BENSON).withRemark("Met at school").build(),
                model.getFilteredPersonList().get(1));
    }

    @Test
    public void execute_invalidInput_doesNotChangeModel() throws Exception {
        AddressBook original = new AddressBook(model.getAddressBook());
        for (String command : new String[] {"remark", "remark r/Test", "remark 0 r/Test",
            "remark -1 r/Test", "remark abc r/Test"}) {
            assertThrows(ParseException.class, () -> logic.execute(command));
        }
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                logic.execute("remark 999 r/Test"));
        logic.execute("find Benson");
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                logic.execute("remark 2 r/Test"));
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void execute_editOtherFields_preservesRemark() throws Exception {
        logic.execute("remark 1 r/Keep this note");
        logic.execute("edit 1 p/91234567");
        assertEquals(new PersonBuilder(ALICE).withPhone("91234567").withRemark("Keep this note").build(),
                model.getFilteredPersonList().get(0));
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_missingRemark_clearsRemarkAsInTutorial() throws Exception {
        logic.execute("remark 1 r/Temporary note");
        logic.execute("remark 1");
        assertEquals(ALICE, model.getFilteredPersonList().get(0));
    }

    @Test
    public void person_remarkAffectsEqualityButNotIdentity() {
        Person remarkedAlice = new PersonBuilder(ALICE).withRemark("A note").build();
        assertFalse(ALICE.equals(remarkedAlice));
        assertTrue(ALICE.isSamePerson(remarkedAlice));
        Person copy = new PersonBuilder(remarkedAlice).build();
        assertEquals(remarkedAlice, copy);
        assertEquals(remarkedAlice.hashCode(), copy.hashCode());
    }
}
