package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a client's relationship goal, which is one of the supported categories adapted from Hinge.
 * Each goal can be entered by its short keyword or by its full name.
 */
public enum RelationshipGoal {
    LIFE_PARTNER("life", "Life partner"),
    LONG_TERM("long", "Long-term relationship"),
    LONG_TERM_OPEN_TO_SHORT("long-open", "Long-term relationship, open to short"),
    SHORT_TERM_OPEN_TO_LONG("short-open", "Short-term relationship, open to long"),
    SHORT_TERM_FUN("short", "Short-term fun"),
    FIGURING_OUT("unsure", "Figuring out my goals");

    public static final String MESSAGE_CONSTRAINTS = "Relationship goal must be one of: "
            + Stream.of(values())
                    .map(goal -> goal.keyword + " (" + goal.fullName + ")")
                    .collect(Collectors.joining(", "))
            + ". Full goal names are also accepted.";

    /** The text displayed when a client's relationship goal has not been recorded. */
    public static final String NOT_SPECIFIED_LABEL = "Not specified";

    private final String keyword;
    private final String fullName;

    RelationshipGoal(String keyword, String fullName) {
        this.keyword = keyword;
        this.fullName = fullName;
    }

    /**
     * Returns the relationship goal whose keyword or full name matches {@code input}.
     * Letter case, surrounding whitespace, and repeated internal whitespace are ignored.
     *
     * @throws IllegalArgumentException if no relationship goal matches
     */
    public static RelationshipGoal fromString(String input) {
        requireNonNull(input);
        String normalizedInput = input.strip().replaceAll("\\s+", " ");
        for (RelationshipGoal goal : values()) {
            if (goal.matches(normalizedInput)) {
                return goal;
            }
        }
        throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
    }

    private boolean matches(String normalizedInput) {
        boolean isKeyword = keyword.equalsIgnoreCase(normalizedInput);
        boolean isFullName = fullName.equalsIgnoreCase(normalizedInput);
        return isKeyword || isFullName;
    }

    /** Returns the full goal name, which is used for display and storage. */
    @Override
    public String toString() {
        return fullName;
    }
}
