package chopchop.model.attributes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import chopchop.model.ingredient.IngredientReference;
import chopchop.testutil.RecipeBuilder;
import org.junit.jupiter.api.Test;

public class IngredientsContainKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("firstIngredient");
        List<String> secondPredicateKeywordList = Arrays.asList("firstIngredient", "secondIngredient");

        IngredientsContainKeywordsPredicate firstPredicate = new IngredientsContainKeywordsPredicate(
            firstPredicateKeywordList);
        IngredientsContainKeywordsPredicate secondPredicate = new IngredientsContainKeywordsPredicate(
            secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        IngredientsContainKeywordsPredicate firstPredicateCopy = new IngredientsContainKeywordsPredicate(
            firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different values -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_ingredientsContainKeywords_returnsTrue() {
        // One keyword
        IngredientsContainKeywordsPredicate predicate = new IngredientsContainKeywordsPredicate(
                Collections.singletonList("white"));
        assertTrue(predicate.test(new RecipeBuilder().withIngredients(
            Arrays.asList(new IngredientReference("white", Optional.empty()))).build()));

        // Partially matching keywords (keywords are contained in the ingredient list)
        predicate = new IngredientsContainKeywordsPredicate(Collections.singletonList("white"));
        assertTrue(predicate.test(new RecipeBuilder().withIngredients(
            Arrays.asList(new IngredientReference("white vinegar", Optional.empty()),
                new IngredientReference("snowWhite", Optional.empty()))).build()));
        assertTrue(predicate.test(new RecipeBuilder().withIngredients(
            Arrays.asList(new IngredientReference("white chocolate", Optional.empty()))).build()));

        // Multiple keywords
        predicate = new IngredientsContainKeywordsPredicate(Arrays.asList("white", "chocolate"));
        assertTrue(predicate.test(new RecipeBuilder().withIngredients(
            Arrays.asList(new IngredientReference("white", Optional.empty()),
                new IngredientReference("chocolate", Optional.empty()))).build()));
        assertTrue(predicate.test(new RecipeBuilder().withIngredients(
            Arrays.asList(new IngredientReference("white chocolate", Optional.empty()))).build()));

        // Mixed-case keywords
        predicate = new IngredientsContainKeywordsPredicate(Arrays.asList("wHIte", "ChocolaTE"));
        assertTrue(predicate.test(new RecipeBuilder().withIngredients(
            Arrays.asList(new IngredientReference("WhiTE", Optional.empty()),
                new IngredientReference("cHOCOLAte", Optional.empty()))).build()));
        assertTrue(predicate.test(new RecipeBuilder().withIngredients(
                Arrays.asList(new IngredientReference("WHITE CHOCOLATE", Optional.empty()))).build()));

        // Combined keywords
        predicate = new IngredientsContainKeywordsPredicate(Arrays.asList("white chocolate"));
        assertTrue(predicate.test(new RecipeBuilder().withIngredients(
            Arrays.asList(new IngredientReference("white chocolate", Optional.empty()),
                new IngredientReference("sweet white chocolate powder", Optional.empty()))).build()));
        assertTrue(predicate.test(new RecipeBuilder().withIngredients(
            Arrays.asList(new IngredientReference("white chocolate", Optional.empty()))).build()));
    }

    @Test
    public void test_ingredientsDoNotContainKeywords_noMatchingTagReturnsFalse() {
        IngredientsContainKeywordsPredicate predicate = new IngredientsContainKeywordsPredicate(
            Collections.singletonList("white chocolate"));
        assertFalse(predicate.test(new RecipeBuilder().withIngredients(Arrays.asList(
            new IngredientReference("green beans", Optional.empty()),
            new IngredientReference("oranges", Optional.empty()))).build()));
    }

    @Test
    public void test_ingredientsDoNotContainKeywords_partiallyMatchingTagReturnsFalse() {
        // keywords are only partially contained in any the ingredients
        IngredientsContainKeywordsPredicate predicate = new IngredientsContainKeywordsPredicate(
            Collections.singletonList("white chocolate"));
        assertFalse(predicate.test(new RecipeBuilder().withIngredients(Arrays.asList(
            new IngredientReference("white", Optional.empty()),
            new IngredientReference("chocolate", Optional.empty()))).build()));
    }

    @Test
    public void test_ingredientsDoesNotContainKeywords_onlyMatchingOtherCriteriaReturnsFalse() {
        // keywords match other fields but not ingredients
        IngredientsContainKeywordsPredicate predicate = new IngredientsContainKeywordsPredicate(
            Collections.singletonList("white chocolate 123 *"));
        assertFalse(predicate.test(new RecipeBuilder()
            .withName("white chocolate 123 *")
            .withIngredients(Arrays.asList(
                new IngredientReference("white chocolate", Optional.empty()),
                new IngredientReference("123 *", Optional.empty())))
            .withSteps(Arrays.asList(new Step("white chocolate")))
            .withTags(new HashSet<>(Arrays.asList(new Tag("white"), new Tag("chocolate"))))
            .build()));
    }
}
