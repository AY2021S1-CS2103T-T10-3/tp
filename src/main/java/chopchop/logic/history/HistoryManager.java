package chopchop.logic.history;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import chopchop.logic.commands.CommandResult;
import chopchop.logic.commands.exceptions.CommandException;
import chopchop.model.Model;

/**
 * The HistoryManager of the main LogicManager.
 */
public class HistoryManager implements History {
    public static final String MESSAGE_CANNOT_UNDO = "No commands to undo";
    public static final String MESSAGE_CANNOT_REDO = "No commands to redo";

    private final List<CommandHistory> commandHistory;
    private int startingIndex;
    private int currentIndex;

    /**
     * Constructs a {@code HistoryManager}.
     */
    public HistoryManager() {
        this.commandHistory = new ArrayList<>();
        this.currentIndex = 0;
    }

    public HistoryManager(List<CommandHistory> commandHistory) {
        this.commandHistory = commandHistory;
        this.startingIndex = commandHistory.size();
        this.currentIndex = startingIndex;
    }

    public List<CommandHistory> getCommandHistory() {
        return new ArrayList<>(commandHistory);
    }

    @Override
    public void add(CommandHistory command) {
        this.commandHistory.subList(this.currentIndex, this.commandHistory.size()).clear();
        this.commandHistory.add(command);
        this.currentIndex = this.commandHistory.size();
    }

    @Override
    public CommandResult undo(Model model) throws CommandException {
        for (var i = this.currentIndex - 1; i >= 0; i--) {
            var command = this.commandHistory.get(i).getCommand();

            if (command.isPresent()) {
                this.currentIndex = i;
                return command.get().undo(model);
            }
        }

        throw new CommandException(MESSAGE_CANNOT_UNDO);
    }

    @Override
    public CommandResult redo(Model model) throws CommandException {
        while (this.currentIndex < this.commandHistory.size()) {
            var command = this.commandHistory.get(this.currentIndex).getCommand();
            this.currentIndex++;

            if (command.isPresent()) {
                return command.get().redo(model, this);
            }
        }

        throw new CommandException(MESSAGE_CANNOT_REDO);
    }

    @Override
    public String getHistory() {
        var sj = new StringJoiner("\n");
        var reversedHistory = this.commandHistory.listIterator(this.currentIndex);

        int curr = currentIndex;
        while (startingIndex < curr) {
            var command = reversedHistory.previous();
            sj.add(command.getCommandText());
            curr--;
        }

        return sj.toString();
    }
}
