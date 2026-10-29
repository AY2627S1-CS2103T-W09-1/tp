package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.MatchAssessment;
import seedu.address.model.person.MatchAssessment.Comparison;
import seedu.address.model.person.Person;

/**
 * Assesses two clients, each given by displayed index or by name, without recording a pairing or changing
 * the list.
 */
public class MatchCommand extends Command {
    public static final String COMMAND_WORD = "match";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Compares two different clients, given by displayed index or by name.\n"
            + "Parameters: INDEX_A INDEX_B, n/NAME_A n/NAME_B, or INDEX_A n/NAME_B "
            + "(indexes are positive integers and come before names)\n"
            + "Examples: " + COMMAND_WORD + " 1 3, " + COMMAND_WORD + " n/Alex Yeoh n/Bernice Yu";
    public static final String MESSAGE_SAME_CLIENT = "Choose two different clients.\n" + MESSAGE_USAGE;

    private final ClientReference firstClient;
    private final ClientReference secondClient;

    /**
     * Creates a comparison of the two referenced clients.
     */
    public MatchCommand(ClientReference firstClient, ClientReference secondClient) {
        requireAllNonNull(firstClient, secondClient);
        this.firstClient = firstClient;
        this.secondClient = secondClient;
    }

    /**
     * Creates a comparison of the clients at the given displayed indexes.
     */
    public MatchCommand(Index firstIndex, Index secondIndex) {
        this(new ClientReference.ByIndex(firstIndex), new ClientReference.ByIndex(secondIndex));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (firstClient.equals(secondClient)) {
            throw new CommandException(MESSAGE_SAME_CLIENT);
        }
        Person first = firstClient.resolve(model);
        Person second = secondClient.resolve(model);
        if (first.isSamePerson(second)) {
            throw new CommandException(MESSAGE_SAME_CLIENT);
        }
        MatchAssessment assessment = new MatchAssessment(first, second);
        String feedback = first.getName() + " <-> " + second.getName()
                + "\nResult: " + assessment.getOutcome().getDisplayName()
                + formatDirection(first, second, assessment.getFirstComparisons())
                + formatDirection(second, first, assessment.getSecondComparisons());
        return new MatchCommandResult(feedback, first.getName().fullName, second.getName().fullName, assessment);
    }

    private static String formatDirection(Person owner, Person candidate, List<Comparison> comparisons) {
        String details = comparisons.isEmpty() ? "No criteria recorded."
                : comparisons.stream().map(MatchCommand::formatComparison).collect(Collectors.joining("\n"));
        return "\n\n" + owner.getName() + "'s criteria -> " + candidate.getName() + "\n" + details;
    }

    private static String formatComparison(Comparison comparison) {
        return comparison.getStatusLabel() + " - " + comparison.getTypeLabel() + ": "
                + comparison.attribute().getDisplayName() + " " + comparison.getCriterionLabel()
                + "; Actual: " + comparison.getActualLabel();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof MatchCommand otherCommand
                && firstClient.equals(otherCommand.firstClient) && secondClient.equals(otherCommand.secondClient));
    }
}
