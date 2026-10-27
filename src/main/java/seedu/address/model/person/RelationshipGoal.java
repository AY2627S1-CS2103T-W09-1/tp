package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a client's relationship goal, which is one of the supported categories adapted from Hinge.
 * Users enter a goal by its short keyword, while the full name is used for display and storage.
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
            + ".";

    /** The text displayed when a client's relationship goal has not been recorded. */
    public static final String NOT_SPECIFIED_LABEL = "Not specified";

    private final String keyword;
    private final String fullName;

    RelationshipGoal(String keyword, String fullName) {
        this.keyword = keyword;
        this.fullName = fullName;
    }

    /**
     * Returns the relationship goal whose keyword matches {@code input}, ignoring letter case and
     * surrounding whitespace. Full goal names are not accepted as user input.
     *
     * @throws IllegalArgumentException if no relationship goal has that keyword
     */
    public static RelationshipGoal fromKeyword(String input) {
        requireNonNull(input);
        String trimmedInput = input.strip();
        for (RelationshipGoal goal : values()) {
            if (goal.keyword.equalsIgnoreCase(trimmedInput)) {
                return goal;
            }
        }
        throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
    }

    /**
     * Returns the relationship goal whose keyword or full name matches {@code input}, as read from the
     * data file. The data file may be edited by hand, so letter case, surrounding whitespace and repeated
     * internal whitespace are ignored.
     *
     * @throws IllegalArgumentException if no relationship goal matches
     */
    public static RelationshipGoal fromStoredValue(String input) {
        requireNonNull(input);
        String normalizedInput = input.strip().replaceAll("\\s+", " ");
        for (RelationshipGoal goal : values()) {
            if (goal.keyword.equalsIgnoreCase(normalizedInput) || goal.fullName.equalsIgnoreCase(normalizedInput)) {
                return goal;
            }
        }
        throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
    }

    /** Returns the short keyword that users type to enter this goal. */
    public String getKeyword() {
        return keyword;
    }

    /** Returns the full goal name, which is used for display and storage. */
    @Override
    public String toString() {
        return fullName;
    }
}
