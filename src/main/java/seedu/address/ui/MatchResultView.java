package seedu.address.ui;

import java.util.List;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import seedu.address.logic.commands.MatchCommandResult;
import seedu.address.model.person.MatchAssessment;
import seedu.address.model.person.MatchAssessment.Comparison;

/**
 * Displays a match assessment with a colored summary and one table per direction.
 */
class MatchResultView extends ScrollPane {
    private static final double MIN_TABLE_WIDTH = 600;

    MatchResultView(MatchCommandResult result) {
        getStyleClass().add("match-scroll");
        setFitToWidth(true);
        VBox content = new VBox();
        content.setMinWidth(MIN_TABLE_WIDTH);
        content.getStyleClass().add("match-result");
        setContent(content);
        MatchAssessment assessment = result.getAssessment();
        Label title = createLabel(result.getFirstName() + " ↔ " + result.getSecondName(), "match-title");
        Label outcome = createLabel("Result: " + assessment.getOutcome().getDisplayName(), "match-outcome");
        outcome.getStyleClass().add(switch (assessment.getOutcome()) {
            case COMPATIBLE -> "match-compatible";
            case INCOMPATIBLE -> "match-incompatible";
            case INSUFFICIENT_INFORMATION -> "match-unknown";
        });
        content.getChildren().addAll(title, outcome,
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

    private VBox createDirection(String owner, String candidate, List<Comparison> comparisons) {
        VBox section = new VBox(6);
        section.getChildren().add(createLabel(owner + "'s criteria → " + candidate, "match-direction"));
        if (comparisons.isEmpty()) {
            section.getChildren().add(createLabel("No criteria recorded.", "match-empty"));
            return section;
        }
        GridPane table = new GridPane();
        for (double width : new double[] {15, 17, 25, 25, 18}) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(width);
            table.getColumnConstraints().add(column);
        }
        addRow(table, 0, "Type", "Attribute", "Criterion", "Actual", "Status");
        for (Comparison comparison : comparisons) {
            addRow(table, table.getRowCount(), comparison.getTypeLabel(), comparison.attribute().getDisplayName(),
                    comparison.getCriterionLabel(), comparison.getActualLabel(), comparison.getStatusLabel());
        }
        section.getChildren().add(table);
        return section;
    }

    private void addRow(GridPane table, int row, String... values) {
        for (int column = 0; column < values.length; column++) {
            Label cell = createLabel(values[column], row == 0 ? "match-table-header" : "match-table-cell");
            cell.setMaxHeight(Double.MAX_VALUE);
            table.add(cell, column, row);
        }
    }

    private Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.getStyleClass().add(styleClass);
        return label;
    }
}
