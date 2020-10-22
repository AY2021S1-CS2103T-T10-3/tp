// JsonAdaptedIngredientSet.java

package chopchop.storage;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import chopchop.commons.exceptions.IllegalValueException;
import chopchop.commons.util.Pair;
import chopchop.model.attributes.ExpiryDate;
import chopchop.model.attributes.Quantity;
import chopchop.model.ingredient.Ingredient;

public class JsonAdaptedIngredientSet {

    private final List<JsonAdaptedPair> sets;

    /**
     * Constructs a {@code JsonAdaptedIngredientSet} with the given ingredient details.
     */
    @JsonCreator
    public JsonAdaptedIngredientSet(@JsonProperty("sets") List<JsonAdaptedPair> sets) {
        this.sets = sets;
    }

    /**
     * Converts a given ingredient set into this class for Jackson use.
     */
    public JsonAdaptedIngredientSet(TreeMap<Optional<ExpiryDate>, Quantity> source) {
        this.sets = source
            .entrySet()
            .stream()
            .map(e -> Pair.of(e.getKey(), e.getValue()))
            .map(JsonAdaptedPair::new)
            .collect(Collectors.toList());
    }

    /**
     * Converts this from the json thingy to the real thingy.
     */
    public TreeMap<Optional<ExpiryDate>, Quantity> toModelType() throws IllegalValueException {

        var missingMsg = JsonAdaptedIngredient.INGREDIENT_MISSING_FIELD_MESSAGE_FORMAT;

        if (this.sets == null) {
            throw new IllegalValueException(String.format(missingMsg, "sets"));
        }

        var ret = new TreeMap<Optional<ExpiryDate>, Quantity>(Ingredient.SET_COMPARATOR);

        // again, can't use lambdas here because eXcEpTiOnS
        for (var pair : this.sets) {
            var m = pair.toModelType();
            ret.put(m.fst(), m.snd());
        }

        return ret;
    }


    // this is dumb.
    static class JsonAdaptedPair {

        private final String quantity;
        private final String expiryDate;

        /**
         * Constructs a {@code JsonAdaptedPair} with the given ingredient details.
         */
        @JsonCreator
        public JsonAdaptedPair(@JsonProperty("quantity") String quantity,
                               @JsonProperty("expiryDate") String expiryDate) {

            this.quantity = quantity;
            this.expiryDate = expiryDate;
        }

        /**
         * Converts a given {@code Ingredient} into this class for Jackson use.
         */
        public JsonAdaptedPair(Pair<Optional<ExpiryDate>, Quantity> source) {
            this.expiryDate = source.fst().map(ExpiryDate::toString).orElse("");
            this.quantity = source.snd().toString();
        }

        /**
         * Converts this from the json thingy to the real thingy.
         */
        public Pair<Optional<ExpiryDate>, Quantity> toModelType() throws IllegalValueException {

            var missingMsg = JsonAdaptedIngredient.INGREDIENT_MISSING_FIELD_MESSAGE_FORMAT;

            if (this.quantity == null) {
                throw new IllegalValueException(String.format(missingMsg, Quantity.class.getSimpleName()));
            }

            if (this.expiryDate == null) {
                throw new IllegalValueException(String.format(missingMsg, ExpiryDate.class.getSimpleName()));
            }

            var qty = Quantity.parse(this.quantity);
            if (qty.isError()) {
                throw new IllegalValueException(qty.getError());
            }

            if (this.expiryDate.isBlank()) {
                return Pair.of(Optional.empty(), qty.getValue());

            } else {

                if (!ExpiryDate.isValidDate(this.expiryDate)) {
                    throw new IllegalValueException(ExpiryDate.MESSAGE_CONSTRAINTS);
                }

                var exp = new ExpiryDate(this.expiryDate);
                return Pair.of(Optional.of(exp), qty.getValue());
            }
        }
    }
}
