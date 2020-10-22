package chopchop.logic.commands;

import chopchop.logic.commands.exceptions.CommandException;
import chopchop.logic.history.History;
import chopchop.model.Model;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {
    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @param history {@code History} which the command should record to.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    public abstract CommandResult execute(Model model, History history) throws CommandException;
}
