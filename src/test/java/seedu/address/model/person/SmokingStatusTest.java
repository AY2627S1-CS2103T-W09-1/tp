package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class SmokingStatusTest {

    @Test
    public void constructor_invalidValue_throwsException() {
        assertThrows(NullPointerException.class, () -> new SmokingStatus(null));
        for (String value : new String[] {"sometimes", "true", "1", "yes no", " ", "ye\u017f"}) {
            assertFalse(SmokingStatus.isValidSmokingStatus(value));
            assertThrows(IllegalArgumentException.class, () -> new SmokingStatus(value));
        }
    }

    @Test
    public void constructor_validValue_normalizesCase() {
        assertEquals("yes", new SmokingStatus("YeS").value);
        assertEquals("no", new SmokingStatus("NO").value);
        assertEquals("", SmokingStatus.UNSPECIFIED.value);
        assertEquals("Not specified", SmokingStatus.UNSPECIFIED.toString());
        assertEquals("yes", new SmokingStatus("YES").toString());
    }

    @Test
    public void equals_normalizedValues_comparesConsistently() {
        SmokingStatus yes = new SmokingStatus("yes");
        assertEquals(yes, yes);
        assertEquals(yes, new SmokingStatus("YES"));
        assertEquals(yes.hashCode(), new SmokingStatus("YES").hashCode());
        assertNotEquals(yes, new SmokingStatus("no"));
        assertNotEquals(yes, null);
        assertNotEquals(yes, "yes");
        assertNotEquals(SmokingStatus.UNSPECIFIED, new SmokingStatus("no"));
    }

    @Test
    public void person_smokingStatusChanges_detailsDifferButIdentityDoesNot() {
        Person unspecified = new PersonBuilder().build();
        Person smoker = new PersonBuilder(unspecified).withSmokingStatus("yes").build();
        assertEquals(SmokingStatus.UNSPECIFIED, unspecified.getSmokingStatus());
        assertNotEquals(unspecified, smoker);
        assertTrue(unspecified.isSamePerson(smoker));
        Person copy = new PersonBuilder(smoker).build();
        assertEquals(smoker, copy);
        assertEquals(smoker.hashCode(), copy.hashCode());
    }
}
