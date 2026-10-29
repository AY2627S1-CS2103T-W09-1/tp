package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * Identifies one client, either by their index in the displayed list or by their name.
 */
public sealed interface ClientReference permits ClientReference.ByIndex, ClientReference.ByName {

    String MESSAGE_CLIENT_NOT_FOUND = "No client is named %1$s.";
    String MESSAGE_AMBIGUOUS_NAME = "Several clients are named %1$s. Use their list indexes instead.";

    /**
     * Returns the client this reference identifies in {@code model}.
     *
     * @throws CommandException if no single client matches the reference.
     */
    Person resolve(Model model) throws CommandException;

    /**
     * Refers to the client at an index of the displayed list.
     */
    record ByIndex(Index index) implements ClientReference {
        public ByIndex {
            requireNonNull(index);
        }

        @Override
        public Person resolve(Model model) throws CommandException {
            requireNonNull(model);
            List<Person> displayed = model.getFilteredPersonList();
            if (index.getZeroBased() >= displayed.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            return displayed.get(index.getZeroBased());
        }
    }

    /**
     * Refers to a client by name, searching every client and not only those displayed.
     * An exact name is preferred. Otherwise, the name may differ in letter case and spacing if only one
     * client matches that way.
     */
    record ByName(Name name) implements ClientReference {
        public ByName {
            requireNonNull(name);
        }

        @Override
        public Person resolve(Model model) throws CommandException {
            requireNonNull(model);
            List<Person> clients = model.getAddressBook().getPersonList();
            for (Person client : clients) {
                if (client.getName().equals(name)) {
                    return client;
                }
            }
            List<Person> looseMatches = clients.stream()
                    .filter(client -> normalize(client.getName()).equals(normalize(name)))
                    .toList();
            if (looseMatches.isEmpty()) {
                throw new CommandException(String.format(MESSAGE_CLIENT_NOT_FOUND, name));
            }
            if (looseMatches.size() > 1) {
                throw new CommandException(String.format(MESSAGE_AMBIGUOUS_NAME, name));
            }
            return looseMatches.get(0);
        }

        private static String normalize(Name name) {
            return name.fullName.replaceAll("\\s+", " ").toLowerCase();
        }
    }
}
