// ItemReference.java

package chopchop.logic.parser;

import chopchop.commons.util.Either;
import chopchop.commons.util.Result;
import chopchop.commons.util.StringView;

public class ItemReference {

    private final Either<Integer, String> reference;

    private ItemReference(Either<Integer, String> ref) {
        this.reference = ref;
    }

    /**
     * Returns the zero-based index of the itemreference, if it was an indexed reference.
     */
    public Integer getZeroIndex() {
        return this.reference.fromLeft();
    }

    /**
     * Returns the lowercased name of the itemreference, if it was a named reference.
     */
    public String getName() {
        return this.reference.fromRight();
    }

    /**
     * Returns true iff the {@code ItemReference} was made with an index.
     */
    public boolean isIndexed() {
        return this.reference.isLeft();
    }

    /**
     * Returns true iff the {@code ItemReference} was made with a name.
     */
    public boolean isNamed() {
        return this.reference.isRight();
    }

    /**
     * Creates an {@code ItemReference} using the given zero-based index.
     *
     * @param idx the zero-based index
     * @return    an ItemReference
     */
    public static ItemReference ofZeroIndex(int idx) {
        return new ItemReference(Either.left(idx));
    }

    /**
     * Creates an {@code ItemReference} using the given one-based index.
     *
     * @param idx the one-based index
     * @return    an ItemReference
     */
    public static ItemReference ofOneIndex(int idx) {
        assert idx > 0;
        return new ItemReference(Either.left(idx - 1));
    }

    /**
     * Creates an {@code ItemReference} using the given name. Note that the name
     * is case-insensitive.
     *
     * @param name the name
     * @return     an ItemReference
     */
    public static ItemReference ofName(String name) {
        return new ItemReference(Either.right(name.toLowerCase()));
    }


    /**
     * Automatically parses an {@code ItemReference} given the string input. The denotation
     * for an indexed reference is {@code #3}, where '3' is a 1-based index (ie. it is the
     * third item here). Anything else not starting with a '#' is considered a name.
     */
    public static Result<ItemReference> parse(String input) {
        if (input.isEmpty()) {
            return Result.error("empty input");
        } else if (input.startsWith("#")) {
            return new StringView(input)
                .drop(1)
                .parseInt()
                .then(i -> {
                    if (i <= 0) {
                        return Result.error("invalid index (cannot be zero or negative)");
                    } else {
                        return Result.of(ItemReference.ofOneIndex(i));
                    }
                });
        } else {
            return Result.of(ItemReference.ofName(input));
        }
    }


    @Override
    public boolean equals(Object obj) {
        return this == obj
            || (obj instanceof ItemReference
                && ((ItemReference) obj).reference.equals(this.reference));
    }
}
