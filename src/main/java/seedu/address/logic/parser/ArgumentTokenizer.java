package seedu.address.logic.parser;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tokenizes arguments string of the form: {@code preamble <prefix>value <prefix>value ...}<br>
 *     e.g. {@code some preamble text t/ 11.00 t/12.00 k/ m/ July}  where prefixes are {@code t/ k/ m/}.<br>
 * 1. An argument's value can be an empty string e.g. the value of {@code k/} in the above example.<br>
 * 2. Leading and trailing whitespaces of an argument value will be discarded.<br>
 * 3. An argument may be repeated and all its values will be accumulated e.g. the value of {@code t/}
 *    in the above example.<br>
 * 4. A value that starts with {@code "} is quoted: prefixes inside it are not recognized, and the quotes are
 *    removed, e.g. {@code n/"Ravi s/o Kumar"} gives the value {@code Ravi s/o Kumar}. The closing {@code "} must be
 *    followed by whitespace or the end of the string.<br>
 */
public class ArgumentTokenizer {

    public static final String MESSAGE_UNCLOSED_QUOTE = "A value that starts with \" must end with a matching \" "
            + "followed by a space or the end of the command, e.g. n/\"Ravi s/o Kumar\".";
    public static final String MESSAGE_TEXT_AFTER_QUOTE = "Unexpected text after a quoted value. "
            + "Put the whole value inside the quotes, e.g. n/\"Ravi s/o Kumar\".";

    private static final char QUOTE = '"';

    /**
     * Tokenizes an arguments string and returns an {@code ArgumentMultimap} object that maps prefixes to their
     * respective argument values. Only the given prefixes will be recognized in the arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes   Prefixes to tokenize the arguments string with
     * @return           ArgumentMultimap object that maps prefixes to their arguments
     * @throws ParseException if a quoted value is not closed or is followed by unquoted text
     */
    public static ArgumentMultimap tokenize(String argsString, Prefix... prefixes) throws ParseException {
        List<PrefixPosition> positions = findAllPrefixPositions(argsString, prefixes);
        return extractArguments(argsString, positions);
    }

    /**
     * Finds all zero-based prefix positions in the given arguments string, in order.
     * Prefixes inside a quoted value are skipped.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes   Prefixes to find in the arguments string
     * @return           List of zero-based prefix positions in the given arguments string
     * @throws ParseException if a quoted value has no closing quote
     */
    private static List<PrefixPosition> findAllPrefixPositions(String argsString, Prefix... prefixes)
            throws ParseException {
        List<PrefixPosition> positions = new ArrayList<>();
        // Cache each prefix's next occurrence so the string is scanned once per prefix, not once per value.
        int[] nextOccurrences = new int[prefixes.length];
        for (int i = 0; i < prefixes.length; i++) {
            nextOccurrences[i] = findPrefixPosition(argsString, prefixes[i].getPrefix(), 0);
        }
        int searchFrom = 0;
        PrefixPosition next = findNextPrefixPosition(argsString, searchFrom, prefixes, nextOccurrences);
        while (next != null) {
            positions.add(next);
            int valueStart = skipWhitespace(argsString, next.getValueStartPosition());
            if (valueStart < argsString.length() && argsString.charAt(valueStart) == QUOTE) {
                int closingQuote = findClosingQuote(argsString, valueStart);
                if (closingQuote == -1) {
                    throw new ParseException(MESSAGE_UNCLOSED_QUOTE);
                }
                next.setQuotedValue(valueStart, closingQuote);
                searchFrom = closingQuote;
            } else {
                searchFrom = next.getStartPosition();
            }
            next = findNextPrefixPosition(argsString, searchFrom, prefixes, nextOccurrences);
        }
        return positions;
    }

    /**
     * Returns the earliest valid occurrence of any of {@code prefixes} from index {@code fromIndex},
     * preferring the longest prefix when several start at the same position, or null if there is none.
     * {@code nextOccurrences} caches each prefix's next occurrence and is updated only when it falls before
     * {@code fromIndex}; {@code fromIndex} must not decrease between calls.
     */
    private static PrefixPosition findNextPrefixPosition(String argsString, int fromIndex, Prefix[] prefixes,
            int[] nextOccurrences) {
        PrefixPosition earliest = null;
        for (int i = 0; i < prefixes.length; i++) {
            Prefix prefix = prefixes[i];
            // An occurrence is valid only if its preceding space is at or after fromIndex.
            if (nextOccurrences[i] != -1 && nextOccurrences[i] - 1 < fromIndex) {
                nextOccurrences[i] = findPrefixPosition(argsString, prefix.getPrefix(), fromIndex);
            }
            int position = nextOccurrences[i];
            if (position == -1) {
                continue;
            }
            if (earliest == null || position < earliest.getStartPosition()
                    || position == earliest.getStartPosition()
                    && prefix.getPrefix().length() > earliest.getPrefix().getPrefix().length()) {
                earliest = new PrefixPosition(prefix, position);
            }
        }
        return earliest;
    }

    /**
     * Returns the index of the quote that closes the quoted value opened at {@code openingQuote}:
     * the next {@code "} followed by whitespace or the end of {@code argsString}. Returns -1 if there is none.
     */
    private static int findClosingQuote(String argsString, int openingQuote) {
        for (int i = openingQuote + 1; i < argsString.length(); i++) {
            boolean isAtValueEnd = i + 1 == argsString.length() || Character.isWhitespace(argsString.charAt(i + 1));
            if (argsString.charAt(i) == QUOTE && isAtValueEnd) {
                return i;
            }
        }
        return -1;
    }

    private static int skipWhitespace(String argsString, int fromIndex) {
        int index = fromIndex;
        while (index < argsString.length() && Character.isWhitespace(argsString.charAt(index))) {
            index++;
        }
        return index;
    }

    /**
     * Returns the index of the first occurrence of {@code prefix} in
     * {@code argsString} starting from index {@code fromIndex}. An occurrence
     * is valid if there is a whitespace before {@code prefix}. Returns -1 if no
     * such occurrence can be found.
     *
     * E.g if {@code argsString} = "e/hip/900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns -1 as there are no valid
     * occurrences of "p/" with whitespace before it. However, if
     * {@code argsString} = "e/hi p/900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns 5.
     */
    private static int findPrefixPosition(String argsString, String prefix, int fromIndex) {
        int prefixIndex = argsString.indexOf(" " + prefix, fromIndex);
        return prefixIndex == -1 ? -1
                : prefixIndex + 1; // +1 as offset for whitespace
    }

    /**
     * Extracts prefixes and their argument values, and returns an {@code ArgumentMultimap} object that maps the
     * extracted prefixes to their respective arguments. Prefixes are extracted based on their zero-based positions in
     * {@code argsString}.
     *
     * @param argsString      Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixPositions Zero-based positions of all prefixes in {@code argsString}
     * @return                ArgumentMultimap object that maps prefixes to their arguments
     */
    private static ArgumentMultimap extractArguments(String argsString, List<PrefixPosition> prefixPositions)
            throws ParseException {

        // Sort by start position
        prefixPositions.sort((prefix1, prefix2) -> prefix1.getStartPosition() - prefix2.getStartPosition());

        // Insert a PrefixPosition to represent the preamble
        PrefixPosition preambleMarker = new PrefixPosition(new Prefix(""), 0);
        prefixPositions.addFirst(preambleMarker);

        // Add a dummy PrefixPosition to represent the end of the string
        PrefixPosition endPositionMarker = new PrefixPosition(new Prefix(""), argsString.length());
        prefixPositions.add(endPositionMarker);

        // Map prefixes to their argument values (if any)
        ArgumentMultimap argMultimap = new ArgumentMultimap();
        for (int i = 0; i < prefixPositions.size() - 1; i++) {
            // Extract and store prefixes and their arguments
            Prefix argPrefix = prefixPositions.get(i).getPrefix();
            String argValue = extractArgumentValue(argsString, prefixPositions.get(i), prefixPositions.get(i + 1));
            argMultimap.put(argPrefix, argValue);
        }

        return argMultimap;
    }

    /**
     * Returns the trimmed value of the argument in the arguments string specified by {@code currentPrefixPosition}.
     * The end position of the value is determined by {@code nextPrefixPosition}.
     * A quoted value is returned without its quotes.
     *
     * @throws ParseException if unquoted text follows the closing quote of a quoted value
     */
    private static String extractArgumentValue(String argsString,
                                        PrefixPosition currentPrefixPosition,
                                        PrefixPosition nextPrefixPosition) throws ParseException {
        if (currentPrefixPosition.isQuoted()) {
            String textAfterQuote = argsString.substring(currentPrefixPosition.getClosingQuote() + 1,
                    nextPrefixPosition.getStartPosition());
            if (!textAfterQuote.isBlank()) {
                throw new ParseException(MESSAGE_TEXT_AFTER_QUOTE);
            }
            return argsString.substring(currentPrefixPosition.getOpeningQuote() + 1,
                    currentPrefixPosition.getClosingQuote()).trim();
        }

        String value = argsString.substring(currentPrefixPosition.getValueStartPosition(),
                nextPrefixPosition.getStartPosition());

        return value.trim();
    }

    /**
     * Represents a prefix's position in an arguments string, and the quotes around its value if it is quoted.
     */
    private static class PrefixPosition {
        private int startPosition;
        private final Prefix prefix;
        private int openingQuote = -1;
        private int closingQuote = -1;

        PrefixPosition(Prefix prefix, int startPosition) {
            this.prefix = prefix;
            this.startPosition = startPosition;
        }

        int getStartPosition() {
            return startPosition;
        }

        int getValueStartPosition() {
            return startPosition + prefix.getPrefix().length();
        }

        Prefix getPrefix() {
            return prefix;
        }

        void setQuotedValue(int openingQuote, int closingQuote) {
            this.openingQuote = openingQuote;
            this.closingQuote = closingQuote;
        }

        boolean isQuoted() {
            return openingQuote != -1;
        }

        int getOpeningQuote() {
            return openingQuote;
        }

        int getClosingQuote() {
            return closingQuote;
        }
    }

}
