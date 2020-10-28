package chopchop.logic.commands;

import java.util.stream.Collectors;

import chopchop.commons.util.Pair;
import chopchop.logic.commands.exceptions.CommandException;
import chopchop.logic.history.HistoryManager;
import chopchop.model.Model;

public class StatsIngredientRecentlyUsedCommand extends Command {
    public static final String COMMAND_WORD = "stats ingredient recently used";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " : Shows a list of"
        + "ingredients that are recently used. (Capped at 10 ingredients)";
    private static final int N_MOST_RECENT = 10;

    /**
     * Executes the command and returns the result message.
     *
     * @param model          {@code Model} which the command should operate on.
     * @param historyManager {@code History} which the command should record to.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    @Override
    public CommandResult execute(Model model, HistoryManager historyManager) throws CommandException {
        try {
            var output = model.getRecentlyUsedIngredient(N_MOST_RECENT);
            var msgOutput = output.stream()
                .map(x -> new Pair<>(x.getName(), x.getPrintableDate()))
                .collect(Collectors.toList());
            return CommandResult.statsMessage(msgOutput, "Here is the list of ingredients recently used");
        } catch (Exception e) {
            return CommandResult.message("Unable to generate ingredient recently used");
        }
    }
}