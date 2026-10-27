package seedu.address.ui;

import java.util.List;
import java.util.Locale;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import seedu.address.logic.commands.MatchCommandResult;
import seedu.address.model.person.MatchAssessment;
import seedu.address.model.person.MatchAssessment.Comparison;

/**
 * Displays a match assessment with an overall result and one table per direction.
 */
class MatchResultView extends ScrollPane {
    private static final double MIN_TABLE_WIDTH = 480;

    MatchResultView(MatchCommandResult result) {
        getStyleClass().add("match-scroll");
        setFitToWidth(true);
        VBox content = new VBox();
        content.setMinWidth(MIN_TABLE_WIDTH);
        content.getStyleClass().add("match-result");
        setContent(content);
        MatchAssessment assessment = result.getAssessment();
        content.getChildren().addAll(createSummary(result),
                createDirection(result.getFirstName(), result.getSecondName(), assessment.getFirstComparisons()),
                createDirection(result.getSecondName(), result.getFirstName(), assessment.getSecondComparisons()));
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    @Override
    protected double computePrefHeight(double width) {
        double contentWidth = width < 0 ? -1
                : Math.max(MIN_TABLE_WIDTH, width - snappedLeftInset() - snappedRightInset());
        return getContent().prefHeight(contentWidth)
                + snappedTopInset() + snappedBottomInset();
    }

    private VBox createSummary(MatchCommandResult result) {
        MatchAssessment.Outcome outcome = result.getAssessment().getOutcome();
        Label outcomeLabel = createLabel(outcome.getDisplayName(), "match-outcome");
        outcomeLabel.getStyleClass().add(switch (outcome) {
            case COMPATIBLE -> "match-compatible";
            case INCOMPATIBLE -> "match-incompatible";
            case INSUFFICIENT_INFORMATION -> "match-unknown";
        });
        String reason = switch (outcome) {
            case COMPATIBLE -> "No recorded dealbreaker is violated.";
            case INCOMPATIBLE -> "At least one dealbreaker is violated.";
            case INSUFFICIENT_INFORMATION -> "Some criteria cannot be checked because details are not recorded.";
        };
        VBox summary = new VBox(2,
                createLabel("Overall result · " + result.getFirstName() + " ↔ " + result.getSecondName(),
                        "match-caption"),
                outcomeLabel,
                createLabel(reason, "match-reason"));
        summary.getStyleClass().add("match-summary");
        return summary;
    }

    private VBox createDirection(String owner, String candidate, List<Comparison> comparisons) {
        VBox section = new VBox(4);
        section.getChildren().add(createLabel(owner + "'s criteria, checked against " + candidate,
                "match-direction"));
        if (comparisons.isEmpty()) {
            section.getChildren().add(createLabel("No criteria recorded.", "match-empty"));
            return section;
        }
        GridPane table = new GridPane();
        for (double width : new double[] {45, 32, 23}) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(width);
            table.getColumnConstraints().add(column);
        }
        table.add(createLabel("Requirement", "match-table-header"), 0, 0);
        table.add(createLabel(candidate, "match-table-header"), 1, 0);
        table.add(createLabel("Assessment", "match-table-header"), 2, 0);
        for (Comparison comparison : comparisons) {
            int row = table.getRowCount();
            table.add(createRequirement(comparison), 0, row);
            table.add(createLabel(comparison.attribute().getDisplayName() + ": " + comparison.getActualLabel(),
                    "match-table-cell"), 1, row);
            Label status = createLabel(comparison.getStatusLabel(), "match-table-cell");
            status.getStyleClass().add(comparison.status() == MatchAssessment.Status.UNMET
                    ? "match-status-failed" : "match-status");
            table.add(status, 2, row);
        }
        section.getChildren().add(table);
        return section;
    }

    private TextFlow createRequirement(Comparison comparison) {
        Text type = new Text(comparison.getTypeLabel().toLowerCase(Locale.ROOT) + " — ");
        type.getStyleClass().add("match-requirement-type");
        Text criterion = new Text((comparison.isDealbreaker() ? "excludes " : "")
                + comparison.attribute().getDisplayName().toLowerCase(Locale.ROOT) + ": " + comparison.criterion());
        criterion.getStyleClass().add("match-requirement");
        TextFlow requirement = new TextFlow(type, criterion);
        requirement.getStyleClass().add("match-table-cell");
        requirement.setMaxHeight(Double.MAX_VALUE);
        return requirement;
    }

    private Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setMinHeight(Region.USE_PREF_SIZE);
        label.getStyleClass().add(styleClass);
        return label;
    }
}
