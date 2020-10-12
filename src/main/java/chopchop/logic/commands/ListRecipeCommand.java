// ListRecipeCommand.java

package chopchop.logic.commands;

import chopchop.model.Model;

import static java.util.Objects.requireNonNull;
import static chopchop.model.Model.PREDICATE_SHOW_ALL_RECIPES;

/**
 * Lists all recipes in the recipe book to the user.
 */
public class ListRecipeCommand extends Command {

    public static final String MESSAGE_SUCCESS = "Listed all recipes";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredRecipeList(PREDICATE_SHOW_ALL_RECIPES);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
