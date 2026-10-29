package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Compares two clients' saved criteria in both directions without modifying either client.
 * Dealbreakers describe excluded values; a met dealbreaker means the exclusion is respected.
 */
public final class MatchAssessment {
    private final List<Comparison> firstComparisons;
    private final List<Comparison> secondComparisons;

    /**
     * Assesses each client's criteria against the other client's own attributes.
     */
    public MatchAssessment(Person first, Person second) {
        requireAllNonNull(first, second);
        firstComparisons = compare(first, second);
        secondComparisons = compare(second, first);
    }

    public List<Comparison> getFirstComparisons() {
        return firstComparisons;
    }

    public List<Comparison> getSecondComparisons() {
        return secondComparisons;
    }

    /**
     * Gives known dealbreaker violations precedence over missing information.
     * Unmet preferences alone do not make a pair incompatible.
     */
    public Outcome getOutcome() {
        List<Comparison> comparisons = Stream.concat(firstComparisons.stream(), secondComparisons.stream()).toList();
        if (comparisons.stream().anyMatch(result -> result.isDealbreaker() && result.status() == Status.UNMET)) {
            return Outcome.INCOMPATIBLE;
        }
        if (comparisons.isEmpty() || comparisons.stream().anyMatch(result -> result.status() == Status.UNKNOWN)) {
            return Outcome.INSUFFICIENT_INFORMATION;
        }
        return Outcome.COMPATIBLE;
    }

    private static List<Comparison> compare(Person owner, Person candidate) {
        List<Comparison> comparisons = new ArrayList<>();
        addComparisons(comparisons, owner.getDatingCriteria().getPreferences(), candidate, false);
        addComparisons(comparisons, owner.getDatingCriteria().getDealbreakers(), candidate, true);
        return List.copyOf(comparisons);
    }

    private static void addComparisons(List<Comparison> comparisons, Map<DatingAttribute, Set<String>> criteria,
            Person candidate, boolean isDealbreaker) {
        criteria.forEach((attribute, criteriaValues) -> {
            Optional<String> actual = getActualValue(candidate, attribute);
            Status status = actual.map(value -> criteriaValues.stream()
                    .anyMatch(criterion -> matches(candidate, attribute, criterion, value)) != isDealbreaker
                    ? Status.MET : Status.UNMET).orElse(Status.UNKNOWN);
            comparisons.add(new Comparison(attribute,
                    criteriaValues.stream().sorted().collect(Collectors.joining(", ")),
                    actual, isDealbreaker, status));
        });
    }

    private static Optional<String> getActualValue(Person person, DatingAttribute attribute) {
        return switch (attribute) {
            case AGE -> Optional.of(Integer.toString(person.getAge().value));
            case GENDER -> person.getGender().equals(Gender.UNSPECIFIED)
                    ? Optional.empty() : Optional.of(person.getGender().getValue());
            case SMOKING -> person.getSmokingStatus().equals(SmokingStatus.UNSPECIFIED)
                    ? Optional.empty() : Optional.of(person.getSmokingStatus().value);
            case RELIGION -> person.getReligion().map(Religion::toString);
            case RELATIONSHIP_GOAL -> person.getRelationshipGoals().isEmpty()
                    ? Optional.empty() : Optional.of(RelationshipGoal.formatCriterion(person.getRelationshipGoals()));
        };
    }

    private static boolean matches(Person candidate, DatingAttribute attribute, String criterion, String actual) {
        if (attribute == DatingAttribute.AGE) {
            String[] bounds = criterion.split("-");
            int lower = Integer.parseInt(bounds[0]);
            int upper = bounds.length == 1 ? lower : Integer.parseInt(bounds[1]);
            return new AgeMatchesPredicate(lower, upper).test(candidate);
        }
        if (attribute == DatingAttribute.RELATIONSHIP_GOAL) {
            // A candidate's goals are the goals they are open to, so any one of them can match
            return candidate.getRelationshipGoals().contains(RelationshipGoal.fromStoredValue(criterion));
        }
        return criterion.equals(actual);
    }

    /**
     * Whether a preference is satisfied or a dealbreaker exclusion is respected.
     */
    public enum Status {
        MET, UNMET, UNKNOWN
    }

    /**
     * The overall assessment based only on recorded criteria.
     */
    public enum Outcome {
        COMPATIBLE("Compatible with recorded dealbreakers"),
        INCOMPATIBLE("Incompatible"),
        INSUFFICIENT_INFORMATION("Insufficient information");

        private final String displayName;

        Outcome(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * One criterion and the candidate's actual value, absent when it has not been recorded.
     */
    public record Comparison(DatingAttribute attribute, String criterion, Optional<String> actual,
            boolean isDealbreaker, Status status) {
        public String getTypeLabel() {
            return isDealbreaker ? "Dealbreaker" : "Preference";
        }

        public String getCriterionLabel() {
            return (isDealbreaker ? "Exclude: " : "") + formatValue(criterion);
        }

        public String getActualLabel() {
            return actual.map(this::formatValue).orElse("Not specified");
        }

        public String getStatusLabel() {
            return switch (status) {
                case MET -> "Met";
                case UNMET -> isDealbreaker ? "Violated" : "Unmet";
                case UNKNOWN -> "Unknown";
            };
        }

        private String formatValue(String value) {
            return attribute == DatingAttribute.SMOKING
                    ? (value.equals("yes") ? "Yes" : "No") : value;
        }
    }
}
