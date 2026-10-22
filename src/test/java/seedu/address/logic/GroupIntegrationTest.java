package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.ClientGroup;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/** Exercises the public command flow with isolated storage, including failure atomicity. */
public class GroupIntegrationTest {
    @TempDir
    public Path folder;

    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(folder.resolve("book.json"));
        initializeLogic();
        logic.execute("add n/Alice p/11111111 e/alice@example.com a/Home age/25 s/no t/VIP <s/yes>");
        logic.execute("add n/Bob p/22222222 e/bob@example.com a/Home age/26 s/no");
        logic.execute("add n/Cara p/33333333 e/cara@example.com a/Home age/27 s/yes");
        logic.execute("add n/Dana p/44444444 e/dana@example.com a/Home age/28");
    }

    @Test
    public void addAndShow_useDisplayedIndexesAndCaseInsensitiveNames() throws Exception {
        AddressBook original = new AddressBook(model.getAddressBook());
        logic.execute("find Bob Cara");
        assertEquals("Group VIP: 2 client(s) added. Total members: 2.",
                logic.execute("grp VIP add 1 2").getFeedbackToUser());
        logic.execute("list");
        logic.execute("grp vip add 1");
        assertEquals(1, logic.getGroupList().size());
        assertEquals("VIP", group().getName().toString());
        assertEquals("Group VIP: 3 client(s) listed.", logic.execute("show vIp").getFeedbackToUser());
        assertDisplayed("Alice", "Bob", "Cara");
        assertEquals(original.getPersonList(), model.getAddressBook().getPersonList());
        assertTrue(model.getAddressBook().getPersonList().getFirst().getTags().stream()
                .anyMatch(tag -> tag.tagName.equals("VIP")));
        logic.execute("list");
        assertDisplayed("Alice", "Bob", "Cara", "Dana");
    }

    @Test
    public void singleMemberAndEmptyGroup_surviveReloadAndCanBeRepopulated() throws Exception {
        logic.execute("grp VIP add 2");
        logic.execute("show VIP");
        assertDisplayed("Bob");
        logic.execute("grp VIP remove 1");
        assertDisplayed();
        assertTrue(group().getMembers().isEmpty());
        reload();
        logic.execute("show vip");
        assertDisplayed();
        logic.execute("list");
        logic.execute("grp vip add 4");
        logic.execute("show VIP");
        assertDisplayed("Dana");
    }

    @Test
    public void removeMultiple_resolvesAllIndexesBeforeChangingFilteredView() throws Exception {
        logic.execute("grp VIP add 2 3 4");
        logic.execute("show VIP");
        logic.execute("grp VIP remove 1 3");
        assertDisplayed("Cara");
        assertEquals(4, model.getAddressBook().getPersonList().size());
        reload();
        logic.execute("show VIP");
        assertDisplayed("Cara");
    }

    @Test
    public void editsAndDeletion_preserveMembershipAndRemoveOnlyDeletedClient() throws Exception {
        logic.execute("grp VIP add 2 3");
        logic.execute("grp Other add 2");
        logic.execute("show VIP");
        logic.execute("edit 1 n/Robert p/99999999 t/friend");
        logic.execute("show VIP");
        assertDisplayed("Robert", "Cara");
        logic.execute("show Other");
        assertDisplayed("Robert");
        reload();
        logic.execute("show VIP");
        logic.execute("delete 1");
        assertDisplayed("Cara");
        logic.execute("show Other");
        assertDisplayed();
        assertEquals(3, model.getAddressBook().getPersonList().size());
        reload();
        logic.execute("show VIP");
        assertDisplayed("Cara");
        logic.execute("clear");
        assertTrue(logic.getGroupList().isEmpty());
        reload();
        assertTrue(logic.getGroupList().isEmpty());
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void invalidMembershipOperations_leaveGroupsFilterAndFileUnchanged() throws Exception {
        logic.execute("grp VIP add 2");
        for (String command : new String[] {"grp VIP add 1 2", "grp New add 1 99", "grp VIP remove 2 1",
            "grp Missing remove 1", "show Missing", "grp VIP matches 1", "grp VIP remove 99"}) {
            assertCommandFailureUnchanged(command);
        }
        logic.execute("show VIP");
        assertCommandFailureUnchanged("grp VIP add 2");
        assertCommandFailureUnchanged("grp VIP remove 1 2");
    }

    @Test
    public void invalidSyntax_leavesGroupsFilterAndFileUnchanged() throws Exception {
        logic.execute("grp VIP add 2");
        logic.execute("show VIP");
        for (String command : new String[] {"grp", "grp VIP", "grp VIP add", "grp VIP ADD 1", "grp VIP delete 1",
            "grp VIP add 0", "grp VIP add -1", "grp VIP add 1.5", "grp VIP add 2147483648",
            "grp VIP add 1,2", "grp VIP add 1 1", "grp VIP add 1 01", "grp VIP matches 1 2",
            "grp /VIP add 1", "grp Bad Name add 1", "grp " + "a".repeat(31) + " add 1",
            "grp VIP add 1 extra", "show", "show VIP extra", "show /VIP"}) {
            AddressBook before = new AddressBook(model.getAddressBook());
            List<Person> displayed = List.copyOf(model.getFilteredPersonList());
            String saved = Files.readString(storage.getAddressBookFilePath());
            assertThrows(ParseException.class, () -> logic.execute(command));
            assertEquals(before, model.getAddressBook());
            assertEquals(displayed, model.getFilteredPersonList());
            assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        }
    }

    @Test
    public void matches_usesFullBookAndExistingAssessmentWithoutChangingCurrentView() throws Exception {
        logic.execute("find Alice");
        logic.execute("grp Candidates matches 1");
        assertDisplayed("Alice");
        List<Person> members = group().getMembers();
        assertFalse(members.stream().anyMatch(person -> person.getName().fullName.equals("Alice")));
        assertFalse(members.stream().anyMatch(person -> person.getName().fullName.equals("Cara")));
        logic.execute("find match/1");
        assertEquals(new HashSet<>(model.getFilteredPersonList()), new HashSet<>(members));
        assertTrue(members.stream().anyMatch(person -> person.getName().fullName.equals("Dana")));
        logic.execute("show Candidates");
        assertDisplayed("Bob", "Dana");
    }

    @Test
    public void matches_membershipIsSnapshotAndDoesNotOverwriteExistingGroup() throws Exception {
        logic.execute("grp Candidates matches 1");
        List<Person> members = group().getMembers();
        logic.execute("edit 1 <s/no>");
        assertEquals(members, group().getMembers());
        assertCommandFailureUnchanged("grp candidates matches 1");
        reload();
        logic.execute("show Candidates");
        assertDisplayed("Bob", "Dana");
    }

    @Test
    public void matches_fromSortedResults_resolvesDisplayedTarget() throws Exception {
        logic.execute("find match/1");
        Person target = model.getFilteredPersonList().getFirst();
        logic.execute("grp FromResults matches 1");
        List<Person> members = group().getMembers();
        assertFalse(members.contains(target));
        logic.execute("find match/1");
        assertEquals(new HashSet<>(model.getFilteredPersonList()), new HashSet<>(members));
    }

    @Test
    public void matches_noCandidates_createsNoGroup() throws Exception {
        logic.execute("clear");
        logic.execute("add n/Alone p/11111111 e/alone@example.com a/Home age/25");
        assertCommandFailureUnchanged("grp Empty matches 1");
        assertTrue(logic.getGroupList().isEmpty());
    }

    @Test
    public void matches_rejectsViolationsInEitherDirectionAndAllowsUnmetPreferences() throws Exception {
        logic.execute("edit 1 {age/99}");
        logic.execute("edit 2 <s/no>");
        logic.execute("grp Candidates matches 1");
        logic.execute("show Candidates");
        // Bob excludes Alice's smoking status; Cara violates Alice's exclusion. Dana remains unknown.
        assertDisplayed("Dana");
        logic.execute("list");
        logic.execute("edit 2 <>");
        logic.execute("grp Updated matches 1");
        logic.execute("show Updated");
        // Bob's age does not meet Alice's preference, but unmet preferences do not exclude clients.
        assertDisplayed("Bob", "Dana");
    }

    @Test
    public void matches_allOtherClientsIncompatible_createsNoGroup() throws Exception {
        logic.execute("edit 2 s/yes");
        logic.execute("edit 4 s/yes");
        assertCommandFailureUnchanged("grp Empty matches 1");
        assertTrue(logic.getGroupList().isEmpty());
    }

    @Test
    public void deleteThenReaddSameName_doesNotRestoreMembership() throws Exception {
        logic.execute("grp VIP add 2");
        logic.execute("delete 2");
        logic.execute("add n/Bob p/22222222 e/bob@example.com a/Home age/26 s/no");
        logic.execute("show VIP");
        assertDisplayed();
        assertTrue(group().getMembers().isEmpty());
        reload();
        logic.execute("show VIP");
        assertDisplayed();
    }

    private void assertCommandFailureUnchanged(String command) throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Person> displayed = List.copyOf(model.getFilteredPersonList());
        String saved = Files.readString(storage.getAddressBookFilePath());
        assertThrows(CommandException.class, () -> logic.execute(command));
        assertEquals(before, model.getAddressBook());
        assertEquals(displayed, model.getFilteredPersonList());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
    }

    private void assertDisplayed(String... names) {
        assertEquals(List.of(names), model.getFilteredPersonList().stream()
                .map(person -> person.getName().fullName).toList());
    }

    private ClientGroup group() {
        return logic.getGroupList().getFirst();
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
