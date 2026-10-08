package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a client's smoking status. An empty value means it has not been recorded.
 * Guarantees: immutable; a recorded value is either {@code yes} or {@code no}.
 */
public final class SmokingStatus {

    public static final String MESSAGE_CONSTRAINTS = "Invalid smoking status. Use yes or no.";
    public static final SmokingStatus UNSPECIFIED = new SmokingStatus("");

    public final String value;

    /**
     * Constructs a {@code SmokingStatus}, normalizing the case of a valid value.
     * An empty string represents an unspecified status in the model and storage.
     */
    public SmokingStatus(String status) {
        requireNonNull(status);
        checkArgument(isValidSmokingStatus(status), MESSAGE_CONSTRAINTS);
        value = status.toLowerCase(Locale.ROOT);
    }

    /**
     * Returns true if {@code status} is yes, no (case-insensitive), or empty (unspecified).
     */
    public static boolean isValidSmokingStatus(String status) {
        String normalizedStatus = status.toLowerCase(Locale.ROOT);
        return normalizedStatus.isEmpty() || normalizedStatus.equals("yes") || normalizedStatus.equals("no");
    }

    @Override
    public String toString() {
        return value.isEmpty() ? "Not specified" : value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof SmokingStatus otherStatus && value.equals(otherStatus.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
