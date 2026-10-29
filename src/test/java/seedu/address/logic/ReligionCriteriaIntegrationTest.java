package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.FindCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.DatingAttribute;
import seedu.address.model.person.DatingCriteria;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class ReligionCriteriaIntegrationTest {
    private static final String ADD_CLIENT = "add n/Religion Demo p/91234567 e/demo@example.com a/Test Lane age/25"
            + " s/no g/nb r/Jainism";

    @TempDir
    Path temporaryFolder;

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
    public void add_religionEnvironments_normalizesAndSeparatesOwnReligion() throws Exception {
        logic.execute(ADD_CLIENT + " {r/ bAhA'i   FAITH } <r/ISLAM>");
        Person person = first();
        assertEquals("Jainism", person.getReligion().orElseThrow().toString());
        assertEquals(Map.of(DatingAttribute.RELIGION, Set.of("Baha'i Faith")),
                person.getDatingCriteria().getPreferences());
        assertEquals(Map.of(DatingAttribute.RELIGION, Set.of("Islam")), person.getDatingCriteria().getDealbreakers());
        assertEquals("25", person.getAge().toString());
        assertEquals("no", person.getSmokingStatus().toString());
        assertEquals("Non-binary", person.getGender().toString());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void add_ownReligionAlsoExcluded_doesNotConflateClientWithPartner() throws Exception {
        logic.execute(ADD_CLIENT + " {r/Buddhism} <r/Jainism>");
        assertEquals("Jainism", first().getReligion().orElseThrow().toString());
        assertEquals(Set.of("Jainism"), first().getDatingCriteria().getDealbreakers().get(DatingAttribute.RELIGION));
    }

    @Test
    public void edit_ownReligionAndOtherFields_preservesPartnerCriteria() throws Exception {
        logic.execute(ADD_CLIENT + " {r/Buddhism} <r/Islam>");
        Person original = first();
        logic.execute("edit 1 p/93456789 r/Christianity");
        assertEquals("Christianity", first().getReligion().orElseThrow().toString());
        assertEquals(original.getDatingCriteria(), first().getDatingCriteria());
        assertEquals(original.getAge(), first().getAge());
        assertEquals(original.getSmokingStatus(), first().getSmokingStatus());
        assertEquals(original.getGender(), first().getGender());
        logic.execute("edit 1 r/");
        assertTrue(first().getReligion().isEmpty());
        assertEquals(original.getDatingCriteria(), first().getDatingCriteria());
        assertEquals("93456789", first().getPhone().value);
    }

    @Test
    public void edit_criteriaEnvironments_updatesOnlySuppliedAttributesAndCanClear() throws Exception {
        logic.execute(ADD_CLIENT + " {r/Buddhism age/25-35} <r/Islam s/yes>");
        Person original = first();
        logic.execute("edit 1 {r/Jainism}");
        assertEquals(Map.of(DatingAttribute.RELIGION, Set.of("Buddhism", "Jainism"),
                DatingAttribute.AGE, Set.of("25-35")),
                first().getDatingCriteria().getPreferences());
        assertEquals(original.getDatingCriteria().getDealbreakers(), first().getDatingCriteria().getDealbreakers());
        assertEquals(original.getReligion(), first().getReligion());
        logic.execute("edit 1 <r/No religion>");
        assertEquals(Map.of(DatingAttribute.RELIGION, Set.of("Islam", "No religion"),
                DatingAttribute.SMOKING, Set.of("yes")),
                first().getDatingCriteria().getDealbreakers());
        logic.execute("edit 1 {}");
        assertTrue(first().getDatingCriteria().getPreferences().isEmpty());
        assertEquals(Map.of(DatingAttribute.RELIGION, Set.of("Islam", "No religion"),
                DatingAttribute.SMOKING, Set.of("yes")),
                first().getDatingCriteria().getDealbreakers());
        logic.execute("edit 1 <>");
        assertEquals(DatingCriteria.EMPTY, first().getDatingCriteria());
        Person expected = new Person(original.getName(), original.getPhone(), original.getEmail(),
                original.getAddress(), original.getAge(), original.getTags(), original.getSmokingStatus(),
                original.getGender(), original.getReligion().orElse(null), DatingCriteria.EMPTY);
        assertEquals(expected, first());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void add_conflictingNormalizedReligionCriteria_rejectsWithoutChangingRecords() throws Exception {
        logic.execute(ADD_CLIENT);
        AddressBook before = new AddressBook(model.getAddressBook());
        String conflicting = ADD_CLIENT.replace("Religion Demo", "Conflict Demo")
                + " {r/Baha'i Faith} <r/ bAhA'i   faith >";
        assertThrows(ParseException.class, () -> logic.execute(conflicting));
        assertEquals(before, model.getAddressBook());
        assertEquals(before, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void edit_conflictingReligionCriteria_rejectsWithoutChangingRecords() throws Exception {
        logic.execute(ADD_CLIENT + " {r/Buddhism} <r/Islam>");
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(CommandException.class, () -> logic.execute("edit 1 <r/bUdDhIsM>"));
        assertEquals(before, model.getAddressBook());
        assertEquals(before, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void criteriaContradictions_reportHelpfulErrors() throws Exception {
        assertThrows(ParseException.class, DatingCriteria.MESSAGE_CONFLICTING_SMOKING, () ->
                logic.execute(ADD_CLIENT + " {s/no} <s/no>"));
        assertThrows(ParseException.class, DatingCriteria.MESSAGE_OVERLAPPING_AGE_RANGES, () ->
                logic.execute(ADD_CLIENT + " {age/20 - 30} <age/25-35>"));
        logic.execute(ADD_CLIENT + " {age/20 - 24} <age/25 - 35>");
    }

    @Test
    public void emptyCriteriaValues_removeOnlyTheirAttributes() throws Exception {
        logic.execute(ADD_CLIENT + " {g/m s/no age/25 r/Buddhism rg/long}"
                + " <g/w s/yes age/30 r/Islam rg/short>");
        logic.execute("edit 1 {g/ age/ r/ rg/} <g/ age/ r/ rg/>");

        assertEquals(Map.of(DatingAttribute.SMOKING, Set.of("no")), first().getDatingCriteria().getPreferences());
        assertEquals(Map.of(DatingAttribute.SMOKING, Set.of("yes")), first().getDatingCriteria().getDealbreakers());
    }

    @Test
    public void add_emptyCriteriaValues_leavesCriteriaUnspecified() throws Exception {
        logic.execute(ADD_CLIENT + " {g/ s/ age/ r/ rg/} <g/ s/ age/ r/ rg/>");

        assertEquals(DatingCriteria.EMPTY, first().getDatingCriteria());
    }

    @Test
    public void criteria_multipleValues_recordsAllSupportedValues() throws Exception {
        logic.execute(ADD_CLIENT + " {g/m, w r/Buddhism, Jainism rg/long, short age/25-35 s/no}"
                + " <g/nb r/Islam, Hinduism rg/life, unsure>");

        assertEquals(Map.of(
                DatingAttribute.GENDER, Set.of("m", "w"),
                DatingAttribute.RELIGION, Set.of("Buddhism", "Jainism"),
                DatingAttribute.RELATIONSHIP_GOAL, Set.of("Long-term relationship", "Short-term fun"),
                DatingAttribute.AGE, Set.of("25-35"),
                DatingAttribute.SMOKING, Set.of("no")), first().getDatingCriteria().getPreferences());
        assertEquals(Map.of(
                DatingAttribute.GENDER, Set.of("nb"),
                DatingAttribute.RELIGION, Set.of("Islam", "Hinduism"),
                DatingAttribute.RELATIONSHIP_GOAL, Set.of("Life partner", "Figuring out my goals")),
                first().getDatingCriteria().getDealbreakers());
    }

    @Test
    public void edit_criteriaMultipleValues_addsOnlyNewValuesAndWarnsForExistingValues() throws Exception {
        logic.execute(ADD_CLIENT + " {g/m r/Buddhism}");

        CommandResult result = logic.execute("edit 1 {g/m, w r/Buddhism, Jainism}");

        assertEquals(Set.of("m", "w"), first().getDatingCriteria().getPreferences().get(DatingAttribute.GENDER));
        assertEquals(Set.of("Buddhism", "Jainism"),
                first().getDatingCriteria().getPreferences().get(DatingAttribute.RELIGION));
        assertTrue(result.getFeedbackToUser().contains(EditCommand.MESSAGE_DUPLICATE_CRITERIA));
        assertTrue(result.getFeedbackToUser().contains("Gender = m"));
        assertTrue(result.getFeedbackToUser().contains("Religion = Buddhism"));
    }

    @Test
    public void criteria_singleValueAttributes_rejectCommaSeparatedValues() {
        assertThrows(ParseException.class, () -> logic.execute(ADD_CLIENT + " {s/yes, no}"));
        assertThrows(ParseException.class, () -> logic.execute(ADD_CLIENT + " <age/25, 30>"));
    }

    @Test
    public void execute_invalidReligionEnvironments_preservesRecordsAndDisplayedResults() throws Exception {
        logic.execute(ADD_CLIENT + " {r/Buddhism} <r/Islam>");
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Person> displayedPersons = List.copyOf(model.getFilteredPersonList());
        List<String> invalidEnvironments = List.of("{r/Other}", "<r/Other>", "{r/Islam r/islam}",
                "<r/Islam r/Buddhism>", "{r/Buddhism} {r/Jainism}",
                "{r/Buddhism>", "{<r/Islam>}", "{rp/Islam}", "<rx/Islam>");
        for (String fields : invalidEnvironments) {
            assertThrows(ParseException.class, () -> logic.execute("edit 1 " + fields));
            assertThrows(ParseException.class, () -> logic.execute(
                    ADD_CLIENT.replace("Religion Demo", "Invalid Demo") + " " + fields));
            assertEquals(before, model.getAddressBook());
            assertEquals(displayedPersons, model.getFilteredPersonList());
        }
        assertEquals(before, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void findAndRestart_usesOwnReligionRatherThanPartnerCriteria() throws Exception {
        logic.execute(ADD_CLIENT + " {r/Buddhism} <r/Islam>");
        Person jain = first();
        logic.execute(ADD_CLIENT.replace("Religion Demo", "Buddhist Demo").replace("r/Jainism", "r/Buddhism")
                + " {r/Jainism} <r/No religion>");
        Person buddhist = model.getAddressBook().getPersonList().get(1);
        logic.execute(ADD_CLIENT.replace("Religion Demo", "Unknown Demo").replace(" r/Jainism", "")
                + " {r/Jainism} <r/No religion>");
        logic.execute("find r/Jainism");
        assertEquals(List.of(jain), model.getFilteredPersonList());
        logic.execute("find r/Buddhism");
        assertEquals(List.of(buddhist), model.getFilteredPersonList());
        logic.execute("find r/No religion");
        assertTrue(model.getFilteredPersonList().isEmpty());
        Model restored = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        new FindCommandParser().parse("r/Jainism").execute(restored);
        assertEquals(List.of(jain), restored.getFilteredPersonList());
        assertEquals(model.getAddressBook(), restored.getAddressBook());
    }

    /**
     * Returns the first displayed client in this isolated fixture.
     */
    private Person first() {
        return model.getFilteredPersonList().get(0);
    }
}
