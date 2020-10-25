// ListCommandParser.java

package chopchop.logic.parser.commands;

import java.util.List;
import java.util.Optional;

import chopchop.commons.util.Result;
import chopchop.commons.util.Strings;

import chopchop.logic.commands.ListRecommendationCommand;
import chopchop.logic.parser.ArgName;
import chopchop.logic.parser.CommandArguments;

import chopchop.logic.commands.Command;
import chopchop.logic.commands.ListRecipeCommand;
import chopchop.logic.commands.ListIngredientCommand;

import static chopchop.logic.parser.commands.CommonParser.getCommandTarget;
import static chopchop.logic.parser.commands.CommonParser.getFirstUnknownArgument;

public class ListCommandParser {

    /**
     * Parses a 'list' command. Syntax(es):
     * {@code list recipe}
     * {@code list ingredient}
     *
     * @param args the parsed command arguments from the {@code CommandParser}.
     * @return     a ListCommand, if the input was valid.
     */
    public static Result<? extends Command> parseListCommand(CommandArguments args) {
        assert args.getCommand().equals(Strings.COMMAND_LIST);

        // we expect no named arguments
        Optional<ArgName> foo;
        if ((foo = getFirstUnknownArgument(args, List.of())).isPresent()) {
            return Result.error("'list' command doesn't support '%s'", foo.get());
        }

        return getCommandTarget(args, /* acceptsPlural: */ true)
            .then(target -> {
                switch (target.fst()) {
                case RECIPE:
                    return Result.of(new ListRecipeCommand());

                case INGREDIENT:
                    return Result.of(new ListIngredientCommand());

                case RECOMMENDATION:
                    return Result.of(new ListRecommendationCommand());

                default:
                    return Result.error("can only list recipes, ingredients or recommendations ('%s' invalid)",
                            target.fst());
                }
            });
    }
}
