package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_AGE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_RELATIONSHIP_GOAL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.SmokingStatus;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditPersonDescriptorTest {

    @Test
    public void equals() {
        // same values -> returns true
        EditPersonDescriptor descriptorWithSameValues = new EditPersonDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditPersonDescriptor editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withPhone(VALID_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different email -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different address -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different age -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withAge(VALID_AGE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different tags -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withReligion("Islam").build();
        assertFalse(DESC_AMY.equals(editedAmy));
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withExcludedReligions("No religion").build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different relationship goal -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withRelationshipGoal(VALID_RELATIONSHIP_GOAL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // relationship goal cleared versus not edited -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withoutRelationshipGoal().build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void gender_distinguishesOmittedClearedAndSpecifiedValues() {
        EditPersonDescriptor omitted = new EditPersonDescriptor();
        EditPersonDescriptor cleared = new EditPersonDescriptorBuilder().withGender("").build();
        EditPersonDescriptor specified = new EditPersonDescriptorBuilder().withGender("nb").build();
        assertFalse(omitted.isAnyFieldEdited());
        assertTrue(cleared.isAnyFieldEdited());
        assertTrue(specified.isAnyFieldEdited());
        assertFalse(omitted.equals(cleared));
        assertFalse(cleared.equals(specified));
        assertEquals(cleared, new EditPersonDescriptor(cleared));
        assertEquals(specified, new EditPersonDescriptor(specified));
    }

    @Test
    public void isAnyFieldEdited_relationshipGoalCleared_returnsTrue() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withoutRelationshipGoal().build();
        assertTrue(descriptor.isAnyFieldEdited());
        assertTrue(new EditPersonDescriptor(descriptor).isRelationshipGoalEdited());
    }

    @Test
    public void toStringMethod() {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        String expected = EditPersonDescriptor.class.getCanonicalName() + "{name="
                + editPersonDescriptor.getName().orElse(null) + ", phone="
                + editPersonDescriptor.getPhone().orElse(null) + ", email="
                + editPersonDescriptor.getEmail().orElse(null) + ", address="
                + editPersonDescriptor.getAddress().orElse(null) + ", smokingStatus="
                + editPersonDescriptor.getSmokingStatus().orElse(null) + ", age="
                + editPersonDescriptor.getAge().orElse(null) + ", tags="
                + editPersonDescriptor.getTags().orElse(null) + ", gender="
                + editPersonDescriptor.getGender().orElse(null) + ", religion=null, preferredReligion=null"
                + ", requiredReligion=null, excludedReligions=null, religionEdited=false"
                + ", preferredReligionEdited=false, requiredReligionEdited=false"
                + ", relationshipGoal=" + editPersonDescriptor.getRelationshipGoal().orElse(null) + "}";
        assertEquals(expected, editPersonDescriptor.toString());
    }

    @Test
    public void copyConstructor_smokingStatus_preservesUpdate() {
        EditPersonDescriptor original = new EditPersonDescriptor();
        original.setSmokingStatus(new SmokingStatus("yes"));
        EditPersonDescriptor copy = new EditPersonDescriptor(original);
        assertTrue(copy.isAnyFieldEdited());
        assertEquals(original, copy);
        original.setSmokingStatus(new SmokingStatus("no"));
        assertEquals(new SmokingStatus("yes"), copy.getSmokingStatus().orElseThrow());
        assertFalse(original.equals(copy));
    }
}
