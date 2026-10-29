package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Name;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class MatchCommandTest {
    private final Person alex = new PersonBuilder().withName("Alex Yeoh").build();
    private final Person bernice = new PersonBuilder().withName("Bernice Yu").build();

    @Test
    public void execute_names_comparesNamedClients() throws Exception {
        Model model = modelOf(alex, bernice);

        MatchCommandResult result = assertInstanceOf(MatchCommandResult.class,
                new MatchCommand(byName("Bernice Yu"), byName("alex   YEOH")).execute(model));

        assertEquals("Bernice Yu", result.getFirstName());
        assertEquals("Alex Yeoh", result.getSecondName());
        assertEquals(new MatchCommand(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON).execute(modelOf(alex, bernice))
                .getFeedbackToUser(), result.getFeedbackToUser());
    }

    @Test
    public void execute_nameOutsideDisplayedList_resolvesWithoutChangingList() throws Exception {
        Model model = modelOf(alex, bernice);
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Alex")));

        new MatchCommand(new ClientReference.ByIndex(INDEX_FIRST_PERSON), byName("Bernice Yu")).execute(model);

        assertEquals(List.of(alex), model.getFilteredPersonList());
    }

    @Test
    public void execute_exactNamePreferredOverCaseVariant() throws Exception {
        Person lowerAlex = new PersonBuilder().withName("alex yeoh").withPhone("99999999").build();
        Model model = modelOf(alex, lowerAlex, bernice);

        MatchCommandResult result = (MatchCommandResult) new MatchCommand(byName("alex yeoh"), byName("Bernice Yu"))
                .execute(model);

        assertEquals("alex yeoh", result.getFirstName());
    }

    @Test
    public void execute_unknownOrAmbiguousName_throwsCommandException() {
        Person lowerAlex = new PersonBuilder().withName("alex yeoh").withPhone("99999999").build();
        Model model = modelOf(alex, lowerAlex, bernice);

        assertCommandFailure(new MatchCommand(byName("Nobody"), byName("Bernice Yu")), model,
                String.format(ClientReference.MESSAGE_CLIENT_NOT_FOUND, "Nobody"));
        assertCommandFailure(new MatchCommand(byName("ALEX YEOH"), byName("Bernice Yu")), model,
                String.format(ClientReference.MESSAGE_AMBIGUOUS_NAME, "ALEX YEOH"));
    }

    @Test
    public void execute_invalidIndexOrSameClient_throwsCommandException() {
        Model model = modelOf(alex, bernice);
        ClientReference first = new ClientReference.ByIndex(INDEX_FIRST_PERSON);

        assertCommandFailure(new MatchCommand(first, new ClientReference.ByIndex(Index.fromOneBased(3))), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        assertCommandFailure(new MatchCommand(first, byName("alex yeoh")), model, MatchCommand.MESSAGE_SAME_CLIENT);
        assertCommandFailure(new MatchCommand(byName("Alex Yeoh"), byName("Alex Yeoh")), model,
                MatchCommand.MESSAGE_SAME_CLIENT);
    }

    @Test
    public void equals() {
        MatchCommand byIndexes = new MatchCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);
        MatchCommand byNames = new MatchCommand(byName("Alex Yeoh"), byName("Bernice Yu"));

        assertTrue(byIndexes.equals(byIndexes));
        assertTrue(byIndexes.equals(new MatchCommand(new ClientReference.ByIndex(INDEX_FIRST_PERSON),
                new ClientReference.ByIndex(INDEX_SECOND_PERSON))));
        assertTrue(byNames.equals(new MatchCommand(byName("Alex Yeoh"), byName("Bernice Yu"))));
        assertFalse(byIndexes.equals(byNames));
        assertFalse(byNames.equals(new MatchCommand(byName("Bernice Yu"), byName("Alex Yeoh"))));
        assertFalse(byIndexes.equals(null));
    }

    private static ClientReference byName(String name) {
        return new ClientReference.ByName(new Name(name));
    }

    private static Model modelOf(Person... persons) {
        AddressBookBuilder builder = new AddressBookBuilder();
        for (Person person : persons) {
            builder.withPerson(person);
        }
        return new ModelManager(builder.build(), new UserPrefs());
    }
}
