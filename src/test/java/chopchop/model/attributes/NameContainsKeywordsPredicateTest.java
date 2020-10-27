package chopchop.model.attributes;

import static chopchop.testutil.TypicalIngredients.BANANA_REF;
import static chopchop.testutil.TypicalRecipes.STEP_BANANA_SALAD;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import chopchop.testutil.RecipeBuilder;
import org.junit.jupiter.api.Test;
import chopchop.testutil.IngredientBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate firstPredicateCopy = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different values -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Collections
                                                            .singletonList("Apricot"));
        assertTrue(predicate.test(new IngredientBuilder().withName("Apricot").build()));
        assertTrue(predicate.test(new RecipeBuilder().withName("Apricot").build()));

        // Multiple keywords
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("Apricot", "Banana"));
        assertTrue(predicate.test(new IngredientBuilder().withName("Apricot Banana").build()));
        assertTrue(predicate.test(new RecipeBuilder().withName("Apricot Banana").build()));

        // Only one matching keyword
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("Apricot", "Caramel"));
        assertTrue(predicate.test(new IngredientBuilder().withName("Alice Caramel").build()));
        assertTrue(predicate.test(new RecipeBuilder().withName("Apricot Salad").build()));

        // Mixed-case keywords
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("aPRIcot", "bAnAnA"));
        assertTrue(predicate.test(new IngredientBuilder().withName("Apricot banana").build()));
        assertTrue(predicate.test(new RecipeBuilder().withName("Apricot Banana").build()));

    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new IngredientBuilder().withName("Apricot").build()));
        assertFalse(predicate.test(new RecipeBuilder().withName("Apricot").build()));

        // Non-matching keyword
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("Caramel"));
        assertFalse(predicate.test(new IngredientBuilder().withName("Apricot Banana").build()));
        assertFalse(predicate.test(new RecipeBuilder().withName("Apricot Banana").build()));

        // For ingredient, keyword matches expiry date, but do not match name
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("2020-05-10"));
        assertFalse(predicate.test(new IngredientBuilder().withName("Alice")
            .withDate("2020-05-10").build()));

        // For Recipe, keywords match ingredient name and step content, but do not match name
        predicate = new NameContainsKeywordsPredicate(Arrays.asList(
                "Banana", "Cut"));
        assertFalse(predicate.test(new RecipeBuilder().withName("Apricot Salad")
            .withIngredients(new ArrayList<>(Arrays.asList(BANANA_REF)))
            .withSteps(new ArrayList<>(Arrays.asList(STEP_BANANA_SALAD)))
            .build()));
    }

}

