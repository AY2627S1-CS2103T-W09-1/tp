package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/** A personal attribute that may appear in a partner preference or dealbreaker. */
public enum DatingAttribute {
    AGE("age", "Age"),
    GENDER("gender", "Gender"),
    SMOKING("smoking", "Smoking"),
    RELIGION("religion", "Religion"),
    RELATIONSHIP_GOAL("relationshipGoal", "Relationship goal");

    public static final String MESSAGE_EMPTY_VALUE = "A dating criterion must have a value.";
    public static final String MESSAGE_INVALID_AGE_RANGE =
            "The lower age bound cannot be greater than the upper age bound.";

    private final String storageKey;
    private final String displayName;

    DatingAttribute(String storageKey, String displayName) {
        this.storageKey = storageKey;
        this.displayName = displayName;
    }

    /** Returns the stable JSON property name for this attribute. */
    public String getStorageKey() {
        return storageKey;
    }

    /** Returns a human-readable name for this attribute. */
    public String getDisplayName() {
        return displayName;
    }

    /** Returns the attribute associated with a JSON property name. */
    public static DatingAttribute fromStorageKey(String storageKey) {
        requireNonNull(storageKey);
        for (DatingAttribute attribute : values()) {
            if (attribute.storageKey.equals(storageKey)) {
                return attribute;
            }
        }
        throw new IllegalArgumentException("Unsupported dating attribute: " + storageKey);
    }

    /** Validates and canonicalizes a criterion value for this attribute. */
    public String normalizeCriterion(String value) {
        requireNonNull(value);
        if (value.isBlank()) {
            throw new IllegalArgumentException(MESSAGE_EMPTY_VALUE);
        }
        return switch (this) {
            case AGE -> normalizeAgeCriterion(value);
            case GENDER -> Gender.parseValue(value).getValue();
            case SMOKING -> new SmokingStatus(value.trim()).value;
            case RELIGION -> new Religion(value).toString();
            case RELATIONSHIP_GOAL -> RelationshipGoal.fromString(value).toString();
        };
    }

    private static String normalizeAgeCriterion(String value) {
        String[] bounds = value.trim().split("-", -1);
        if (bounds.length > 2 || !Age.isValidAge(bounds[0])
                || (bounds.length == 2 && !Age.isValidAge(bounds[1]))) {
            throw new IllegalArgumentException(Age.MESSAGE_CONSTRAINTS);
        }
        int lowerBound = Integer.parseInt(bounds[0]);
        int upperBound = bounds.length == 2 ? Integer.parseInt(bounds[1]) : lowerBound;
        if (lowerBound > upperBound) {
            throw new IllegalArgumentException(MESSAGE_INVALID_AGE_RANGE);
        }
        return bounds.length == 1 ? Integer.toString(lowerBound) : lowerBound + "-" + upperBound;
    }
}
