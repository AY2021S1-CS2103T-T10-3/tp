package chopchop.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import chopchop.commons.exceptions.IllegalValueException;
import chopchop.logic.history.CommandHistory;


/**
 * Essentially JsonAdaptedCommandHistory for now.
 */
public class JsonAdaptedState {
    public static final String STATE_MISSING_FIELD_MESSAGE_FORMAT = "State's %s field is missing!";

    private final String cmd;
    //todo: add timestamp

    /**
     * Constructs a {@code JsonAdaptedState} with the given command
     */
    @JsonCreator
    public JsonAdaptedState(@JsonProperty("command") String cmd) {
        this.cmd = cmd;
    }

    /**
     * Converts a given {@code CommandHistory} into this class for Jackson use.
     */
    public JsonAdaptedState(CommandHistory source) {
        this.cmd = source.getCommandText();
    }

    /**
     * Converts this Jackson-friendly adapted Command History object into its original object.
     */
    public CommandHistory toType() throws IllegalValueException {
        if (this.cmd == null) {
            throw new IllegalValueException(String.format(STATE_MISSING_FIELD_MESSAGE_FORMAT, "cmd"));
        }
        //todo: add the actionable command? uwu just make it work first.
        return new CommandHistory(this.cmd, null);
    }
}
