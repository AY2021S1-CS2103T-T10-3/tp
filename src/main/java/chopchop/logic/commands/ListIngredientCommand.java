package chopchop.logic.commands;

import static chopchop.model.Model.PREDICATE_SHOW_ALL_ENTRIES;
import static java.util.Objects.requireNonNull;

import chopchop.logic.history.HistoryManager;
import chopchop.model.Model;
import chopchop.ui.DisplayNavigator;

/**
 * Lists all ingredients in the ingredient book to the user.
 */
public class ListIngredientCommand extends Command {

    @Override
    public CommandResult execute(Model model, HistoryManager historyManager) {
        requireNonNull(model);
        model.updateFilteredIngredientList(PREDICATE_SHOW_ALL_ENTRIES);

        if (DisplayNavigator.hasDisplayController()) {
            DisplayNavigator.loadIngredientPanel();
        }

        return CommandResult.message("Listed ingredients");
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof ListIngredientCommand);
    }

    @Override
    public String toString() {
        return String.format("ListIngredientCommand");
    }
}
