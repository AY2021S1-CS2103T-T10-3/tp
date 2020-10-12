package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.history.HistoryManager;
import seedu.address.model.Model;

/**
 * Represents an undoable command with the ability to be undone and redone.
 */
public interface Undoable {
    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @param historyManager {@code HistoryManager} which the command should record to.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    CommandResult execute(Model model, HistoryManager historyManager) throws CommandException;

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
     * @param historyManager {@code HistoryManager} which the command should record to.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    CommandResult redo(Model model, HistoryManager historyManager) throws CommandException;
}
