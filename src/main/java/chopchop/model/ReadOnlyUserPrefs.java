package chopchop.model;

import java.nio.file.Path;

import chopchop.commons.core.GuiSettings;

/**
 * Unmodifiable view of user prefs.
 */
public interface ReadOnlyUserPrefs {
    GuiSettings getGuiSettings();

    Path getIngredientBookFilePath();

    Path getRecipeBookFilePath();

    Path getStateFilePath();
}
