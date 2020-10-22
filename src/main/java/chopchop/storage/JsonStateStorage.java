package chopchop.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import chopchop.commons.exceptions.DataConversionException;
import chopchop.logic.history.CommandHistory;

public class JsonStateStorage implements StateStorage {
    private final Path filePath;

    public JsonStateStorage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the file path of the data file.
     */
    @Override
    public Path getStateFilePath() {
        return this.filePath;
    }

    @Override
    public Optional<List<CommandHistory>> readState() throws DataConversionException {
        return Optional.empty();
    }

    @Override
    public Optional<List<CommandHistory>> readState(Path filePath) throws DataConversionException {
        return Optional.empty();
    }

    @Override
    public void saveState(List<CommandHistory> states) throws IOException {

    }

    @Override
    public void saveState(List<CommandHistory> states, Path filePath) throws IOException {

    }
}
