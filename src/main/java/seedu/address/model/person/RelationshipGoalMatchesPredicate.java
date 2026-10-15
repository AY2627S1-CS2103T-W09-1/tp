package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s relationship goal is the given goal.
 */
public class RelationshipGoalMatchesPredicate implements Predicate<Person> {
    private final RelationshipGoal goal;

    public RelationshipGoalMatchesPredicate(RelationshipGoal goal) {
        this.goal = requireNonNull(goal);
    }

    @Override
    public boolean test(Person person) {
        return person.getRelationshipGoal().equals(Optional.of(goal));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof RelationshipGoalMatchesPredicate otherPredicate && goal == otherPredicate.goal;
    }

    @Override
    public int hashCode() {
        return goal.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("goal", goal).toString();
    }
}
