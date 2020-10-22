// Strings.java

package chopchop.commons.util;

import chopchop.logic.parser.ArgName;

/**
 * Defines user-facing strings for the whole program.
 */
public class Strings {

    // argument names
    public static final ArgName ARG_STEP            = new ArgName("step");
    public static final ArgName ARG_INGREDIENT      = new ArgName("ingredient");
    public static final ArgName ARG_QUANTITY        = new ArgName("qty");
    public static final ArgName ARG_EXPIRY          = new ArgName("expiry");
    public static final ArgName ARG_TAG             = new ArgName("tag");

    // command names
    public static final String COMMAND_ADD          = "add";
    public static final String COMMAND_HELP         = "help";
    public static final String COMMAND_LIST         = "list";
    public static final String COMMAND_FIND         = "find";
    public static final String COMMAND_QUIT         = "quit";
    public static final String COMMAND_DELETE       = "delete";
    public static final String COMMAND_MAKE         = "make";
    public static final String COMMAND_UNDO         = "undo";
    public static final String COMMAND_REDO         = "redo";

    // command targets
    // public static final String TARGET_RECIPE        = "recipe";
    // public static final String TARGET_INGREDIENT    = "ingredient";
}
