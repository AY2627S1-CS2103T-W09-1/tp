package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * A client's gender. The empty value represents a gender that has not been recorded.
 */
public enum Gender {
    MAN("m", "Man"),
    WOMAN("w", "Woman"),
    NON_BINARY("nb", "Non-binary"),
    UNSPECIFIED("", "Unspecified");

    public static final String MESSAGE_CONSTRAINTS =
            "Gender must be m (man), w (woman), or nb (non-binary), or empty for unspecified.";

    private final String value;
    private final String displayName;

    Gender(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    /**
     * Parses a command or stored value, ignoring case and surrounding whitespace.
     *
     * @throws IllegalArgumentException if the value is not supported.
     */
    public static Gender parseValue(String value) {
        requireNonNull(value);
        String trimmedValue = value.trim();
        for (Gender gender : values()) {
            if (gender.value.equalsIgnoreCase(trimmedValue)) {
                return gender;
            }
        }
        throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
