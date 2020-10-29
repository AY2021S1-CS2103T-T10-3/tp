//@@author fall9x

package chopchop.ui;

import chopchop.model.recipe.Recipe;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * An UI component that displays information of a {@code Recipe}.
 */
public class RecipeCard extends UiPart<Region> {
    private static final String FXML = "RecipeCard.fxml";

    public final Recipe recipe;

    @FXML
    private Button recipeButton;

    @FXML
    private Label index;

    /**
     * Creates a {@code RecipeCard} with the given {@code Recipe}.
     */
    public RecipeCard(Recipe recipe, int id) {
        super(FXML);
        this.recipe = recipe;

        this.recipeButton.setText(recipe.getName());
        this.index.setText(String.format("#%d", id));
    }

    @FXML
    public void handleSelectRecipe() {
        DisplayNavigator.loadRecipeDisplay(this.recipe);
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
        return this.recipe.equals(card.recipe);
    }
}
