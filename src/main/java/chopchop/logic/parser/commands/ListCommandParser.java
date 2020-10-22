// ListCommandParser.java

package chopchop.logic.parser.commands;

import java.util.Optional;
import java.util.ArrayList;

import chopchop.commons.util.Result;
import chopchop.commons.util.Strings;

import chopchop.logic.parser.ArgName;
import chopchop.logic.parser.CommandArguments;

import chopchop.logic.commands.Command;
import chopchop.logic.commands.ListRecipeCommand;
import chopchop.logic.commands.ListIngredientCommand;

import static chopchop.logic.parser.commands.CommonParser.getCommandTarget;
import static chopchop.logic.parser.commands.CommonParser.getFirstUnknownArgument;

public class ListCommandParser {

    private static final String commandName = Strings.COMMAND_LIST;

    /**
     * Parses a 'list' command. Syntax(es):
     * {@code list recipe}
     * {@code list ingredient}
     *
     * @param args the parsed command arguments from the {@code CommandParser}.
     * @return     a ListCommand, if the input was valid.
     */
    public static Result<? extends Command> parseListCommand(CommandArguments args) {

        if (!args.getCommand().equals(commandName)) {
            return Result.error("invalid command '%s' (expected '%s')", args.getCommand(), commandName);
        }

        // we expect no named arguments
        Optional<ArgName> foo;
        if ((foo = getFirstUnknownArgument(args, new ArrayList<>())).isPresent()) {
            return Result.error("'list' command doesn't support '%s'", foo.get());
        }

        return getCommandTarget(args)
            .then(target -> {
                switch (target.fst()) {
                case RECIPE:
                    return Result.of(new ListRecipeCommand());

                case INGREDIENT:
                    return Result.of(new ListIngredientCommand());

                default:
                    return Result.error("can only list recipes or ingredients ('%s' invalid)", target.fst());
                }
            });
    }
}
