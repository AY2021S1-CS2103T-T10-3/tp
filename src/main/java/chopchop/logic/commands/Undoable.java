package chopchop.logic.commands;

import chopchop.logic.commands.exceptions.CommandException;
import chopchop.logic.history.History;
import chopchop.model.Model;

/**
 * Represents an undoable command with the ability to be undone and redone.
 */
public interface Undoable {
    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @param history {@code History} which the command should record to.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    CommandResult execute(Model model, History history) throws CommandException;

    /**
     * Undo the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    CommandResult undo(Model model) throws CommandException;

    /**
     * Redo the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @param history {@code History} which the command should record to.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    default CommandResult redo(Model model, History history) throws CommandException {
        return this.execute(model, history);
    }
}
