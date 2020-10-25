package chopchop.logic.commands;

import static java.util.Objects.requireNonNull;

import chopchop.logic.history.HistoryManager;
import chopchop.model.Model;
import chopchop.ui.DisplayNavigator;

/**
 * Lists all recommended recipes in the recipe book to the user.
 */
public class ListRecommendationCommand extends Command {

    public static final String MESSAGE_SUCCESS = "Listed all recommendations";

    @Override
    public CommandResult execute(Model model, HistoryManager historyManager) {
        requireNonNull(model);

        if (DisplayNavigator.hasDisplayController()) {
            DisplayNavigator.loadRecommendationPanel();
        }

        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof ListRecommendationCommand);
    }

    @Override
    public String toString() {
        return String.format("ListRecommendationCommand");
    }
}
