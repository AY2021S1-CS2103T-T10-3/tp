package chopchop.ui;

import chopchop.model.recipe.Recipe;

/**
 * Utility class for controlling navigation between displays.
 *
 * All methods on the navigator are static to facilitate
 * simple access from the different displays.
 */
public class DisplayNavigator {

    private static DisplayController displayController;

    /**
     * Sets the displayController for the navigator.
     */
    protected static void setDisplayController(DisplayController displayController) {
        DisplayNavigator.displayController = displayController;
    }

    /**
     * Loads the RecipeDisplay onto the swappable display region.
     * @param recipe
     */
    protected static void loadRecipeDisplay(Recipe recipe) {
        displayController.displayRecipe(recipe);
    }

    /**
     * Loads the RecipeViewPanel onto the swappable display region.
     */
    public static void loadRecipePanel() {
        displayController.displayRecipeList();
    }

    /**
     * Loads the IngredientViewPanel into the swappable display region.
     */
    public static void loadIngredientPanel() {
        displayController.displayIngredientList();
    }

    /**
     * Loads the RecommendationViewPanel into the swappable display region.
     */
    public static void loadRecommendationPanel() {
        displayController.displayRecommendationList();
    }

    /**
     * Returns true iff there is a valid display controller.
     */
    public static boolean hasDisplayController() {
        return displayController != null;
    }
}
