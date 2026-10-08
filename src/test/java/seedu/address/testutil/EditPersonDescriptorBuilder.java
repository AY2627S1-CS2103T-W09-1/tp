package seedu.address.testutil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
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
import seedu.address.model.tag.Tag;

/**
 * A utility class to help with building EditPersonDescriptor objects.
 */
public class EditPersonDescriptorBuilder {

    private EditPersonDescriptor descriptor;

    public EditPersonDescriptorBuilder() {
        descriptor = new EditPersonDescriptor();
    }

    public EditPersonDescriptorBuilder(EditPersonDescriptor descriptor) {
        this.descriptor = new EditPersonDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditPersonDescriptor} with fields containing {@code person}'s details
     */
    public EditPersonDescriptorBuilder(Person person) {
        descriptor = new EditPersonDescriptor();
        descriptor.setName(person.getName());
        descriptor.setPhone(person.getPhone());
        descriptor.setEmail(person.getEmail());
        descriptor.setAddress(person.getAddress());
        descriptor.setGender(person.getGender());
        if (!person.getSmokingStatus().value.isEmpty()) {
            descriptor.setSmokingStatus(person.getSmokingStatus());
        }
        descriptor.setAge(person.getAge());
        descriptor.setTags(person.getTags());
        person.getRelationshipGoal().ifPresent(descriptor::setRelationshipGoal);
    }

    /**
     * Sets the {@code Name} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new Email(email));
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withAddress(String address) {
        descriptor.setAddress(new Address(address));
        return this;
    }

    /**
     * Sets the smoking status in the edit descriptor being built.
     */
    public EditPersonDescriptorBuilder withSmokingStatus(String status) {
        descriptor.setSmokingStatus(new SmokingStatus(status));
        return this;
    }

    /** Sets the {@code Age} of the {@code EditPersonDescriptor} that we are building. */
    public EditPersonDescriptorBuilder withAge(int age) {
        descriptor.setAge(new Age(age));
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code EditPersonDescriptor}
     * that we are building.
     */
    public EditPersonDescriptorBuilder withTags(String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        descriptor.setTags(tagSet);
        return this;
    }

    /**
     * Sets the gender of the edit descriptor, including an explicit clear with an empty value.
     */
    public EditPersonDescriptorBuilder withGender(String gender) {
        descriptor.setGender(Gender.parseValue(gender));
        return this;
    }

    /** Sets or clears the client's religion; null means clear. */
    public EditPersonDescriptorBuilder withReligion(String value) {
        descriptor.setReligion(value == null ? null : new Religion(value));
        return this;
    }

    /** Sets or clears the preferred partner religion. */
    public EditPersonDescriptorBuilder withPreferredReligion(String value) {
        descriptor.setPreferredReligion(value == null ? null : new Religion(value));
        return this;
    }

    /** Sets or clears the required partner religion. */
    public EditPersonDescriptorBuilder withRequiredReligion(String value) {
        descriptor.setRequiredReligion(value == null ? null : new Religion(value));
        return this;
    }

    /** Replaces excluded partner religions, clearing them for zero values. */
    public EditPersonDescriptorBuilder withExcludedReligions(String... values) {
        Set<Religion> religions = Stream.of(values).map(Religion::new).collect(Collectors.toSet());
        descriptor.setExcludedReligions(religions);
        return this;
    }

    /**
     * Sets the {@code RelationshipGoal} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withRelationshipGoal(String relationshipGoal) {
        descriptor.setRelationshipGoal(RelationshipGoal.fromString(relationshipGoal));
        return this;
    }

    /**
     * Clears the {@code RelationshipGoal} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withoutRelationshipGoal() {
        descriptor.setRelationshipGoal(null);
        return this;
    }

    /** Returns the prepared edit descriptor. */
    public EditPersonDescriptor build() {
        return descriptor;
    }
}
