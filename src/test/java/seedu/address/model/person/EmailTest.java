package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        String invalidEmail = "";
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    public void isValidEmail() {
        // null email
        assertThrows(NullPointerException.class, () -> Email.isValidEmail(null));

        // blank email
        assertFalse(Email.isValidEmail("")); // empty string
        assertFalse(Email.isValidEmail(" ")); // spaces only

        // missing parts
        assertFalse(Email.isValidEmail("@example.com")); // missing local part
        assertFalse(Email.isValidEmail("peterjackexample.com")); // missing '@' symbol
        assertFalse(Email.isValidEmail("peterjack@")); // missing domain name

        // invalid parts
        assertFalse(Email.isValidEmail("peterjack@-")); // invalid domain name
        assertFalse(Email.isValidEmail("peterjack@exam_ple.com")); // underscore in domain name
        assertFalse(Email.isValidEmail("peter jack@example.com")); // spaces in local part
        assertFalse(Email.isValidEmail("peterjack@exam ple.com")); // spaces in domain name
        assertFalse(Email.isValidEmail(" peterjack@example.com")); // leading space
        assertFalse(Email.isValidEmail("peterjack@example.com ")); // trailing space
        assertFalse(Email.isValidEmail("peterjack@@example.com")); // double '@' symbol
        assertFalse(Email.isValidEmail("peter@jack@example.com")); // '@' symbol in local part
        assertFalse(Email.isValidEmail("-peterjack@example.com")); // local part starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack-@example.com")); // local part ends with a hyphen
        assertFalse(Email.isValidEmail("peter..jack@example.com")); // local part has two consecutive periods
        assertFalse(Email.isValidEmail("peterjack@example@com")); // '@' symbol in domain name
        assertFalse(Email.isValidEmail("peterjack@.example.com")); // domain name starts with a period
        assertFalse(Email.isValidEmail("peterjack@example.com.")); // domain name ends with a period
        assertFalse(Email.isValidEmail("peterjack@-example.com")); // domain name starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.com-")); // domain name ends with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.c")); // top level domain has less than two chars

        // valid email
        assertTrue(Email.isValidEmail("PeterJack_1190@example.com")); // underscore in local part
        assertTrue(Email.isValidEmail("PeterJack.1190@example.com")); // period in local part
        assertTrue(Email.isValidEmail("PeterJack+1190@example.com")); // '+' symbol in local part
        assertTrue(Email.isValidEmail("PeterJack-1190@example.com")); // hyphen in local part
        assertTrue(Email.isValidEmail("a@bc")); // minimal
        assertTrue(Email.isValidEmail("test@localhost")); // alphabets only
        assertTrue(Email.isValidEmail("123@145")); // numeric local part and domain name
        assertTrue(Email.isValidEmail("a1+be.d@example1.com")); // mixture of alphanumeric and special characters
        assertTrue(Email.isValidEmail("peter_jack@very-very-very-long-example.com")); // long domain name
        assertTrue(Email.isValidEmail("if.you.dream.it_you.can.do.it@example.com")); // long local part
        assertTrue(Email.isValidEmail("e1234567@u.nus.edu")); // more than one period in domain
    }

    @Test
    public void isValidEmail_consecutivePermittedCharacters_returnsTrue() {
        assertTrue(Email.isValidEmail("first_+last@example.com"));
        assertTrue(Email.isValidEmail("first+-last@example.com"));
        assertTrue(Email.isValidEmail("user@xn--bcher-kva.de"));
        assertTrue(Email.isValidEmail("user@example--domain.com"));
    }

    @Test
    public void isValidEmail_longValues_doesNotOverflow() {
        String longLocalPart = "a+".repeat(5000) + "a@example.com";
        String longDomain = "a@" + "a.".repeat(15000) + "com";

        // Large pastes must use normal validation rather than exhaust the regex stack.
        assertTrue(Email.isValidEmail(longLocalPart));
        assertTrue(Email.isValidEmail(longDomain));
        assertFalse(Email.isValidEmail(longLocalPart + "!"));
        assertFalse(Email.isValidEmail(longDomain + "-"));
    }

    @Test
    public void isValidEmail_localPunctuation_respectsBoundaries() {
        for (char first : "+_.-".toCharArray()) {
            for (char second : "+_.-".toCharArray()) {
                String email = "first" + first + second + "last@example.com";
                // Only adjacent periods are forbidden; other supported pairs may be consecutive.
                boolean isExpectedValid = first != '.' || second != '.';
                assertEquals(isExpectedValid, Email.isValidEmail(email), email);
            }
            assertFalse(Email.isValidEmail(first + "first@example.com"));
            assertFalse(Email.isValidEmail("first" + first + "@example.com"));
        }
    }

    @Test
    public void isValidEmail_domainLabels_respectsBoundaries() {
        List<String> validEmails = List.of("a@b.cd", "a@bc", "a@0.12", "a@x--y.cd",
                "a@XN--BCHER-KVA.DE", "a@xn--bcher-kva");
        for (String email : validEmails) {
            assertTrue(Email.isValidEmail(email), email);
        }
        List<String> invalidEmails = List.of("a@b.c", "a@.bc", "a@bc.", "a@b..cd", "a@-b.cd",
                "a@b-.cd", "a@b.-cd", "a@b.cd-", "a@b_c.de");
        for (String email : invalidEmails) {
            assertFalse(Email.isValidEmail(email), email);
        }
    }

    @Test
    public void isValidEmail_whitespaceAndUnsupportedCharacters_returnsFalse() {
        List<String> invalidEmails = List.of(" a@bc", "a@bc ", "a\tb@cd", "a@b\tcd", "a@bc\n",
                "a@bc\r", "a@bc\r\n", "a@bc\u2028", "a@bc\u2029", "a\u0000b@cd", "a\u00e9@bc",
                "a@b\u00e9", "a\u2003b@cd", "a@b\u2003cd", "a@b/cd", "a@b:cd", "a@b\u200bcd");
        for (String email : invalidEmails) {
            assertFalse(Email.isValidEmail(email), email);
        }
    }

    @Test
    public void isValidEmail_longBoundaryValues_doesNotOverflow() {
        String punctuation = "+".repeat(30000);
        String hyphens = "-".repeat(30000);
        String letters = "a".repeat(30000);

        // Long runs without separators exercise different paths from many short domain labels.
        assertTrue(Email.isValidEmail("a" + punctuation + "b@example.com"));
        assertTrue(Email.isValidEmail("a@b" + hyphens + "c"));
        assertTrue(Email.isValidEmail(letters + "@" + letters));
        assertFalse(Email.isValidEmail("a" + punctuation + "@example.com"));
        assertFalse(Email.isValidEmail("a@b" + hyphens));
        assertFalse(Email.isValidEmail("a@" + hyphens + "bc"));
        assertFalse(Email.isValidEmail(letters));
        assertFalse(Email.isValidEmail(letters + "..b@example.com"));
        assertFalse(Email.isValidEmail("a@" + "a.".repeat(15000) + "c"));
    }

    @Test
    public void equals() {
        Email email = new Email("valid@email");

        // same values -> returns true
        assertTrue(email.equals(new Email("valid@email")));

        // same object -> returns true
        assertTrue(email.equals(email));

        // null -> returns false
        assertFalse(email.equals(null));

        // different types -> returns false
        assertFalse(email.equals(5.0f));

        // different values -> returns false
        assertFalse(email.equals(new Email("other.valid@email")));
    }
}
