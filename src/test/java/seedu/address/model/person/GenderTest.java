package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class GenderTest {
    @Test
    public void parseValue_normalizesCaseAndWhitespace() {
        assertEquals(Gender.MAN, Gender.parseValue(" M "));
        assertEquals(Gender.WOMAN, Gender.parseValue("w"));
        assertEquals(Gender.NON_BINARY, Gender.parseValue("\tNb\n"));
        assertEquals(Gender.UNSPECIFIED, Gender.parseValue(" \t "));
    }

    @Test
    public void parseValue_invalidValue_throwsIllegalArgumentException() {
        for (String value : new String[] {"man", "woman", "non-binary", "unspecified", "x", "m,w", "n b"}) {
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> Gender.parseValue(value), value);
            assertEquals(Gender.MESSAGE_CONSTRAINTS, exception.getMessage());
        }
        assertThrows(NullPointerException.class, () -> Gender.parseValue(null));
    }

    @Test
    public void values_haveCanonicalCodesAndDisplayNames() {
        String[] codes = {"m", "w", "nb", ""};
        String[] labels = {"Man", "Woman", "Non-binary", "Unspecified"};
        Gender[] genders = Gender.values();
        for (int i = 0; i < genders.length; i++) {
            assertEquals(codes[i], genders[i].getValue());
            assertEquals(labels[i], genders[i].toString());
        }
    }
}
