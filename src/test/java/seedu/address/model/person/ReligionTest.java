package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests supported religion categories and their canonicalization.
 */
public class ReligionTest {

    /**
     * Checks canonicalization and equality for a known category.
     */
    @Test
    public void constructor_knownCategory_canonicalizes() {
        Religion religion = new Religion("  no   RELIGION ");
        assertEquals("No religion", religion.toString());
        assertEquals(new Religion("No religion"), religion);
    }

    /**
     * Checks canonicalization and equality for an additional category.
     */
    @Test
    public void constructor_additionalCategory_canonicalizes() {
        Religion religion = new Religion("  jAiNiSm ");
        assertEquals("Jainism", religion.toString());
        assertEquals(new Religion("JAINISM"), religion);
        assertEquals(religion.hashCode(), new Religion("jainism").hashCode());
    }

    /**
     * Checks that the listed Baha'i Faith spelling is accepted.
     */
    @Test
    public void constructor_bahaiCategory_canonicalizes() {
        Religion religion = new Religion("Baha'i Faith");
        assertEquals("Baha'i Faith", religion.toString());
        assertEquals(new Religion("baha'i faith"), religion);
    }

    /**
     * Checks rejection of free text and unsupported spellings.
     */
    @Test
    public void constructor_unsupportedValue_throwsIllegalArgumentException() {
        String message = Religion.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalArgumentException.class, message, () -> new Religion("Unlisted faith"));
        assertThrows(IllegalArgumentException.class, message, () -> new Religion("Other: Jainism"));
        assertThrows(IllegalArgumentException.class, message, () -> new Religion("Bahá’í Faith"));
    }
}
