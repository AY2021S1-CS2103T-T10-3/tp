package chopchop.model.ingredient;

import static java.util.Objects.requireNonNull;

import chopchop.model.Entry;
import chopchop.model.attributes.Quantity;
import chopchop.util.Result;

/**
 * A reference to an ingredient. Not the actual {@code Ingredient}, and knows nothing about it.
 */
public class IngredientReference extends Entry {
    private final Quantity quantity;

    /**
     * Constructs an ingredient reference with the given name and quantity.
     */
    public IngredientReference(String name, Quantity quantity) {
        super(name);
        requireNonNull(quantity);
        this.quantity = quantity;
    }

    /**
     * Parse an IngredientReference.
     *
     * @param source String input.
     * @return the IngredientReference or an error message.
     */
    public static Result<IngredientReference> parse(String source) {
        var words = source.split(" \\(|\\)");
        if (words.length != 2) {
            return Result.error("Unable to parse string: %s", source);
        }
        var qtyResult = Quantity.parse(words[1]);
        if (qtyResult.isError()) {
            return Result.error(qtyResult.getError());
        }
        return Quantity.parse(words[1]).map(qty -> new IngredientReference(words[0], qty));
    }

    public Quantity getQuantity() {
        return this.quantity;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", this.name, this.quantity);
    }

    @Override
    public boolean isSame(Entry other) {
        return other == this
                || (other instanceof IngredientReference
                && this.name.equals(((IngredientReference) other).name));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof IngredientReference
                && this.name.equals(((IngredientReference) other).name)
                && this.quantity.equals(((IngredientReference) other).quantity));
    }
}
