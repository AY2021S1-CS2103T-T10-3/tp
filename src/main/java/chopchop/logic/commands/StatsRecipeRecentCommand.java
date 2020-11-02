package chopchop.logic.commands;

import java.util.stream.Collectors;

import chopchop.commons.util.Pair;
import chopchop.logic.history.HistoryManager;
import chopchop.model.Model;

public class StatsRecipeRecentCommand extends Command {

    private static final int N_MOST_RECENT = 10;

    /**
     * Executes the command and returns the result message.
     *
     * @param model          {@code Model} which the command should operate on.
     * @param historyManager {@code History} which the command should record to.
     * @return feedback message of the operation result for display
     */
    @Override
    public CommandResult execute(Model model, HistoryManager historyManager) {
        var output = model.getRecentlyUsedRecipes(N_MOST_RECENT);
        var msgOutput = output.stream()
            .map(x -> new Pair<>(x.getName(), x.getPrintableDate()))
            .collect(Collectors.toList());
        return CommandResult.statsMessage(msgOutput, "Here are your recently used ingredients");
    }

    @Override
    public String toString() {
        return "StatsRecipeRecentCommand";
    }

    public static String getCommandString() {
        return "stats recipe recent";
    }

    public static String getCommandHelp() {
        return "Shows the recipes that were recently made";
    }
}
