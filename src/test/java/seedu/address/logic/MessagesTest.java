package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_person_includesAge() {
        assertTrue(Messages.format(new PersonBuilder().withAge(37).build()).contains("; Age: 37;"));
    }
}
