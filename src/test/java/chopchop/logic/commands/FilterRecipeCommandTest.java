package chopchop.logic.commands;

import static chopchop.logic.commands.CommandTestUtil.assertCommandSuccess;
import static chopchop.testutil.TypicalRecipes.APRICOT_SALAD;
import static chopchop.testutil.TypicalRecipes.BANANA_SALAD;
import static chopchop.testutil.TypicalRecipes.getTypicalRecipeBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

import chopchop.model.attributes.IngredientsContainKeywordsPredicate;
import chopchop.model.attributes.TagContainsKeywordsPredicate;
import chopchop.model.usage.IngredientUsage;
import chopchop.model.usage.RecipeUsage;
import chopchop.model.EntryBook;
import chopchop.model.Model;
import chopchop.model.ModelManager;
import chopchop.model.UsageList;
import chopchop.model.UserPrefs;

public class FilterRecipeCommandTest {
    private Model model = new ModelManager(getTypicalRecipeBook(), new EntryBook<>(), new UsageList<RecipeUsage>(),
        new UsageList<IngredientUsage>(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalRecipeBook(), new EntryBook<>(),
        new UsageList<RecipeUsage>(), new UsageList<IngredientUsage>(), new UserPrefs());

    @Test
    public void equals() {
        var firstTagPredicate = new TagContainsKeywordsPredicate(Collections.singletonList("firstTag"));
        var secondTagPredicate = new TagContainsKeywordsPredicate(Collections.singletonList("secondTag"));

        var firstIngredientPredicate = new IngredientsContainKeywordsPredicate(
            Collections.singletonList("firstIngredient"));
        var secondIngredientPredicate = new IngredientsContainKeywordsPredicate(
            Collections.singletonList("secondIngredient"));

        var filterFirstCommand = new FilterRecipeCommand(firstTagPredicate, firstIngredientPredicate);
        var filterSecondCommand = new FilterRecipeCommand(firstTagPredicate, secondIngredientPredicate);
        var filterThirdCommand = new FilterRecipeCommand(secondTagPredicate, firstIngredientPredicate);
        var filterFourthCommand = new FilterRecipeCommand(secondTagPredicate, secondIngredientPredicate);

        // same object -> returns true
        assertTrue(filterFirstCommand.equals(filterFirstCommand));

        // same value -> returns true
        var filterFirstCommandCopy = new FilterRecipeCommand(firstTagPredicate, firstIngredientPredicate);
        assertTrue(filterFirstCommand.equals(filterFirstCommandCopy));

        // different types -> returns false
        assertFalse(filterFirstCommand.equals(1));

        // null -> returns false
        assertFalse(filterFirstCommand.equals(null));

        // different values -> returns false
        assertFalse(filterFirstCommand.equals(filterSecondCommand));
        assertFalse(filterFirstCommand.equals(filterThirdCommand));
        assertFalse(filterFirstCommand.equals(filterFourthCommand));
    }

    @Test
    public void execute_multipleTags_noRecipeFound() {
        var tagPredicate = prepareTagPredicate("Salad", "Spicy");
        var command = new FilterRecipeCommand(tagPredicate, null);
        expectedModel.updateFilteredRecipeList(tagPredicate);
        assertCommandSuccess(command, model, expectedModel);
        assertEquals(Arrays.asList(), model.getFilteredRecipeList());
    }

    @Test
    public void execute_multipleTags_multipleRecipesFound() {
        var tagPredicate = prepareTagPredicate("Salad", "Cold Food");
        var command = new FilterRecipeCommand(tagPredicate, null);
        expectedModel.updateFilteredRecipeList(tagPredicate);
        assertCommandSuccess(command, model, expectedModel);
        assertEquals(Arrays.asList(APRICOT_SALAD, BANANA_SALAD), model.getFilteredRecipeList());
    }

    @Test
    public void execute_multipleIngredients_moreThanZeroRecipeFound() {
        var indPredicate = prepareIngredientPredicate("Apricot", "Custard");
        var command = new FilterRecipeCommand(null, indPredicate);
        expectedModel.updateFilteredRecipeList(indPredicate);
        assertCommandSuccess(command, model, expectedModel);
        assertEquals(Arrays.asList(APRICOT_SALAD), model.getFilteredRecipeList());
    }

    @Test
    public void execute_multipleTagsIngredients_multipleRecipesFound() {
        var tagPredicate = prepareTagPredicate("Salad", "Cold Food");
        var indPredicate = prepareIngredientPredicate("Custard");
        var command = new FilterRecipeCommand(tagPredicate, indPredicate);
        expectedModel.updateFilteredRecipeList(indPredicate.and(tagPredicate));
        assertCommandSuccess(command, model, expectedModel);
        assertEquals(Arrays.asList(APRICOT_SALAD, BANANA_SALAD), model.getFilteredRecipeList());
    }

    @Test
    public void execute_multipleTagsIngredients_noRecipeFound() {
        var tagPredicate = prepareTagPredicate("Spicy", "Salad");
        var indPredicate = prepareIngredientPredicate("Custard");
        var command = new FilterRecipeCommand(tagPredicate, indPredicate);
        expectedModel.updateFilteredRecipeList(indPredicate.and(tagPredicate));
        assertCommandSuccess(command, model, expectedModel);
        assertEquals(Arrays.asList(), model.getFilteredRecipeList());
    }

    /**
     * Parses {@code userInputs} into a {@code TagContainsKeywordsPredicate}.
     */
    private TagContainsKeywordsPredicate prepareTagPredicate(String... userInputs) {
        return new TagContainsKeywordsPredicate(Arrays.asList(userInputs));
    }

    /**
     * Parses {@code userInputs} into a {@code IngredientContainsKeywordsPredicate}.
     */
    private IngredientsContainKeywordsPredicate prepareIngredientPredicate(String... userInputs) {
        return new IngredientsContainKeywordsPredicate(Arrays.asList(userInputs));
    }
}
