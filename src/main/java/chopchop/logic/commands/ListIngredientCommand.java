package chopchop.logic.commands;

import static chopchop.model.Model.PREDICATE_SHOW_ALL_ENTRIES;
import static java.util.Objects.requireNonNull;

import chopchop.logic.history.History;
import chopchop.model.Model;
import chopchop.ui.DisplayNavigator;

/**
 * Lists all ingredients in the ingredient book to the user.
 */
public class ListIngredientCommand extends Command {

    public static final String MESSAGE_SUCCESS = "Listed all ingredients";

    @Override
    public CommandResult execute(Model model, History history) {
        requireNonNull(model);
        model.updateFilteredIngredientList(PREDICATE_SHOW_ALL_ENTRIES);

        if (DisplayNavigator.hasDisplayController()) {
            DisplayNavigator.loadIngredientPanel();
        }

        return new CommandResult(MESSAGE_SUCCESS);
    }
}
