package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a relationship goal, which is one of the supported categories adapted from Hinge.
 * A client records the goals they are open to as a partner preference, and these goals also describe
 * the client when others are matched against them.
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

    public static final String MESSAGE_INVALID_LIST =
            "Use distinct relationship goals separated by commas, e.g. rg/long,long-open.";

    /** Separates goal names in a stored criterion. Commas cannot be used as some goal names contain one. */
    private static final String CRITERION_SEPARATOR = " or ";

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

    /**
     * Returns the distinct goals listed in {@code input}, a comma-separated list of keywords.
     *
     * @throws IllegalArgumentException if a keyword is unsupported, empty or repeated
     */
    public static Set<RelationshipGoal> parseKeywordList(String input) {
        requireNonNull(input);
        Set<RelationshipGoal> goals = EnumSet.noneOf(RelationshipGoal.class);
        for (String token : input.split(",", -1)) {
            if (token.isBlank() || !goals.add(fromKeyword(token))) {
                throw new IllegalArgumentException(MESSAGE_INVALID_LIST);
            }
        }
        return goals;
    }

    /**
     * Returns the goals in a criterion created by {@link #formatCriterion(Set)}, or in a hand-edited data
     * file where each goal may also be given in the forms accepted by {@link #fromStoredValue(String)}.
     *
     * @throws IllegalArgumentException if a goal is unsupported, empty or repeated
     */
    public static Set<RelationshipGoal> parseStoredCriterion(String criterion) {
        requireNonNull(criterion);
        Set<RelationshipGoal> goals = EnumSet.noneOf(RelationshipGoal.class);
        for (String goalName : criterion.split(CRITERION_SEPARATOR, -1)) {
            if (!goals.add(fromStoredValue(goalName))) {
                throw new IllegalArgumentException(MESSAGE_INVALID_LIST);
            }
        }
        return goals;
    }

    /**
     * Returns the canonical criterion for {@code goals}: their full names in a fixed order.
     */
    public static String formatCriterion(Set<RelationshipGoal> goals) {
        requireNonNull(goals);
        assert !goals.isEmpty() : "A relationship goal criterion must contain at least one goal";
        return EnumSet.copyOf(goals).stream()
                .map(RelationshipGoal::toString)
                .collect(Collectors.joining(CRITERION_SEPARATOR));
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
