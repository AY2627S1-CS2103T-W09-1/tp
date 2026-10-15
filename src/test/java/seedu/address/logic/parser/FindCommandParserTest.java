package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.Age;
import seedu.address.model.person.AgeMatchesPredicate;
import seedu.address.model.person.Gender;
import seedu.address.model.person.GenderMatchesPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.RelationshipGoal;
import seedu.address.model.person.RelationshipGoalMatchesPredicate;
import seedu.address.model.person.Religion;
import seedu.address.model.person.ReligionMatchesPredicate;
import seedu.address.model.person.SmokingStatus;
import seedu.address.model.person.SmokingStatusMatchesPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        assertParseSuccess(parser, "age/18", new FindCommand(new AgeMatchesPredicate(18, 18)));
        assertParseSuccess(parser, " age/18-35 ", new FindCommand(new AgeMatchesPredicate(18, 35)));
    }

    @Test
    public void parse_invalidAge_throwsParseException() {
        assertParseFailure(parser, "age/17", Age.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "age/100", Age.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "age/18-100", Age.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "age/35-18", "The lower age bound cannot be greater than the upper age bound.");
    }

    @Test
    public void parse_nameKeywords_returnsNameSearch() {
        assertParseSuccess(parser, " Alice Bob ",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob"))));
    }

    @Test
    public void parse_genderList_normalizesCaseWhitespaceAndOrder() {
        FindCommand expected = new FindCommand(new GenderMatchesPredicate(Set.of(Gender.MAN, Gender.NON_BINARY)));
        assertParseSuccess(parser, "g/ Nb , M ", expected);
    }

    @Test
    public void parse_emptyGender_selectsAllSpecifiedGenders() {
        FindCommand expected = new FindCommand(new GenderMatchesPredicate(
                Set.of(Gender.MAN, Gender.WOMAN, Gender.NON_BINARY)));
        assertParseSuccess(parser, " g/   ", expected);
    }

    @Test
    public void parse_malformedOrDuplicateGenderList_throwsParseException() {
        for (String list : new String[] {"m,m", "m,M", "nb,NB", ",m", "w,", "m,,nb", ",", "m, ,w"}) {
            assertParseFailure(parser, "g/" + list, FindCommandParser.MESSAGE_INVALID_GENDER_LIST);
        }
    }

    @Test
    public void parse_unknownGenderOrMissingComma_throwsParseException() {
        for (String list : new String[] {"x", "m,x", "m w", "unspecified"}) {
            assertParseFailure(parser, "g/" + list, Gender.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_repeatedGenderPrefix_throwsParseException() {
        assertParseFailure(parser, "g/m g/w", Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_GENDER));
    }

    @Test
    public void parse_mixedNameOrIncorrectPrefix_throwsParseException() {
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        for (String args : new String[] {"Alice g/m", "G/m", "g /m", "gp/m"}) {
            assertParseFailure(parser, args, usage);
        }
        assertParseFailure(parser, "g/m Alice", Gender.MESSAGE_CONSTRAINTS);
    }
    @Test
    public void parse_religion_normalizesSupportedCategories() {
        for (String category : Religion.CATEGORIES) {
            assertParseSuccess(parser, "r/" + category,
                    new FindCommand(new ReligionMatchesPredicate(new Religion(category))));
        }
        assertParseSuccess(parser, " r/  bAhA'i   FAITH  ",
                new FindCommand(new ReligionMatchesPredicate(new Religion("Baha'i Faith"))));
    }

    @Test
    public void parse_blankOrInvalidReligion_throwsParseException() {
        for (String value : new String[] {"", "   ", "Other", "Jain", "Jainism,Buddhism", "Jainism Alice"}) {
            assertParseFailure(parser, "r/" + value, Religion.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_repeatedReligionPrefix_throwsParseException() {
        String message = Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_RELIGION);
        assertParseFailure(parser, "r/Jainism r/Islam", message);
        assertParseFailure(parser, "r/Jainism r/jainism", message);
        assertParseFailure(parser, "r/ r/Jainism", message);
    }

    @Test
    public void parse_mixedReligionSearchModes_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        for (String args : new String[] {"Alice r/Jainism", "r/Jainism age/25", "age/25 r/Jainism",
            "r/Jainism g/nb", "g/nb r/Jainism", "R/Jainism", "r /Jainism", "rp/Jainism", "rx/Islam"}) {
            assertParseFailure(parser, args, message);
        }
    }

    @Test
    public void parse_combinedAgeAndGender_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "age/25-35 g/m", message);
        assertParseFailure(parser, "g/m age/25-35", message);
    }

    @Test
    public void parse_relationshipGoal_returnsGoalSearch() {
        FindCommand expected = new FindCommand(
                new RelationshipGoalMatchesPredicate(RelationshipGoal.LONG_TERM_OPEN_TO_SHORT));
        assertParseSuccess(parser, " rg/long-open ", expected);
    }

    @Test
    public void parse_invalidRelationshipGoal_throwsParseException() {
        assertParseFailure(parser, "rg/", RelationshipGoal.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "rg/forever", RelationshipGoal.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "rg/long rg/short",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_RELATIONSHIP_GOAL));
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        for (String args : new String[] {"rg/long g/m", "r/Jainism rg/long", "Alice rg/long"}) {
            assertParseFailure(parser, args, usage);
        }
    }

    @Test
    public void parse_smoking_normalizesCaseAndSurroundingWhitespace() {
        for (String value : new String[] {"yes", "YES", "yEs", "no", "NO", "nO"}) {
            assertParseSuccess(parser, " \t s/ \t" + value + " \t ",
                    new FindCommand(new SmokingStatusMatchesPredicate(new SmokingStatus(value))));
        }
    }

    @Test
    public void parse_blankOrInvalidSmoking_throwsParseException() {
        for (String value : new String[] {"", "   ", "unknown", "sometimes", "true", "0", "y", "n",
            "yes,no", "yes no", "y es", "yeſ", "yes Alice", "yes x/no", "yes\ts/no"}) {
            assertParseFailure(parser, "s/" + value, SmokingStatus.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_repeatedSmokingPrefix_throwsParseException() {
        String message = Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_SMOKING);
        for (String args : new String[] {"s/yes s/no", "s/yes s/yes", "s/ s/no", "s/no s/"}) {
            assertParseFailure(parser, args, message);
        }
    }

    @Test
    public void parse_combinedSmokingSearchModes_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        for (String criterion : new String[] {"age/25", "g/m", "r/Jainism", "rg/long"}) {
            assertParseFailure(parser, "s/yes " + criterion, message);
            assertParseFailure(parser, criterion + " s/no", message);
        }
        assertParseFailure(parser, "Alice s/yes", message);
    }

    @Test
    public void parse_incorrectSmokingPrefixOrCriteriaEnvironment_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        for (String args : new String[] {"S/yes", "s /yes", "smoking/yes", "{s/yes}", "<s/no>"}) {
            assertParseFailure(parser, args, message);
        }
        assertParseFailure(parser, "s/yes {s/no}", SmokingStatus.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "s/no <s/yes>", SmokingStatus.MESSAGE_CONSTRAINTS);
    }
}
