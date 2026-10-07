package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests supported religion categories and their canonicalization. */
public class ReligionTest {

    /** Known categories are canonicalized without losing case-insensitive equality. */
    @Test
    public void constructor_knownCategory_canonicalizes() {
        Religion religion = new Religion("  no   RELIGION ");
        assertEquals("No religion", religion.toString());
        assertEquals(new Religion("No religion"), religion);
    }

    /** Additional categories are canonicalized and compare case-insensitively. */
    @Test
    public void constructor_additionalCategory_canonicalizes() {
        Religion religion = new Religion("  jAiNiSm ");
        assertEquals("Jainism", religion.toString());
        assertEquals(new Religion("JAINISM"), religion);
        assertEquals(religion.hashCode(), new Religion("jainism").hashCode());
    }

    /** Common spellings of the Baha'i Faith use the same stored category. */
    @Test
    public void constructor_bahaiAliases_canonicalizes() {
        Religion religion = new Religion("Bahá’í Faith");
        assertEquals("Baha'i Faith", religion.toString());
        assertEquals(new Religion("Bahá'í Faith"), religion);
    }

    /** Free text and the former Other syntax are rejected. */
    @Test
    public void constructor_unsupportedValue_throwsIllegalArgumentException() {
        String message = Religion.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalArgumentException.class, message, () -> new Religion("Unlisted faith"));
        assertThrows(IllegalArgumentException.class, message, () -> new Religion("Other: Jainism"));
    }
}
