package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} contains a phrase as consecutive whole words, ignoring letter case
 * and the amount of whitespace between words.
 */
public class NameContainsPhrasePredicate implements Predicate<Person> {
    private final List<String> phraseWords;

    /**
     * Creates a predicate for {@code phrase}, which must contain at least one word.
     */
    public NameContainsPhrasePredicate(String phrase) {
        requireNonNull(phrase);
        assert !phrase.isBlank() : "A name phrase must contain at least one word";
        this.phraseWords = toWords(phrase);
    }

    @Override
    public boolean test(Person person) {
        return Collections.indexOfSubList(toWords(person.getName().fullName), phraseWords) != -1;
    }

    private static List<String> toWords(String text) {
        return List.of(text.strip().toLowerCase().split("\\s+"));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsPhrasePredicate otherPredicate)) {
            return false;
        }

        return phraseWords.equals(otherPredicate.phraseWords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("phrase", String.join(" ", phraseWords)).toString();
    }
}
