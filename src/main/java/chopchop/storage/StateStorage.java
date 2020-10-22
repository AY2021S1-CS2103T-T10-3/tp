package chopchop.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import chopchop.commons.exceptions.DataConversionException;
import chopchop.logic.history.CommandHistory;

public interface StateStorage {

    /**
     * Returns the file path of the data file.
     */
    Path getStateFilePath();

    Optional<List<CommandHistory>> readState() throws DataConversionException;

    Optional<List<CommandHistory>> readState(Path filePath) throws DataConversionException;

    void saveState(List<CommandHistory> states) throws IOException;

    void saveState(List<CommandHistory> states, Path filePath) throws IOException;

}
