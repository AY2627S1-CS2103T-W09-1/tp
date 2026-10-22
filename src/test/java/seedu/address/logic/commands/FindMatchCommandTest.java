package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.DatingAttribute;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.EditPersonDescriptorBuilder;
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
    public void execute_followedByList_updatesObservers() throws Exception {
        assertFollowUpUpdatesList(new ListCommand(), getTypicalPersons());
    }

    @Test
    public void execute_followedByFind_updatesObservers() throws Exception {
        FindCommand command = new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Carl")));
        assertFollowUpUpdatesList(command, List.of(ALICE, CARL));
    }

    @Test
    public void execute_followedByFindMatch_updatesObservers() throws Exception {
        List<Person> expectedPersons = getTypicalPersons();
        expectedPersons.remove(BENSON);
        assertFollowUpUpdatesList(new FindMatchCommand(INDEX_FIRST_PERSON), expectedPersons);
    }

    @Test
    public void execute_followedByAdd_updatesObservers() throws Exception {
        List<Person> expectedPersons = getTypicalPersons();
        expectedPersons.add(HOON);
        assertFollowUpUpdatesList(new AddCommand(HOON), expectedPersons);
    }

    @Test
    public void execute_followedByEdit_updatesObservers() throws Exception {
        EditCommand command = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withPhone("12345678").build());
        List<Person> expectedPersons = getTypicalPersons();
        expectedPersons.set(1, new PersonBuilder(BENSON).withPhone("12345678").build());
        assertFollowUpUpdatesList(command, expectedPersons);
    }

    private void assertFollowUpUpdatesList(Command command, List<Person> expectedPersons) throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        ObservableList<Person> observedPersons = FXCollections.observableArrayList();
        Bindings.bindContent(observedPersons, model.getFilteredPersonList());

        // JavaFX reports listener exceptions to this handler instead of throwing them from execute().
        List<Throwable> listenerExceptions = new ArrayList<>();
        Thread thread = Thread.currentThread();
        Thread.UncaughtExceptionHandler originalHandler = thread.getUncaughtExceptionHandler();
        thread.setUncaughtExceptionHandler((unused, exception) -> listenerExceptions.add(exception));
        try {
            new FindMatchCommand(INDEX_FIRST_PERSON).execute(model);
            command.execute(model);

            assertEquals(List.of(), listenerExceptions);
            assertEquals(expectedPersons, model.getFilteredPersonList());
            assertEquals(expectedPersons, observedPersons);
        } finally {
            thread.setUncaughtExceptionHandler(originalHandler);
            Bindings.unbindContent(observedPersons, model.getFilteredPersonList());
        }
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
