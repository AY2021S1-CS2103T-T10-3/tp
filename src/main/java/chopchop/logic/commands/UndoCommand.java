package chopchop.logic.commands;

import static java.util.Objects.requireNonNull;

import chopchop.logic.commands.exceptions.CommandException;
import chopchop.logic.history.History;
import chopchop.model.Model;

/**
 * Undo the last undoable command.
 */
public class UndoCommand extends Command {
    public static final String COMMAND_WORD = "undo";

    @Override
    public CommandResult execute(Model model, History history) throws CommandException {
        requireNonNull(model);
        requireNonNull(history);

        return history.undo(model);
    }
}
