package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.model.person.MatchAssessment.Outcome.COMPATIBLE;
import static seedu.address.model.person.MatchAssessment.Outcome.INCOMPATIBLE;
import static seedu.address.model.person.MatchAssessment.Outcome.INSUFFICIENT_INFORMATION;
import static seedu.address.model.person.MatchAssessment.Status.MET;
import static seedu.address.model.person.MatchAssessment.Status.UNKNOWN;
import static seedu.address.model.person.MatchAssessment.Status.UNMET;

import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class MatchAssessmentTest {
    private final Person candidate = new PersonBuilder().withAge(30).withSmokingStatus("no")
            .withGender("m").withReligion("Buddhism").withRelationshipGoal("long").build();

    @Test
    public void assess_normalizedPreferences_allMet() {
        Map<DatingAttribute, String> preferences = Map.of(DatingAttribute.AGE, "25-35",
                DatingAttribute.SMOKING, "NO", DatingAttribute.RELIGION, "buddhism",
                DatingAttribute.RELATIONSHIP_GOAL, "LONG");
        MatchAssessment assessment = assess(preferences, Map.of(), candidate);
        assertEquals(COMPATIBLE, assessment.getOutcome());
        assertEquals(4, assessment.getFirstComparisons().size());
        assessment.getFirstComparisons().forEach(result -> assertEquals(MET, result.status()));
    }

    @Test
    public void assess_unmetPreferences_remainsCompatible() {
        MatchAssessment assessment = assess(Map.of(DatingAttribute.AGE, "40",
                DatingAttribute.SMOKING, "yes", DatingAttribute.RELIGION, "Islam",
                DatingAttribute.RELATIONSHIP_GOAL, "life"), Map.of(), candidate);
        assertEquals(COMPATIBLE, assessment.getOutcome());
        assessment.getFirstComparisons().forEach(result -> assertEquals(UNMET, result.status()));
    }

    @Test
    public void assess_ageRange_includesBothBounds() {
        for (int age : new int[] {24, 25, 35, 36}) {
            Person person = new PersonBuilder().withAge(age).build();
            MatchAssessment assessment = assess(Map.of(DatingAttribute.AGE, "25-35"), Map.of(), person);
            assertEquals(age == 25 || age == 35 ? MET : UNMET,
                    assessment.getFirstComparisons().getFirst().status());
        }
    }

    @Test
    public void assess_exactAge_matchesOnlyThatAge() {
        assertEquals(MET, assess(Map.of(DatingAttribute.AGE, "30"), Map.of(), candidate)
                .getFirstComparisons().getFirst().status());
        assertEquals(UNMET, assess(Map.of(DatingAttribute.AGE, "31"), Map.of(), candidate)
                .getFirstComparisons().getFirst().status());
    }

    @Test
    public void assess_genderPreferenceAndDealbreaker_comparesGender() {
        MatchAssessment preferenceAssessment = assess(Map.of(DatingAttribute.GENDER, "m"), Map.of(), candidate);
        assertEquals(MET, preferenceAssessment.getFirstComparisons().getFirst().status());

        MatchAssessment dealbreakerAssessment = assess(Map.of(), Map.of(DatingAttribute.GENDER, "m"), candidate);
        assertEquals(UNMET, dealbreakerAssessment.getFirstComparisons().getFirst().status());
        assertEquals(INCOMPATIBLE, dealbreakerAssessment.getOutcome());
    }

    @Test
    public void assess_excludedValue_violatesEachDealbreaker() {
        Map<DatingAttribute, String> exclusions = Map.of(DatingAttribute.AGE, "30-40",
                DatingAttribute.SMOKING, "no", DatingAttribute.RELIGION, "Buddhism",
                DatingAttribute.RELATIONSHIP_GOAL, "long");
        exclusions.forEach((attribute, value) -> {
            MatchAssessment assessment = assess(Map.of(), Map.of(attribute, value), candidate);
            assertEquals(INCOMPATIBLE, assessment.getOutcome());
            assertEquals(UNMET, assessment.getFirstComparisons().getFirst().status());
        });
    }

    @Test
    public void assess_nonExcludedValues_respectsDealbreakers() {
        MatchAssessment assessment = assess(Map.of(), Map.of(DatingAttribute.AGE, "18-29",
                DatingAttribute.SMOKING, "yes", DatingAttribute.RELIGION, "Islam",
                DatingAttribute.RELATIONSHIP_GOAL, "short"), candidate);
        assertEquals(COMPATIBLE, assessment.getOutcome());
        assessment.getFirstComparisons().forEach(result -> assertEquals(MET, result.status()));
    }

    @Test
    public void assess_missingAttributes_unknownForPreferencesAndDealbreakers() {
        Person unknown = new PersonBuilder().build();
        Map<DatingAttribute, String> criteria = Map.of(DatingAttribute.SMOKING, "yes",
                DatingAttribute.RELIGION, "No religion", DatingAttribute.RELATIONSHIP_GOAL, "unsure");
        MatchAssessment assessment = assess(criteria, criteria, unknown);
        assertEquals(INSUFFICIENT_INFORMATION, assessment.getOutcome());
        assertEquals(6, assessment.getFirstComparisons().size());
        assessment.getFirstComparisons().forEach(result -> assertEquals(UNKNOWN, result.status()));
    }

    @Test
    public void assess_explicitNoReligionAndUnsure_areKnownValues() {
        Person person = new PersonBuilder().withReligion("No religion").withRelationshipGoal("unsure").build();
        MatchAssessment assessment = assess(Map.of(DatingAttribute.RELIGION, "No religion",
                DatingAttribute.RELATIONSHIP_GOAL, "unsure"), Map.of(), person);
        assertEquals(COMPATIBLE, assessment.getOutcome());
        assessment.getFirstComparisons().forEach(result -> assertEquals(MET, result.status()));
    }

    @Test
    public void assess_reverseViolation_takesPrecedenceOverUnknownPreference() {
        Person first = withCriteria(candidate, Map.of(DatingAttribute.RELIGION, "Buddhism"), Map.of());
        Person second = withCriteria(new PersonBuilder().build(), Map.of(), Map.of(DatingAttribute.SMOKING, "no"));
        MatchAssessment assessment = new MatchAssessment(first, second);
        assertEquals(UNKNOWN, assessment.getFirstComparisons().getFirst().status());
        assertEquals(UNMET, assessment.getSecondComparisons().getFirst().status());
        assertEquals(INCOMPATIBLE, assessment.getOutcome());
        assertEquals(INCOMPATIBLE, new MatchAssessment(second, first).getOutcome());
    }

    @Test
    public void assess_unknownDealbreakerAndKnownViolation_incompatible() {
        Person person = new PersonBuilder().withSmokingStatus("yes").build();
        assertEquals(INCOMPATIBLE, assess(Map.of(), Map.of(DatingAttribute.SMOKING, "yes",
                DatingAttribute.RELIGION, "Islam"), person).getOutcome());
    }

    @Test
    public void assess_noCriteria_insufficientInformation() {
        assertEquals(INSUFFICIENT_INFORMATION, new MatchAssessment(candidate, candidate).getOutcome());
    }

    @Test
    public void assess_oneSidedCriteria_checksOnlyRecordedCriteria() {
        Person owner = withCriteria(new PersonBuilder().build(), Map.of(DatingAttribute.AGE, "30"), Map.of());
        assertEquals(COMPATIBLE, new MatchAssessment(owner, candidate).getOutcome());
        assertEquals(COMPATIBLE, new MatchAssessment(candidate, owner).getOutcome());
    }

    private MatchAssessment assess(Map<DatingAttribute, String> preferences,
            Map<DatingAttribute, String> dealbreakers, Person person) {
        return new MatchAssessment(withCriteria(candidate, preferences, dealbreakers), person);
    }

    private Person withCriteria(Person person, Map<DatingAttribute, String> preferences,
            Map<DatingAttribute, String> dealbreakers) {
        return new Person(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(),
                person.getAge(), person.getTags(), person.getSmokingStatus(), person.getGender(),
                person.getReligion().orElse(null), person.getRelationshipGoal().orElse(null),
                new DatingCriteria(preferences, dealbreakers));
    }
}
