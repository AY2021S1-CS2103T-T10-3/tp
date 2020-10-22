package chopchop.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import chopchop.commons.exceptions.IllegalValueException;
import chopchop.commons.util.Result;
import chopchop.model.attributes.Name;
import chopchop.model.attributes.Quantity;
import chopchop.model.ingredient.IngredientReference;

public class JsonAdaptedIngredientReference {
    public static final String INGREDIENT_REFERENCE_MISSING_FIELD_MESSAGE_FORMAT =
            "Ingredient reference's %s field is missing!";

    private final String name;
    private final String quantity;

    /**
     * Constructs a {@code JsonAdaptedIngredientReference} with the given ingredient reference details.
     *
     * @param name name of ingredient.
     * @param quantity just quantity to string.
     */
    @JsonCreator
    public JsonAdaptedIngredientReference(@JsonProperty("name") String name,
                                          @JsonProperty("quantity") String quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Converts a given {@code IngredientReference} into this class for Jackson use.
     * @param source
     */
    public JsonAdaptedIngredientReference(IngredientReference source) {
        this.name = source.getName();
        this.quantity = source.getQuantity().toString();
    }

    /**
     * Converts this into Jackson-friendly adapted ingredient reference object into the model's
     * {@code IngredientReference} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted reference.
     */
    public IngredientReference toModelType() throws IllegalValueException {
        if (this.name == null) {
            throw new IllegalValueException(String.format(INGREDIENT_REFERENCE_MISSING_FIELD_MESSAGE_FORMAT,
                Name.class.getSimpleName()));
        }

        if (this.quantity == null) {
            throw new IllegalValueException(String.format(INGREDIENT_REFERENCE_MISSING_FIELD_MESSAGE_FORMAT,
                Quantity.class.getSimpleName()));
        }
        Result<Quantity> modelQuantity = Quantity.parse(this.quantity);
        if (modelQuantity.isError()) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }

        return new IngredientReference(this.name, modelQuantity.getValue());
    }
}
