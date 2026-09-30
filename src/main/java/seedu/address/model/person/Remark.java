package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * An immutable, optional remark about a person. Empty remarks are allowed.
 */
public class Remark {
    public final String value;

    /**
     * Constructs a remark with the given non-null text.
     */
    public Remark(String remark) {
        requireNonNull(remark);
        value = remark;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof Remark otherRemark && value.equals(otherRemark.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
