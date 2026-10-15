package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_RELATIONSHIP_GOAL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.DatingAttribute;
import seedu.address.model.person.DatingCriteria;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_religion_success() {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person edited = new PersonBuilder(original).withReligion("Jainism").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withReligion("Jainism").build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, edited);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(edited));
        assertCommandSuccess(new EditCommand(INDEX_FIRST_PERSON, descriptor), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clearReligion_preservesOtherFields() throws Exception {
        Person original = new PersonBuilder(model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()))
                .withReligion("Jainism").withSmokingStatus("no").withGender("nb").withRelationshipGoal("long").build();
        model.setPerson(model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()), original);
        Person cleared = new PersonBuilder(original).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withReligion(null).build();
        EditCommand command = new EditCommand(INDEX_FIRST_PERSON, descriptor);
        command.execute(model);
        Person actual = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(actual.getReligion().isEmpty());
        assertEquals(cleared.getName(), actual.getName());
        assertEquals(cleared.getAge(), actual.getAge());
        assertEquals(cleared.getGender(), actual.getGender());
        assertEquals(cleared.getSmokingStatus(), actual.getSmokingStatus());
        assertEquals(cleared.getRelationshipGoal(), actual.getRelationshipGoal());
        assertEquals(cleared.getTags(), actual.getTags());
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    @Test
    public void execute_relationshipGoalSpecifiedUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withRelationshipGoal(VALID_RELATIONSHIP_GOAL_AMY).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withRelationshipGoal(VALID_RELATIONSHIP_GOAL_AMY).build();

        assertFirstPersonEditSuccess(descriptor, editedPerson);
    }

    @Test
    public void execute_otherFieldEdited_relationshipGoalPreserved() {
        Person personWithGoal = setFirstPersonRelationshipGoal(VALID_RELATIONSHIP_GOAL_AMY);
        Person editedPerson = new PersonBuilder(personWithGoal).withName(VALID_NAME_BOB).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();

        assertFirstPersonEditSuccess(descriptor, editedPerson);
    }

    @Test
    public void execute_emptyRelationshipGoal_relationshipGoalCleared() {
        Person personWithoutGoal = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        setFirstPersonRelationshipGoal(VALID_RELATIONSHIP_GOAL_AMY);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withoutRelationshipGoal().build();

        assertFirstPersonEditSuccess(descriptor, personWithoutGoal);
    }

    @Test
    public void execute_criteriaEdits_existingCriteriaPreservedAndSuppliedCriteriaUpdated() {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithCriteria = withDatingCriteria(original, new DatingCriteria(
                Map.of(DatingAttribute.AGE, "20-30", DatingAttribute.SMOKING, "no"),
                Map.of(DatingAttribute.RELIGION, "Islam", DatingAttribute.RELATIONSHIP_GOAL, "long")));
        model.setPerson(original, personWithCriteria);

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setPreferences(Map.of(DatingAttribute.AGE, "25-35", DatingAttribute.RELIGION, "Buddhism"));
        descriptor.setDealbreakers(Map.of(DatingAttribute.RELIGION, "Hinduism", DatingAttribute.AGE, "18-24"));

        Person expectedPerson = withDatingCriteria(personWithCriteria, new DatingCriteria(
                Map.of(DatingAttribute.AGE, "25-35", DatingAttribute.SMOKING, "no",
                        DatingAttribute.RELIGION, "Buddhism"),
                Map.of(DatingAttribute.RELIGION, "Hinduism", DatingAttribute.RELATIONSHIP_GOAL, "long",
                        DatingAttribute.AGE, "18-24")));

        assertFirstPersonEditSuccess(descriptor, expectedPerson);
    }

    /**
     * Gives the first person in {@code model} the relationship goal {@code relationshipGoal}
     * and returns the updated person.
     */
    private Person setFirstPersonRelationshipGoal(String relationshipGoal) {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithGoal = new PersonBuilder(firstPerson).withRelationshipGoal(relationshipGoal).build();
        model.setPerson(firstPerson, personWithGoal);
        return personWithGoal;
    }

    /** Returns a copy of {@code person} with the supplied dating criteria. */
    private Person withDatingCriteria(Person person, DatingCriteria datingCriteria) {
        return new Person(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(), person.getAge(),
                person.getTags(), person.getSmokingStatus(), person.getGender(), person.getReligion().orElse(null),
                person.getRelationshipGoal().orElse(null), datingCriteria);
    }

    /**
     * Asserts that editing the first person with {@code descriptor} succeeds and results in {@code editedPerson}.
     */
    private void assertFirstPersonEditSuccess(EditPersonDescriptor descriptor, Person editedPerson) {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }
}
