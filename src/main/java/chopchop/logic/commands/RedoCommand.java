package chopchop.logic.commands;

import static java.util.Objects.requireNonNull;

import chopchop.logic.commands.exceptions.CommandException;
import chopchop.logic.history.History;
import chopchop.model.Model;

/**
 * Redo the last redoable command.
 */
public class RedoCommand extends Command {
    public static final String COMMAND_WORD = "redo";

    @Override
    public CommandResult execute(Model model, History history) throws CommandException {
        requireNonNull(model);
        requireNonNull(history);

        return history.redo(model);
    }
}
