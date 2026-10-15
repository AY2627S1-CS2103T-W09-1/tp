package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class ReligionMatchesPredicateTest {
    @Test
    public void test_matchesOnlyRecordedCategory() {
        ReligionMatchesPredicate predicate = new ReligionMatchesPredicate(new Religion("Jainism"));
        assertTrue(predicate.test(new PersonBuilder().withReligion(" jAiNiSm ").build()));
        assertFalse(predicate.test(new PersonBuilder().withReligion("Buddhism").build()));
        assertFalse(predicate.test(new PersonBuilder().build()));

        ReligionMatchesPredicate noReligion = new ReligionMatchesPredicate(new Religion("No religion"));
        assertTrue(noReligion.test(new PersonBuilder().withReligion("No religion").build()));
        assertFalse(noReligion.test(new PersonBuilder().build()));
    }

    @Test
    public void equals_comparesNormalizedCategory() {
        ReligionMatchesPredicate predicate = new ReligionMatchesPredicate(new Religion("Baha'i Faith"));
        ReligionMatchesPredicate same = new ReligionMatchesPredicate(new Religion(" BAHA'I   faith "));
        assertEquals(predicate, predicate);
        assertEquals(predicate, same);
        assertEquals(predicate.hashCode(), same.hashCode());
        assertNotEquals(predicate, new ReligionMatchesPredicate(new Religion("Jainism")));
        assertNotEquals(predicate, null);
        assertNotEquals(predicate, "Baha'i Faith");
        assertEquals(ReligionMatchesPredicate.class.getCanonicalName() + "{religion=Baha'i Faith}",
                predicate.toString());
    }

    @Test
    public void constructor_nullReligion_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ReligionMatchesPredicate(null));
    }
}
