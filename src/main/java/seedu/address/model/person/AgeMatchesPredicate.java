package seedu.address.model.person;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/** Tests whether a person's age is within an inclusive range. */
public class AgeMatchesPredicate implements Predicate<Person> {
    private final int lowerBound;
    private final int upperBound;

    /** Creates a predicate for the inclusive range from {@code lowerBound} to {@code upperBound}. */
    public AgeMatchesPredicate(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public boolean test(Person person) {
        int age = person.getAge().value;
        return age >= lowerBound && age <= upperBound;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AgeMatchesPredicate otherPredicate)) {
            return false;
        }
        return lowerBound == otherPredicate.lowerBound && upperBound == otherPredicate.upperBound;
    }

    @Override
    public int hashCode() {
        return 31 * lowerBound + upperBound;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("lowerBound", lowerBound)
                .add("upperBound", upperBound)
                .toString();
    }
}
