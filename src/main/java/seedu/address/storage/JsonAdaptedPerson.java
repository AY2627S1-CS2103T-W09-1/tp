package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Age;
import seedu.address.model.person.Email;
import seedu.address.model.person.Gender;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
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
    private final String preferredReligion;
    private final String requiredReligion;
    private final List<String> excludedReligions = new ArrayList<>();

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
            @JsonProperty("preferredReligion") String preferredReligion,
            @JsonProperty("requiredReligion") String requiredReligion,
            @JsonProperty("excludedReligions") List<String> excludedReligions) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.smokingStatus = smokingStatus;
        this.age = age;
        this.religion = religion;
        this.preferredReligion = preferredReligion;
        this.requiredReligion = requiredReligion;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (excludedReligions != null) {
            this.excludedReligions.addAll(excludedReligions);
        }
    }

    /** Creates an adapted legacy person with no recorded religion details. */
    public JsonAdaptedPerson(String name, String phone, String email, String address, List<JsonAdaptedTag> tags) {
        this(name, phone, email, address, null, tags, null, null, null, null, null, null);
    }

    /**
     * Constructs a person with the age, smoking-status, and gender fields but no religion criteria.
     */
    public JsonAdaptedPerson(String name, String phone, String email, String address, String age,
            List<JsonAdaptedTag> tags, String smokingStatus, String gender) {
        this(name, phone, email, address, age, tags, smokingStatus, gender, null, null, null, null);
    }

    /**
     * Constructs a legacy religion-criteria test fixture with default values for subsequently added fields.
     */
    public JsonAdaptedPerson(String name, String phone, String email, String address, List<JsonAdaptedTag> tags,
            String religion, String preferredReligion, String requiredReligion, List<String> excludedReligions) {
        this(name, phone, email, address, "18", tags, null, null, religion, preferredReligion,
                requiredReligion, excludedReligions);
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
        preferredReligion = source.getPreferredReligion().map(Religion::toString).orElse(null);
        requiredReligion = source.getRequiredReligion().map(Religion::toString).orElse(null);
        excludedReligions.addAll(source.getExcludedReligions().stream()
                .map(Religion::toString)
                .sorted()
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
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
            Religion modelPreferred = preferredReligion == null ? null : new Religion(preferredReligion);
            Religion modelRequired = requiredReligion == null ? null : new Religion(requiredReligion);
            Set<Religion> modelExcluded = new HashSet<>();
            for (String excluded : excludedReligions) {
                if (excluded == null || !modelExcluded.add(new Religion(excluded))) {
                    throw new IllegalArgumentException("Excluded religions must be valid and unique.");
                }
            }
            return new Person(modelName, modelPhone, modelEmail, modelAddress, modelAge, modelTags,
                    modelSmokingStatus, modelGender, modelReligion, modelPreferred, modelRequired, modelExcluded);
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(exception.getMessage());
        }
    }

}
