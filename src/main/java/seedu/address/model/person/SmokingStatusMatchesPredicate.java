package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a client's own smoking status matches a specified yes/no status.
 * Clients with an unspecified smoking status do not match either search.
 */
public class SmokingStatusMatchesPredicate implements Predicate<Person> {
    private final SmokingStatus smokingStatus;

    /**
     * Creates a predicate for a recorded smoking status.
     *
     * @throws NullPointerException if {@code smokingStatus} is null.
     * @throws IllegalArgumentException if {@code smokingStatus} is unspecified.
     */
    public SmokingStatusMatchesPredicate(SmokingStatus smokingStatus) {
        requireNonNull(smokingStatus);
        checkArgument(!SmokingStatus.UNSPECIFIED.equals(smokingStatus), SmokingStatus.MESSAGE_CONSTRAINTS);
        this.smokingStatus = smokingStatus;
    }

    @Override
    public boolean test(Person person) {
        return smokingStatus.equals(person.getSmokingStatus());
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof SmokingStatusMatchesPredicate otherPredicate
                && smokingStatus.equals(otherPredicate.smokingStatus);
    }

    @Override
    public int hashCode() {
        return smokingStatus.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("smokingStatus", smokingStatus).toString();
    }
}
