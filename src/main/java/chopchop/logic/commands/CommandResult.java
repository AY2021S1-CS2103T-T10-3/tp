// CommandResult.java

package chopchop.logic.commands;

import static chopchop.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import chopchop.commons.util.Pair;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String message;
    private final boolean isError;
    private final boolean showHelp;
    private final boolean shouldExit;
    private final boolean isStatsOutput;
    private final List<Pair<String, String>> statsMessage;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    private CommandResult(String message, boolean isError, boolean shouldExit, boolean showHelp, boolean isStatsOutput,
                          List<Pair<String, String>> statsMessage) {
        requireAllNonNull(message, statsMessage);

        this.message = message;
        this.isError = isError;
        this.showHelp = showHelp;
        this.shouldExit = shouldExit;
        this.isStatsOutput = isStatsOutput;
        this.statsMessage = statsMessage;
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
     * Returns true if !isError()
     */
    public boolean didSucceed() {
        return !this.isError;
    }

    /**
     * Returns the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Returns true if the command result comes from stats command.
     */
    public boolean isStatsOutput() {
        return this.isStatsOutput;
    }

    /**
     * Returns the list to be displayed in list view of stats box.
     */
    public ArrayList<Pair<String, String>> getStatsMessage() {
        return new ArrayList<>(this.statsMessage);
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
            /* isError: */ false, /* shouldExit: */ false, /* showHelp: */ false, false,
            new ArrayList<>());
    }

    /**
     * Constructs a new command result that shows an error.
     *
     * @param error the error to show
     */
    public static CommandResult error(String error, Object... args) {
        return new CommandResult(String.format(error, args),
            /* isError: */ true, /* shouldExit: */ false, /* showHelp: */ false, false,
            new ArrayList<>());
    }

    /**
     * Constructs a new command result that shows help
     */
    public static CommandResult help() {
        return new CommandResult("", /* isError: */ false, /* shouldExit: */ false,
            /* showHelp: */ true, false, new ArrayList<>());
    }

    /**
     * Constructs a new command result that quits.
     */
    public static CommandResult exit() {
        return new CommandResult("", /* isError: */ false, /* shouldExit: */ true,
            /* showHelp: */ false, false, new ArrayList<>());
    }

    /**
     * Constructs a new command result from stats that contains the list of values to be output in stats box and the
     * message that indicates the status of the command.
     */
    public static CommandResult statsMessage(List<Pair<String, String>> outputList, String message, Object... args) {
        return new CommandResult(String.format(message, args),
            /* isError: */ false, /* shouldExit: */ false, /* showHelp: */ false, true,
            outputList);
    }
}
