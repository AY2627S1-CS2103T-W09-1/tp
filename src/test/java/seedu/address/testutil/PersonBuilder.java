package seedu.address.testutil;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";
    public static final int DEFAULT_AGE = 25;

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Gender gender = Gender.UNSPECIFIED;
    private Age age;
    private Set<Tag> tags;
    private SmokingStatus smokingStatus;
    private Religion religion;
    private Map<DatingAttribute, Set<String>> preferences = new EnumMap<>(DatingAttribute.class);
    private Map<DatingAttribute, Set<String>> dealbreakers = new EnumMap<>(DatingAttribute.class);

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        age = new Age(DEFAULT_AGE);
        tags = new HashSet<>();
        smokingStatus = SmokingStatus.UNSPECIFIED;
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        gender = personToCopy.getGender();
        smokingStatus = personToCopy.getSmokingStatus();
        age = personToCopy.getAge();
        tags = new HashSet<>(personToCopy.getTags());
        religion = personToCopy.getReligion().orElse(null);
        preferences = new EnumMap<>(DatingAttribute.class);
        preferences.putAll(personToCopy.getDatingCriteria().getPreferences());
        dealbreakers = new EnumMap<>(DatingAttribute.class);
        dealbreakers.putAll(personToCopy.getDatingCriteria().getDealbreakers());
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Age} of the {@code Person} that we are building.
     */
    public PersonBuilder withAge(int age) {
        this.age = new Age(age);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the gender of the person being built.
     */
    public PersonBuilder withGender(String gender) {
        this.gender = Gender.parseValue(gender);
        return this;
    }

    /**
     * Sets the smoking status of the person being built.
     */
    public PersonBuilder withSmokingStatus(String status) {
        smokingStatus = new SmokingStatus(status);
        return this;
    }

    /**
     * Sets the client's own religion.
     */
    public PersonBuilder withReligion(String value) {
        religion = new Religion(value);
        return this;
    }

    /**
     * Sets the relationship goals the {@code Person} we are building is open to, recorded as a preference.
     */
    public PersonBuilder withRelationshipGoal(String relationshipGoals) {
        preferences.put(DatingAttribute.RELATIONSHIP_GOAL, RelationshipGoal.parseStoredCriterion(relationshipGoals)
                .stream().map(RelationshipGoal::toString).collect(Collectors.toSet()));
        return this;
    }

    /**
     * Adds a partner preference to the {@code Person} that we are building.
     */
    public PersonBuilder withPreference(DatingAttribute attribute, String value) {
        preferences.computeIfAbsent(attribute, ignored -> new HashSet<>()).add(value);
        return this;
    }

    /**
     * Adds a partner dealbreaker to the {@code Person} that we are building.
     */
    public PersonBuilder withDealbreaker(DatingAttribute attribute, String value) {
        dealbreakers.computeIfAbsent(attribute, ignored -> new HashSet<>()).add(value);
        return this;
    }

    /**
     * Builds an immutable person with the selected details.
     */
    public Person build() {
        return new Person(name, phone, email, address, age, tags, smokingStatus, gender, religion,
                new DatingCriteria(preferences, dealbreakers));
    }

}
