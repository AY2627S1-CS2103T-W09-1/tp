package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class GenderMatchesPredicateTest {
    @Test
    public void constructor_copiesSearchCriteria() {
        Set<Gender> genders = EnumSet.of(Gender.MAN);
        GenderMatchesPredicate predicate = new GenderMatchesPredicate(genders);
        genders.clear();
        assertTrue(predicate.test(new PersonBuilder().withGender("m").build()));
    }

    @Test
    public void equals_comparesGenderSets() {
        GenderMatchesPredicate predicate = new GenderMatchesPredicate(Set.of(Gender.MAN, Gender.WOMAN));
        GenderMatchesPredicate same = new GenderMatchesPredicate(Set.of(Gender.WOMAN, Gender.MAN));
        assertEquals(predicate, predicate);
        assertEquals(predicate, same);
        assertEquals(predicate.hashCode(), same.hashCode());
        assertNotEquals(predicate, new GenderMatchesPredicate(Set.of(Gender.MAN)));
        assertNotEquals(predicate, null);
        assertNotEquals(predicate, "m,w");
    }
}
