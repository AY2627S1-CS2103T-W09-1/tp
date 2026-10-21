package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.EditCommandParser;
import seedu.address.model.person.DatingAttribute;
import seedu.address.model.person.Person;

public class PersonUtilTest {

    @Test
    public void getAddCommand_allDatingFields_preservesPerson() throws Exception {
        Person person = buildPersonWithCriteria();
        assertEquals(new AddCommand(person), new AddressBookParser().parseCommand(PersonUtil.getAddCommand(person)));
    }

    @Test
    public void getEditPersonDescriptorDetails_allDatingFields_preservesDescriptor() throws Exception {
        Person person = buildPersonWithCriteria();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        assertEquals(person.getReligion(), descriptor.getReligion());
        assertEquals(person.getDatingCriteria().getPreferences(), descriptor.getPreferences().orElseThrow());
        assertEquals(person.getDatingCriteria().getDealbreakers(), descriptor.getDealbreakers().orElseThrow());
        assertDescriptorRoundTrip(descriptor);
    }

    @Test
    public void getEditPersonDescriptorDetails_omittedFields_preservesOmissions() throws Exception {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName("Amy Bee").build();
        assertDescriptorRoundTrip(descriptor);
    }

    @Test
    public void getEditPersonDescriptorDetails_clearedFields_preservesClears() throws Exception {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().withReligion(null)
                .withoutRelationshipGoal().build();
        descriptor.setPreferences(Map.of());
        descriptor.setDealbreakers(Map.of());
        assertDescriptorRoundTrip(descriptor);
    }

    private void assertDescriptorRoundTrip(EditPersonDescriptor descriptor) throws Exception {
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor),
                new EditCommandParser().parse("1 " + PersonUtil.getEditPersonDescriptorDetails(descriptor)));
    }

    private Person buildPersonWithCriteria() {
        return new PersonBuilder().withReligion("Jainism").withGender("nb").withSmokingStatus("no")
                .withRelationshipGoal("long").withTags("friend")
                .withPreference(DatingAttribute.AGE, "25-35")
                .withPreference(DatingAttribute.GENDER, "w")
                .withPreference(DatingAttribute.SMOKING, "no")
                .withPreference(DatingAttribute.RELIGION, "Baha'i Faith")
                .withPreference(DatingAttribute.RELATIONSHIP_GOAL, "long-open")
                .withDealbreaker(DatingAttribute.RELIGION, "Islam")
                .withDealbreaker(DatingAttribute.SMOKING, "yes").build();
    }
}
