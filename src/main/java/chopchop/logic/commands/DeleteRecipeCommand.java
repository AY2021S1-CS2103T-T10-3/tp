package chopchop.logic.commands;

import static java.util.Objects.requireNonNull;

import chopchop.commons.core.Messages;
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

    public static final String MESSAGE_DELETE_RECIPE_SUCCESS = "Recipe deleted: %s";
    public static final String MESSAGE_RECIPE_NOT_FOUND = "No recipe named '%s'";
    public static final String MESSAGE_UNDO_SUCCESS = "Recipe added: %s";

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

        if (this.item.isIndexed()) {
            var lastShownList = model.getFilteredRecipeList();

            if (this.item.getZeroIndex() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_RECIPE_DISPLAYED_INDEX);
            }

            this.recipe = lastShownList.get(this.item.getZeroIndex());
        } else {
            this.recipe = model
                    .findRecipeWithName(this.item.getName())
                    .orElseThrow(() -> new CommandException(String.format(MESSAGE_RECIPE_NOT_FOUND,
                            this.item.getName())));
        }

        model.deleteRecipe(this.recipe);
        return CommandResult.message(MESSAGE_DELETE_RECIPE_SUCCESS, this.recipe);
    }

    @Override
    public CommandResult undo(Model model) {
        requireNonNull(model);

        model.addRecipe(this.recipe);
        return CommandResult.message(MESSAGE_UNDO_SUCCESS, this.recipe);
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
