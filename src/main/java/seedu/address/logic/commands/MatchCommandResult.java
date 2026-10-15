package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import seedu.address.model.person.MatchAssessment;

/**
 * Supplies structured match details alongside the plain-text command feedback.
 */
public class MatchCommandResult extends CommandResult {
    private final String firstName;
    private final String secondName;
    private final MatchAssessment assessment;

    /**
     * Creates a result that the UI can display without parsing feedback text.
     */
    public MatchCommandResult(String feedback, String firstName, String secondName, MatchAssessment assessment) {
        super(feedback);
        requireAllNonNull(firstName, secondName, assessment);
        this.firstName = firstName;
        this.secondName = secondName;
        this.assessment = assessment;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public MatchAssessment getAssessment() {
        return assessment;
    }
}
