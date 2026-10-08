package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Gender gender;
    private final SmokingStatus smokingStatus;
    private final Age age;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Creates a person whose smoking status has not been recorded.
     */
    public Person(Name name, Phone phone, Email email, Address address, Age age, Set<Tag> tags) {
        this(name, phone, email, address, age, tags, SmokingStatus.UNSPECIFIED);
    }

    /**
     * Creates a person with the given details. Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Age age, Set<Tag> tags,
                  SmokingStatus smokingStatus) {
        this(name, phone, email, address, age, tags, smokingStatus, Gender.UNSPECIFIED);
    }

    /**
     * Creates a person with the given details. Use {@code Gender.UNSPECIFIED} for an unrecorded gender.
     */
    public Person(Name name, Phone phone, Email email, Address address, Age age, Set<Tag> tags,
                  SmokingStatus smokingStatus, Gender gender) {
        requireAllNonNull(name, phone, email, address, age, tags, smokingStatus, gender);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.smokingStatus = smokingStatus;
        this.age = age;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Gender getGender() {
        return gender;
    }

    public Address getAddress() {
        return address;
    }

    public SmokingStatus getSmokingStatus() {
        return smokingStatus;
    }

    public Age getAge() {
        return age;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && Objects.equals(otherPerson.getName(), getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && smokingStatus.equals(otherPerson.smokingStatus)
                && age.equals(otherPerson.age)
                && tags.equals(otherPerson.tags)
                && gender == otherPerson.gender;
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, age, tags, smokingStatus, gender);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("smokingStatus", smokingStatus)
                .add("age", age)
                .add("tags", tags)
                .add("gender", gender)
                .toString();
    }

}
