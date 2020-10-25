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
     * Finds the first augmented argument in the given {@code args} and returns it (optionally), so
     * we can print a nice error message. The intended use of this function is to parse commands
     * that don't take augmented arguments (which is all commands except EDIT).
     */
    public static Optional<ArgName> getFirstAugmentedComponent(CommandArguments args) {

        return args.getAllArguments()
            .stream()
            .filter(arg -> !arg.fst().getComponents().isEmpty())
            .map(p -> p.fst())
            .findFirst();
    }

    /**
     * Gets the 'target' of a command, which is either 'ingredient' or 'recipe'. Returns either an error
     * if the target was invalid or empty, or a pair consisting of the {@code CommandTarget}, and the
     * rest of the unnamed arguments.
     *
     * @param acceptsPlural determines if 'recipeS' and 'ingredientS' are accepted in addition
     *                      to their singular counterparts.
     */
    public static Result<Pair<CommandTarget, String>> getCommandTarget(CommandArguments args,
        boolean acceptsPlural) {

        var str = args.getRemaining();

        if (str.isEmpty()) {
            return Result.error("no target specified (either 'recipe', 'ingredient' or 'recommendation')");
        }

        var x = new StringView(str).bisect(' ').fst().trim();
        var xs = new StringView(str).bisect(' ').snd().trim();

        return Result.ofOptional(
            CommandTarget.of(x.toString(), acceptsPlural)
                .map(target -> Pair.of(target, xs.toString())),

            String.format("unknown target '%s'", x)
        );
    }

    /**
     * See {@code getCommandTarget(CommandArguments)}; this one is just a convenience overload.
     */
    public static Result<Pair<CommandTarget, String>> getCommandTarget(CommandArguments args) {
        return getCommandTarget(args, /* acceptsPlural: */ false);
    }
}
