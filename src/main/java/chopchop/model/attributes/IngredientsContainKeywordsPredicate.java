package chopchop.model.attributes;

import chopchop.model.recipe.Recipe;

import java.util.List;
import java.util.function.Predicate;

/**
 * Tests that an item's {@code Name} matches any of the keywords given.
 */
public class IngredientsContainKeywordsPredicate implements Predicate<Recipe> {
    private final List<String> keywords;

    public IngredientsContainKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Recipe recipe) {
        // Returns recipes whose ingredient name list containing any of these keywords
        // return indNameList.anyMatch(indName -> this.keywords.stream()
        //     .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(indName, keyword)));

        // Returns recipes whose ingredient name list contains all these keywords
        return this.keywords.stream()
                .map(kw -> kw.toLowerCase())
                .allMatch(keyword -> recipe.getIngredients()
                .stream()
                .map(indRef -> indRef.getName())
                .anyMatch(indName -> indName.toLowerCase().contains(keyword)));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof IngredientsContainKeywordsPredicate // instanceof handles nulls
                && this.keywords.equals(((IngredientsContainKeywordsPredicate) other).keywords)); // state check
    }

}
