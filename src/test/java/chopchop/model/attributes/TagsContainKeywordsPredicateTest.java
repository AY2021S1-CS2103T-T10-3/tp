package chopchop.model.attributes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import chopchop.testutil.IngredientBuilder;
import chopchop.testutil.RecipeBuilder;
import org.junit.jupiter.api.Test;

public class TagsContainKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        TagContainsKeywordsPredicate firstPredicate = new TagContainsKeywordsPredicate(firstPredicateKeywordList);
        TagContainsKeywordsPredicate secondPredicate = new TagContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        TagContainsKeywordsPredicate firstPredicateCopy = new TagContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different values -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_tagContainsKeywords_returnsTrue() {
        // One keyword
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(
            Collections.singletonList("Family 123*"));

        assertTrue(predicate.test(new IngredientBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("Family 123*")))).build()));
        assertTrue(predicate.test(new RecipeBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("Family 123*")))).build()));

        // Multiple keywords
        predicate = new TagContainsKeywordsPredicate(Arrays.asList("snacks", "movie"));
        assertTrue(predicate.test(new IngredientBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("snacks"), new Tag("movie")))).build()));
        assertTrue(predicate.test(new RecipeBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("snacks"), new Tag("movie")))).build()));

        // Partially matching keywords (keywords are contained in the tags)
        predicate = new TagContainsKeywordsPredicate(Arrays.asList("christmas"));
        assertTrue(predicate.test(new IngredientBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("christmas eve")))).build()));
        assertTrue(predicate.test(new IngredientBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("christmas eve"), new Tag("dinner time")))).build()));
        assertTrue(predicate.test(new RecipeBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("christmas eve")))).build()));
        assertTrue(predicate.test(new RecipeBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("christmas eve"), new Tag("dinner time")))).build()));

        // Mixed-case keywords
        predicate = new TagContainsKeywordsPredicate(Arrays.asList("cHrIstMas", "DiNNeR"));
        assertTrue(predicate.test(new IngredientBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("chRistmAs"), new Tag("diNnEr tiMe")))).build()));
        assertTrue(predicate.test(new RecipeBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("ChriSTmaS"), new Tag("dInnEr TImE")))).build()));

        // Combined keywords
        predicate = new TagContainsKeywordsPredicate(Arrays.asList("All Time Favourite"));
        assertTrue(predicate.test(new IngredientBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("All Time Favourite"), new Tag("snacks")))).build()));
        assertTrue(predicate.test(new RecipeBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("All Time Favourite"), new Tag("snacks")))).build()));
    }

    @Test
    public void test_tagDoesNotContainKeywords_noMatchingTagReturnsFalse() {
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(
            Collections.singletonList("Family 123*"));
        assertFalse(predicate.test(new IngredientBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("TagOne"), new Tag("TagTwo")))).build()));
        assertFalse(predicate.test(new IngredientBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("TagOne TagTwo")))).build()));
        assertFalse(predicate.test(new RecipeBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("TagOne"), new Tag("TagTwo")))).build()));
        assertFalse(predicate.test(new RecipeBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("TagOne TagTwo")))).build()));
    }

    @Test
    public void test_tagDoesNotContainKeywords_partiallyMatchingTagReturnsFalse() {
        // keywords are only partially contained in the tags
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(
            Collections.singletonList("Family 123*"));
        assertFalse(predicate.test(new IngredientBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("Fami"), new Tag("123*")))).build()));
        assertFalse(predicate.test(new IngredientBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("Fami 123*")))).build()));
        assertFalse(predicate.test(new RecipeBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("Fami"), new Tag("123*")))).build()));
        assertFalse(predicate.test(new RecipeBuilder().withTags(
            new HashSet<>(Arrays.asList(new Tag("Fami 123*")))).build()));
    }

    @Test
    public void test_tagDoesNotContainKeywords_onlyMatchingOtherCriteriaReturnsFalse() {
        // keywords match other fields but not tags
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(
            Collections.singletonList("Family 123*"));
        assertFalse(predicate.test(new IngredientBuilder()
            .withName("Family 123*")
            .withTags(new HashSet<>(Arrays.asList(new Tag("Famil"), new Tag("123*"))))
            .build()));
        assertFalse(predicate.test(new IngredientBuilder()
            .withName("Family 123*")
            .withTags(new HashSet<>(Arrays.asList(new Tag("Famil 123*"))))
            .build()));
        assertFalse(predicate.test(new RecipeBuilder()
            .withName("Family 123*")
            .withSteps(Arrays.asList(new Step("Family 123*")))
            .withTags(new HashSet<>(Arrays.asList(new Tag("Famil"), new Tag("123*"))))
            .build()));
        assertFalse(predicate.test(new RecipeBuilder()
            .withName("Family 123*")
            .withSteps(Arrays.asList(new Step("Family 123*")))
            .withTags(new HashSet<>(Arrays.asList(new Tag("Famil 123*"))))
            .build()));
    }
}
