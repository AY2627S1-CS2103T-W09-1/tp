package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * An immutable, unconstrained note used by the adding-a-command tutorial.
 * An empty value means that no remark is recorded.
 */
public class Remark {

    public static final Remark EMPTY = new Remark("");

    public final String value;

    /**
     * Creates a remark, including an empty remark for clearing an existing note.
     */
    public Remark(String value) {
        this.value = requireNonNull(value);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Remark otherRemark && value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
