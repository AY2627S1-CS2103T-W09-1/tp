package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXCLUDED_RELIGION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PREFERRED_RELIGION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RELATIONSHIP_GOAL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RELIGION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REQUIRED_RELIGION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SMOKING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Religion;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG,
                        PREFIX_GENDER, PREFIX_AGE, PREFIX_SMOKING,
                        PREFIX_RELIGION, PREFIX_PREFERRED_RELIGION, PREFIX_REQUIRED_RELIGION,
                        PREFIX_EXCLUDED_RELIGION, PREFIX_RELATIONSHIP_GOAL);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_GENDER, PREFIX_SMOKING, PREFIX_AGE, PREFIX_RELIGION, PREFIX_PREFERRED_RELIGION,
                PREFIX_REQUIRED_RELIGION, PREFIX_RELATIONSHIP_GOAL);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            editPersonDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            editPersonDescriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }
        if (argMultimap.getValue(PREFIX_GENDER).isPresent()) {
            editPersonDescriptor.setGender(ParserUtil.parseGender(argMultimap.getValue(PREFIX_GENDER).get()));
        }
        if (argMultimap.getValue(PREFIX_AGE).isPresent()) {
            editPersonDescriptor.setAge(ParserUtil.parseAge(argMultimap.getValue(PREFIX_AGE).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);
        if (argMultimap.getValue(PREFIX_SMOKING).isPresent()) {
            editPersonDescriptor.setSmokingStatus(ParserUtil.parseSmokingStatus(argMultimap.getValue(PREFIX_SMOKING)
                    .get()));
        }
        if (argMultimap.getValue(PREFIX_RELIGION).isPresent()) {
            String value = argMultimap.getValue(PREFIX_RELIGION).get();
            editPersonDescriptor.setReligion(value.isEmpty() ? null : ParserUtil.parseReligion(value));
        }
        if (argMultimap.getValue(PREFIX_PREFERRED_RELIGION).isPresent()) {
            String value = argMultimap.getValue(PREFIX_PREFERRED_RELIGION).get();
            editPersonDescriptor.setPreferredReligion(value.isEmpty() ? null : ParserUtil.parseReligion(value));
        }
        if (argMultimap.getValue(PREFIX_REQUIRED_RELIGION).isPresent()) {
            String value = argMultimap.getValue(PREFIX_REQUIRED_RELIGION).get();
            editPersonDescriptor.setRequiredReligion(value.isEmpty() ? null : ParserUtil.parseReligion(value));
        }
        parseExcludedReligionsForEdit(argMultimap.getAllValues(PREFIX_EXCLUDED_RELIGION))
                .ifPresent(editPersonDescriptor::setExcludedReligions);
        if (argMultimap.getValue(PREFIX_RELATIONSHIP_GOAL).isPresent()) {
            String value = argMultimap.getValue(PREFIX_RELATIONSHIP_GOAL).get();
            editPersonDescriptor.setRelationshipGoal(value.isEmpty() ? null : ParserUtil.parseRelationshipGoal(value));
        }

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Parses excluded religions for edit, including a sole empty prefix to clear the set.
     *
     * @throws ParseException if any exclusion is invalid or repeated
     */
    private Optional<Set<Religion>> parseExcludedReligionsForEdit(
            Collection<String> values) throws ParseException {
        if (values.isEmpty()) {
            return Optional.empty();
        }
        if (values.size() == 1 && values.contains("")) {
            return Optional.of(Collections.emptySet());
        }
        return Optional.of(ParserUtil.parseExcludedReligions(values));
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contains only one empty element, it resets the person's tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}
