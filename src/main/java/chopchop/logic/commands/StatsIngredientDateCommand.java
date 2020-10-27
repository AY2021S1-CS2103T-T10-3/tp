package chopchop.logic.commands;

import static chopchop.commons.util.Strings.ARG_AFTER;
import static chopchop.commons.util.Strings.ARG_BEFORE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import chopchop.logic.commands.exceptions.CommandException;
import chopchop.logic.history.HistoryManager;
import chopchop.model.Model;

public class StatsIngredientDateCommand extends Command {
    public static final String COMMAND_WORD = "stats ingredient";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows a list of ingredient. "
        + "Parameters: "
        + "[" + ARG_BEFORE + " DATE] "
        + "[" + ARG_AFTER + " DATE] "
        + "Example: " + COMMAND_WORD + " " + ARG_BEFORE + " 2020-02-13 ";

    private final LocalDateTime before;
    private final LocalDateTime after;

    /**
     * Creates an StatsIngredientCommand to add the specified {@code Command}.
     * On takes precedence over before and after.
     * If on is specified together with before and after, only 'on' is considered.
     */
    public StatsIngredientDateCommand(LocalDateTime before, LocalDateTime after) {
        if (before == null && after == null) {
            var now = LocalDateTime.now();
            this.before = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfYear(), 0, 0, 0);
            this.after = this.before.plusDays(1);
        } else {
            this.before = before;
            this.after = after;
        }
    }

    private String getMessage() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        String msg;
        if (this.before != null && this.after != null) {
            var before = this.before.format(formatter);
            var after = this.after.format(formatter);
            if (this.before.plusDays(1).equals(this.after)) {
                msg = String.format("Here is the list of recipes made on %s", before);
            } else {
                msg = String.format("Here is the list of recipes made from the period %s to %s", after, before);
            }
        } else if (this.before != null) {
            var before = this.before.format(formatter);
            msg = String.format("Here is the list of recipes made before %s", before);
        } else {
            var before = this.after.format(formatter);
            msg = String.format("Here is the list of recipes made after %s", before);
        }
        return msg;
    }

    @Override
    public CommandResult execute(Model model, HistoryManager historyManager) throws CommandException {
        var output = model.getRecipeUsageList().getUsagesBetween(before, after);
        return CommandResult.statsMessage(output, getMessage());
    }

    @Override
    public boolean equals(Object other) {
        return other == this
            || (other instanceof StatsIngredientDateCommand
            && this.before.equals(((StatsIngredientDateCommand) other).before)
            && this.after.equals(((StatsIngredientDateCommand) other).after));
    }

    @Override
    public String toString() {
        return String.format("StatsIngredientDateCommand");
    }
}
