package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GroupCommand.Operation;
import seedu.address.model.group.GroupName;

public class GroupCommandTest {

    private final GroupName vip = new GroupName("VIP");

    @Test
    public void constructor_invalidIndexes_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> new GroupCommand(null, Operation.ADD,
                List.of(INDEX_FIRST_PERSON)));
        assertThrows(IllegalArgumentException.class, () -> new GroupCommand(vip, Operation.ADD, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new GroupCommand(vip, Operation.REMOVE,
                List.of(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON)));
        assertThrows(IllegalArgumentException.class, () -> new GroupCommand(vip, Operation.MATCHES,
                List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)));
    }

    @Test
    public void equals() {
        GroupCommand add = new GroupCommand(vip, Operation.ADD, List.of(INDEX_FIRST_PERSON));

        // same object -> returns true
        assertTrue(add.equals(add));

        // same values, group name in a different case -> returns true
        assertTrue(add.equals(new GroupCommand(new GroupName("vip"), Operation.ADD, List.of(INDEX_FIRST_PERSON))));

        // different types -> returns false
        assertFalse(add.equals(1));
        assertFalse(add.equals(new ShowGroupCommand(vip)));

        // null -> returns false
        assertFalse(add.equals(null));

        // different operation, indexes or group name -> returns false
        assertFalse(add.equals(new GroupCommand(vip, Operation.REMOVE, List.of(INDEX_FIRST_PERSON))));
        assertFalse(add.equals(new GroupCommand(vip, Operation.ADD, List.of(INDEX_SECOND_PERSON))));
        assertFalse(add.equals(new GroupCommand(new GroupName("Other"), Operation.ADD, List.of(INDEX_FIRST_PERSON))));
    }

    @Test
    public void toStringMethod() {
        List<Index> indexes = List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);
        GroupCommand command = new GroupCommand(vip, Operation.REMOVE, indexes);
        String expected = GroupCommand.class.getCanonicalName() + "{name=" + vip + ", operation=" + Operation.REMOVE
                + ", indexes=" + indexes + "}";
        assertEquals(expected, command.toString());
    }
}
