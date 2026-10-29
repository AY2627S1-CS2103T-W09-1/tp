package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a person's age in the address book.
 */
public class Age {
    public static final String MESSAGE_CONSTRAINTS = "Age must be an integer between 18 and 99.";
    public static final String MESSAGE_INVALID_RANGE =
            "The lower age bound cannot be greater than the upper age bound.";
    private static final String VALIDATION_REGEX = "\\d+";
    private static final int MINIMUM_AGE = 18;
    private static final int MAXIMUM_AGE = 99;

    public final int value;

    /** Constructs an age value that is between 18 and 99, inclusive. */
    public Age(int age) {
        checkArgument(isValidAge(age), MESSAGE_CONSTRAINTS);
        value = age;
    }

    /** Returns true if {@code age} is a valid age string. */
    public static boolean isValidAge(String age) {
        requireNonNull(age);
        if (!age.matches(VALIDATION_REGEX)) {
            return false;
        }
        try {
            return isValidAge(Integer.parseInt(age));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Returns true if {@code age} is within the supported range. */
    public static boolean isValidAge(int age) {
        return age >= MINIMUM_AGE && age <= MAXIMUM_AGE;
    }

    /**
     * Validates and canonicalizes an exact age or inclusive age range.
     *
     * @throws IllegalArgumentException If either bound is invalid or the lower bound exceeds the upper bound.
     */
    public static String normalizeRange(String value) {
        requireNonNull(value);
        String[] bounds = value.trim().split("-", -1);
        boolean isValidLength = bounds.length <= 2;
        String lowerBoundText = bounds[0].trim();
        String upperBoundText = bounds.length == 2 ? bounds[1].trim() : lowerBoundText;
        boolean isFirstBoundValid = isValidAge(lowerBoundText);
        boolean isSecondBoundValid = isValidAge(upperBoundText);
        if (!isValidLength || !isFirstBoundValid || !isSecondBoundValid) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        int lowerBound = Integer.parseInt(lowerBoundText);
        int upperBound = Integer.parseInt(upperBoundText);
        if (lowerBound > upperBound) {
            throw new IllegalArgumentException(MESSAGE_INVALID_RANGE);
        }
        return bounds.length == 1 ? Integer.toString(lowerBound) : lowerBound + "-" + upperBound;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof Age otherAge && value == otherAge.value);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
