package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * A personal attribute that may appear in a partner preference or dealbreaker.
 */
public enum DatingAttribute {
    AGE("age", "Age"),
    GENDER("gender", "Gender"),
    SMOKING("smoking", "Smoking"),
    RELIGION("religion", "Religion"),
    RELATIONSHIP_GOAL("relationshipGoal", "Relationship goal");

    public static final String MESSAGE_EMPTY_VALUE = "A dating criterion must have a value.";

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

    /** Returns whether this attribute may record more than one partner criterion. */
    public boolean allowsMultipleValues() {
        return this == GENDER || this == RELIGION || this == RELATIONSHIP_GOAL;
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
            case AGE -> Age.normalizeRange(value);
            case GENDER -> Gender.parseValue(value).getValue();
            case SMOKING -> new SmokingStatus(value.trim()).value;
            case RELIGION -> new Religion(value).toString();
            case RELATIONSHIP_GOAL -> RelationshipGoal.fromString(value).toString();
        };
    }
}
