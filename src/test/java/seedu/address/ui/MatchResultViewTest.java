package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import seedu.address.logic.commands.MatchCommandResult;
import seedu.address.model.person.DatingAttribute;
import seedu.address.model.person.MatchAssessment;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MatchResultViewTest {

    @BeforeAll
    public static void initializeToolkit() throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> {
            Platform.setImplicitExit(false);
            return null;
        });
        Platform.startup(startup);
        startup.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void constructor_noCriteria_explainsAbsentCriteria() throws Exception {
        Person first = new PersonBuilder().withName("Alex").build();
        Person second = new PersonBuilder().withName("Bernice").build();

        assertReason(first, second, "Neither client has any recorded preferences or dealbreakers.");
    }

    @Test
    public void constructor_missingAttribute_explainsMissingDetailsInEitherDirection() throws Exception {
        Person first = new PersonBuilder().withName("Alex")
                .withPreference(DatingAttribute.SMOKING, "no").build();
        Person second = new PersonBuilder().withName("Bernice").build();
        String expected = "Some criteria cannot be checked because details are not recorded.";

        assertReason(first, second, expected);
        assertReason(second, first, expected);
    }

    private void assertReason(Person first, Person second, String expected) throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            MatchAssessment assessment = new MatchAssessment(first, second);
            MatchCommandResult result = new MatchCommandResult("", first.getName().fullName,
                    second.getName().fullName, assessment);
            MatchResultView view = new MatchResultView(result);
            VBox content = (VBox) view.getContent();
            VBox summary = (VBox) content.getChildren().getFirst();
            Label outcome = (Label) summary.getChildren().get(1);
            Label reason = (Label) summary.getChildren().get(2);
            assertEquals("Insufficient information", outcome.getText());
            assertEquals(expected, reason.getText());
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }
}
