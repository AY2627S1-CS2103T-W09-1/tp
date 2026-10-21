package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RELATIONSHIP_GOAL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RELIGION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SMOKING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
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
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_GENDER + "GENDER] "
            + "[" + PREFIX_SMOKING + "yes|no] "
            + "[" + PREFIX_AGE + "AGE] "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_RELIGION + "RELIGION] "
            + "[" + PREFIX_RELATIONSHIP_GOAL + "RELATIONSHIP_GOAL] [{CRITERIA}] [<CRITERIA>]\n"
            + "Gender: m, w, nb, or empty for unspecified.\n"
            + "Partner criteria: {...} prefers; <...> excludes (must not have). "
            + "Use age/, g/, s/, r/, or rg/ once per block, each with a value.\n"
            + "Supplied criteria add or update entries; {} or <> clears that entire set.\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * Creates an edit command for a person in the displayed list.
     *
     * @param index Index of the person to edit.
     * @param editPersonDescriptor Details to apply to the person.
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    /**
     * Applies the edits to the selected person and reports the result.
     *
     * @throws CommandException If the index or resulting person is invalid.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson;
        try {
            editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);
        } catch (IllegalArgumentException exception) {
            throw new CommandException(exception.getMessage());
        }

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Age updatedAge = editPersonDescriptor.getAge().orElse(personToEdit.getAge());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        Gender updatedGender = editPersonDescriptor.getGender().orElse(personToEdit.getGender());
        SmokingStatus updatedSmokingStatus = editPersonDescriptor.getSmokingStatus()
                .orElse(personToEdit.getSmokingStatus());
        RelationshipGoal updatedRelationshipGoal = editPersonDescriptor.isRelationshipGoalEdited()
                ? editPersonDescriptor.getRelationshipGoal().orElse(null)
                : personToEdit.getRelationshipGoal().orElse(null);
        Map<DatingAttribute, String> updatedPreferences = mergeCriteria(
                personToEdit.getDatingCriteria().getPreferences(), editPersonDescriptor.getPreferences());
        Map<DatingAttribute, String> updatedDealbreakers = mergeCriteria(
                personToEdit.getDatingCriteria().getDealbreakers(), editPersonDescriptor.getDealbreakers());
        DatingCriteria updatedDatingCriteria = new DatingCriteria(updatedPreferences, updatedDealbreakers);

        Religion updatedReligion = editPersonDescriptor.isReligionEdited()
                ? editPersonDescriptor.getReligion().orElse(null) : personToEdit.getReligion().orElse(null);

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedAge, updatedTags,
                updatedSmokingStatus, updatedGender, updatedReligion, updatedRelationshipGoal, updatedDatingCriteria);
    }

    /**
     * Applies supplied criteria to the existing criteria without removing attributes that were not supplied.
     * An explicitly empty environment clears all criteria of that type.
     */
    private static Map<DatingAttribute, String> mergeCriteria(Map<DatingAttribute, String> existingCriteria,
            Optional<Map<DatingAttribute, String>> editedCriteria) {
        if (editedCriteria.filter(Map::isEmpty).isPresent()) {
            return Map.of();
        }
        Map<DatingAttribute, String> mergedCriteria = new HashMap<>(existingCriteria);
        editedCriteria.ifPresent(mergedCriteria::putAll);
        return mergedCriteria;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit a person. Supplied fields replace existing values;
     * an explicitly empty religion field clears the recorded religion.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Gender gender;
        private SmokingStatus smokingStatus;
        private Age age;
        private Set<Tag> tags;
        private Religion religion;
        // Track supplied fields separately so null can mean an explicit clear.
        private boolean religionEdited;
        private RelationshipGoal relationshipGoal;
        private boolean relationshipGoalEdited;
        private Map<DatingAttribute, String> preferences;
        private boolean preferencesEdited;
        private Map<DatingAttribute, String> dealbreakers;
        private boolean dealbreakersEdited;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setGender(toCopy.gender);
            setSmokingStatus(toCopy.smokingStatus);
            setAge(toCopy.age);
            setTags(toCopy.tags);
            if (toCopy.religionEdited) {
                setReligion(toCopy.religion);
            }
            if (toCopy.relationshipGoalEdited) {
                setRelationshipGoal(toCopy.relationshipGoal);
            }
            if (toCopy.preferencesEdited) {
                setPreferences(toCopy.preferences);
            }
            if (toCopy.dealbreakersEdited) {
                setDealbreakers(toCopy.dealbreakers);
            }
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, age, tags, smokingStatus, gender)
                    || religionEdited || relationshipGoalEdited || preferencesEdited || dealbreakersEdited;
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public Optional<Gender> getGender() {
            return Optional.ofNullable(gender);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setSmokingStatus(SmokingStatus smokingStatus) {
            this.smokingStatus = smokingStatus;
        }

        public Optional<SmokingStatus> getSmokingStatus() {
            return Optional.ofNullable(smokingStatus);
        }

        public void setAge(Age age) {
            this.age = age;
        }

        public Optional<Age> getAge() {
            return Optional.ofNullable(age);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        /**
         * Sets or clears the client's religion; null explicitly clears it.
         */
        public void setReligion(Religion religion) {
            this.religion = religion;
            religionEdited = true;
        }

        /**
         * Returns the new religion, if one was supplied rather than cleared.
         */
        public Optional<Religion> getReligion() {
            return Optional.ofNullable(religion);
        }

        /**
         * Returns whether the religion was supplied, including an explicit clear.
         */
        public boolean isReligionEdited() {
            return religionEdited;
        }

        /**
         * Sets or clears the client's relationship goal; null explicitly clears it.
         */
        public void setRelationshipGoal(RelationshipGoal relationshipGoal) {
            this.relationshipGoal = relationshipGoal;
            relationshipGoalEdited = true;
        }

        /**
         * Returns the new relationship goal, if one was supplied rather than cleared.
         */
        public Optional<RelationshipGoal> getRelationshipGoal() {
            return Optional.ofNullable(relationshipGoal);
        }

        /**
         * Returns whether the relationship goal was supplied, including an explicit clear.
         */
        public boolean isRelationshipGoalEdited() {
            return relationshipGoalEdited;
        }

        /** Records partner preferences to add or update. */
        public void setPreferences(Map<DatingAttribute, String> preferences) {
            this.preferences = Map.copyOf(requireNonNull(preferences));
            preferencesEdited = true;
        }

        /** Returns preferences to add or update when the preference environment was supplied. */
        public Optional<Map<DatingAttribute, String>> getPreferences() {
            return Optional.ofNullable(preferences);
        }

        /** Records partner dealbreakers to add or update. */
        public void setDealbreakers(Map<DatingAttribute, String> dealbreakers) {
            this.dealbreakers = Map.copyOf(requireNonNull(dealbreakers));
            dealbreakersEdited = true;
        }

        /** Returns dealbreakers to add or update when the dealbreaker environment was supplied. */
        public Optional<Map<DatingAttribute, String>> getDealbreakers() {
            return Optional.ofNullable(dealbreakers);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(gender, otherEditPersonDescriptor.gender)
                    && Objects.equals(smokingStatus, otherEditPersonDescriptor.smokingStatus)
                    && Objects.equals(age, otherEditPersonDescriptor.age)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags)
                    && Objects.equals(religion, otherEditPersonDescriptor.religion)
                    && religionEdited == otherEditPersonDescriptor.religionEdited
                    && Objects.equals(relationshipGoal, otherEditPersonDescriptor.relationshipGoal)
                    && relationshipGoalEdited == otherEditPersonDescriptor.relationshipGoalEdited
                    && Objects.equals(preferences, otherEditPersonDescriptor.preferences)
                    && preferencesEdited == otherEditPersonDescriptor.preferencesEdited
                    && Objects.equals(dealbreakers, otherEditPersonDescriptor.dealbreakers)
                    && dealbreakersEdited == otherEditPersonDescriptor.dealbreakersEdited;
        }

        /**
         * Returns a string representation of the supplied fields and edit flags.
         */
        @Override
        public String toString() {
            ToStringBuilder builder = new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("smokingStatus", smokingStatus)
                    .add("age", age)
                    .add("tags", tags)
                    .add("gender", gender)
                    .add("religion", religion)
                    .add("religionEdited", religionEdited)
                    .add("relationshipGoal", relationshipGoal);
            if (preferencesEdited) {
                builder.add("preferences", preferences)
                        .add("preferencesEdited", true);
            }
            if (dealbreakersEdited) {
                builder.add("dealbreakers", dealbreakers)
                        .add("dealbreakersEdited", true);
            }
            return builder.toString();
        }
    }
}
