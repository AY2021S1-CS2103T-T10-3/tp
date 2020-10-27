// FilterCommandParser.java
//@@author hjl99

package chopchop.logic.parser.commands;

import static chopchop.logic.parser.commands.CommonParser.getCommandTarget;
import static chopchop.logic.parser.commands.CommonParser.getFirstUnknownArgument;
import static chopchop.logic.parser.commands.CommonParser.getFirstAugmentedComponent;

import chopchop.commons.util.Result;
import chopchop.commons.util.Strings;
import chopchop.logic.commands.Command;
import chopchop.logic.commands.FilterIngredientCommand;
import chopchop.logic.commands.FilterRecipeCommand;
import chopchop.logic.parser.ArgName;
import chopchop.logic.parser.CommandArguments;
import chopchop.model.attributes.ExpiryDate;
import chopchop.model.attributes.ExpiryDateMatchesKeywordsPredicate;
import chopchop.model.attributes.IngredientsContainKeywordsPredicate;
import chopchop.model.attributes.TagContainsKeywordsPredicate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FilterCommandParser {

    private static final String commandName = Strings.COMMAND_FILTER;

    /**
     * Parses a 'filter' command. Syntax(es):
     * {@code filter recipe (keywords)+}
     * {@code filter ingredient (keywords)+}
     *
     * @param args the parsed command arguments from the {@code CommandParser}.
     * @return     a FilterCommand, if the input was valid.
     */
    public static Result<? extends Command> parseFilterCommand(CommandArguments args) {

        assert args.getCommand().equals(Strings.COMMAND_FILTER);

        // since the sub-commands check for the individual args, we don't need to check here.
        // just check for no edit-args.
        Optional<ArgName> foo;
        if ((foo = getFirstAugmentedComponent(args)).isPresent()) {
            return Result.error("'filter' command doesn't support edit-arguments");
        }

        return getCommandTarget(args)
            .then(target -> {
                if (args.getAllArguments().isEmpty()) {
                    return Result.error("Filtering criteria cannot be empty!");
                }

                switch (target.fst()) {
                case RECIPE:
                    return parseFilterRecipeCommand(args);

                case INGREDIENT:
                    return parseFilterIngredientCommand(args);

                default:
                    return Result.error("Can only filter recipes or ingredients ('%s' invalid)", target.fst());
                }
            });
    }


    private static Result<? extends Command> parseFilterIngredientCommand(CommandArguments args) {

        Optional<ArgName> foo;
        var supportedArgs = List.of(Strings.ARG_TAG, Strings.ARG_EXPIRY);
        if ((foo = getFirstUnknownArgument(args, supportedArgs)).isPresent()) {
            return Result.error("'filter ingredient' command doesn't support '%s'", foo.get());
        }

        var exps = args.getArgument(Strings.ARG_EXPIRY);
        var tags = args.getArgument(Strings.ARG_TAG);

        Optional<String> error;
        if ((error = checkImproperFieldInput("Expiry date", exps)).isPresent()) {
            return Result.error(error.get());
        } else if ((error = checkImproperFieldInput("Tag", tags)).isPresent()) {
            return Result.error(error.get());
        }

        return parseExpiryDates(exps)
            .map(optExpiry -> new FilterIngredientCommand(
                optExpiry.map(ExpiryDateMatchesKeywordsPredicate::new).orElse(null),
                tags.isEmpty() ? null : new TagContainsKeywordsPredicate(tags)
            ));
    }


    private static Result<? extends Command> parseFilterRecipeCommand(CommandArguments args) {

        Optional<ArgName> foo;
        var supportedArgs = List.of(Strings.ARG_TAG, Strings.ARG_INGREDIENT);
        if ((foo = getFirstUnknownArgument(args, supportedArgs)).isPresent()) {
            return Result.error("'filter recipe' command doesn't support '%s'", foo.get());
        }

        var ingredients = args.getArgument(Strings.ARG_INGREDIENT);
        var tags = args.getArgument(Strings.ARG_TAG);

        Optional<String> error;
        if ((error = checkImproperFieldInput("Tag", tags)).isPresent()) {
            return Result.error(error.get());
        } else if ((error = checkImproperFieldInput("Ingredient", ingredients)).isPresent()) {
            return Result.error(error.get());
        }

        return Result.of(new FilterRecipeCommand(
            tags.isEmpty() ? null : new TagContainsKeywordsPredicate(tags),
            ingredients.isEmpty() ? null : new IngredientsContainKeywordsPredicate(ingredients)
        ));
    }


    /**
     * Returns the first expiry date (chronologically) from the given list, or an
     * error if any of them failed to parse.
     */
    private static Result<Optional<ExpiryDate>> parseExpiryDates(List<String> args) {

        return Result.sequence(args.stream()
            .map(ExpiryDate::of)
            .collect(Collectors.toList())
        ).map(exps -> exps.stream().sorted().findFirst());
    }

    /**
     * Returns an error message if there was an error.
     */
    private static Optional<String> checkImproperFieldInput(String kind, List<String> list) {

        for (int i = 0; i < list.size(); i++) {
            var s = list.get(i);

            if (s.isEmpty()) {
                return Optional.of(String.format("%s cannot be empty", kind));
            } else if (kind.equals("Expiry date") && s.split("\\s+").length > 1) {
                return Optional.of(String.format("Expiry date cannot contain spaces"));
            }
        }

        return Optional.empty();
    }
}
