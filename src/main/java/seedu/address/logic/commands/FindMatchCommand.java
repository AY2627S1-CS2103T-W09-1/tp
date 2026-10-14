package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.MatchAssessment;
import seedu.address.model.person.MatchAssessment.Outcome;
import seedu.address.model.person.MatchAssessment.Status;
import seedu.address.model.person.Person;

/**
 * Lists persons compatible with the person at a displayed index, best matches first.
 * Uses the same assessment as {@link MatchCommand}: pairs assessed as incompatible are hidden,
 * and the rest are ranked by the number of preferences met in both directions.
 */
public class FindMatchCommand extends Command {

    private final Index targetIndex;

    public FindMatchCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person target = lastShownList.get(targetIndex.getZeroBased());
        Map<Person, Integer> scores = new HashMap<>();
        for (Person candidate : model.getAddressBook().getPersonList()) {
            if (candidate.isSamePerson(target)) {
                continue;
            }
            MatchAssessment assessment = new MatchAssessment(target, candidate);
            if (assessment.getOutcome() != Outcome.INCOMPATIBLE) {
                scores.put(candidate, countPreferencesMet(assessment));
            }
        }
        model.updateFilteredPersonList(scores::containsKey,
                Comparator.comparingInt((Person person) -> scores.get(person)).reversed());
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    private static int countPreferencesMet(MatchAssessment assessment) {
        return (int) Stream.concat(assessment.getFirstComparisons().stream(),
                        assessment.getSecondComparisons().stream())
                .filter(comparison -> !comparison.isDealbreaker() && comparison.status() == Status.MET)
                .count();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindMatchCommand otherFindMatchCommand)) {
            return false;
        }

        return targetIndex.equals(otherFindMatchCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
