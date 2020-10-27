package chopchop.ui;

import chopchop.logic.history.HistoryManager;
import chopchop.model.recipe.Recipe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * An UI component that displays information of a {@code Recipe}.
 */
public class RecommendationCard extends UiPart<Region> {

    private static final String FXML = "RecommendationCard.fxml";
    private static final String RECOMMENDED_MESSAGE = "These recommended recipes all contain ingredients that you " +
            "currently have.";
    private static final String EXPIRING_MESSAGE = "Consider cooking this recipe to use ingredients that are about to" +
            " expire.";
    private static final String OLD_MESSAGE = "Consider cooking this recipe that you haven't tried for a while.";

    public final Recipe recipe;

    private final int id;

    @FXML
    private TextArea recommendationText;

    @FXML
    private TextArea expiringRecipeText;

    @FXML
    private TextArea oldRecipeText;

    @FXML
    private HBox expiringRecipeContainer;

    @FXML
    private HBox oldRecipeContainer;

    /**
     * Creates a {@code RecipeCard} with the given {@code Recipe}.
     */
    public RecommendationCard(Recipe recipe, int id) {
        super(FXML);
        this.recipe = recipe;
        this.id = id;

        recommendationText.setText(RECOMMENDED_MESSAGE);
        RecipeCard expiringRecipeCard = new RecipeCard(recipe, id);
        expiringRecipeContainer.getChildren().add(expiringRecipeCard.getRoot());
        expiringRecipeText.setText(EXPIRING_MESSAGE);
        RecipeCard oldRecipeCard = new RecipeCard(recipe, id);
        oldRecipeContainer.getChildren().add(oldRecipeCard.getRoot());
        oldRecipeText.setText(OLD_MESSAGE);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }
        // instanceof handles nulls
        if (!(other instanceof RecommendationCard)) {
            return false;
        }
        // state check
        RecommendationCard card = (RecommendationCard) other;
        return recipe.equals(card.recipe);
    }
}
