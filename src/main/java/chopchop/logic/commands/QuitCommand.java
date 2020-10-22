// QuitCommand.java

package chopchop.logic.commands;

import chopchop.logic.history.History;
import chopchop.model.Model;

public class QuitCommand extends Command {
    @Override
    public CommandResult execute(Model model, History history) {
        return new CommandResult("", /* showHelp: */ false, /* exit: */ true);
    }
}
