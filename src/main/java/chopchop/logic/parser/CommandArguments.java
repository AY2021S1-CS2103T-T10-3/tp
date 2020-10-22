// CommandArguments.java

package chopchop.logic.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import chopchop.commons.util.Pair;

/**
 * A container class to hold a parsed command, holding its:
 * - name
 * - unnamed arguments
 * - named arguments
 *
 * For example, a command such as:
 * {@code add ingredient onions /quantity 500g /location fridge /location shelf}
 *
 * Would have the following properties:
 * {@code
 *      command     = "add"
 *      remaining   = "ingredient onions"
 *      arguments   = [
 *                      ("quantity", "500g"),
 *                      ("location", "fridge"),
 *                      ("location", "shelf")
 *                    ]
 * }
 *
 * For named arguments, the name cannot be empty, while the value can be empty. For example,
 * {@code ... /tag /tag /tag} is valid, but {@code ... / owo / uwu} is not.
 *
 * This class makes no judgement on the contents of any of the properties. As seen in the
 * example above, multiple arguments with the same name are allowed; the ordering of named
 * arguments is preserved.
 */
public class CommandArguments {

    private final String command;
    private final String remaining;
    private final List<Pair<ArgName, String>> arguments;

    /**
     * Constructs a set of command arguments consisting of only the command name.
     *
     * @param command   the name of the command
     */
    public CommandArguments(String command) {
        this.command    = command;
        this.remaining  = "";
        this.arguments  = new ArrayList<>();
    }

    /**
     * Constructs a set of command arguments consisting of the command name and
     * some number of named arguments.
     *
     * @param command   the name of the command
     * @param arguments a map of named arguments and their values
     */
    public CommandArguments(String command, List<Pair<ArgName, String>> arguments) {
        this.command    = command;
        this.remaining  = "";
        this.arguments  = new ArrayList<>(arguments);
    }

    /**
     * Constructs a set of command arguments consisting of the command name, the command
     * target, any remaining non-named arguments, and some number of named arguments.
     *
     * @param command   the name of the command
     * @param remaining any remaining non-named arguments
     * @param arguments a map of named arguments and their values
     */
    public CommandArguments(String command, String remaining, List<Pair<ArgName, String>> arguments) {

        this.command    = command;
        this.remaining  = remaining;
        this.arguments  = new ArrayList<>(arguments);
    }

    public String getCommand() {
        return this.command;
    }

    public String getRemaining() {
        return this.remaining;
    }

    /**
     * Gets the arguments with the given name. Since it makes sense for some parameters to
     * be specified more than once, this method returns a list of all arguments with the
     * given name.
     */
    public List<String> getArgument(ArgName name) {
        return this.arguments
            .stream()
            .filter(p -> p.fst().equals(name))
            .map(p -> p.snd())
            .collect(Collectors.toList());
    }

    public List<Pair<ArgName, String>> getAllArguments() {
        return new ArrayList<>(this.arguments);
    }
}
