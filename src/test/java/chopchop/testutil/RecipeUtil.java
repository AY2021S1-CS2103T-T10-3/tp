package chopchop.testutil;

import static chopchop.commons.util.Strings.ARG_INGREDIENT;
import static chopchop.commons.util.Strings.ARG_STEP;

import chopchop.logic.commands.AddRecipeCommand;
import chopchop.model.recipe.Recipe;

/**
 * A utility class for Ingredient.
 */
public class RecipeUtil {

    /**
     * Returns an add command string for adding the {@code ingredient}.
     */
    public static String getAddCommand(Recipe recipe) {
        return AddRecipeCommand.COMMAND_WORD + " " + getRecipeDetails(recipe);
    }

    /**
     * Returns the part of command string for the given {@code ingredient}'s details.
     */
    public static String getRecipeDetails(Recipe recipe) {
        StringBuilder sb = new StringBuilder();
        sb.append(recipe.getName() + " ");
        recipe.getIngredients().stream().forEach(ind ->
                        sb.append(ARG_INGREDIENT + " " + ind.getQuantity().toString() + " "));
        recipe.getSteps().stream().forEach(step ->
                sb.append(ARG_STEP + step.toString() + " "));
        return sb.toString();
    }

}
