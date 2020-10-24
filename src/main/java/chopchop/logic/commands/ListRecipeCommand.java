package chopchop.logic.commands;

import static chopchop.model.Model.PREDICATE_SHOW_ALL_ENTRIES;
import static java.util.Objects.requireNonNull;

import chopchop.logic.history.HistoryManager;
import chopchop.model.Model;
import chopchop.ui.DisplayNavigator;

/**
 * Lists all recipes in the recipe book to the user.
 */
public class ListRecipeCommand extends Command {

    public static final String MESSAGE_SUCCESS = "Listed all recipes";

    @Override
    public CommandResult execute(Model model, HistoryManager historyManager) {
        requireNonNull(model);
        model.updateFilteredRecipeList(PREDICATE_SHOW_ALL_ENTRIES);

        if (DisplayNavigator.hasDisplayController()) {
            DisplayNavigator.loadRecipePanel();
        }

        return CommandResult.message(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof ListRecipeCommand);
    }

    @Override
    public String toString() {
        return String.format("ListRecipeCommand");
    }
}
