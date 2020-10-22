// DeleteCommandParser.java

package chopchop.logic.parser.commands;

import java.util.Optional;
import java.util.ArrayList;

import chopchop.util.Result;
import chopchop.util.Strings;

import chopchop.logic.parser.ArgName;
import chopchop.logic.parser.ItemReference;
import chopchop.logic.parser.CommandArguments;

import chopchop.logic.commands.Command;
import chopchop.logic.commands.DeleteRecipeCommand;
import chopchop.logic.commands.DeleteIngredientCommand;

import static chopchop.logic.parser.commands.CommonParser.getCommandTarget;
import static chopchop.logic.parser.commands.CommonParser.getFirstUnknownArgument;

public class DeleteCommandParser {

    private static final String commandName = Strings.COMMAND_DELETE;

    /**
     * Parses a 'delete' command. Syntax(es):
     * {@code delete recipe REF}
     * {@code delete ingredient REF}
     *
     * @param args the parsed command arguments from the {@code CommandParser}.
     * @return     a DeleteCommand, if the input was valid.
     */
    public static Result<? extends Command> parseDeleteCommand(CommandArguments args) {

        if (!args.getCommand().equals(commandName)) {
            return Result.error("invalid command '%s' (expected '%s')", args.getCommand(), commandName);
        }

        // we expect no named arguments
        Optional<ArgName> foo;
        if ((foo = getFirstUnknownArgument(args, new ArrayList<>())).isPresent()) {
            return Result.error("'delete' command doesn't support '%s'\n%s",
                foo.get(), DeleteRecipeCommand.MESSAGE_USAGE);
        }

        return getCommandTarget(args)
            .then(target -> {
                if (target.snd().isEmpty()) {
                    return Result.error("recipe or ingredient name cannot be empty");
                }

                switch (target.fst()) {
                case RECIPE:
                    return parseDeleteRecipeCommand(target.snd().strip(), args);

                case INGREDIENT:
                    return parseDeleteIngredientCommand(target.snd().strip(), args);

                default:
                    return Result.error("can only delete recipes or ingredients ('%s' invalid)", target.fst());
                }
            });
    }

    /**
     * Parses a 'delete ingredient' command. Syntax:
     * {@code delete ingredient REF}
     */
    private static Result<DeleteIngredientCommand> parseDeleteIngredientCommand(String name, CommandArguments args) {
        assert args.getCommand().equals(commandName);

        return ItemReference.parse(name)
            .map(DeleteIngredientCommand::new);
    }

    /**
     * Parses a 'delete recipe' command. Syntax:
     * {@code delete recipe REF}
     */
    private static Result<DeleteRecipeCommand> parseDeleteRecipeCommand(String name, CommandArguments args) {
        assert args.getCommand().equals(commandName);

        return ItemReference.parse(name)
            .map(DeleteRecipeCommand::new);
    }
}
