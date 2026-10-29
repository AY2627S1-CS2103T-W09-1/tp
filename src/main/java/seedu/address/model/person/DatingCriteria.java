package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * An immutable collection of a client's soft partner preferences and hard dealbreakers.
 */
public final class DatingCriteria {

    public static final String MESSAGE_CONFLICTING_RELIGION =
            "A partner religion cannot be both preferred and excluded.";
    public static final String MESSAGE_CONFLICTING_GENDER =
            "A partner gender cannot be both preferred and excluded.";
    public static final String MESSAGE_CONFLICTING_SMOKING =
            "A partner smoking status cannot be both preferred and excluded.";
    public static final String MESSAGE_CONFLICTING_RELATIONSHIP_GOAL =
            "A partner relationship goal cannot be both preferred and excluded.";
    public static final String MESSAGE_OVERLAPPING_AGE_RANGES =
            "A preferred age range cannot overlap an excluded age range.";

    public static final DatingCriteria EMPTY = new DatingCriteria(Map.of(), Map.of());

    private final Map<DatingAttribute, Set<String>> preferences;
    private final Map<DatingAttribute, Set<String>> dealbreakers;

    /**
     * Creates criteria after validating and canonicalizing all supplied values.
     * Dealbreaker values describe attributes a partner must not have.
     *
     * @throws IllegalArgumentException If a preference contradicts a dealbreaker.
     */
    public DatingCriteria(Map<DatingAttribute, ?> preferences, Map<DatingAttribute, ?> dealbreakers) {
        this.preferences = normalize(preferences);
        this.dealbreakers = normalize(dealbreakers);
        validateNoContradictions();
    }

    /**
     * Returns the preference criteria indexed by their target attribute.
     */
    public Map<DatingAttribute, Set<String>> getPreferences() {
        return preferences;
    }

    /**
     * Returns the excluded partner values indexed by their target attribute.
     */
    public Map<DatingAttribute, Set<String>> getDealbreakers() {
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
    public Map<String, List<String>> getPreferencesForStorage() {
        return toStorageMap(preferences);
    }

    /**
     * Converts dealbreakers to stable JSON-ready keys and values.
     */
    public Map<String, List<String>> getDealbreakersForStorage() {
        return toStorageMap(dealbreakers);
    }

    /**
     * Reconstructs criteria saved using {@link #getPreferencesForStorage()}.
     *
     * @throws IllegalArgumentException If stored criteria are invalid or contradictory.
     */
    public static DatingCriteria fromStorage(Map<String, ?> preferences,
            Map<String, ?> dealbreakers) {
        return new DatingCriteria(fromStorageMap(preferences), fromStorageMap(dealbreakers));
    }

    private static Map<DatingAttribute, Set<String>> normalize(Map<DatingAttribute, ?> criteria) {
        requireNonNull(criteria);
        Map<DatingAttribute, Set<String>> normalized = new EnumMap<>(DatingAttribute.class);
        for (Map.Entry<DatingAttribute, ?> entry : criteria.entrySet()) {
            DatingAttribute attribute = requireNonNull(entry.getKey());
            Set<String> values = normalizeValues(attribute, entry.getValue());
            normalized.put(attribute, values);
        }
        return Collections.unmodifiableMap(normalized);
    }

    /** Rejects criteria that would both prefer and exclude the same potential partner. */
    private void validateNoContradictions() {
        for (DatingAttribute attribute : DatingAttribute.values()) {
            Set<String> preferenceValues = preferences.get(attribute);
            Set<String> dealbreakerValues = dealbreakers.get(attribute);
            if (preferenceValues == null || dealbreakerValues == null) {
                continue;
            }
            for (String preference : preferenceValues) {
                for (String dealbreaker : dealbreakerValues) {
                    if (attribute == DatingAttribute.AGE && rangesOverlap(preference, dealbreaker)) {
                        throw new IllegalArgumentException(MESSAGE_OVERLAPPING_AGE_RANGES);
                    }
                    if (preference.equals(dealbreaker)) {
                        throw new IllegalArgumentException(getContradictionMessage(attribute));
                    }
                }
            }
        }
    }

    /** Returns an error message that identifies the contradicted dating attribute. */
    private static String getContradictionMessage(DatingAttribute attribute) {
        return switch (attribute) {
            case GENDER -> MESSAGE_CONFLICTING_GENDER;
            case SMOKING -> MESSAGE_CONFLICTING_SMOKING;
            case RELIGION -> MESSAGE_CONFLICTING_RELIGION;
            case RELATIONSHIP_GOAL -> MESSAGE_CONFLICTING_RELATIONSHIP_GOAL;
            case AGE -> MESSAGE_OVERLAPPING_AGE_RANGES;
        };
    }

    /** Returns whether two normalized inclusive age ranges share at least one age. */
    private static boolean rangesOverlap(String firstRange, String secondRange) {
        int[] firstBounds = toAgeBounds(firstRange);
        int[] secondBounds = toAgeBounds(secondRange);
        return firstBounds[0] <= secondBounds[1] && secondBounds[0] <= firstBounds[1];
    }

    /** Converts a normalized exact age or inclusive range into lower and upper bounds. */
    private static int[] toAgeBounds(String range) {
        String[] bounds = range.split("-");
        int lowerBound = Integer.parseInt(bounds[0]);
        int upperBound = bounds.length == 2 ? Integer.parseInt(bounds[1]) : lowerBound;
        return new int[] {lowerBound, upperBound};
    }

    private static Map<String, List<String>> toStorageMap(Map<DatingAttribute, Set<String>> criteria) {
        Map<String, List<String>> storage = new LinkedHashMap<>();
        criteria.forEach((attribute, values) -> storage.put(attribute.getStorageKey(), List.copyOf(values)));
        return storage;
    }

    private static Map<DatingAttribute, Object> fromStorageMap(Map<String, ?> criteria) {
        if (criteria == null) {
            return Map.of();
        }
        Map<DatingAttribute, Object> converted = new EnumMap<>(DatingAttribute.class);
        for (Map.Entry<String, ?> entry : criteria.entrySet()) {
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

    private static Set<String> normalizeValues(DatingAttribute attribute, Object rawValues) {
        requireNonNull(rawValues);
        Iterable<?> values = rawValues instanceof Iterable<?> iterable ? iterable : List.of(rawValues);
        Set<String> normalized = new LinkedHashSet<>();
        for (Object rawValue : values) {
            if (!(rawValue instanceof String value)) {
                throw new IllegalArgumentException(DatingAttribute.MESSAGE_EMPTY_VALUE);
            }
            normalized.add(attribute.normalizeCriterion(value));
        }
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(DatingAttribute.MESSAGE_EMPTY_VALUE);
        }
        if (!attribute.allowsMultipleValues() && normalized.size() > 1) {
            throw new IllegalArgumentException(attribute.getDisplayName() + " may have only one value.");
        }
        return Collections.unmodifiableSet(normalized);
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
