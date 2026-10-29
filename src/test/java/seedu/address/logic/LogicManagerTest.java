package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.AGE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
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
import seedu.address.model.group.ClientGroup;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Email;
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
        model.addPerson(AMY);
        model.addPerson(BOB);
        GroupName groupName = new GroupName("VIP");
        model.setGroups(List.of(new ClientGroup(groupName, List.of(AMY))));
        model.showGroup(groupName);
        AddressBook original = new AddressBook(model.getAddressBook());
        for (IOException failure : List.of(DUMMY_IO_EXCEPTION, DUMMY_AD_EXCEPTION)) {
            int[] saveAttempts = {0};
            Logic unavailableStorageLogic = createLogicWithFailingStorage(failure, saveAttempts);

            // UI control actions must reach MainWindow without attempting to save client data.
            assertEquals(new CommandResult(HelpCommand.SHOWING_HELP_MESSAGE, true, false),
                    unavailableStorageLogic.execute("help"));
            assertEquals(new CommandResult(ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT, false, true),
                    unavailableStorageLogic.execute("exit"));
            assertEquals(new CommandResult(HelpCommand.SHOWING_HELP_MESSAGE, true, false),
                    unavailableStorageLogic.execute("  help ignored arguments  "));
            assertEquals(new CommandResult(ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT, false, true),
                    unavailableStorageLogic.execute("  exit ignored arguments  "));
            assertEquals(0, saveAttempts[0]);
            assertEquals(original, model.getAddressBook());
            assertEquals(List.of(AMY), model.getFilteredPersonList());
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
    public void execute_helpAndExitWithMissingFile_doesNotCreateDataFile() throws Exception {
        model.addPerson(AMY);
        Path dataFile = temporaryFolder.resolve("addressBook.json");

        logic.execute("help");
        logic.execute("exit");
        assertFalse(Files.exists(dataFile));
        assertEquals(List.of(AMY), model.getAddressBook().getPersonList());

        // Skipping control-action saves must not disable the next ordinary command's save.
        logic.execute("list");
        assertEquals(model.getAddressBook(), new JsonAddressBookStorage(dataFile).readAddressBook().orElseThrow());
    }

    @Test
    public void execute_readOnlyCommands_preserveExistingSavingBehaviour() throws Exception {
        model.addPerson(AMY);
        model.addPerson(BOB);
        model.setGroups(List.of(new ClientGroup(new GroupName("VIP"), List.of(AMY))));
        AddressBook original = new AddressBook(model.getAddressBook());
        int[] saveAttempts = {0};
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        StorageManager storage = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"))) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                saveAttempts[0]++;
                super.saveAddressBook(addressBook);
            }
        };
        Logic savingLogic = new LogicManager(model, storage);
        List<String> commands = List.of("list", "find n/Amy", "list", "match 1 2", "show VIP", "list",
                "find match/1");
        for (int i = 0; i < commands.size(); i++) {
            savingLogic.execute(commands.get(i));
            assertEquals(i + 1, saveAttempts[0]);
            assertEquals(original, model.getAddressBook());
            assertEquals(original, addressBookStorage.readAddressBook().orElseThrow());
        }
    }

    @Test
    public void execute_rejectedEmails_preserveRecordsResultsAndFile() throws Exception {
        model.addPerson(AMY);
        model.addPerson(BOB);
        logic.execute("grp VIP add 1");
        logic.execute("show VIP");
        AddressBook original = new AddressBook(model.getAddressBook());
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        byte[] originalFile = Files.readAllBytes(dataFile);
        List<String> invalidEmails = List.of("first..last@example.com", "a@x.c", "a@-ab.cd", "a@ab-.cd",
                "a@x..cd", "a@b\u200bcd", "a@" + "a.".repeat(15000) + "com-");
        for (String email : invalidEmails) {
            List<String> commands = List.of("edit 1 e/" + email,
                    "add n/Gamma p/81234569 e/" + email + " a/30 Orchard Road age/25");
            for (String command : commands) {
                assertThrows(ParseException.class, Email.MESSAGE_CONSTRAINTS, () -> logic.execute(command));
                assertEquals(original, model.getAddressBook());
                assertEquals(List.of(AMY), model.getFilteredPersonList());
                assertArrayEquals(originalFile, Files.readAllBytes(dataFile));
            }
        }
    }

    @Test
    public void execute_failedSaveThenControls_preserveOriginalState() throws Exception {
        model.addPerson(AMY);
        model.addPerson(BOB);
        logic.execute("grp VIP add 1");
        logic.execute("show VIP");
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        byte[] originalFile = Files.readAllBytes(dataFile);
        AddressBook original = new AddressBook(model.getAddressBook());
        List<Person> displayed = List.copyOf(model.getFilteredPersonList());
        int[] saveAttempts = {0};
        Logic unavailableStorageLogic = createLogicWithFailingStorage(DUMMY_IO_EXCEPTION, saveAttempts);

        String editCommand = "edit 1 p/82345678";
        String expectedMessage = String.format(LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage());
        assertThrows(CommandException.class, expectedMessage, () -> unavailableStorageLogic.execute(editCommand));

        // Control actions must not retry saving or reapply the rejected edit.
        assertEquals(new CommandResult(HelpCommand.SHOWING_HELP_MESSAGE, true, false),
                unavailableStorageLogic.execute("help"));
        assertEquals(new CommandResult(ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT, false, true),
                unavailableStorageLogic.execute("exit"));
        assertEquals(1, saveAttempts[0]);
        assertEquals(original, model.getAddressBook());
        assertEquals(displayed, model.getFilteredPersonList());
        assertArrayEquals(originalFile, Files.readAllBytes(dataFile));
    }

    @Test
    public void execute_failedDeleteRetry_deletesOnlyOriginalTarget() throws Exception {
        model.addPerson(AMY);
        model.addPerson(BOB);
        logic.execute("grp VIP add 1 2");
        logic.execute("show VIP");
        for (IOException failure : List.of(DUMMY_IO_EXCEPTION, DUMMY_AD_EXCEPTION)) {
            assertSaveFailurePreservesState("delete 1", failure);
        }

        // Retrying the retained command after storage recovers must still target Amy, not Bob.
        logic.execute("delete 1");
        assertEquals(List.of(BOB), model.getAddressBook().getPersonList());
        assertEquals(List.of(BOB), model.getFilteredPersonList());
        assertEquals(List.of(BOB), model.getAddressBook().getGroupList().getFirst().getMembers());
        assertEquals(model.getAddressBook(), new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow());
    }

    @Test
    public void execute_failedMutations_restoreDataAndLiveGroupView() throws Exception {
        model.addPerson(AMY);
        model.addPerson(BOB);
        logic.execute("grp VIP add 1");
        logic.execute("show VIP");
        List<String> commands = List.of(
                "add n/Gamma p/81234569 e/gamma@example.com a/Test Road age/30",
                "edit 1 n/Changed p/82345678", "grp VIP remove 1", "grp New add 1", "clear", "delete 1",
                "list", "find n/Benson");
        for (IOException failure : List.of(DUMMY_IO_EXCEPTION, DUMMY_AD_EXCEPTION)) {
            for (String command : commands) {
                assertSaveFailurePreservesState(command, failure);
            }
        }

        // Restoring just the displayed members is insufficient: the group view must remain live.
        model.setGroups(List.of(new ClientGroup(new GroupName("VIP"), List.of(AMY, BOB))));
        assertEquals(List.of(AMY, BOB), model.getFilteredPersonList());
    }

    @Test
    public void execute_failedCommands_restoreRankedIndexes() throws Exception {
        model.addPerson(AMY);
        model.addPerson(BOB);
        model.addPerson(new PersonBuilder().withName("Gamma").build());
        logic.execute("grp VIP add 1");
        logic.execute("find match/1");
        List<String> commands = List.of(
                "add n/Delta p/81234569 e/delta@example.com a/Test Road age/30",
                "edit 1 p/82345678", "delete 1", "clear", "list", "show VIP", "find n/Amy");
        for (IOException failure : List.of(DUMMY_IO_EXCEPTION, DUMMY_AD_EXCEPTION)) {
            for (String command : commands) {
                assertSaveFailurePreservesState(command, failure);
            }
        }
        Person target = model.getFilteredPersonList().getFirst();
        logic.execute("delete 1");
        assertFalse(model.getAddressBook().getPersonList().contains(target));
        assertEquals(model.getAddressBook(), new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow());
    }

    /** Checks both data and displayed indexes, since failed input remains available for a retry. */
    private void assertSaveFailurePreservesState(String command, IOException failure) throws Exception {
        AddressBook original = new AddressBook(model.getAddressBook());
        List<Person> displayed = List.copyOf(model.getFilteredPersonList());
        var observablePersons = model.getFilteredPersonList();
        var observableGroups = model.getAddressBook().getGroupList();
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        byte[] originalFile = Files.readAllBytes(dataFile);
        int[] saveAttempts = {0};
        Logic failingLogic = createLogicWithFailingStorage(failure, saveAttempts);
        assertThrows(CommandException.class, () -> failingLogic.execute(command));
        assertEquals(1, saveAttempts[0]);
        assertEquals(original, model.getAddressBook());
        assertEquals(displayed, observablePersons);
        assertEquals(original.getGroupList(), observableGroups);
        assertArrayEquals(originalFile, Files.readAllBytes(dataFile));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /** Creates a storage-failing logic instance while tracking attempted client-data saves. */
    private Logic createLogicWithFailingStorage(IOException failure, int[] saveAttempts) {
        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"))) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                saveAttempts[0]++;
                throw failure;
            }
        };
        return new LogicManager(model, storage);
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
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, new ModelManager());
    }
}
