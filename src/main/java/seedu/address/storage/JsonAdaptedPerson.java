package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Age;
import seedu.address.model.person.DatingAttribute;
import seedu.address.model.person.DatingCriteria;
import seedu.address.model.person.Email;
import seedu.address.model.person.Gender;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.RelationshipGoal;
import seedu.address.model.person.Religion;
import seedu.address.model.person.SmokingStatus;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String gender;
    private final String smokingStatus;
    private final String age;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final String religion;
    /** The client's own goal in older data files. It is read for migration but no longer saved. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String relationshipGoal;
    private final Map<String, List<String>> preferences;
    private final Map<String, List<String>> dealbreakers;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("age") String age,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("smokingStatus") String smokingStatus, @JsonProperty("gender") String gender,
            @JsonProperty("religion") String religion,
            @JsonProperty("relationshipGoal") String relationshipGoal,
            @JsonProperty("preferences") Map<String, ?> preferences,
            @JsonProperty("dealbreakers") Map<String, ?> dealbreakers) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.smokingStatus = smokingStatus;
        this.age = age;
        this.religion = religion;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        this.relationshipGoal = relationshipGoal;
        this.preferences = convertCriteria(preferences);
        this.dealbreakers = convertCriteria(dealbreakers);
    }

    /**
     * Creates an adapted legacy person with no recorded religion details.
     */
    public JsonAdaptedPerson(String name, String phone, String email, String address, List<JsonAdaptedTag> tags) {
        this(name, phone, email, address, null, tags, null, null, null, null, null, null);
    }

    /**
     * Constructs a person with the age, smoking-status, and gender fields but no recorded religion.
     */
    public JsonAdaptedPerson(String name, String phone, String email, String address, String age,
            List<JsonAdaptedTag> tags, String smokingStatus, String gender) {
        this(name, phone, email, address, age, tags, smokingStatus, gender, null, null, null, null);
    }

    /**
     * Constructs a religion test fixture with default values for subsequently added fields.
     */
    public JsonAdaptedPerson(String name, String phone, String email, String address, List<JsonAdaptedTag> tags,
            String religion) {
        this(name, phone, email, address, "18", tags, null, null, religion, null, null, null);
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        gender = source.getGender().getValue();
        smokingStatus = source.getSmokingStatus().value;
        age = source.getAge().toString();
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        religion = source.getReligion().map(Religion::toString).orElse(null);
        relationshipGoal = null;
        preferences = source.getDatingCriteria().getPreferencesForStorage();
        dealbreakers = source.getDatingCriteria().getDealbreakersForStorage();
    }

    /** Accepts legacy single-string values while writing all criteria as value lists. */
    private static Map<String, List<String>> convertCriteria(Map<String, ?> criteria) {
        if (criteria == null) {
            return null;
        }
        Map<String, List<String>> converted = new LinkedHashMap<>();
        criteria.forEach((attribute, rawValues) -> {
            if (rawValues instanceof String value) {
                converted.put(attribute, List.of(value));
            } else if (rawValues instanceof List<?> values) {
                List<String> strings = new ArrayList<>();
                for (Object value : values) {
                    if (!(value instanceof String string)) {
                        throw new IllegalArgumentException("Dating criteria values must be strings.");
                    }
                    strings.add(string);
                }
                converted.put(attribute, List.copyOf(strings));
            } else {
                throw new IllegalArgumentException("Dating criteria values must be strings or lists of strings.");
            }
        });
        return Map.copyOf(converted);
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            if (tag == null) {
                throw new IllegalValueException("Tags must not contain null entries.");
            }
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        if (age == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Age.class.getSimpleName()));
        }
        if (!Age.isValidAge(age)) {
            throw new IllegalValueException(Age.MESSAGE_CONSTRAINTS);
        }
        final Age modelAge = new Age(Integer.parseInt(age));

        final Set<Tag> modelTags = new HashSet<>(personTags);

        if (smokingStatus != null && !SmokingStatus.isValidSmokingStatus(smokingStatus)) {
            throw new IllegalValueException(SmokingStatus.MESSAGE_CONSTRAINTS);
        }
        final SmokingStatus modelSmokingStatus = smokingStatus == null
                ? SmokingStatus.UNSPECIFIED : new SmokingStatus(smokingStatus);

        final Gender modelGender;
        try {
            modelGender = gender == null ? Gender.UNSPECIFIED : Gender.parseValue(gender);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(Gender.MESSAGE_CONSTRAINTS);
        }

        try {
            Religion modelReligion = religion == null ? null : new Religion(religion);
            DatingCriteria modelDatingCriteria = migrateLegacyRelationshipGoal(
                    DatingCriteria.fromStorage(preferences, dealbreakers));
            return new Person(modelName, modelPhone, modelEmail, modelAddress, modelAge, modelTags,
                    modelSmokingStatus, modelGender, modelReligion, modelDatingCriteria);
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(exception.getMessage());
        }
    }

    /**
     * Moves a legacy own relationship goal into the relationship goal preference, which now describes the
     * client. The legacy goal is dropped if a goal preference is already recorded or the goal is excluded.
     *
     * @throws IllegalArgumentException if the legacy goal is not a supported relationship goal.
     */
    private DatingCriteria migrateLegacyRelationshipGoal(DatingCriteria criteria) {
        if (relationshipGoal == null || !criteria.getRelationshipGoals().isEmpty()) {
            return criteria;
        }
        RelationshipGoal goal = RelationshipGoal.fromStoredValue(relationshipGoal);
        Set<String> excludedGoals = criteria.getDealbreakers()
                .getOrDefault(DatingAttribute.RELATIONSHIP_GOAL, Set.of());
        boolean isExcluded = excludedGoals.contains(goal.toString());
        if (isExcluded) {
            return criteria;
        }
        Map<DatingAttribute, Set<String>> migratedPreferences = new HashMap<>(criteria.getPreferences());
        migratedPreferences.put(DatingAttribute.RELATIONSHIP_GOAL, Set.of(goal.toString()));
        return new DatingCriteria(migratedPreferences, criteria.getDealbreakers());
    }

}
