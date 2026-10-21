package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;

public class ClientGroupTest {
    @Test
    public void groupName_validatesAndNormalizesIdentity() {
        GroupName name = new GroupName("VIP");
        assertEquals(name, new GroupName("vip"));
        assertEquals(name.hashCode(), new GroupName("vIp").hashCode());
        assertEquals("VIP", name.toString());
        assertNotEquals(name, new GroupName("Other"));
        assertNotEquals(name, "VIP");
        assertNotEquals(name, null);
        for (String valid : new String[] {"1", "A_B-2", "a".repeat(30)}) {
            assertEquals(valid, new GroupName(valid).toString());
        }
        for (String invalid : new String[] {"", " ", "VIP Club", " VIP", "VIP ", "-VIP", "_VIP", "é", "a/b",
            "a".repeat(31)}) {
            assertThrows(IllegalArgumentException.class, () -> new GroupName(invalid));
        }
        assertThrows(NullPointerException.class, () -> new GroupName(null));
    }

    @Test
    public void group_isImmutableAndRejectsDuplicateMembers() {
        List<Person> members = new ArrayList<>(List.of(ALICE));
        ClientGroup group = new ClientGroup(new GroupName("VIP"), members);
        members.clear();
        assertEquals(List.of(ALICE), group.getMembers());
        assertTrue(group.contains(ALICE));
        assertFalse(group.contains(BOB));
        assertThrows(UnsupportedOperationException.class, () -> group.getMembers().clear());
        assertThrows(IllegalArgumentException.class, () ->
                new ClientGroup(new GroupName("VIP"), List.of(ALICE, ALICE)));
        assertThrows(NullPointerException.class, () -> new ClientGroup(null, List.of()));
        assertThrows(NullPointerException.class, () -> new ClientGroup(new GroupName("VIP"), null));
        assertEquals(List.of(BOB), group.withMembers(List.of(BOB)).getMembers());
        assertEquals(List.of(ALICE), group.getMembers());
    }

    @Test
    public void equals_comparesNameAndMembership() {
        ClientGroup group = new ClientGroup(new GroupName("VIP"), List.of(ALICE));
        ClientGroup same = new ClientGroup(new GroupName("vip"), List.of(ALICE));
        assertEquals(group, group);
        assertEquals(group, same);
        assertEquals(group.hashCode(), same.hashCode());
        assertNotEquals(group, group.withMembers(List.of()));
        assertNotEquals(group, new ClientGroup(new GroupName("Other"), List.of(ALICE)));
        assertNotEquals(group, null);
        assertNotEquals(group, "VIP");
    }
}
