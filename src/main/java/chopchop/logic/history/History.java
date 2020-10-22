package chopchop.logic.history;

import java.util.List;
import chopchop.logic.commands.CommandResult;
import chopchop.logic.commands.exceptions.CommandException;
import chopchop.model.Model;

/**
 * API of the History component
 */
public interface History {
    /**
     * Adds an undoable command to the history.
     */
    void add(CommandHistory command);

    /**
     * Undo a command and returns the result. Skips any commands that are not undoable.
     *
     * @param model {@code Model} which the undo should operate on.
     * @return feedback message of the operation result for display.
     * @throws CommandException If an error occurs during undo execution.
     */
    CommandResult undo(Model model) throws CommandException;

    /**
     * Redo a command and returns the result. Skips any commands that are not redoable.
     *
     * @param model {@code Model} which the redo should operate on.
     * @return feedback message of the operation result for display.
     * @throws CommandException If an error occurs during redo execution.
     */
    CommandResult redo(Model model) throws CommandException;

    /**
     * Returns the command history in reverse chronological order.
     */
    String getHistory();

}
