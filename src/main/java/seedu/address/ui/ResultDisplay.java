package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.MatchCommandResult;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private TextArea resultDisplay;

    @FXML
    private StackPane matchDisplay;

    public ResultDisplay() {
        super(FXML);
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        showMatch(false);
        resultDisplay.setText(feedbackToUser);
        resultDisplay.setPrefRowCount(Math.max(3, resultDisplay.getParagraphs().size()));
    }

    /**
     * Shows structured match results or the ordinary feedback for other commands.
     */
    public void setCommandResult(CommandResult result) {
        requireNonNull(result);
        if (result instanceof MatchCommandResult matchResult) {
            matchDisplay.getChildren().setAll(new MatchResultView(matchResult));
            showMatch(true);
        } else {
            setFeedbackToUser(result.getFeedbackToUser());
        }
    }

    private void showMatch(boolean isMatch) {
        resultDisplay.setVisible(!isMatch);
        resultDisplay.setManaged(!isMatch);
        matchDisplay.setVisible(isMatch);
        matchDisplay.setManaged(isMatch);
    }

}
