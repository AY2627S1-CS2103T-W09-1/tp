package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class AgeTest {

    @Test
    public void normalizeRange_validBounds_normalizesValues() {
        assertEquals("18", Age.normalizeRange(" 018 "));
        assertEquals("99", Age.normalizeRange("99"));
        assertEquals("18-99", Age.normalizeRange("018-099"));
        assertEquals("18-99", Age.normalizeRange("018 - 099"));
        assertEquals("25-30", Age.normalizeRange(" 25 - 30 "));
        assertEquals("25-25", Age.normalizeRange("25-25"));
        assertEquals("18-99", DatingAttribute.AGE.normalizeCriterion(" 018-099 "));
    }

    @Test
    public void normalizeRange_invalidBounds_throwsIllegalArgumentException() {
        for (String value : new String[] {"", " ", "17", "100", "18-100", "18-", "-25", "18-25-30",
            "twenty", "999999999999999999999"}) {
            assertThrows(IllegalArgumentException.class, Age.MESSAGE_CONSTRAINTS, () -> Age.normalizeRange(value));
        }
    }

    @Test
    public void normalizeRange_reversedBounds_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Age.MESSAGE_INVALID_RANGE, () -> Age.normalizeRange("35-18"));
    }
}
