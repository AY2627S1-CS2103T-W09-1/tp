package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.MatchAssessment;
import seedu.address.model.person.MatchAssessment.Comparison;
import seedu.address.model.person.Person;

/**
 * Assesses two clients from the displayed list without recording a pairing or changing the list.
 */
public class MatchCommand extends Command {
    public static final String COMMAND_WORD = "match";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Compares two different clients in the displayed list.\n"
            + "Parameters: INDEX_A INDEX_B (must be distinct positive integers)\n"
            + "Example: " + COMMAND_WORD + " 1 3";
    public static final String MESSAGE_SAME_CLIENT = "Choose two different clients.\n" + MESSAGE_USAGE;

    private final Index firstIndex;
    private final Index secondIndex;

    /**
     * Creates a comparison of the clients at the given displayed indexes.
     */
    public MatchCommand(Index firstIndex, Index secondIndex) {
        requireAllNonNull(firstIndex, secondIndex);
        this.firstIndex = firstIndex;
        this.secondIndex = secondIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (firstIndex.equals(secondIndex)) {
            throw new CommandException(MESSAGE_SAME_CLIENT);
        }
        List<Person> displayed = model.getFilteredPersonList();
        if (firstIndex.getZeroBased() >= displayed.size() || secondIndex.getZeroBased() >= displayed.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person first = displayed.get(firstIndex.getZeroBased());
        Person second = displayed.get(secondIndex.getZeroBased());
        MatchAssessment assessment = new MatchAssessment(first, second);
        String outcome = switch (assessment.getOutcome()) {
            case COMPATIBLE -> "Compatible with recorded dealbreakers";
            case INCOMPATIBLE -> "Incompatible";
            case INSUFFICIENT_INFORMATION -> "Insufficient information";
        };
        return new CommandResult(first.getName() + " <-> " + second.getName() + "\nResult: " + outcome
                + formatDirection(first, second, assessment.getFirstComparisons())
                + formatDirection(second, first, assessment.getSecondComparisons()));
    }

    private static String formatDirection(Person owner, Person candidate, List<Comparison> comparisons) {
        String details = comparisons.isEmpty() ? "No criteria recorded."
                : comparisons.stream().map(MatchCommand::formatComparison).collect(Collectors.joining("\n"));
        return "\n\n" + owner.getName() + "'s criteria -> " + candidate.getName() + "\n" + details;
    }

    private static String formatComparison(Comparison comparison) {
        String kind = comparison.isDealbreaker() ? "Dealbreaker: exclude " : "Preference: ";
        String status = switch (comparison.status()) {
            case MET -> "Met";
            case UNMET -> comparison.isDealbreaker() ? "Unmet (violated)" : "Unmet";
            case UNKNOWN -> "Unknown";
        };
        return status + " - " + kind + comparison.attribute().getDisplayName() + " " + comparison.criterion()
                + "; actual: " + comparison.actual().orElse("Not specified");
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof MatchCommand otherCommand
                && firstIndex.equals(otherCommand.firstIndex) && secondIndex.equals(otherCommand.secondIndex));
    }
}
