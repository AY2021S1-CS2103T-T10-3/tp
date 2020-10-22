package chopchop.model;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import chopchop.commons.core.GuiSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefs implements ReadOnlyUserPrefs {
    private GuiSettings guiSettings = new GuiSettings();
    private Path ingredientBookFilePath = Paths.get("data" , "ingredientbook.json");
    private Path recipeBookFilePath = Paths.get("data" , "recipebook.json");
    private Path stateFilePath = Paths.get("data", "state.json");

    /**
     * Creates a {@code UserPrefs} with default values.
     */
    public UserPrefs() {}

    /**
     * Creates a {@code UserPrefs} with the prefs in {@code userPrefs}.
     */
    public UserPrefs(ReadOnlyUserPrefs userPrefs) {
        this();
        this.resetData(userPrefs);
    }

    /**
     * Resets the existing data of this {@code UserPrefs} with {@code newUserPrefs}.
     */
    public void resetData(ReadOnlyUserPrefs newUserPrefs) {
        requireNonNull(newUserPrefs);
        this.setGuiSettings(newUserPrefs.getGuiSettings());
        this.setIngredientBookFilePath(newUserPrefs.getIngredientBookFilePath());
        this.setRecipeBookFilePath(newUserPrefs.getRecipeBookFilePath());
    }

    @Override
    public GuiSettings getGuiSettings() {
        return this.guiSettings;
    }

    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        this.guiSettings = guiSettings;
    }

    @Override
    public Path getIngredientBookFilePath() {
        return this.ingredientBookFilePath;
    }

    @Override
    public Path getRecipeBookFilePath() {
        return this.recipeBookFilePath;
    }

    @Override
    public Path getStateFilePath() {
        return this.stateFilePath;
    }

    public void setIngredientBookFilePath(Path ingredientBookFilePath) {
        requireNonNull(ingredientBookFilePath);
        this.ingredientBookFilePath = ingredientBookFilePath;
    }

    public void setRecipeBookFilePath(Path recipeBookFilePath) {
        requireNonNull(recipeBookFilePath);
        this.recipeBookFilePath = recipeBookFilePath;
    }

    public void setStateFilePath(Path stateFilePath) {
        requireNonNull(stateFilePath);
        this.stateFilePath = stateFilePath;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UserPrefs)) { //this handles null as well.
            return false;
        }

        UserPrefs o = (UserPrefs) other;

        return this.guiSettings.equals(o.guiSettings)
            && this.ingredientBookFilePath.equals(o.ingredientBookFilePath)
            && this.recipeBookFilePath.equals(o.recipeBookFilePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.guiSettings, this.ingredientBookFilePath, this.recipeBookFilePath);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Gui Settings : " + this.guiSettings);
        sb.append("\nLocal ingredient data file location : " + this.ingredientBookFilePath);
        sb.append("\nLocal recipe data file location : " + this.recipeBookFilePath);
        return sb.toString();
    }
}
