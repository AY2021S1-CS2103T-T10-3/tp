// CommandResult.java

package chopchop.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String message;
    private final boolean isError;
    private final boolean showHelp;
    private final boolean shouldExit;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    private CommandResult(String message, boolean isError, boolean shouldExit, boolean showHelp) {
        requireNonNull(message);

        this.message = message;
        this.isError = isError;
        this.showHelp = showHelp;
        this.shouldExit = shouldExit;
    }

    /**
     * Returns true if the application should exit after this command
     */
    public boolean shouldExit() {
        return this.shouldExit;
    }

    /**
     * Returns true if the app should open the help window
     */
    public boolean shouldShowHelp() {
        return this.showHelp;
    }

    /**
     * Returns true if the message should be styled as an error
     */
    public boolean isError() {
        return this.isError;
    }

    /**
     * Returns the message
     */
    public String getMessage() {
        return this.message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof CommandResult)) {
            return false;
        }

        var cr = (CommandResult) obj;

        return this.message.equals(cr.message)
            && this.isError == cr.isError
            && this.showHelp == cr.showHelp
            && this.shouldExit == cr.shouldExit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.message, this.isError, this.showHelp, this.shouldExit);
    }

    /**
     * Constructs a new command result that only shows a message.
     *
     * @param message the message to show
     */
    public static CommandResult message(String message, Object... args) {
        return new CommandResult(String.format(message, args),
            /* isError: */ false, /* shouldExit: */ false, /* showHelp: */ false);
    }

    /**
     * Constructs a new command result that shows an error.
     *
     * @param error the error to show
     */
    public static CommandResult error(String error, Object... args) {
        return new CommandResult(String.format(error, args),
            /* isError: */ true, /* shouldExit: */ false, /* showHelp: */ false);
    }

    /**
     * Constructs a new command result that shows help
     */
    public static CommandResult help() {
        return new CommandResult("", /* isError: */ false, /* shouldExit: */ false,
            /* showHelp: */ true);
    }

    /**
     * Constructs a new command result that quits.
     */
    public static CommandResult exit() {
        return new CommandResult("", /* isError: */ false, /* shouldExit: */ true,
            /* showHelp: */ false);
    }
}
