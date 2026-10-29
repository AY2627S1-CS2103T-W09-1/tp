package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

public class RelationshipGoalTest {

    @Test
    public void fromKeyword_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> RelationshipGoal.fromKeyword(null));
    }

    @Test
    public void fromKeyword_unsupportedValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, RelationshipGoal.MESSAGE_CONSTRAINTS, () ->
                RelationshipGoal.fromKeyword(""));
        assertThrows(IllegalArgumentException.class, () -> RelationshipGoal.fromKeyword(" "));
        assertThrows(IllegalArgumentException.class, () -> RelationshipGoal.fromKeyword("Marriage"));
        assertThrows(IllegalArgumentException.class, () -> RelationshipGoal.fromKeyword("lo"));
        assertThrows(IllegalArgumentException.class, () -> RelationshipGoal.fromKeyword("long open"));
        assertThrows(IllegalArgumentException.class, () -> RelationshipGoal.fromKeyword("long-term relationsip "));
    }

    @Test
    public void fromKeyword_fullName_throwsIllegalArgumentException() {
        for (RelationshipGoal goal : RelationshipGoal.values()) {
            assertThrows(IllegalArgumentException.class, () -> RelationshipGoal.fromKeyword(goal.toString()));
        }
    }

    @Test
    public void fromKeyword_keyword_returnsMatchingGoal() {
        assertEquals(RelationshipGoal.LIFE_PARTNER, RelationshipGoal.fromKeyword("life"));
        assertEquals(RelationshipGoal.LONG_TERM, RelationshipGoal.fromKeyword("long"));
        assertEquals(RelationshipGoal.LONG_TERM_OPEN_TO_SHORT, RelationshipGoal.fromKeyword("long-open"));
        assertEquals(RelationshipGoal.SHORT_TERM_OPEN_TO_LONG, RelationshipGoal.fromKeyword("short-open"));
        assertEquals(RelationshipGoal.SHORT_TERM_FUN, RelationshipGoal.fromKeyword("short"));
        assertEquals(RelationshipGoal.FIGURING_OUT, RelationshipGoal.fromKeyword("unsure"));
    }

    @Test
    public void fromKeyword_differentCaseAndSurroundingSpaces_returnsMatchingGoal() {
        assertEquals(RelationshipGoal.SHORT_TERM_FUN, RelationshipGoal.fromKeyword("  SHORT "));
        assertEquals(RelationshipGoal.LONG_TERM_OPEN_TO_SHORT, RelationshipGoal.fromKeyword(" LONG-open "));
    }

    @Test
    public void fromStoredValue_keywordOrFullName_returnsMatchingGoal() {
        for (RelationshipGoal goal : RelationshipGoal.values()) {
            assertEquals(goal, RelationshipGoal.fromStoredValue(goal.toString()));
            assertEquals(goal, RelationshipGoal.fromStoredValue(goal.getKeyword()));
        }
    }

    @Test
    public void fromStoredValue_differentCaseAndSpacing_returnsMatchingGoal() {
        assertEquals(RelationshipGoal.SHORT_TERM_FUN, RelationshipGoal.fromStoredValue("  short-term   FUN "));
        assertEquals(RelationshipGoal.LONG_TERM_OPEN_TO_SHORT, RelationshipGoal.fromStoredValue(" LONG-open "));
    }

    @Test
    public void fromStoredValue_unsupportedValue_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> RelationshipGoal.fromStoredValue(null));
        assertThrows(IllegalArgumentException.class, () -> RelationshipGoal.fromStoredValue("Marriage"));
        assertThrows(IllegalArgumentException.class, () -> RelationshipGoal.fromStoredValue("Long-term"));
    }

    @Test
    public void getKeyword_returnsKeywordAcceptedByFromKeyword() {
        for (RelationshipGoal goal : RelationshipGoal.values()) {
            assertEquals(goal, RelationshipGoal.fromKeyword(goal.getKeyword()));
        }
    }

    @Test
    public void toString_returnsFullGoalName() {
        assertEquals("Long-term relationship, open to short", RelationshipGoal.LONG_TERM_OPEN_TO_SHORT.toString());
    }

    @Test
    public void parseKeywordList_distinctKeywords_returnsGoals() {
        assertEquals(Set.of(RelationshipGoal.LONG_TERM, RelationshipGoal.SHORT_TERM_FUN),
                RelationshipGoal.parseKeywordList(" short , LONG"));
    }

    @Test
    public void parseKeywordList_repeatedOrEmptyKeyword_throwsIllegalArgumentException() {
        String invalidList = RelationshipGoal.MESSAGE_INVALID_LIST;
        for (String input : new String[] {"long,long", "long,LONG", "long,", ",long", "long,,short", ""}) {
            assertThrows(IllegalArgumentException.class, invalidList, () -> RelationshipGoal.parseKeywordList(input));
        }
        String constraints = RelationshipGoal.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalArgumentException.class, constraints, () -> RelationshipGoal.parseKeywordList("long,x"));
    }

    @Test
    public void formatCriterion_roundTripsThroughParseStoredCriterion() {
        Set<RelationshipGoal> goals = Set.of(RelationshipGoal.SHORT_TERM_FUN, RelationshipGoal.LONG_TERM_OPEN_TO_SHORT);
        String criterion = RelationshipGoal.formatCriterion(goals);
        assertEquals("Long-term relationship, open to short or Short-term fun", criterion);
        assertEquals(goals, RelationshipGoal.parseStoredCriterion(criterion));
    }

    @Test
    public void parseStoredCriterion_repeatedGoal_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, RelationshipGoal.MESSAGE_INVALID_LIST, () ->
                RelationshipGoal.parseStoredCriterion("long or Long-term relationship"));
    }

    @Test
    public void formatCriterion_noGoals_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> RelationshipGoal.formatCriterion(Set.of()));
    }
}
