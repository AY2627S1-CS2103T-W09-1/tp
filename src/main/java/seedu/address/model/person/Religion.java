package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A client's religion, represented by one of the supported categories.
 * Equality ignores case and repeated whitespace.
 */
public final class Religion {

    /** The fixed, immutable categories accepted for religion fields. */
    public static final List<String> CATEGORIES = List.of("Christianity", "Islam", "Hinduism", "Buddhism",
            "Sikhism", "Judaism", "Jainism", "Baha'i Faith", "Shinto", "Taoism", "Confucianism",
            "Zoroastrianism", "Rastafari", "Wicca", "Paganism", "Tenrikyo", "Cao Dai", "Druze",
            "Atheism", "Agnosticism", "No religion");

    public static final String MESSAGE_CONSTRAINTS = "Religion must be one of: "
            + String.join(", ", CATEGORIES) + ".";
    public static final String MESSAGE_CONFLICT = "Preferred and required religions must agree, and neither can be "
            + "an excluded religion.";

    private final String displayValue;
    private final String comparisonValue;

    /**
     * Creates a religion from a supported category.
     *
     * @param value the value entered by the user or loaded from storage
     * @throws IllegalArgumentException if the value is not supported
     */
    public Religion(String value) {
        requireNonNull(value);
        String cleaned = value.strip().replaceAll("\\s+", " ");
        for (String category : CATEGORIES) {
            if (category.equalsIgnoreCase(cleaned)) {
                displayValue = category;
                comparisonValue = category.toLowerCase(Locale.ROOT);
                return;
            }
        }
        throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
    }

    /** Returns the canonical category for display and storage. */
    @Override
    public String toString() {
        return displayValue;
    }

    /** Returns whether two religions represent the same category. */
    @Override
    public boolean equals(Object other) {
        return other instanceof Religion religion && comparisonValue.equals(religion.comparisonValue);
    }

    /** Returns a hash code consistent with case-insensitive religion equality. */
    @Override
    public int hashCode() {
        return Objects.hash(comparisonValue);
    }
}
