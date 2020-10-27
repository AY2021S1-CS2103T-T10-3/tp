package chopchop.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import chopchop.logic.history.HistoryManager;
import chopchop.model.Model;
import chopchop.model.attributes.IngredientsContainKeywordsPredicate;
import chopchop.model.attributes.TagContainsKeywordsPredicate;
import chopchop.model.recipe.Recipe;
import chopchop.ui.DisplayNavigator;

/**
 * Filters and lists all recipes in recipe book that match all filtering criteria.
 * Keyword matching is case insensitive.
 */
public class FilterRecipeCommand extends Command {

    public static final String COMMAND_WORD = "filter recipe";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Filters and gets all recipes whose ingredient "
            + "and tag list contain any of the specified keywords (case-insensitive) and displays them as a "
            + "list with index numbers.\nParameters: /FIELD_NAME KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " /ingredient apple /tag home /ingredient chocolate /tag snacks"
            + "Note: Tag(s) and Ingredient names should be split into single words during your search.";

    private final IngredientsContainKeywordsPredicate ingredientPredicates;
    private final TagContainsKeywordsPredicate tagPredicates;

    /**
     * Constructs a command that filters and finds the matching recipe items.
     * @param indPredicates
     * @param tagPredicates
     */
    public FilterRecipeCommand(TagContainsKeywordsPredicate tagPredicates,
            IngredientsContainKeywordsPredicate indPredicates) {
        this.tagPredicates = tagPredicates;
        this.ingredientPredicates = indPredicates;
    }

    @Override
    public CommandResult execute(Model model, HistoryManager historyManager) {
        requireNonNull(model);

        Predicate<? super Recipe> p = x -> true;
        if (ingredientPredicates != null && tagPredicates != null) {
            p = ingredientPredicates.and(tagPredicates);
        } else if (ingredientPredicates != null) {
            p = ingredientPredicates;
        } else if (tagPredicates != null) {
            p = tagPredicates;
        }
        model.updateFilteredRecipeList(p);

        if (DisplayNavigator.hasDisplayController()) {
            DisplayNavigator.loadRecipePanel();
        }

        var sz = model.getFilteredRecipeList().size();
        return CommandResult.message("Found %d recipe%s", sz, sz == 1 ? "" : "s");
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof FilterRecipeCommand // instanceof handles nulls
            && tagPredicates.equals(((FilterRecipeCommand) other).tagPredicates)
            && ingredientPredicates.equals(((FilterRecipeCommand) other).ingredientPredicates)); // state check
    }

}
