package chopchop.logic.recommendation;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

import chopchop.model.Model;
import chopchop.model.exceptions.IncompatibleIngredientsException;
import chopchop.model.ingredient.Ingredient;
import chopchop.model.recipe.Recipe;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class RecommendationManager {
    private final FilteredList<Recipe> recommendedRecipes;
    private final FilteredList<Recipe> expiringRecipes;
    private final FilteredList<Recipe> oldRecipes;

    /**
     * Recommendation Manager for recommendations.
     */
    public RecommendationManager(Model model) {
        this.recommendedRecipes = new FilteredList<>(model.getRecipeBook().getEntryList(),
                this.getRecommendedRecipesPredicate(model));
        this.expiringRecipes = new FilteredList<>(
                new SortedList<>(
                        model.getRecipeBook().getEntryList(),
                        this.getExpiringRecipesComparator(model)),
                this.getExpiringRecipesPredicate(model));
        this.oldRecipes = new FilteredList<>(model.getRecipeBook().getEntryList());

        model.getIngredientBook().getEntryList().addListener((ListChangeListener<Ingredient>) ingredient -> {
            this.recommendedRecipes.setPredicate(this.getRecommendedRecipesPredicate(model));
            this.expiringRecipes.setPredicate(this.getExpiringRecipesPredicate(model));
        });
    }

    public FilteredList<Recipe> getRecommendedRecipeList() {
        return this.recommendedRecipes;
    }

    public FilteredList<Recipe> getExpiringRecipeList() {
        return this.expiringRecipes;
    }

    private Predicate<Recipe> getRecommendedRecipesPredicate(Model model) {
        return recipe -> recipe.getIngredients().stream()
                .allMatch(ingredientRef -> model.findIngredientWithName(ingredientRef.getName())
                        .map(ingredient -> {
                            try {
                                return ingredientRef.getQuantity().compareTo(ingredient.getQuantity()) <= 0;
                            } catch (IncompatibleIngredientsException e) {
                                return false;
                            }
                        }).orElse(false));
    }

    private Predicate<Recipe> getExpiringRecipesPredicate(Model model) {
        return this.getRecommendedRecipesPredicate(model).and(recipe -> recipe.getIngredients().stream()
                .anyMatch(ingredientRef -> model.findIngredientWithName(ingredientRef.getName())
                        .flatMap(ingredient -> ingredient.getExpiryDate()
                                .map(expiryDate -> LocalDate.now().plusDays(7).isAfter(expiryDate.getDate())))
                        .orElse(false)));
    }

    private Comparator<Recipe> getExpiringRecipesComparator(Model model) {
        return (a, b) -> {
            var expiryA = a.getIngredients().stream()
                    // map all IngredientReferences to Ingredients
                    .map(ingredientRef -> model.findIngredientWithName(ingredientRef.getName())
                            // get the expiry date if present
                            .flatMap(Ingredient::getExpiryDate))
                    // Compare all the expiry dates and get the earliest
                    .reduce(Optional.empty(), (result, ingredient) ->
                            Ingredient.SET_COMPARATOR.compare(ingredient, result) < 0 ? ingredient : result);
            var expiryB = b.getIngredients().stream()
                    .map(ingredientRef -> model.findIngredientWithName(ingredientRef.getName())
                            .flatMap(Ingredient::getExpiryDate))
                    .reduce(Optional.empty(), (result, ingredient) ->
                            Ingredient.SET_COMPARATOR.compare(ingredient, result) < 0 ? ingredient : result);

            return Ingredient.SET_COMPARATOR.compare(expiryA, expiryB);
        };
    }
}
