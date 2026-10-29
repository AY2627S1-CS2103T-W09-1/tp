package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.group.GroupName;

public class ShowGroupCommandTest {

    private final GroupName vip = new GroupName("VIP");

    @Test
    public void constructor_nullName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ShowGroupCommand(null));
    }

    @Test
    public void equals() {
        ShowGroupCommand showVip = new ShowGroupCommand(vip);

        // same object -> returns true
        assertTrue(showVip.equals(showVip));

        // same group name in a different case -> returns true
        assertTrue(showVip.equals(new ShowGroupCommand(new GroupName("vip"))));

        // different types -> returns false
        assertFalse(showVip.equals(1));

        // null -> returns false
        assertFalse(showVip.equals(null));

        // different group name -> returns false
        assertFalse(showVip.equals(new ShowGroupCommand(new GroupName("Other"))));
    }

    @Test
    public void toStringMethod() {
        String expected = ShowGroupCommand.class.getCanonicalName() + "{name=" + vip + "}";
        assertEquals(expected, new ShowGroupCommand(vip).toString());
    }
}
