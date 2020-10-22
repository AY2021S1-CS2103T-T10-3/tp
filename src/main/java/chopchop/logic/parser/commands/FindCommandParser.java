// FindCommandParser.java

package chopchop.logic.parser.commands;

import java.util.Optional;
import java.util.ArrayList;

import chopchop.commons.util.Result;
import chopchop.commons.util.Strings;
import chopchop.commons.util.StringView;

import chopchop.logic.parser.ArgName;
import chopchop.logic.parser.CommandArguments;

import chopchop.logic.commands.Command;
import chopchop.logic.commands.FindRecipeCommand;
import chopchop.logic.commands.FindIngredientCommand;

import chopchop.model.attributes.NameContainsKeywordsPredicate;

import static chopchop.logic.parser.commands.CommonParser.getCommandTarget;
import static chopchop.logic.parser.commands.CommonParser.getFirstUnknownArgument;

public class FindCommandParser {

    private static final String commandName = Strings.COMMAND_FIND;

    /**
     * Parses a 'find' command. Syntax(es):
     * {@code delete recipe (keywords)+}
     * {@code delete ingredient (keywords)+}
     *
     * @param args the parsed command arguments from the {@code CommandParser}.
     * @return     a FindCommand, if the input was valid.
     */
    public static Result<? extends Command> parseFindCommand(CommandArguments args) {

        if (!args.getCommand().equals(commandName)) {
            return Result.error("invalid command '%s' (expected '%s')", args.getCommand(), commandName);
        }

        // we expect no named arguments
        Optional<ArgName> foo;
        if ((foo = getFirstUnknownArgument(args, new ArrayList<>())).isPresent()) {
            return Result.error("'find' command doesn't support '%s'\n%s",
                foo.get(), FindRecipeCommand.MESSAGE_USAGE);
        }

        return getCommandTarget(args)
            .then(target -> {
                var words = new StringView(target.snd()).words();

                if (words.isEmpty()) {
                    return Result.error("'%s' command requires at least one search term\n%s",
                        commandName, FindRecipeCommand.MESSAGE_USAGE);
                }

                switch (target.fst()) {
                case RECIPE:
                    return Result.of(new FindRecipeCommand(new NameContainsKeywordsPredicate(words)));

                case INGREDIENT:
                    return Result.of(new FindIngredientCommand(new NameContainsKeywordsPredicate(words)));

                default:
                    return Result.error("can only find recipes or ingredients ('%s' invalid)", target.fst());
                }
            });
    }
}
