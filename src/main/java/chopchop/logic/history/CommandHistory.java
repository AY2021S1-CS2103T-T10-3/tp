package chopchop.logic.history;

import java.util.Optional;

import chopchop.logic.commands.Command;
import chopchop.logic.commands.Undoable;

/**
 * Represents a command history.
 */
public class CommandHistory {
    private final String commandText;
    private final Undoable command;

    public CommandHistory(String commandText) {
        this(commandText, null);
    }

    /**
     * @param commandText of the command.
     * @param command the command.
     */
    public CommandHistory(String commandText, Command command) {
        this.commandText = commandText;

        if (command instanceof Undoable) {
            this.command = (Undoable) command;
        } else {
            this.command = null;
        }
    }

    /**
     * Gets the command text.
     */
    public String getCommandText() {
        return this.commandText;
    }

    /**
     * Gets the undoable command if it exists.
     * @return An Optional of the undoable command.
     */
    public Optional<Undoable> getCommand() {
        return Optional.ofNullable(this.command);
    }
}
