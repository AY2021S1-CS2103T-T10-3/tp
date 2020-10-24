package chopchop.logic.commands;

import static chopchop.commons.util.Strings.ARG_QUANTITY;
import static java.util.Objects.requireNonNull;

import java.util.Optional;

import chopchop.commons.core.Messages;
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

    public static final String MESSAGE_DELETE_INGREDIENT_SUCCESS = "Ingredient deleted: %s";
    public static final String MESSAGE_REMOVE_INGREDIENT_SUCCESS = "Removed %s of '%s'";
    public static final String MESSAGE_INGREDIENT_NOT_FOUND = "No ingredient named '%s'";
    public static final String MESSAGE_UNDO_SUCCESS = "Ingredient updated: %s";

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

        if (this.item.isIndexed()) {
            var lastShownList = model.getFilteredIngredientList();

            if (this.item.getZeroIndex() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_INGREDIENT_DISPLAYED_INDEX);
            }

            this.ingredient = lastShownList.get(this.item.getZeroIndex());
        } else {
            this.ingredient = model
                    .findIngredientWithName(this.item.getName())
                    .orElseThrow(() -> new CommandException(String.format(MESSAGE_INGREDIENT_NOT_FOUND,
                            this.item.getName())));
        }

        if (this.quantity.isPresent()) {
            try {
                var splitIngredient = this.ingredient.split(this.quantity.get());
                this.updatedIngredient = splitIngredient.snd();

                if (this.updatedIngredient.getIngredientSets().isEmpty()) {
                    model.deleteIngredient(this.ingredient);
                } else {
                    model.setIngredient(this.ingredient, this.updatedIngredient);
                }

                return CommandResult.message(MESSAGE_REMOVE_INGREDIENT_SUCCESS,
                        this.quantity.get().toString(), this.updatedIngredient.getName());
            } catch (IncompatibleIngredientsException | IllegalValueException e) {
                throw new CommandException(e.getMessage());
            }
        } else {
            model.deleteIngredient(this.ingredient);
            return CommandResult.message(MESSAGE_DELETE_INGREDIENT_SUCCESS, this.ingredient);
        }
    }

    @Override
    public CommandResult undo(Model model) {
        requireNonNull(model);

        if (this.quantity.isEmpty() || this.updatedIngredient.getIngredientSets().isEmpty()) {
            model.addIngredient(this.ingredient);
        } else {
            model.setIngredient(this.updatedIngredient, this.ingredient);
        }

        return CommandResult.message(MESSAGE_UNDO_SUCCESS, this.ingredient);
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
