package chopchop.logic.commands;

import static chopchop.commons.util.Strings.ARG_QUANTITY;
import static java.util.Objects.requireNonNull;

import java.util.Optional;

import chopchop.commons.exceptions.IllegalValueException;
import chopchop.logic.commands.exceptions.CommandException;
import chopchop.logic.history.HistoryManager;
import chopchop.logic.parser.ItemReference;
import chopchop.model.Model;
import chopchop.model.attributes.Quantity;
import chopchop.model.exceptions.IncompatibleIngredientsException;
import chopchop.model.ingredient.Ingredient;

/**
 * Removes a given quantity of an ingredient identified using it's displayed index or name from the ingredient book.
 * If no quantity is specified, the ingredient will be deleted.
 */
public class DeleteIngredientCommand extends Command implements Undoable {
    public static final String COMMAND_WORD = "delete ingredient";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes the given quantity of an ingredient identified by the index number or name used in the "
            + "displayed ingredient list. "
            + "If no quantity is specified, the ingredient will be deleted. "
            + "Parameters: "
            + "INDEX (must be a positive integer) / NAME "
            + "[" + ARG_QUANTITY + " QUANTITY]\n"
            + "Example: " + COMMAND_WORD
            + " Chili "
            + ARG_QUANTITY + " 3";

    private final ItemReference item;
    private final Optional<Quantity> quantity;
    private Ingredient ingredient;
    private Ingredient updatedIngredient;

    public DeleteIngredientCommand(ItemReference item) {
        this(item, Optional.empty());
    }

    /**
     * Constructs a command that deletes the given ingredient item.
     */
    public DeleteIngredientCommand(ItemReference item, Optional<Quantity> quantity) {
        requireNonNull(item);
        this.item = item;
        this.quantity = quantity;
    }

    @Override
    public CommandResult execute(Model model, HistoryManager historyManager) throws CommandException {
        requireNonNull(model);

        var res = resolveIngredientReference(this.item, model);
        if (res.isError()) {
            return CommandResult.error(res.getError());
        }

        this.ingredient = res.getValue();

        if (this.quantity.isPresent()) {
            try {
                var splitIngredient = this.ingredient.split(this.quantity.get());
                this.updatedIngredient = splitIngredient.snd();

                if (this.updatedIngredient.getIngredientSets().isEmpty()) {
                    model.deleteIngredient(this.ingredient);
                } else {
                    model.setIngredient(this.ingredient, this.updatedIngredient);
                }

                return CommandResult.message("removed %s of ingredient '%s'", this.quantity.get().toString(),
                    this.updatedIngredient.getName());

            } catch (IncompatibleIngredientsException | IllegalValueException e) {
                return CommandResult.error(e.getMessage());
            }
        } else {
            model.deleteIngredient(this.ingredient);
            return CommandResult.message("deleted ingredient '%s'", this.ingredient.getName());
        }
    }

    @Override
    public CommandResult undo(Model model) {
        requireNonNull(model);

        String action = "";

        if (this.quantity.isEmpty() || this.updatedIngredient.getIngredientSets().isEmpty()) {
            model.addIngredient(this.ingredient);
            action = "re-added";
        } else {
            model.setIngredient(this.updatedIngredient, this.ingredient);
            action = "updated";
        }

        return CommandResult.message("undo: %s ingredient '%s'", action, this.ingredient.getName());
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof DeleteIngredientCommand
                && this.item.equals(((DeleteIngredientCommand) other).item)
                && this.quantity.equals(((DeleteIngredientCommand) other).quantity));
    }

    @Override
    public String toString() {
        return String.format("DeleteIngredientCommand(%s%s)", this.item,
            this.quantity.map(q -> String.format(" (%s)", q)).orElse(""));
    }
}
