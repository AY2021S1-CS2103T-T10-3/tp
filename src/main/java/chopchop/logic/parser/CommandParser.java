// CommandParser.java

package chopchop.logic.parser;

import java.util.List;
import java.util.ArrayList;

import chopchop.logic.commands.RedoCommand;
import chopchop.logic.commands.UndoCommand;
import chopchop.util.Pair;
import chopchop.util.Result;
import chopchop.util.Strings;
import chopchop.util.StringView;

import chopchop.logic.commands.Command;
import chopchop.logic.commands.QuitCommand;

import static chopchop.logic.parser.commands.AddCommandParser.parseAddCommand;
import static chopchop.logic.parser.commands.HelpCommandParser.parseHelpCommand;
import static chopchop.logic.parser.commands.ListCommandParser.parseListCommand;
import static chopchop.logic.parser.commands.FindCommandParser.parseFindCommand;
import static chopchop.logic.parser.commands.DeleteCommandParser.parseDeleteCommand;
import static chopchop.logic.parser.commands.MakeCommandParser.parseMakeCommand;

public class CommandParser {

    private Result<List<Pair<ArgName, String>>> parseNamedArguments(StringView input) {

        var ret = new ArrayList<Pair<ArgName, String>>();
        while (input.size() > 0) {

            if (input.find('/') != 0) {
                break;
            }

            // TODO: this won't handle things like slashes in dates. ideally we want to
            // split based on " /" (ie. there must be a leading space before the slash),
            // but that requires changing StringView::bisect. later.
            var currentArg = input.drop(1).bisect('/', input);

            {
                var argName = new StringView("");
                var argValue = new StringView("");

                currentArg.bisect(argName, ' ', argValue);
                if (argName.isEmpty()) {
                    return Result.error("argument name cannot be empty");
                }

                ret.add(Pair.of(new ArgName(argName.trim().toString()), argValue.trim().toString()));
            }

            if (input.isEmpty()) {
                break;
            }

            input = input.undrop(1);
        }

        return Result.of(ret);
    }

    /**
     * Parse an input string into its constituent components, including the name of the command, its target,
     * and its arguments. See the documentation for {@link chopchop.logic.parser.CommandArguments} for the recognised
     * components of a given input string.
     *
     * @param input the input string to parse
     * @return      the parsed components, iff parsing succeeded; an empty optional otherwise.
     */
    private Result<CommandArguments> parseArgs(String input) {

        var sv = new StringView(input);

        var x = new StringView("");
        var xs = new StringView("");

        sv.bisect(x, ' ', xs);

        var command = x.toString().strip();

        xs.bisect(x, '/', xs);
        var theRest = x.toString().strip();

        if (!xs.isEmpty()) {
            xs = xs.undrop(1);
            assert xs.at(0) == '/';
        }

        return this.parseNamedArguments(xs)
            .map(args -> new CommandArguments(command, theRest, args));
    }


    /**
     * Parse a user input into a {@code Command}, or an error message if parsing failed.
     *
     * @param input the input string to parse
     * @return      the parsed command on success; an error message otherwise.
     */
    public Result<Command> parse(String input) {

        return this.parseArgs(input)
            .then(args -> {
                switch (args.getCommand()) {

                case Strings.COMMAND_ADD:       return parseAddCommand(args);
                case Strings.COMMAND_HELP:      return parseHelpCommand(args);
                case Strings.COMMAND_FIND:      return parseFindCommand(args);
                case Strings.COMMAND_LIST:      return parseListCommand(args);
                case Strings.COMMAND_DELETE:    return parseDeleteCommand(args);
                case Strings.COMMAND_MAKE:      return parseMakeCommand(args);
                case Strings.COMMAND_UNDO:      return Result.of(new UndoCommand());
                case Strings.COMMAND_REDO:      return Result.of(new RedoCommand());

                case Strings.COMMAND_QUIT:      return Result.of(new QuitCommand());

                default:
                    return Result.error("unknown command '%s'", args.getCommand());
                }
            });
    }
}
