package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Address;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/** Integration tests for mandatory address validation through the command interface. */
public class AddressValidationIntegrationTest {
    private static final String ADD_CLIENT = "add n/Alpha p/81234567 e/alpha@example.com a/10 Orchard Road age/25";

    @TempDir
    Path temporaryFolder;

    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    @Test
    public void edit_unicodeWhitespaceOnlyAddress_rejectsAndPreservesSavedAddress() throws Exception {
        logic.execute(ADD_CLIENT);
        AddressBook before = new AddressBook(model.getAddressBook());

        assertThrows(ParseException.class, Address.MESSAGE_CONSTRAINTS, () -> logic.execute("edit 1 a/\u2003"));

        assertEquals(before, model.getAddressBook());
        assertEquals("10 Orchard Road", model.getFilteredPersonList().get(0).getAddress().value);
        assertEquals(before, storage.readAddressBook().orElseThrow());
    }
}
