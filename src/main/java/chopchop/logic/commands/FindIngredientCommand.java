package chopchop.logic.commands;

import static java.util.Objects.requireNonNull;

import chopchop.commons.core.Messages;
import chopchop.logic.history.HistoryManager;
import chopchop.model.Model;
import chopchop.model.attributes.NameContainsKeywordsPredicate;
import chopchop.ui.DisplayNavigator;

/**
 * Finds and lists all ingredients in ingredient book whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindIngredientCommand extends Command {

    public static final String COMMAND_WORD = "find ingredient";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all ingredients whose content contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " sugar";


    private final NameContainsKeywordsPredicate predicate;

    /**
     * Constructs a command that finds the given ingredient item.
     */
    public FindIngredientCommand(NameContainsKeywordsPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model, HistoryManager historyManager) {
        requireNonNull(model);
        model.updateFilteredIngredientList(predicate);

        if (DisplayNavigator.hasDisplayController()) {
            DisplayNavigator.loadIngredientPanel();
        }

        return CommandResult.message(Messages.MESSAGE_INGREDIENTS_LISTED_OVERVIEW,
            model.getFilteredIngredientList().size());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FindIngredientCommand // instanceof handles nulls
                && predicate.equals(((FindIngredientCommand) other).predicate)); // state check
    }

    @Override
    public String toString() {
        return String.format("FindIngredientCommand(keywords: %s)", this.predicate.getKeywords());
    }
}
