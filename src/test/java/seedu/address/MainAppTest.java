package seedu.address;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.Model;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

class MainAppTest {

    @TempDir
    Path temporaryFolder;

    @Test
    void initModelManager_nullPersonOrTagEntry_startsWithEmptyAddressBook() throws Exception {
        Path addressBookFile = temporaryFolder.resolve("addressbook.json");
        String personWithTags = "{\"name\":\"Alice\",\"phone\":\"12345678\","
                + "\"email\":\"alice@example.com\",\"address\":\"Home\",\"age\":\"25\","
                + "\"tags\":%s}";
        for (String contents : new String[] {
            "{\"persons\":[null]}",
            "{\"persons\":[" + String.format(personWithTags, "[null]") + "]}"}) {
            Files.writeString(addressBookFile, contents);
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(addressBookFile),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));

            Model model = new MainApp().initModelManager(storage, new UserPrefs());

            assertTrue(model.getAddressBook().getPersonList().isEmpty());
        }
    }
}
