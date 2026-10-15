package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class RelationshipGoalMatchesPredicateTest {

    @Test
    public void test() {
        RelationshipGoalMatchesPredicate predicate = new RelationshipGoalMatchesPredicate(RelationshipGoal.LONG_TERM);
        assertTrue(predicate.test(new PersonBuilder().withRelationshipGoal("long").build()));
        assertFalse(predicate.test(new PersonBuilder().withRelationshipGoal("short").build()));
        assertFalse(predicate.test(new PersonBuilder().build()));
    }

    @Test
    public void equals() {
        RelationshipGoalMatchesPredicate predicate = new RelationshipGoalMatchesPredicate(RelationshipGoal.LONG_TERM);
        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(new RelationshipGoalMatchesPredicate(RelationshipGoal.LONG_TERM)));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals(new RelationshipGoalMatchesPredicate(RelationshipGoal.LIFE_PARTNER)));
    }
}
