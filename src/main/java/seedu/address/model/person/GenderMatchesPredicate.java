package seedu.address.model.person;

import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a client's gender is one of the requested genders.
 */
public class GenderMatchesPredicate implements Predicate<Person> {
    private final Set<Gender> genders;

    public GenderMatchesPredicate(Set<Gender> genders) {
        this.genders = Set.copyOf(genders);
    }

    @Override
    public boolean test(Person person) {
        return genders.contains(person.getGender());
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof GenderMatchesPredicate otherPredicate && genders.equals(otherPredicate.genders);
    }

    @Override
    public int hashCode() {
        return genders.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("genders", genders).toString();
    }
}
