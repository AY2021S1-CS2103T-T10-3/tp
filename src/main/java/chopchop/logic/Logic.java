package chopchop.logic;

import java.nio.file.Path;
import java.util.List;

import chopchop.commons.core.GuiSettings;
import chopchop.logic.commands.CommandResult;
import chopchop.logic.commands.exceptions.CommandException;
import chopchop.logic.parser.exceptions.ParseException;
import chopchop.model.Model;
import chopchop.model.ReadOnlyEntryBook;
import chopchop.model.ingredient.Ingredient;
import chopchop.model.recipe.Recipe;
import javafx.collections.ObservableList;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     *
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /**
     * Computes the completion for the given user input. If there is no completion
     * available, the string is returned as-is.
     *
     * @param commandText the command
     * @return            the auto-completed input
     */
    String getCompletionForInput(String commandText);

    /**
     * Resets the autocompleter's internal state. This function should be called when
     * the text field is modified by the user.
     */
    void resetCompletionState();

    /**
     * Returns the RecipeBook.
     */
    ReadOnlyEntryBook<Recipe> getRecipeBook();

    /**
     * Returns the IngredientBook.
     */
    ReadOnlyEntryBook<Ingredient> getIngredientBook();

    /**
     * Returns an unmodifiable view of the filtered list of recipes.
     */
    ObservableList<Recipe> getFilteredRecipeList();

    /**
     * Returns an unmodifiable view of the filtered ingredient list.
     */
    ObservableList<Ingredient> getFilteredIngredientList();

    /**
     * Returns the user prefs' recipe book file path.
     */
    Path getRecipeBookFilePath();

    /**
     * Returns the user prefs' ingredient book file path.
     */
    Path getIngredientBookFilePath();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Returns the input history.
     */
    List<String> getInputHistory();

    /**
     * Returns the input history filtered by a prefix.
     */
    List<String> getInputHistory(String prefix);

    /**
     * Returns an unmodifiable view of the recommended recipe list.
     */
    ObservableList<Recipe> getRecommendedRecipeList();

    /**
     * Returns an unmodifiable view of the recommended recipe list that have ingredients that expire soon.
     */
    ObservableList<Recipe> getExpiringRecipeList();

    /**
     * Returns an unmodifiable view of the recommended recipe list that have not been made for a long time.
     */
//    ObservableList<Recipe> getOldRecipeList();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);
}
