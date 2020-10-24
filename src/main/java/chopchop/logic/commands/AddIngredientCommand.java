package chopchop.logic.commands;

import static chopchop.commons.util.Strings.ARG_EXPIRY;
import static chopchop.commons.util.Strings.ARG_QUANTITY;
import static java.util.Objects.requireNonNull;

import chopchop.logic.commands.exceptions.CommandException;
import chopchop.logic.history.HistoryManager;
import chopchop.model.Model;
import chopchop.model.exceptions.IncompatibleIngredientsException;
import chopchop.model.ingredient.Ingredient;

/**
 * Adds an ingredient to the ingredient book.
 */
public class AddIngredientCommand extends Command implements Undoable {
    public static final String COMMAND_WORD = "add ingredient";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds an ingredient to the ingredient book. "
            + "Parameters: "
            + "NAME "
            + ARG_QUANTITY + " QUANTITY "
            + ARG_EXPIRY + " EXPIRY\n"
            + "Example: " + COMMAND_WORD
            + " Chili "
            + ARG_QUANTITY + " 3 "
            + ARG_EXPIRY + " 2020-10-05";

    private final Ingredient ingredient;
    private Ingredient existingIngredient;
    private Ingredient combinedIngredient;

    /**
     * Creates an AddIngredientCommand to add the specified {@code Ingredient}
     */
    public AddIngredientCommand(Ingredient ingredient) {
        requireNonNull(ingredient);
        this.ingredient = ingredient;
    }

    @Override
    public CommandResult execute(Model model, HistoryManager historyManager) throws CommandException {
        requireNonNull(model);
        var existingIngredientOptional = model.findIngredientWithName(this.ingredient.getName());

        if (existingIngredientOptional.isPresent()) {
            this.existingIngredient = existingIngredientOptional.get();

            try {
                this.combinedIngredient = this.existingIngredient.combine(this.ingredient);
                model.setIngredient(this.existingIngredient, this.combinedIngredient);

            } catch (IncompatibleIngredientsException e) {
                return CommandResult.error(e.toString());
            }

            return CommandResult.message("updated ingredient '%s'", this.combinedIngredient.getName());

        } else {

            model.addIngredient(this.ingredient);
            return CommandResult.message("added ingredient '%s'", this.ingredient.getName());
        }
    }

    @Override
    public CommandResult undo(Model model) {
        requireNonNull(model);

        String action = "";
        Ingredient ingr = null;

        if (this.existingIngredient == null && this.combinedIngredient == null) {

            model.deleteIngredient(this.ingredient);

            ingr = this.ingredient;
            action = "removed";
        } else {
            model.setIngredient(this.combinedIngredient, this.existingIngredient);

            ingr = this.existingIngredient;
            action = "updated";
        }

        return CommandResult.message("undo: %s ingredient '%s'", action, ingr.getName());
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof AddIngredientCommand
                && this.ingredient.equals(((AddIngredientCommand) other).ingredient));
    }

    @Override
    public String toString() {
        return String.format("AddIngredientCommand: %s", this.ingredient);
    }
}
