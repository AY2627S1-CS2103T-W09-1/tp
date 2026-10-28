package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * An immutable collection of a client's soft partner preferences and hard dealbreakers.
 */
public final class DatingCriteria {

    public static final String MESSAGE_CONFLICTING_RELIGION =
            "A partner religion cannot be both preferred and excluded.";
    public static final String MESSAGE_CONFLICTING_RELATIONSHIP_GOAL =
            "A relationship goal cannot be both preferred and excluded.";

    public static final DatingCriteria EMPTY = new DatingCriteria(Map.of(), Map.of());

    private final Map<DatingAttribute, String> preferences;
    private final Map<DatingAttribute, String> dealbreakers;

    /**
     * Creates criteria after validating and canonicalizing all supplied values.
     * Dealbreaker values describe attributes a partner must not have.
     *
     * @throws IllegalArgumentException If the same religion or relationship goal is both preferred and excluded.
     */
    public DatingCriteria(Map<DatingAttribute, String> preferences, Map<DatingAttribute, String> dealbreakers) {
        this.preferences = normalize(preferences);
        this.dealbreakers = normalize(dealbreakers);
        String preferredReligion = this.preferences.get(DatingAttribute.RELIGION);
        if (preferredReligion != null && preferredReligion.equals(this.dealbreakers.get(DatingAttribute.RELIGION))) {
            throw new IllegalArgumentException(MESSAGE_CONFLICTING_RELIGION);
        }
        if (!Collections.disjoint(goalsIn(this.preferences), goalsIn(this.dealbreakers))) {
            throw new IllegalArgumentException(MESSAGE_CONFLICTING_RELATIONSHIP_GOAL);
        }
    }

    /**
     * Returns the relationship goals the client is open to, which also describe the client to potential
     * partners. The set is empty if no relationship goal preference is recorded.
     */
    public Set<RelationshipGoal> getRelationshipGoals() {
        return goalsIn(preferences);
    }

    /**
     * Returns the preference criteria indexed by their target attribute.
     */
    public Map<DatingAttribute, String> getPreferences() {
        return preferences;
    }

    /**
     * Returns the excluded partner values indexed by their target attribute.
     */
    public Map<DatingAttribute, String> getDealbreakers() {
        return dealbreakers;
    }

    /**
     * Returns whether no preference or dealbreaker is recorded.
     */
    public boolean isEmpty() {
        return preferences.isEmpty() && dealbreakers.isEmpty();
    }

    /**
     * Converts criteria to stable JSON-ready keys and values.
     */
    public Map<String, String> getPreferencesForStorage() {
        return toStorageMap(preferences);
    }

    /**
     * Converts dealbreakers to stable JSON-ready keys and values.
     */
    public Map<String, String> getDealbreakersForStorage() {
        return toStorageMap(dealbreakers);
    }

    /**
     * Reconstructs criteria saved using {@link #getPreferencesForStorage()}.
     *
     * @throws IllegalArgumentException If stored criteria are invalid or contradictory.
     */
    public static DatingCriteria fromStorage(Map<String, String> preferences,
            Map<String, String> dealbreakers) {
        return new DatingCriteria(fromStorageMap(preferences), fromStorageMap(dealbreakers));
    }

    private static Map<DatingAttribute, String> normalize(Map<DatingAttribute, String> criteria) {
        requireNonNull(criteria);
        Map<DatingAttribute, String> normalized = new EnumMap<>(DatingAttribute.class);
        for (Map.Entry<DatingAttribute, String> entry : criteria.entrySet()) {
            DatingAttribute attribute = requireNonNull(entry.getKey());
            normalized.put(attribute, attribute.normalizeCriterion(requireNonNull(entry.getValue())));
        }
        return Collections.unmodifiableMap(normalized);
    }

    private static Set<RelationshipGoal> goalsIn(Map<DatingAttribute, String> criteria) {
        String criterion = criteria.get(DatingAttribute.RELATIONSHIP_GOAL);
        return criterion == null ? Set.of() : RelationshipGoal.parseStoredCriterion(criterion);
    }

    private static Map<String, String> toStorageMap(Map<DatingAttribute, String> criteria) {
        Map<String, String> storage = new LinkedHashMap<>();
        criteria.forEach((attribute, value) -> storage.put(attribute.getStorageKey(), value));
        return storage;
    }

    private static Map<DatingAttribute, String> fromStorageMap(Map<String, String> criteria) {
        if (criteria == null) {
            return Map.of();
        }
        Map<DatingAttribute, String> converted = new EnumMap<>(DatingAttribute.class);
        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            if (entry.getValue() == null) {
                throw new IllegalArgumentException(DatingAttribute.MESSAGE_EMPTY_VALUE);
            }
            DatingAttribute attribute = DatingAttribute.fromStorageKey(entry.getKey());
            if (converted.put(attribute, entry.getValue()) != null) {
                throw new IllegalArgumentException("A dating attribute may be specified only once.");
            }
        }
        return converted;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof DatingCriteria otherCriteria
                && preferences.equals(otherCriteria.preferences)
                && dealbreakers.equals(otherCriteria.dealbreakers));
    }

    @Override
    public int hashCode() {
        return Objects.hash(preferences, dealbreakers);
    }
}
