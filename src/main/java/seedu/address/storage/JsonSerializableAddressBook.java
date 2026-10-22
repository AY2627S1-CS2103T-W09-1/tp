package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.group.ClientGroup;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<JsonAdaptedGroup> groups = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons.
     * Older address books may omit the groups field, so groups are populated by the
     * annotated setter only when Jackson encounters that field.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons) {
        this.persons.addAll(persons);
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        groups.addAll(source.getGroupList().stream()
                .map(JsonAdaptedGroup::new).toList());
    }

    /**
     * Loads the groups field when it is present in JSON. An explicitly null groups field is malformed;
     * an omitted field is accepted for compatibility with older address books.
     */
    @JsonProperty("groups")
    public void setGroupsFromJson(List<JsonAdaptedGroup> groups) {
        if (groups == null) {
            throw new IllegalArgumentException("Groups field must be an array when present.");
        }
        this.groups.addAll(groups);
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }
        Map<String, Person> personsByName = addressBook.getPersonList().stream()
                .collect(Collectors.toMap(person -> person.getName().fullName, person -> person));
        List<ClientGroup> loadedGroups = new ArrayList<>();
        for (JsonAdaptedGroup group : groups) {
            if (group == null) {
                throw new IllegalValueException("Groups must not contain null entries.");
            }
            loadedGroups.add(group.toModelType(personsByName));
        }
        try {
            addressBook.setGroups(loadedGroups);
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(exception.getMessage());
        }
        return addressBook;
    }

}
