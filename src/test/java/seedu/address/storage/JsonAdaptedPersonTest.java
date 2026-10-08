package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.person.Address;
import seedu.address.model.person.Age;
import seedu.address.model.person.Email;
import seedu.address.model.person.Gender;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.RelationshipGoal;
import seedu.address.model.person.Religion;
import seedu.address.model.person.SmokingStatus;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_AGE = "17";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final String VALID_AGE = BENSON.getAge().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    /** New religion fields survive JSON serialization while legacy fields remain readable. */
    @Test
    public void jsonRoundTrip_religionFieldsPreserved() throws Exception {
        var original = new PersonBuilder(BENSON).withReligion("Jainism")
                .withPreferredReligion("Buddhism").withRequiredReligion("Buddhism")
                .withExcludedReligions("Islam", "No religion").build();
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(original));
        JsonAdaptedPerson restored = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class);
        assertEquals(original, restored.toModelType());
    }

    /** Invalid persisted religion values are rejected instead of silently becoming unknown. */
    @Test
    public void toModelType_invalidReligion_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, "Other: Jainism", null, null, List.of());
        assertThrows(IllegalValueException.class, Religion.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_smokingStatus_roundTripsAllStates() throws Exception {
        for (String status : new String[] {"yes", "no", ""}) {
            Person person = new PersonBuilder(BENSON).withSmokingStatus(status).build();
            String json = JsonUtil.toJsonString(new JsonAdaptedPerson(person));
            assertEquals(person, JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType());
        }
    }

    @Test
    public void toModelType_legacyJsonWithoutSmokingStatus_preservesPerson() throws Exception {
        String json = "{\"name\":\"Benson\",\"phone\":\"12345678\",\"email\":\"benson@example.com\","
                + "\"address\":\"Home\",\"age\":\"25\",\"tags\":[]}";
        Person person = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType();
        assertEquals(new PersonBuilder().withName("Benson").withPhone("12345678")
                .withEmail("benson@example.com").withAddress("Home").withTags().build(), person);
        assertEquals(SmokingStatus.UNSPECIFIED, person.getSmokingStatus());
    }

    @Test
    public void toModelType_invalidSmokingStatus_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_AGE, VALID_TAGS, "sometimes", null);
        assertThrows(IllegalValueException.class, SmokingStatus.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_mixedCaseSmokingStatus_normalizesValue() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_AGE, VALID_TAGS, "YeS", null);
        assertEquals(new SmokingStatus("yes"), person.toModelType().getSmokingStatus());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_AGE,
                        VALID_TAGS, null, null);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(null, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_AGE,
                VALID_TAGS, null, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_AGE,
                        VALID_TAGS, null, null);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, null, VALID_EMAIL, VALID_ADDRESS, VALID_AGE,
                VALID_TAGS, null, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS, VALID_AGE,
                        VALID_TAGS, null, null);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, VALID_ADDRESS, VALID_AGE,
                VALID_TAGS, null, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS, VALID_AGE,
                        VALID_TAGS, null, null);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, null, VALID_AGE,
                VALID_TAGS, null, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_AGE,
                        invalidTags, null, null);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_invalidGender_throwsIllegalValueException() {
        for (String value : new String[] {"other", "m,w"}) {
            JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                    VALID_ADDRESS, VALID_AGE, VALID_TAGS, null, value);
            assertThrows(IllegalValueException.class, Gender.MESSAGE_CONSTRAINTS, person::toModelType);
        }
    }

    @Test
    public void fromJson_legacyAndOptionalGenderValues_loadSuccessfully() throws Exception {
        String json = "{\"name\":\"Benson Meier\",\"phone\":\"98765432\",\"email\":\"benson@example.com\","
                + "\"address\":\"New Road\",\"age\":\"25\",\"tags\":[]%s}";
        String[] fields = {"", ",\"gender\":null", ",\"gender\":\"\"", ",\"gender\":\" Nb \""};
        String[] expectedValues = {"", "", "", "nb"};
        for (int i = 0; i < fields.length; i++) {
            JsonAdaptedPerson person = JsonUtil.fromJsonString(String.format(json, fields[i]), JsonAdaptedPerson.class);
            assertEquals(new PersonBuilder().withName("Benson Meier").withPhone("98765432")
                    .withEmail("benson@example.com").withAddress("New Road").withGender(expectedValues[i]).build(),
                    person.toModelType());
        }
    }
    @Test
    public void toModelType_invalidAge_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                INVALID_AGE, VALID_TAGS, null, null);
        assertThrows(IllegalValueException.class, Age.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_nullAge_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                null, VALID_TAGS, null, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Age.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_validRelationshipGoal_returnsPerson() throws Exception {
        Person bensonWithGoal = new PersonBuilder(BENSON).withRelationshipGoal("Life partner").build();
        assertEquals(bensonWithGoal, new JsonAdaptedPerson(bensonWithGoal).toModelType());
    }

    @Test
    public void fromJson_relationshipGoalValues_loadSuccessfully() throws Exception {
        String json = "{\"name\":\"Benson Meier\",\"phone\":\"98765432\",\"email\":\"benson@example.com\","
                + "\"address\":\"New Road\",\"age\":\"25\",\"tags\":[]%s}";
        Person personWithoutGoal = new PersonBuilder().withName("Benson Meier").withPhone("98765432")
                .withEmail("benson@example.com").withAddress("New Road").build();
        Person personWithGoal = new PersonBuilder(personWithoutGoal).withRelationshipGoal("Life partner").build();

        // missing or null goal -> not recorded
        assertEquals(personWithoutGoal,
                JsonUtil.fromJsonString(String.format(json, ""), JsonAdaptedPerson.class).toModelType());
        assertEquals(personWithoutGoal, JsonUtil.fromJsonString(String.format(json, ",\"relationshipGoal\":null"),
                JsonAdaptedPerson.class).toModelType());

        // stored value in a different letter case is still accepted
        assertEquals(personWithGoal, JsonUtil.fromJsonString(
                String.format(json, ",\"relationshipGoal\":\"life PARTNER\""), JsonAdaptedPerson.class).toModelType());
    }

    @Test
    public void toModelType_invalidRelationshipGoal_throwsIllegalValueException() throws Exception {
        String json = "{\"name\":\"Benson Meier\",\"phone\":\"98765432\",\"email\":\"benson@example.com\","
                + "\"address\":\"New Road\",\"age\":\"25\",\"tags\":[],\"relationshipGoal\":\"Marriage\"}";
        JsonAdaptedPerson person = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class);
        assertThrows(IllegalValueException.class, RelationshipGoal.MESSAGE_CONSTRAINTS, person::toModelType);
    }

}
