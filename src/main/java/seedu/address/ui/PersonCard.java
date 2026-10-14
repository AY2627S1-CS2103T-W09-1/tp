package seedu.address.ui;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.person.DatingAttribute;
import seedu.address.model.person.RelationshipGoal;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

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
    private Label address;
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
    private Label relationshipGoal;
    @FXML
    private Label preferences;
    @FXML
    private Label dealbreakers;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);
        gender.setText("Gender: " + person.getGender());
        smokingStatus.setText("Smoking: " + person.getSmokingStatus());
        age.setText("Age: " + person.getAge().value);
        religion.setText("Religion: " + person.getReligion().map(Object::toString).orElse("Not specified"));
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        relationshipGoal.setText("Relationship goal: "
                + person.getRelationshipGoal().map(Object::toString).orElse(RelationshipGoal.NOT_SPECIFIED_LABEL));
        preferences.setText(formatCriteria("Preferences", person.getDatingCriteria().getPreferences()));
        preferences.setManaged(!person.getDatingCriteria().getPreferences().isEmpty());
        preferences.setVisible(!person.getDatingCriteria().getPreferences().isEmpty());
        dealbreakers.setText(formatCriteria("Dealbreakers", person.getDatingCriteria().getDealbreakers()));
        dealbreakers.setManaged(!person.getDatingCriteria().getDealbreakers().isEmpty());
        dealbreakers.setVisible(!person.getDatingCriteria().getDealbreakers().isEmpty());
    }

    private static String formatCriteria(String label, Map<DatingAttribute, String> criteria) {
        return label + ": " + criteria.entrySet().stream()
                .map(entry -> entry.getKey().getDisplayName() + " = " + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}
