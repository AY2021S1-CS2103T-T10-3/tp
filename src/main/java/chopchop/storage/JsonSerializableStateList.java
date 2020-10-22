package chopchop.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonSerializableStateList {
    private final List<JsonAdaptedState> states;

    /**
     * Constructs a {@code JsonSerializable} with the given list of command histories.
     */
    @JsonCreator
    public JsonSerializableStateList(@JsonProperty("states") List<JsonAdaptedState> states) {
        this.states = new ArrayList<>(states);
    }
}
