// CommonParser.java

package chopchop.logic.parser.commands;

import java.util.List;
import java.util.Optional;

import chopchop.commons.util.Pair;
import chopchop.commons.util.Result;
import chopchop.commons.util.StringView;

import chopchop.logic.parser.ArgName;
import chopchop.logic.parser.CommandArguments;

public class CommonParser {

    /**
     * Finds the first named argument in the given {@code args} that isn't part of {@code knownArgs}
     * and returns it so we can print a nice error message. If all names are part of the given list,
     * then an empty optional is returned.
     */
    public static Optional<ArgName> getFirstUnknownArgument(CommandArguments args, List<ArgName> knownArgs) {
        return args.getAllArguments()
            .stream()
            .filter(p -> !knownArgs.contains(p.fst()))
            .map(p -> p.fst())
            .findFirst();
    }


    /**
     * Gets the 'target' of a command, which is either 'ingredient' or 'recipe'. Returns either an error
     * if the target was invalid or empty, or a pair consisting of the {@code CommandTarget}, and the
     * rest of the unnamed arguments.
     */
    public static Result<Pair<CommandTarget, String>> getCommandTarget(CommandArguments args) {
        var str = args.getRemaining();

        if (str.isEmpty()) {
            return Result.error("no target specified (either 'recipe' or 'ingredient')");
        }

        var x = new StringView(str).bisect(' ').fst().trim();
        var xs = new StringView(str).bisect(' ').snd().trim();

        // any prefix of "recipes" and "ingredients" can be used.
        if ("recipes".startsWith(x.toString())) {
            return Result.of(Pair.of(CommandTarget.RECIPE, xs.toString()));
        } else if ("ingredients".startsWith(x.toString())) {
            return Result.of(Pair.of(CommandTarget.INGREDIENT, xs.toString()));
        } else {
            return Result.error("unknown target '%s...'",
                x.takeWhile(c -> !Character.isWhitespace(c))
            );
        }
    }
}
