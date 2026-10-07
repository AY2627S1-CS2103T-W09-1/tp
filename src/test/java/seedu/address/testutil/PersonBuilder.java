package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Religion;
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

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Set<Tag> tags;
    private Religion religion;
    private Religion preferredReligion;
    private Religion requiredReligion;
    private Set<Religion> excludedReligions;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        tags = new HashSet<>();
        excludedReligions = new HashSet<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        tags = new HashSet<>(personToCopy.getTags());
        religion = personToCopy.getReligion().orElse(null);
        preferredReligion = personToCopy.getPreferredReligion().orElse(null);
        requiredReligion = personToCopy.getRequiredReligion().orElse(null);
        excludedReligions = new HashSet<>(personToCopy.getExcludedReligions());
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

    /** Sets the client's own religion. */
    public PersonBuilder withReligion(String value) {
        religion = new Religion(value);
        return this;
    }

    /** Sets a soft preference for a partner's religion. */
    public PersonBuilder withPreferredReligion(String value) {
        preferredReligion = new Religion(value);
        return this;
    }

    /** Sets a required partner religion. */
    public PersonBuilder withRequiredReligion(String value) {
        requiredReligion = new Religion(value);
        return this;
    }

    /** Replaces the excluded partner religions. */
    public PersonBuilder withExcludedReligions(String... values) {
        excludedReligions = new HashSet<>();
        for (String value : values) {
            excludedReligions.add(new Religion(value));
        }
        return this;
    }

    /** Builds an immutable person with the selected details. */
    public Person build() {
        return new Person(name, phone, email, address, tags, religion, preferredReligion, requiredReligion,
                excludedReligions);
    }

}
