package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class NameContainsPhrasePredicateTest {

    @Test
    public void test_nameContainsPhrase_returnsTrue() {
        Person ravi = new PersonBuilder().withName("Ravi s/o Kumar").build();
        assertTrue(new NameContainsPhrasePredicate("Ravi s/o").test(ravi));
        assertTrue(new NameContainsPhrasePredicate("s/o kumar").test(ravi));
        assertTrue(new NameContainsPhrasePredicate("  RAVI   S/O  KUMAR ").test(ravi));
        assertTrue(new NameContainsPhrasePredicate("s/o").test(ravi));
    }

    @Test
    public void test_nameDoesNotContainPhrase_returnsFalse() {
        Person ravi = new PersonBuilder().withName("Ravi s/o Kumar").build();
        // Words out of order or not consecutive
        assertFalse(new NameContainsPhrasePredicate("Kumar Ravi").test(ravi));
        assertFalse(new NameContainsPhrasePredicate("Ravi Kumar").test(ravi));
        // Partial words
        assertFalse(new NameContainsPhrasePredicate("Rav").test(ravi));
        assertFalse(new NameContainsPhrasePredicate("s/").test(ravi));
        // Longer than the name
        assertFalse(new NameContainsPhrasePredicate("Ravi s/o Kumar Tan").test(ravi));
    }

    @Test
    public void equals() {
        NameContainsPhrasePredicate phrase = new NameContainsPhrasePredicate("Ravi s/o");

        assertTrue(phrase.equals(phrase));
        assertTrue(phrase.equals(new NameContainsPhrasePredicate(" ravi  S/O ")));
        assertFalse(phrase.equals(new NameContainsPhrasePredicate("Ravi")));
        assertFalse(phrase.equals(null));
        assertFalse(phrase.equals(1));
    }

    @Test
    public void toStringMethod() {
        assertEquals(NameContainsPhrasePredicate.class.getCanonicalName() + "{phrase=ravi s/o}",
                new NameContainsPhrasePredicate("Ravi s/o").toString());
    }
}
