package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RelationshipGoalTest {

    @Test
    public void fromString_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> RelationshipGoal.fromString(null));
    }

    @Test
    public void fromString_unsupportedValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, RelationshipGoal.MESSAGE_CONSTRAINTS, () ->
                RelationshipGoal.fromString(""));
        assertThrows(IllegalArgumentException.class, () -> RelationshipGoal.fromString(" "));
        assertThrows(IllegalArgumentException.class, () -> RelationshipGoal.fromString("Marriage"));
        assertThrows(IllegalArgumentException.class, () -> RelationshipGoal.fromString("Long term relationship"));
        assertThrows(IllegalArgumentException.class, () -> RelationshipGoal.fromString("Long-term"));
    }

    @Test
    public void fromString_keyword_returnsMatchingGoal() {
        assertEquals(RelationshipGoal.LIFE_PARTNER, RelationshipGoal.fromString("life"));
        assertEquals(RelationshipGoal.LONG_TERM, RelationshipGoal.fromString("long"));
        assertEquals(RelationshipGoal.LONG_TERM_OPEN_TO_SHORT, RelationshipGoal.fromString("long-open"));
        assertEquals(RelationshipGoal.SHORT_TERM_OPEN_TO_LONG, RelationshipGoal.fromString("short-open"));
        assertEquals(RelationshipGoal.SHORT_TERM_FUN, RelationshipGoal.fromString("short"));
        assertEquals(RelationshipGoal.FIGURING_OUT, RelationshipGoal.fromString("unsure"));
    }

    @Test
    public void fromString_fullName_returnsMatchingGoal() {
        for (RelationshipGoal goal : RelationshipGoal.values()) {
            assertEquals(goal, RelationshipGoal.fromString(goal.toString()));
        }
    }

    @Test
    public void fromString_differentCaseAndSpacing_returnsMatchingGoal() {
        assertEquals(RelationshipGoal.SHORT_TERM_FUN, RelationshipGoal.fromString("  short-term   FUN "));
        assertEquals(RelationshipGoal.LONG_TERM_OPEN_TO_SHORT, RelationshipGoal.fromString(" LONG-open "));
    }

    @Test
    public void toString_returnsFullGoalName() {
        assertEquals("Long-term relationship, open to short", RelationshipGoal.LONG_TERM_OPEN_TO_SHORT.toString());
    }
}
