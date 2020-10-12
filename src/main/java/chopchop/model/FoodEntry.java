package chopchop.model;

import static java.util.Objects.requireNonNull;

import chopchop.model.attributes.Name;
import seedu.address.model.tag.Tag;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class FoodEntry {
    protected final Name name;
    protected final Set<Tag> tags = new HashSet<>();

    protected FoodEntry(Name name, Set<Tag> tags) {
        requireNonNull(name);
        this.name = name;
        this.tags.addAll(tags);
    }

    protected FoodEntry(Name name) {
        requireNonNull(name);
        this.name = name;
    }

    public Name getName() {
        return name;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public abstract int hashCode();
}
