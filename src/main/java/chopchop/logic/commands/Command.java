// Command.java

package chopchop.logic.commands;

import chopchop.commons.util.Result;
import chopchop.logic.commands.exceptions.CommandException;
import chopchop.logic.history.HistoryManager;
import chopchop.logic.parser.ItemReference;
import chopchop.model.Model;
import chopchop.model.ingredient.Ingredient;
import chopchop.model.recipe.Recipe;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {

    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @param historyManager {@code History} which the command should record to.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    public abstract CommandResult execute(Model model, HistoryManager historyManager) throws CommandException;


    /**
     * Resolves the ingredient reference, or returns an error message.
     *
     * @param ref   the reference to resolve
     * @param model the model to use
     * @return      the found ingredient, or an error message
     */
    public Result<Ingredient> resolveIngredientReference(ItemReference ref, Model model) {

        if (ref.isIndexed()) {
            var lastShownList = model.getFilteredIngredientList();

            if (ref.getZeroIndex() >= lastShownList.size()) {
                return Result.error("Ingredient index '%d' is out of range (should be between 1 and %d)",
                    ref.getOneIndex(), lastShownList.size()
                );
            }

            return Result.of(lastShownList.get(ref.getZeroIndex()));

        } else {

            return Result.ofOptional(model.findIngredientWithName(ref.getName()),
                String.format("No ingredient named '%s'", ref.getName()));
        }
    }

    /**
     * Resolves the recipe reference, or returns an error message.
     *
     * @param ref   the reference to resolve
     * @param model the model to use
     * @return      the found recipe, or an error message
     */
    public Result<Recipe> resolveRecipeReference(ItemReference ref, Model model) {

        if (ref.isIndexed()) {
            var lastShownList = model.getFilteredRecipeList();

            if (ref.getZeroIndex() >= lastShownList.size()) {
                return Result.error("Recipe index '%d' is out of range (should be between 1 and %d)",
                    ref.getOneIndex(), lastShownList.size()
                );
            }

            return Result.of(lastShownList.get(ref.getZeroIndex()));

        } else {

            return Result.ofOptional(model.findRecipeWithName(ref.getName()),
                String.format("No recipe named '%s'", ref.getName()));
        }
    }
}
