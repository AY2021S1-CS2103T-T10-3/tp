package chopchop.logic.commands;

import static chopchop.commons.util.Strings.ARG_INGREDIENT;
import static chopchop.commons.util.Strings.ARG_QUANTITY;
import static chopchop.commons.util.Strings.ARG_STEP;
import static java.util.Objects.requireNonNull;

import chopchop.logic.commands.exceptions.CommandException;
import chopchop.logic.history.HistoryManager;
import chopchop.model.Model;
import chopchop.model.recipe.Recipe;

/**
 * Adds a recipe to the recipe book.
 */
public class AddRecipeCommand extends Command implements Undoable {
    public static final String COMMAND_WORD = "add recipe";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a recipe to the recipe book. "
            + "Parameters: "
            + "NAME "
            + "[" + ARG_INGREDIENT + " INGREDIENT [" + ARG_QUANTITY + " QUANTITY]]... "
            + "[" + ARG_STEP + " STEP]...\n"
            + "Example: " + COMMAND_WORD
            + " Sugar Tomato "
            + ARG_INGREDIENT + " Sugar "
            + ARG_INGREDIENT + " Tomato " + ARG_QUANTITY + " 5 "
            + ARG_STEP + " Chop tomatoes. "
            + ARG_STEP + " Add sugar to it and mix well.";

    private final Recipe recipe;

    /**
     * Creates an AddRecipeCommand to add the specified {@code Recipe}
     */
    public AddRecipeCommand(Recipe recipe) {
        requireNonNull(recipe);
        this.recipe = recipe;
    }

    @Override
    public CommandResult execute(Model model, HistoryManager historyManager) throws CommandException {
        requireNonNull(model);

        if (model.hasRecipe(this.recipe)) {
            return CommandResult.error("Recipe '%s' already exists", this.recipe.getName());
        }

        model.addRecipe(this.recipe);
        return CommandResult.message("Added recipe '%s'", this.recipe.getName());
    }

    @Override
    public CommandResult undo(Model model) {
        requireNonNull(model);

        model.deleteRecipe(this.recipe);
        return CommandResult.message("Undo: removed recipe '%s'", this.recipe.getName());
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof AddRecipeCommand
                && this.recipe.equals(((AddRecipeCommand) other).recipe));
    }

    @Override
    public String toString() {
        return String.format("AddRecipeCommand: %s", this.recipe);
    }
}


