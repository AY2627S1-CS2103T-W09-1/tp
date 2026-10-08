package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.Gender;
import seedu.address.model.person.GenderMatchesPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
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
}
