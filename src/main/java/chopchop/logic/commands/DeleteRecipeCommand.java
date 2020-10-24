package chopchop.logic.commands;

import static java.util.Objects.requireNonNull;

import chopchop.logic.commands.exceptions.CommandException;
import chopchop.logic.history.HistoryManager;
import chopchop.logic.parser.ItemReference;
import chopchop.model.Model;
import chopchop.model.recipe.Recipe;

/**
 * Deletes a recipe identified using it's displayed index or name from the recipe book.
 */
public class DeleteRecipeCommand extends Command implements Undoable {
    public static final String COMMAND_WORD = "delete recipe";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the recipe identified by the index number or name used in the displayed recipe list.\n"
            + "Parameters: INDEX (must be a positive integer) / NAME\n"
            + "Example: " + COMMAND_WORD + " 1";

    private final ItemReference item;
    private Recipe recipe;

    /**
     * Constructs a command that deletes the given recipe item.
     */
    public DeleteRecipeCommand(ItemReference item) {
        requireNonNull(item);
        this.item = item;
    }

    @Override
    public CommandResult execute(Model model, HistoryManager historyManager) throws CommandException {
        requireNonNull(model);

        var res = resolveRecipeReference(this.item, model);
        if (res.isError()) {
            return CommandResult.error(res.getError());
        }

        this.recipe = res.getValue();

        model.deleteRecipe(this.recipe);
        return CommandResult.message("deleted recipe '%s'", this.recipe.getName());
    }

    @Override
    public CommandResult undo(Model model) {
        requireNonNull(model);

        model.addRecipe(this.recipe);
        return CommandResult.message("undo: re-added recipe '%s'", this.recipe.getName());
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof DeleteRecipeCommand
                && this.item.equals(((DeleteRecipeCommand) other).item));
    }

    @Override
    public String toString() {
        return String.format("DeleteRecipeCommand(%s)", this.item);
    }
}
