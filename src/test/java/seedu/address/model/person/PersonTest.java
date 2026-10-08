package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_AGE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different age -> returns false
        editedAlice = new PersonBuilder(ALICE).withAge(VALID_AGE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));

        // different religion or partner criteria -> returns false
        editedAlice = new PersonBuilder(ALICE).withReligion("Islam").build();
        assertFalse(ALICE.equals(editedAlice));
        editedAlice = new PersonBuilder(ALICE).withExcludedReligions("No religion").build();
        assertFalse(ALICE.equals(editedAlice));
    }

    /** Religion criteria must not contradict each other. */
    @Test
    public void constructor_conflictingReligionCriteria_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Religion.MESSAGE_CONFLICT, () -> new PersonBuilder()
                .withPreferredReligion("Islam").withRequiredReligion("Buddhism").build());
        assertThrows(IllegalArgumentException.class, Religion.MESSAGE_CONFLICT, () -> new PersonBuilder()
                .withRequiredReligion("Islam").withExcludedReligions("Islam").build());
    }

    /** Exclusion sets cannot be modified through the getter. */
    @Test
    public void getExcludedReligions_unmodifiable() {
        Person person = new PersonBuilder().withExcludedReligions("Islam").build();
        assertThrows(UnsupportedOperationException.class, () -> person.getExcludedReligions()
                .add(new Religion("Buddhism")));
    }

    @Test
    public void gender_affectsEqualityButNotIdentity() {
        Person specified = new PersonBuilder(ALICE).withGender("w").build();
        Person copy = new PersonBuilder(specified).build();
        assertEquals(Gender.UNSPECIFIED, ALICE.getGender());
        assertFalse(ALICE.equals(specified));
        assertTrue(ALICE.isSamePerson(specified));
        assertEquals(specified, copy);
        assertEquals(specified.hashCode(), copy.hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress() + ", smokingStatus="
                + ALICE.getSmokingStatus() + ", age=" + ALICE.getAge()
                + ", tags=" + ALICE.getTags() + ", gender=" + ALICE.getGender()
                + ", religion=null, preferredReligion=null, requiredReligion=null, excludedReligions=[]}";
        assertEquals(expected, ALICE.toString());
    }
}
