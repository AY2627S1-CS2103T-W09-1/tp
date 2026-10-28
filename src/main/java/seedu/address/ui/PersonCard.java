package seedu.address.ui;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.DatingAttribute;
import seedu.address.model.person.Gender;
import seedu.address.model.person.Person;
import seedu.address.model.person.SmokingStatus;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    private static final String NOT_SET_LABEL = "not set";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private VBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label address;
    @FXML
    private GridPane attributes;
    @FXML
    private VBox preferences;
    @FXML
    private VBox dealbreakers;
    @FXML
    private FlowPane badges;

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
        id.setText(Integer.toString(displayedIndex));
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        email.setText(person.getEmail().value);
        address.setText(person.getAddress().value);
        addAttribute("Age", Optional.of(Integer.toString(person.getAge().value)));
        addAttribute("Gender", formatGender(person.getGender()));
        addAttribute("Smoking", formatSmokingStatus(person.getSmokingStatus()));
        addAttribute("Religion", person.getReligion().map(Object::toString));
        addAttribute("Goal", person.getRelationshipGoal().map(Object::toString))
                .setTooltip(new Tooltip("Relationship goal"));
        setCriteria(preferences, person.getDatingCriteria().getPreferences());
        setCriteria(dealbreakers, person.getDatingCriteria().getDealbreakers());
        addBadges("Tags", person.getTags().stream()
                .map(tag -> tag.tagName)
                .sorted()
                .toList(), "tag-badge");
        addBadges("Groups", groupNames.stream()
                .sorted(Comparator.comparing(GroupName::toString, String.CASE_INSENSITIVE_ORDER))
                .map(GroupName::toString)
                .toList(), "group-badge");
        badges.setVisible(!badges.getChildren().isEmpty());
        badges.setManaged(!badges.getChildren().isEmpty());
    }

    /**
     * Adds a row of the attributes table and returns its field label.
     */
    private Label addAttribute(String label, Optional<String> value) {
        Label fieldLabel = createLabel(label, "card-field-label");
        fieldLabel.setMinWidth(Region.USE_PREF_SIZE);
        Label valueLabel = createLabel(value.orElse(NOT_SET_LABEL),
                value.isPresent() ? "card-field-value" : "card-field-unset");
        attributes.addRow(attributes.getRowCount(), fieldLabel, valueLabel);
        return fieldLabel;
    }

    private void addBadges(String label, List<String> names, String badgeStyleClass) {
        if (names.isEmpty()) {
            return;
        }
        Label fieldLabel = createLabel(label, "card-field-label");
        fieldLabel.getStyleClass().add(badges.getChildren().isEmpty()
                ? "card-badges-label" : "card-badges-label-next");
        badges.getChildren().add(fieldLabel);
        names.forEach(badgeName -> badges.getChildren().add(createLabel(badgeName, badgeStyleClass)));
    }

    private static void setCriteria(VBox container, Map<DatingAttribute, String> criteria) {
        if (criteria.isEmpty()) {
            container.getChildren().add(createLabel("none", "card-field-unset"));
            return;
        }
        criteria.forEach((attribute, value) -> container.getChildren().add(createLabel(
                attribute.getDisplayName().toLowerCase(Locale.ROOT) + " = " + value, "card-criterion")));
    }

    private static Optional<String> formatSmokingStatus(SmokingStatus smokingStatus) {
        if (smokingStatus.equals(SmokingStatus.UNSPECIFIED)) {
            return Optional.empty();
        }
        return Optional.of(smokingStatus.value.substring(0, 1).toUpperCase(Locale.ROOT)
                + smokingStatus.value.substring(1));
    }

    private static Optional<String> formatGender(Gender gender) {
        return gender.equals(Gender.UNSPECIFIED) ? Optional.empty() : Optional.of(gender.toString());
    }

    private static Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add(styleClass);
        return label;
    }
}
