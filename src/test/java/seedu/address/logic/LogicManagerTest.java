package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.AGE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.FindCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_findReligion_preservesRecordsAndReplacesResults() throws Exception {
        Person jain = new PersonBuilder().withName("Jain Demo").withReligion("Jainism")
                .withSmokingStatus("no").withGender("nb").withRelationshipGoal("long").build();
        Person none = new PersonBuilder().withName("None Demo").withReligion("No religion").build();
        Person unknown = new PersonBuilder().withName("Unknown Demo").build();
        model.addPerson(jain);
        model.addPerson(none);
        model.addPerson(unknown);
        AddressBook original = new AddressBook(model.getAddressBook());

        logic.execute("find n/Unknown");
        assertEquals(List.of(unknown), model.getFilteredPersonList());
        CommandResult result = logic.execute("find r/jAiNiSm");
        assertEquals(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 1), result.getFeedbackToUser());
        assertEquals(List.of(jain), model.getFilteredPersonList());
        List<String> invalidQueries = List.of("find r/", "find r/Other", "find r/Jainism r/Islam",
                "find Jain r/Jainism", "find r/Jainism age/25", "find r/Jainism g/nb");
        for (String query : invalidQueries) {
            assertThrows(ParseException.class, () -> logic.execute(query));
            assertEquals(List.of(jain), model.getFilteredPersonList());
            assertEquals(original, model.getAddressBook());
        }
        logic.execute("find r/No religion");
        assertEquals(List.of(none), model.getFilteredPersonList());
        result = logic.execute("find r/Buddhism");
        assertEquals(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 0), result.getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(original, model.getAddressBook());

        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        Model restored = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        new FindCommandParser().parse("r/Jainism").execute(restored);
        assertEquals(List.of(jain), restored.getFilteredPersonList());
        assertEquals(original, restored.getAddressBook());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_emailEdits_supportedAndLongValuesPersist() throws Exception {
        logic.execute("add n/Alpha p/81234567 e/alpha@example.com a/10 Orchard Road age/25");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        List<String> emails = List.of("first_+last@example.com", "user@xn--bcher-kva.de",
                "a+".repeat(5000) + "a@example.com", "a@" + "a.".repeat(15000) + "com");
        for (String email : emails) {
            logic.execute("edit 1 e/" + email);
            assertEquals(email, model.getAddressBook().getPersonList().getFirst().getEmail().value);
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        }
    }

    @Test
    public void execute_helpAndExitWithUnavailableStorage_success() throws Exception {
        AddressBook original = new AddressBook(model.getAddressBook());
        for (IOException failure : List.of(DUMMY_IO_EXCEPTION, DUMMY_AD_EXCEPTION)) {
            int[] saveAttempts = {0};
            StorageManager storage = new StorageManager(
                    new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"))) {
                @Override
                public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                    saveAttempts[0]++;
                    throw failure;
                }
            };
            Logic unavailableStorageLogic = new LogicManager(model, storage);

            // UI control actions must reach MainWindow without attempting to save client data.
            assertEquals(new CommandResult(HelpCommand.SHOWING_HELP_MESSAGE, true, false),
                    unavailableStorageLogic.execute("help"));
            assertEquals(new CommandResult(ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT, false, true),
                    unavailableStorageLogic.execute("exit"));
            assertEquals(0, saveAttempts[0]);
            assertEquals(original, model.getAddressBook());
        }
    }

    @Test
    public void execute_dataChangingCommands_savesAfterEachCommand() throws Exception {
        List<String> commands = List.of(
                "add n/Alpha p/81234567 e/alpha@example.com a/10 Orchard Road age/25",
                "add n/Beta p/81234568 e/beta@example.com a/20 Orchard Road age/30",
                "edit 1 p/82345678", "grp VIP add 1", "grp VIP remove 1", "delete 2", "clear");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        for (String command : commands) {
            logic.execute(command);
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        }
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + AGE_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
