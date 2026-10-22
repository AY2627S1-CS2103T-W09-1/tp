package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.group.ClientGroup;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;

/** Stores references by the existing unique client name, never by a changing displayed index. */
class JsonAdaptedGroup {
    private final JsonNode name;
    private final List<JsonNode> memberNames;

    @JsonCreator
    public JsonAdaptedGroup(@JsonProperty("name") JsonNode name,
                            @JsonProperty("memberNames") List<JsonNode> memberNames) {
        this.name = name;
        this.memberNames = memberNames == null ? null : new ArrayList<>(memberNames);
    }

    /** Uses current client names, so app-driven renames are saved consistently with memberships. */
    public JsonAdaptedGroup(ClientGroup group) {
        name = TextNode.valueOf(group.getName().toString());
        memberNames = new ArrayList<>();
        for (Person member : group.getMembers()) {
            memberNames.add(TextNode.valueOf(member.getName().fullName));
        }
    }

    /** Resolves references against the loaded book and rejects missing, duplicate or malformed members. */
    public ClientGroup toModelType(Map<String, Person> personsByName) throws IllegalValueException {
        if (name == null || !name.isTextual() || memberNames == null) {
            throw new IllegalValueException("A textual group name and a memberNames array are required.");
        }
        List<Person> members = new ArrayList<>();
        for (JsonNode member : memberNames) {
            if (member == null || !member.isTextual() || !personsByName.containsKey(member.textValue())) {
                throw new IllegalValueException("Each group member must name an existing client exactly.");
            }
            members.add(personsByName.get(member.textValue()));
        }
        try {
            return new ClientGroup(new GroupName(name.textValue()), members);
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(exception.getMessage());
        }
    }
}
