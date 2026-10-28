package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.person.DatingAttribute;
import seedu.address.model.person.Person;

public class ReligionCriteriaStorageTest {
    private static final String CLIENT = "{\"name\":\"Religion Demo\",\"phone\":\"91234567\","
            + "\"email\":\"demo@example.com\",\"address\":\"Test Lane\",\"age\":\"25\","
            + "\"smokingStatus\":\"no\",\"gender\":\"nb\","
            + "\"tags\":[],\"religion\":\"Jainism\"";

    @TempDir
    Path temporaryFolder;

    @Test
    public void roundTrip_religionCriteria_preservesIndependentOwnReligion() throws Exception {
        String json = CLIENT + ",\"preferences\":{\"religion\":\" bAhA'i   faith \"},"
                + "\"dealbreakers\":{\"religion\":\"ISLAM\"}}";
        Person original = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType();
        assertEquals("Jainism", original.getReligion().orElseThrow().toString());
        assertEquals(Map.of(DatingAttribute.RELIGION, "Baha'i Faith"),
                original.getDatingCriteria().getPreferences());
        assertEquals(Map.of(DatingAttribute.RELIGION, "Islam"), original.getDatingCriteria().getDealbreakers());
        String saved = JsonUtil.toJsonString(new JsonAdaptedPerson(original));
        assertEquals(original, JsonUtil.fromJsonString(saved, JsonAdaptedPerson.class).toModelType());
    }

    @Test
    public void toModelType_missingCriteria_loadsEmptyMaps() throws Exception {
        Person person = JsonUtil.fromJsonString(CLIENT + "}", JsonAdaptedPerson.class).toModelType();
        assertEquals("Jainism", person.getReligion().orElseThrow().toString());
        assertTrue(person.getDatingCriteria().isEmpty());
    }

    @Test
    public void toModelType_conflictingReligionCriteria_throwsIllegalValueException() throws Exception {
        JsonAdaptedPerson person = JsonUtil.fromJsonString(CLIENT
                + ",\"preferences\":{\"religion\":\"Baha'i Faith\"},"
                + "\"dealbreakers\":{\"religion\":\" bAhA'i   FAITH \"}}", JsonAdaptedPerson.class);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void readAddressBook_invalidReligionCriteria_throwsDataLoadingException() throws Exception {
        Path data = temporaryFolder.resolve("addressbook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(data);
        for (String bucket : new String[] {"preferences", "dealbreakers"}) {
            for (String value : new String[] {"null", "\"\"", "\"Other\""}) {
                String json = "{\"persons\":[" + CLIENT + ",\"" + bucket + "\":{\"religion\":" + value + "}}]}";
                FileUtil.writeToFile(data, json);
                assertThrows(DataLoadingException.class, storage::readAddressBook);
                assertEquals(json, FileUtil.readFromFile(data));
            }
        }
    }
}
