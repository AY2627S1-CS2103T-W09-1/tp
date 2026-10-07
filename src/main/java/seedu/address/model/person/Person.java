package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Required contact details are non-null; religion details may be absent.
 * Recorded values are validated, and person details are immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final Religion religion;
    private final Religion preferredReligion;
    private final Religion requiredReligion;
    private final Set<Religion> excludedReligions = new HashSet<>();

    /**
     * Creates a person with required contact details and no religion details.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, phone, email, address, tags, null, null, null, Set.of());
    }

    /**
     * Creates a person with optional religion details. A null religion means it is not recorded.
     * Preferred is a soft criterion; required and excluded are hard criteria for later matching.
     *
     * @throws IllegalArgumentException if the religion criteria contradict each other
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags,
            Religion religion, Religion preferredReligion, Religion requiredReligion,
            Set<Religion> excludedReligions) {
        requireAllNonNull(name, phone, email, address, tags, excludedReligions);
        if ((preferredReligion != null && requiredReligion != null
                && !preferredReligion.equals(requiredReligion))
                || (preferredReligion != null && excludedReligions.contains(preferredReligion))
                || (requiredReligion != null && excludedReligions.contains(requiredReligion))) {
            throw new IllegalArgumentException(Religion.MESSAGE_CONFLICT);
        }
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.religion = religion;
        this.preferredReligion = preferredReligion;
        this.requiredReligion = requiredReligion;
        this.excludedReligions.addAll(excludedReligions);
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

    public Address getAddress() {
        return address;
    }

    /** Returns the client's recorded religion, if known. */
    public Optional<Religion> getReligion() {
        return Optional.ofNullable(religion);
    }

    /** Returns the religion the client would prefer in a partner, if specified. */
    public Optional<Religion> getPreferredReligion() {
        return Optional.ofNullable(preferredReligion);
    }

    /** Returns the religion the client requires in a partner, if specified. */
    public Optional<Religion> getRequiredReligion() {
        return Optional.ofNullable(requiredReligion);
    }

    /** Returns religions the client excludes in a partner. */
    public Set<Religion> getExcludedReligions() {
        return Collections.unmodifiableSet(excludedReligions);
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
                && otherPerson.getName().equals(getName());
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
                && tags.equals(otherPerson.tags)
                && Objects.equals(religion, otherPerson.religion)
                && Objects.equals(preferredReligion, otherPerson.preferredReligion)
                && Objects.equals(requiredReligion, otherPerson.requiredReligion)
                && excludedReligions.equals(otherPerson.excludedReligions);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, religion, preferredReligion, requiredReligion,
                excludedReligions);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("religion", religion)
                .add("preferredReligion", preferredReligion)
                .add("requiredReligion", requiredReligion)
                .add("excludedReligions", excludedReligions)
                .toString();
    }

}
