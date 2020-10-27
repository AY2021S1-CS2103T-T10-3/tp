package chopchop.ui;

import chopchop.model.recipe.Recipe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * An UI component that displays information of a {@code Recipe}.
 */
public class RecipeCard extends UiPart<Region> {

    private static final String FXML = "RecipeCard.fxml";

    public final Recipe recipe;

    private final int id;

    @FXML
    private Label recipeName;

    @FXML
    private Label recipeIndex;



    /**
     * Creates a {@code RecipeCard} with the given {@code Recipe}.
     */
    public RecipeCard(Recipe recipe, int id) {
        super(FXML);
        this.recipe = recipe;
        this.id = id;
        recipeName.setText(recipe.getName());
        recipeIndex.setText(String.valueOf(id));
    }

    @FXML
    public void handleSelectRecipe(ActionEvent event) {
        DisplayNavigator.loadRecipeDisplay(recipe);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }
        // instanceof handles nulls
        if (!(other instanceof RecipeCard)) {
            return false;
        }
        // state check
        RecipeCard card = (RecipeCard) other;
        return recipe.equals(card.recipe);
    }
}
