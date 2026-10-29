package seedu.address.model.person;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person} is open to at least one of the given relationship goals.
 */
public class RelationshipGoalMatchesPredicate implements Predicate<Person> {
    private final Set<RelationshipGoal> goals;

    /**
     * Creates a predicate that accepts clients open to any goal in {@code goals}.
     */
    public RelationshipGoalMatchesPredicate(Set<RelationshipGoal> goals) {
        assert !goals.isEmpty() : "At least one relationship goal must be searched for";
        this.goals = Set.copyOf(goals);
    }

    @Override
    public boolean test(Person person) {
        return !Collections.disjoint(goals, person.getRelationshipGoals());
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof RelationshipGoalMatchesPredicate otherPredicate
                && goals.equals(otherPredicate.goals);
    }

    @Override
    public int hashCode() {
        return goals.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("goals", goals).toString();
    }
}
