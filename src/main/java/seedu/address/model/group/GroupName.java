package seedu.address.model.group;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/** A case-insensitive group identifier that retains its spelling for display. */
public final class GroupName {
    public static final String MESSAGE_CONSTRAINTS = "Group names must contain 1-30 ASCII letters, digits, hyphens or "
            + "underscores, beginning with a letter or digit (no spaces).";
    private final String value;
    private final String key;

    /** Creates a name without silently discarding invalid whitespace. */
    public GroupName(String value) {
        requireNonNull(value);
        checkArgument(value.matches("[A-Za-z0-9][A-Za-z0-9_-]{0,29}"), MESSAGE_CONSTRAINTS);
        this.value = value;
        key = value.toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof GroupName name && key.equals(name.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
