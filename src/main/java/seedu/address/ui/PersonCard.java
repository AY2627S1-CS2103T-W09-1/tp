package seedu.address.ui;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.DatingAttribute;
import seedu.address.model.person.Person;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    private static final String UNSPECIFIED = "Unspecified";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label gender;
    @FXML
    private Label smokingStatus;
    @FXML
    private Label age;
    @FXML
    private Label religion;
    @FXML
    private FlowPane tags;
    @FXML
    private FlowPane groups;
    @FXML
    private Label preferredAge;
    @FXML
    private Label preferredGender;
    @FXML
    private Label preferredReligion;
    @FXML
    private Label preferredSmoking;
    @FXML
    private Label preferredRelationshipGoal;
    @FXML
    private Label dealbreakerAge;
    @FXML
    private Label dealbreakerGender;
    @FXML
    private Label dealbreakerReligion;
    @FXML
    private Label dealbreakerSmoking;
    @FXML
    private Label dealbreakerRelationshipGoal;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        this(person, displayedIndex, List.of());
    }

    /** Creates a card with separate, clearly labelled group badges. */
    public PersonCard(Person person, int displayedIndex, List<GroupName> groupNames) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText("Phone: " + displayOrUnspecified(person.getPhone().value));
        email.setText("Email: " + displayOrUnspecified(person.getEmail().value));
        gender.setText("Gender: " + person.getGender());
        smokingStatus.setText("Smoking: " + displayOrUnspecified(person.getSmokingStatus().value));
        age.setText("Age: " + person.getAge().value);
        religion.setText("Religion: " + person.getReligion().map(Object::toString).orElse(UNSPECIFIED));
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        groupNames.stream()
                .sorted(Comparator.comparing(GroupName::toString, String.CASE_INSENSITIVE_ORDER))
                .forEach(groupName -> groups.getChildren().add(new Label(groupName.toString())));
        groups.setVisible(!groupNames.isEmpty());
        groups.setManaged(!groupNames.isEmpty());
        setCriteria(person.getDatingCriteria().getPreferences(), preferredAge, preferredGender, preferredReligion,
                preferredSmoking, preferredRelationshipGoal);
        setCriteria(person.getDatingCriteria().getDealbreakers(), dealbreakerAge, dealbreakerGender,
                dealbreakerReligion, dealbreakerSmoking, dealbreakerRelationshipGoal);
    }

    private static void setCriteria(Map<DatingAttribute, String> criteria, Label age, Label gender, Label religion,
            Label smoking, Label relationshipGoal) {
        age.setText(formatCriterion(DatingAttribute.AGE, criteria));
        gender.setText(formatCriterion(DatingAttribute.GENDER, criteria));
        religion.setText(formatCriterion(DatingAttribute.RELIGION, criteria));
        smoking.setText(formatCriterion(DatingAttribute.SMOKING, criteria));
        relationshipGoal.setText(formatCriterion(DatingAttribute.RELATIONSHIP_GOAL, criteria));
    }

    private static String formatCriterion(DatingAttribute attribute, Map<DatingAttribute, String> criteria) {
        return attribute.getDisplayName() + ": " + displayOrUnspecified(criteria.get(attribute));
    }

    private static String displayOrUnspecified(String value) {
        return value == null || value.isBlank() ? UNSPECIFIED : value;
    }
}
