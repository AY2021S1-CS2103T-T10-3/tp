package chopchop.logic.parser.commands;

import static chopchop.logic.parser.commands.CommonParser.getCommandTarget;
import static chopchop.logic.parser.commands.CommonParser.getFirstUnknownArgument;

import java.util.ArrayList;
import java.util.Optional;

import chopchop.logic.commands.Command;
import chopchop.logic.commands.MakeRecipeCommand;
import chopchop.logic.parser.ArgName;
import chopchop.logic.parser.CommandArguments;
import chopchop.logic.parser.ItemReference;
import chopchop.util.Result;
import chopchop.util.Strings;

public class MakeCommandParser {
    private static final String commandName = Strings.COMMAND_MAKE;

    /**
     * Parses a 'make' command. Syntax:
     * {@code make recipe REF}
     *
     * @param args the parsed command arguments from the {@code CommandParser}.
     * @return     a MakeCommand, if the input was valid.
     */
    public static Result<? extends Command> parseMakeCommand(CommandArguments args) {
        if (!args.getCommand().equals(commandName)) {
            return Result.error("invalid command '%s' (expected '%s')", args.getCommand(), commandName);
        }

        // we expect no named arguments
        Optional<ArgName> foo;
        if ((foo = getFirstUnknownArgument(args, new ArrayList<>())).isPresent()) {
            return Result.error("'make' command doesn't support '%s'", foo.get());
        }

        return getCommandTarget(args)
            .then(target -> {
                if (target.fst() == CommandTarget.RECIPE) {
                    if (target.snd().isEmpty()) {
                        return Result.error("recipe name cannot be empty");
                    }

                    return parseMakeRecipeCommand(target.snd().strip(), args);
                }

                return Result.error("can only make recipes ('%s' invalid)", target.fst());
            });
    }

    /**
     * Parses a 'make recipe' command. Syntax:
     * {@code make recipe REF}
     */
    private static Result<MakeRecipeCommand> parseMakeRecipeCommand(String name, CommandArguments args) {
        assert args.getCommand().equals(commandName);

        return ItemReference.parse(name)
            .map(MakeRecipeCommand::new);
    }
}
