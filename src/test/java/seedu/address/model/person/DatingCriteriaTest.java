package seedu.address.model.person;

import static java.util.Map.of;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class DatingCriteriaTest {

    @Test
    public void constructor_sameNonAgeCriterionInBothBuckets_throwsIllegalArgumentException() {
        for (DatingAttribute attribute : new DatingAttribute[] {
            DatingAttribute.GENDER, DatingAttribute.SMOKING, DatingAttribute.RELIGION,
            DatingAttribute.RELATIONSHIP_GOAL}) {
            String value = switch (attribute) {
                case GENDER -> "m";
                case SMOKING -> "no";
                case RELIGION -> "Islam";
                case RELATIONSHIP_GOAL -> "long";
                case AGE -> throw new AssertionError("Age must be tested as a range");
            };
            String message = attribute == DatingAttribute.RELIGION
                    ? DatingCriteria.MESSAGE_CONFLICTING_RELIGION : DatingCriteria.MESSAGE_CONFLICTING_CRITERIA;
            assertThrows(IllegalArgumentException.class, message, () -> new DatingCriteria(
                    of(attribute, value), of(attribute, value)));
        }
    }

    @Test
    public void constructor_overlappingAgeRanges_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, DatingCriteria.MESSAGE_OVERLAPPING_AGE_RANGES, () ->
                new DatingCriteria(of(DatingAttribute.AGE, "20-30"), of(DatingAttribute.AGE, "25-35")));
        assertThrows(IllegalArgumentException.class, DatingCriteria.MESSAGE_OVERLAPPING_AGE_RANGES, () ->
                new DatingCriteria(of(DatingAttribute.AGE, "25"), of(DatingAttribute.AGE, "25-35")));
    }

    @Test
    public void constructor_nonOverlappingAgeRanges_succeeds() {
        new DatingCriteria(of(DatingAttribute.AGE, "18-24"), of(DatingAttribute.AGE, "25-35"));
    }
}
