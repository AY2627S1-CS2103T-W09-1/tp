package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class RelationshipGoalMatchesPredicateTest {

    @Test
    public void test() {
        RelationshipGoalMatchesPredicate predicate = new RelationshipGoalMatchesPredicate(
                Set.of(RelationshipGoal.LONG_TERM, RelationshipGoal.LIFE_PARTNER));
        assertTrue(predicate.test(new PersonBuilder().withRelationshipGoal("long").build()));
        assertTrue(predicate.test(new PersonBuilder().withRelationshipGoal("short or life").build()));
        assertFalse(predicate.test(new PersonBuilder().withRelationshipGoal("short").build()));
        assertFalse(predicate.test(new PersonBuilder().build()));
    }

    @Test
    public void equals() {
        RelationshipGoalMatchesPredicate predicate = new RelationshipGoalMatchesPredicate(
                Set.of(RelationshipGoal.LONG_TERM));
        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(new RelationshipGoalMatchesPredicate(Set.of(RelationshipGoal.LONG_TERM))));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals(new RelationshipGoalMatchesPredicate(Set.of(RelationshipGoal.LIFE_PARTNER))));
        assertEquals(predicate.hashCode(),
                new RelationshipGoalMatchesPredicate(Set.of(RelationshipGoal.LONG_TERM)).hashCode());
    }

    @Test
    public void constructor_noGoals_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new RelationshipGoalMatchesPredicate(Set.of()));
    }

    @Test
    public void toStringMethod() {
        RelationshipGoalMatchesPredicate predicate = new RelationshipGoalMatchesPredicate(
                Set.of(RelationshipGoal.LONG_TERM));
        assertEquals(RelationshipGoalMatchesPredicate.class.getCanonicalName() + "{goals=[Long-term relationship]}",
                predicate.toString());
    }
}
