// AddCommandParser.java

package chopchop.logic.parser.commands;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import chopchop.model.attributes.Tag;
import chopchop.commons.util.Result;
import chopchop.commons.util.Strings;
import chopchop.commons.util.StringView;

import chopchop.model.recipe.Recipe;
import chopchop.model.ingredient.Ingredient;
import chopchop.model.ingredient.IngredientReference;

import chopchop.model.attributes.Step;
import chopchop.model.attributes.Quantity;
import chopchop.model.attributes.ExpiryDate;
import chopchop.model.attributes.units.Count;

import chopchop.logic.parser.ArgName;
import chopchop.logic.parser.CommandArguments;

import chopchop.logic.commands.Command;
import chopchop.logic.commands.AddRecipeCommand;
import chopchop.logic.commands.AddIngredientCommand;

import static chopchop.logic.parser.commands.CommonParser.getCommandTarget;
import static chopchop.logic.parser.commands.CommonParser.getFirstUnknownArgument;

public class AddCommandParser {

    /**
     * Parses an 'add' command. Syntax(es):
     * {@code add recipe NAME [/ingredient INGREDIENT_NAME [/qty QTY1]...]... [/step STEP]...}
     * {@code add ingredient NAME [/qty QUANTITY] [/expiry DATE]}
     *
     * @param args the parsed command arguments from the {@code CommandParser}.
     * @return     an AddCommand, if the input was valid.
     */
    public static Result<? extends Command> parseAddCommand(CommandArguments args) {
        if (!args.getCommand().equals(Strings.COMMAND_ADD)) {
            return Result.error("invalid command '%s' (expected '%s')", args.getCommand(), Strings.COMMAND_ADD);
        }

        return getCommandTarget(args)
            .then(target -> {
                if (target.snd().isEmpty()) {
                    return Result.error("recipe or ingredient name cannot be empty");
                }

                switch (target.fst()) {
                case RECIPE:
                    return parseAddRecipeCommand(target.snd().strip(), args);

                case INGREDIENT:
                    return parseAddIngredientCommand(target.snd().strip(), args);

                default:
                    return Result.error("can only add recipes or ingredients ('%s' invalid)", target.fst());
                }
            });
    }

    /**
     * Parses an 'add ingredient' command. Syntax:
     * {@code add ingredient NAME [/qty QUANTITY] [/expiry DATE]}
     */
    private static Result<AddIngredientCommand> parseAddIngredientCommand(String name, CommandArguments args) {
        assert args.getCommand().equals(Strings.COMMAND_ADD);

        Optional<ArgName> foo;
        if ((foo = getFirstUnknownArgument(args, List.of(Strings.ARG_QUANTITY,
            Strings.ARG_EXPIRY, Strings.ARG_TAG))).isPresent()) {

            return Result.error("'add ingredient' command doesn't support '%s'\n%s",
                foo.get(), AddIngredientCommand.MESSAGE_USAGE);
        }

        var qtys = args.getArgument(Strings.ARG_QUANTITY);
        if (qtys.size() > 1) {
            return Result.error("multiple quantities specified\n%s", AddIngredientCommand.MESSAGE_USAGE);
        }

        var exps = args.getArgument(Strings.ARG_EXPIRY);
        if (exps.size() > 1) {
            return Result.error("multiple expiry dates specified\n%s", AddIngredientCommand.MESSAGE_USAGE);
        }

        var tags = args.getArgument(Strings.ARG_TAG);

        // looks weird, but basically this extracts the /qty and /expiry arguments (if present),
        // then constructs the command from it -- while returning any intermediate error messages.
        try {
            Optional<Set<String>> tagSet = tags.isEmpty() ? Optional.empty() : Optional.of(Set.copyOf(tags));
            return Result.transpose(qtys
                .stream()
                .findFirst()
                .map(Quantity::parse))
                .then(qty -> Result.transpose(exps
                    .stream()
                    .findFirst()
                    .map(e -> Result.of(e)))
                    .map(exp -> createAddIngredientCommand(name, qty, exp, tagSet))
                );
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * Parses an 'add ingredient' command. Syntax:
     * {@code add recipe NAME [/ingredient INGREDIENT_NAME [/qty QTY1]...]... [/step STEP]...}
     */
    private static Result<AddRecipeCommand> parseAddRecipeCommand(String name, CommandArguments args) {
        assert args.getCommand().equals(Strings.COMMAND_ADD);

        Optional<ArgName> foo;
        if ((foo = getFirstUnknownArgument(args, List.of(Strings.ARG_QUANTITY,
            Strings.ARG_INGREDIENT, Strings.ARG_STEP))).isPresent()) {

            return Result.error("'add recipe' command doesn't support '%s'\n%s",
                foo.get(), AddRecipeCommand.MESSAGE_USAGE);
        }

        return parseIngredientList(args)
            .map(ingrs -> createAddRecipeCommand(name, ingrs,
                    args.getAllArguments()
                        .stream()
                        .filter(p -> p.fst().equals(Strings.ARG_STEP))
                        .map(p -> p.snd())
                        .map(x -> new Step(x))
                        .collect(Collectors.toList()))
            );
    }

    /**
     * Parse the list of ingredients.
     */
    private static Result<List<IngredientReference>> parseIngredientList(CommandArguments args) {

        // what is this, imperative code??
        var arglist = args.getAllArguments();

        var ingredients = new ArrayList<IngredientReference>();

        for (int i = 0; i < arglist.size(); i++) {

            var p = arglist.get(i);
            if (p.fst().equals(Strings.ARG_INGREDIENT)) {

                var name = p.snd();
                Optional<Quantity> quantity = Optional.empty();

                // check the next argument for a quantity (which is optional)
                if (i + 1 < arglist.size()) {
                    var q = arglist.get(i + 1);
                    if (q.fst().equals(Strings.ARG_QUANTITY)) {
                        var qty = Quantity.parse(q.snd());
                        if (qty.isError()) {
                            return Result.error(qty.getError());
                        } else {
                            quantity = Optional.of(qty.getValue());

                            // skip the quantity now that we've handled it.
                            i++;
                        }
                    }
                }

                ingredients.add(createIngredientReference(name, quantity));

            } else if (p.fst().equals(Strings.ARG_QUANTITY)) {
                return Result.error("'%s' without ingredient in argument %d [/qty %s...]",
                    Strings.ARG_QUANTITY, i + 1, new StringView(p.snd()).take(4));
            } else {
                // do nothing.
            }
        }

        return Result.of(ingredients);
    }












    private static IngredientReference createIngredientReference(String name, Optional<Quantity> qty) {
        return new IngredientReference(name, qty.orElse(Count.of(1)));
    }

    private static AddRecipeCommand createAddRecipeCommand(String name,
        List<IngredientReference> ingredients, List<Step> steps) {

        return new AddRecipeCommand(new Recipe(
            name, ingredients, steps
        ));
    }


    private static AddIngredientCommand createAddIngredientCommand(String name, Optional<Quantity> qty,
        Optional<String> expiry, Optional<Set<String>> tags) {

        return new AddIngredientCommand(new Ingredient(name,
            qty.orElse(Count.of(1)),
            expiry.map(ExpiryDate::new).orElse(null),
            tags.map(x->x.stream().map(Tag::new).collect(Collectors.toSet())).orElse(null)));
    }
}
