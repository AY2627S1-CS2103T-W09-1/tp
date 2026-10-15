package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.DatingAttribute;
import seedu.address.model.person.Person;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class FindMatchCommandTest {

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index outOfBounds = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new FindMatchCommand(outOfBounds), model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndex_listsEveryoneElse() throws Exception {
        Person target = new PersonBuilder().withName("Target").build();
        Person first = new PersonBuilder().withName("First").build();
        Person second = new PersonBuilder().withName("Second").build();
        Model model = new ModelManager(new AddressBookBuilder().withPerson(first).withPerson(target)
                .withPerson(second).build(), new UserPrefs());

        CommandResult result = new FindMatchCommand(INDEX_SECOND_PERSON).execute(model);

        assertEquals(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 2), result.getFeedbackToUser());
        assertEquals(List.of(first, second), model.getFilteredPersonList());
    }

    @Test
    public void execute_criteriaRecorded_hidesIncompatibleAndRanksByPreferencesMet() throws Exception {
        Person target = new PersonBuilder().withName("Target").withRelationshipGoal("short")
                .withPreference(DatingAttribute.RELIGION, "Buddhism")
                .withDealbreaker(DatingAttribute.SMOKING, "yes").build();
        Person smoker = new PersonBuilder().withName("Smoker").withSmokingStatus("yes").build();
        Person excludesShortTerm = new PersonBuilder().withName("Excludes Short Term")
                .withDealbreaker(DatingAttribute.RELATIONSHIP_GOAL, "Short-term fun").build();
        Person unknown = new PersonBuilder().withName("Unknown").build();
        Person buddhist = new PersonBuilder().withName("Buddhist").withSmokingStatus("no")
                .withReligion("Buddhism").build();
        Model model = new ModelManager(new AddressBookBuilder().withPerson(target).withPerson(smoker)
                .withPerson(excludesShortTerm).withPerson(unknown).withPerson(buddhist).build(), new UserPrefs());

        CommandResult result = new FindMatchCommand(INDEX_FIRST_PERSON).execute(model);

        assertEquals(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 2), result.getFeedbackToUser());
        assertEquals(List.of(buddhist, unknown), model.getFilteredPersonList());
    }

    @Test
    public void equals() {
        FindMatchCommand matchFirst = new FindMatchCommand(INDEX_FIRST_PERSON);

        assertTrue(matchFirst.equals(matchFirst));
        assertTrue(matchFirst.equals(new FindMatchCommand(INDEX_FIRST_PERSON)));
        assertFalse(matchFirst.equals(null));
        assertFalse(matchFirst.equals(1));
        assertFalse(matchFirst.equals(new FindMatchCommand(INDEX_SECOND_PERSON)));
    }

    @Test
    public void toStringMethod() {
        FindMatchCommand command = new FindMatchCommand(INDEX_FIRST_PERSON);
        String expected = FindMatchCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON + "}";
        assertEquals(expected, command.toString());
    }
}
