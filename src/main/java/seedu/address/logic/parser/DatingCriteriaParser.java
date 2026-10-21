package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RELATIONSHIP_GOAL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RELIGION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SMOKING;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.DatingAttribute;

/**
 * Parses the preference ({@code {...}}) and dealbreaker ({@code <...>}) environments in a command.
 *
 * <p>Environments do not nest. Each may occur once, and may contain only dating-attribute prefixes.
 * The parser removes the environments from the returned outer arguments so that command parsers can
 * parse a client's own fields with {@link ArgumentTokenizer} as usual.</p>
 */
public final class DatingCriteriaParser {

    public static final String MESSAGE_INVALID_ENVIRONMENT = "Dating criteria must use one non-nested "
            + "{...} preference environment and one non-nested <...> dealbreaker environment.";
    public static final String MESSAGE_INVALID_CRITERION_PREFIX =
            "Dating criteria may use only age/, g/, s/, r/, or rg/.";
    private static final Set<Prefix> SUPPORTED_PREFIXES = Set.of(
            PREFIX_AGE, PREFIX_GENDER, PREFIX_SMOKING, PREFIX_RELIGION, PREFIX_RELATIONSHIP_GOAL);
    private static final Pattern PREFIX_TOKEN = Pattern.compile("(?:^|\\s)([A-Za-z]+/)");

    private DatingCriteriaParser() {}

    /**
     * Splits command arguments into ordinary arguments and optional partner criteria.
     *
     * @throws ParseException if an environment is malformed or contains invalid criteria
     */
    public static ParsedCriteria parse(String arguments) throws ParseException {
        requireNonNull(arguments);

        StringBuilder outsideArguments = new StringBuilder(arguments.length());
        StringBuilder environmentContents = new StringBuilder();
        Environment currentEnvironment = Environment.OUTSIDE;
        boolean preferencesSupplied = false;
        boolean dealbreakersSupplied = false;
        Map<DatingAttribute, String> preferences = Map.of();
        Map<DatingAttribute, String> dealbreakers = Map.of();

        for (int index = 0; index < arguments.length(); index++) {
            char character = arguments.charAt(index);
            if (currentEnvironment == Environment.OUTSIDE) {
                if (character == '{') {
                    if (preferencesSupplied) {
                        throw new ParseException(MESSAGE_INVALID_ENVIRONMENT);
                    }
                    preferencesSupplied = true;
                    currentEnvironment = Environment.PREFERENCES;
                    environmentContents.setLength(0);
                    outsideArguments.append(' ');
                } else if (character == '<') {
                    if (dealbreakersSupplied) {
                        throw new ParseException(MESSAGE_INVALID_ENVIRONMENT);
                    }
                    dealbreakersSupplied = true;
                    currentEnvironment = Environment.DEALBREAKERS;
                    environmentContents.setLength(0);
                    outsideArguments.append(' ');
                } else if (character == '}' || character == '>') {
                    throw new ParseException(MESSAGE_INVALID_ENVIRONMENT);
                } else {
                    outsideArguments.append(character);
                }
                continue;
            }

            if (character == '{' || character == '<') {
                throw new ParseException(MESSAGE_INVALID_ENVIRONMENT);
            }
            if (isClosingDelimiter(character, currentEnvironment)) {
                Map<DatingAttribute, String> criteria = parseEnvironment(environmentContents.toString());
                if (currentEnvironment == Environment.PREFERENCES) {
                    preferences = criteria;
                } else {
                    dealbreakers = criteria;
                }
                currentEnvironment = Environment.OUTSIDE;
                outsideArguments.append(' ');
            } else if (character == '}' || character == '>') {
                throw new ParseException(MESSAGE_INVALID_ENVIRONMENT);
            } else {
                environmentContents.append(character);
            }
        }

        if (currentEnvironment != Environment.OUTSIDE) {
            throw new ParseException(MESSAGE_INVALID_ENVIRONMENT);
        }

        return new ParsedCriteria(outsideArguments.toString(), preferences, dealbreakers,
                preferencesSupplied, dealbreakersSupplied);
    }

    private static boolean isClosingDelimiter(char character, Environment environment) {
        return (environment == Environment.PREFERENCES && character == '}')
                || (environment == Environment.DEALBREAKERS && character == '>');
    }

    private static Map<DatingAttribute, String> parseEnvironment(String contents) throws ParseException {
        if (contents.isBlank()) {
            return Map.of();
        }
        verifyOnlySupportedPrefixes(contents);

        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + contents, supportedPrefixes());
        if (!arguments.getPreamble().isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_CRITERION_PREFIX);
        }
        arguments.verifyNoDuplicatePrefixesFor(supportedPrefixes());

        Map<DatingAttribute, String> criteria = new LinkedHashMap<>();
        for (Prefix prefix : supportedPrefixes()) {
            if (arguments.getValue(prefix).isPresent()) {
                String value = arguments.getValue(prefix).get();
                if (value.isEmpty()) {
                    throw new ParseException(DatingAttribute.MESSAGE_EMPTY_VALUE);
                }
                criteria.put(toDatingAttribute(prefix), validateAndNormalize(prefix, value));
            }
        }
        return criteria;
    }

    private static void verifyOnlySupportedPrefixes(String contents) throws ParseException {
        Matcher matcher = PREFIX_TOKEN.matcher(contents);
        while (matcher.find()) {
            Prefix prefix = new Prefix(matcher.group(1));
            if (!SUPPORTED_PREFIXES.contains(prefix)) {
                throw new ParseException(MESSAGE_INVALID_CRITERION_PREFIX);
            }
        }
    }

    private static Prefix[] supportedPrefixes() {
        return new Prefix[] {PREFIX_AGE, PREFIX_GENDER, PREFIX_SMOKING, PREFIX_RELIGION,
            PREFIX_RELATIONSHIP_GOAL};
    }

    private static String validateAndNormalize(Prefix prefix, String value) throws ParseException {
        try {
            return toDatingAttribute(prefix).normalizeCriterion(value);
        } catch (IllegalArgumentException exception) {
            throw new ParseException(exception.getMessage(), exception);
        }
    }

    private static DatingAttribute toDatingAttribute(Prefix prefix) {
        if (prefix.equals(PREFIX_AGE)) {
            return DatingAttribute.AGE;
        }
        if (prefix.equals(PREFIX_GENDER)) {
            return DatingAttribute.GENDER;
        }
        if (prefix.equals(PREFIX_SMOKING)) {
            return DatingAttribute.SMOKING;
        }
        if (prefix.equals(PREFIX_RELIGION)) {
            return DatingAttribute.RELIGION;
        }
        return DatingAttribute.RELATIONSHIP_GOAL;
    }

    private enum Environment {
        OUTSIDE,
        PREFERENCES,
        DEALBREAKERS
    }

    /**
     * Parsed environments together with the ordinary command arguments.
     */
    public record ParsedCriteria(String outsideArguments, Map<DatingAttribute, String> preferences,
            Map<DatingAttribute, String> dealbreakers, boolean preferencesSupplied,
            boolean dealbreakersSupplied) {
        /** Makes defensive copies of the parsed preference and dealbreaker maps. */
        public ParsedCriteria {
            requireNonNull(outsideArguments);
            preferences = Map.copyOf(preferences);
            dealbreakers = Map.copyOf(dealbreakers);
        }
    }
}
