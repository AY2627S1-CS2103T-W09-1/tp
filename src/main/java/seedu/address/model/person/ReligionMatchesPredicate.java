package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a client's recorded own religion matches the requested category.
 * Unknown religion does not match any category, including No religion.
 */
public class ReligionMatchesPredicate implements Predicate<Person> {
    private final Religion religion;

    /**
     * Creates a predicate for one supported religion category.
     */
    public ReligionMatchesPredicate(Religion religion) {
        this.religion = requireNonNull(religion);
    }

    @Override
    public boolean test(Person person) {
        return person.getReligion().filter(religion::equals).isPresent();
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof ReligionMatchesPredicate otherPredicate && religion.equals(otherPredicate.religion);
    }

    @Override
    public int hashCode() {
        return religion.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("religion", religion).toString();
    }
}
