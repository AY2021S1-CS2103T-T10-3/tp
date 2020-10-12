package chopchop.logic.commands;

import static java.util.Objects.requireNonNull;
import chopchop.model.FoodEntry;
import chopchop.logic.commands.exceptions.CommandException;
import chopchop.model.Model;

public abstract class AddCommand extends Command {

    protected final FoodEntry toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(FoodEntry entry) {
        requireNonNull(entry);
        toAdd = entry;
    }

    @Override
    public abstract CommandResult execute(Model model) throws CommandException;

    @Override
    public abstract boolean equals(Object other);

    @Override
    public String toString() {
        return String.format("AddCommand: %s", this.toAdd);
    }
}
