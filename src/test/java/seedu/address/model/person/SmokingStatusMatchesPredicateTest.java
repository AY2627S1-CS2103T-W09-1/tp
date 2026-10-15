package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class SmokingStatusMatchesPredicateTest {
    @Test
    public void test_matchesOnlyTheRecordedStatus() {
        for (String requested : new String[] {"yes", "no"}) {
            SmokingStatusMatchesPredicate predicate = new SmokingStatusMatchesPredicate(new SmokingStatus(requested));
            for (String recorded : new String[] {"yes", "no", ""}) {
                assertEquals(requested.equals(recorded),
                        predicate.test(new PersonBuilder().withSmokingStatus(recorded).build()));
            }
        }
    }

    @Test
    public void constructor_nullOrUnspecifiedStatus_throwsException() {
        assertThrows(NullPointerException.class, () -> new SmokingStatusMatchesPredicate(null));
        assertThrows(IllegalArgumentException.class, () ->
                new SmokingStatusMatchesPredicate(SmokingStatus.UNSPECIFIED));
    }

    @Test
    public void equals_comparesNormalizedStatus() {
        SmokingStatusMatchesPredicate predicate = new SmokingStatusMatchesPredicate(new SmokingStatus("yes"));
        SmokingStatusMatchesPredicate same = new SmokingStatusMatchesPredicate(new SmokingStatus("YES"));
        assertEquals(predicate, predicate);
        assertEquals(predicate, same);
        assertEquals(predicate.hashCode(), same.hashCode());
        assertNotEquals(predicate, new SmokingStatusMatchesPredicate(new SmokingStatus("no")));
        assertNotEquals(predicate, null);
        assertNotEquals(predicate, "yes");
        assertEquals(SmokingStatusMatchesPredicate.class.getCanonicalName() + "{smokingStatus=yes}",
                predicate.toString());
    }
}
