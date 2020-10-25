package chopchop.model;

import static chopchop.commons.util.CollectionUtil.requireAllNonNull;
import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import chopchop.commons.core.GuiSettings;
import chopchop.commons.core.LogsCenter;
import chopchop.model.ingredient.Ingredient;
import chopchop.model.recipe.Recipe;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/**
 * Represents the in-memory model of the recipe and ingredient book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final UserPrefs userPrefs;
    private final EntryBook<Recipe> recipeBook;
    private final EntryBook<Ingredient> ingredientBook;
    private final FilteredList<Recipe> filteredRecipes;
    private final FilteredList<Ingredient> filteredIngredients;
    private final FilteredList<Recipe> recommendedRecipes;

    /**
     * Initializes a ModelManager with the given RecipeBook, IngredientBook and userPrefs.
     */
    public ModelManager(ReadOnlyEntryBook<Recipe> recipeBook, ReadOnlyEntryBook<Ingredient> ingredientBook,
                        ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(recipeBook, ingredientBook, userPrefs);

        logger.fine("Initializing with recipe book: " + recipeBook + ", ingredient book: " + ingredientBook
                + " and user prefs " + userPrefs);

        this.userPrefs = new UserPrefs(userPrefs);
        this.recipeBook = new EntryBook<>(recipeBook);
        this.ingredientBook = new EntryBook<>(ingredientBook);

        this.filteredRecipes = new FilteredList<>(this.recipeBook.getEntryList());
        this.filteredIngredients = new FilteredList<>(this.ingredientBook.getEntryList());
        this.recommendedRecipes = new FilteredList<>(this.recipeBook.getEntryList());
        this.updateRecommendedRecipeList();
    }

    public ModelManager() {
        this(new EntryBook<>(), new EntryBook<>(), new UserPrefs());
    }

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return this.userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return this.userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        this.userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getRecipeBookFilePath() {
        return this.userPrefs.getRecipeBookFilePath();
    }

    @Override
    public void setRecipeBookFilePath(Path recipeBookFilePath) {
        requireNonNull(recipeBookFilePath);
        this.userPrefs.setRecipeBookFilePath(recipeBookFilePath);
    }

    @Override
    public void setRecipeBook(ReadOnlyEntryBook<Recipe> recipeBook) {
        this.recipeBook.resetData(recipeBook);
    }

    @Override
    public ReadOnlyEntryBook<Recipe> getRecipeBook() {
        return this.recipeBook;
    }

    @Override
    public boolean hasRecipe(Recipe recipe) {
        requireNonNull(recipe);
        return this.recipeBook.has(recipe);
    }

    @Override
    public void deleteRecipe(Recipe target) {
        this.recipeBook.remove(target);
    }

    @Override
    public void addRecipe(Recipe recipe) {
        this.recipeBook.add(recipe);
        this.updateFilteredRecipeList(PREDICATE_SHOW_ALL_ENTRIES);
    }

    @Override
    public void setRecipe(Recipe target, Recipe editedRecipe) {
        requireAllNonNull(target, editedRecipe);

        this.recipeBook.set(target, editedRecipe);
    }

    @Override
    public Optional<Recipe> findRecipeWithName(String name) {
        return this.recipeBook.getEntryList()
            .stream()
            .filter(recipe -> recipe.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    /**
     * Returns an unmodifiable view of the list of {@code Recipe} backed by the internal list of
     * {@code versionedEntryBook<Recipe>}
     */
    @Override
    public ObservableList<Recipe> getFilteredRecipeList() {
        return this.filteredRecipes;
    }

    @Override
    public void updateFilteredRecipeList(Predicate<? super Recipe> predicate) {
        requireNonNull(predicate);
        this.filteredRecipes.setPredicate(predicate);
    }

    /**
     * Returns the user prefs' ingredient book file path.
     */
    @Override
    public Path getIngredientBookFilePath() {
        return this.userPrefs.getIngredientBookFilePath();
    }

    /**
     * Sets the user prefs' address book file path.
     *
     * @param ingredientBookFilePath
     */
    @Override
    public void setIngredientBookFilePath(Path ingredientBookFilePath) {
        requireNonNull(ingredientBookFilePath);
        this.userPrefs.setIngredientBookFilePath(ingredientBookFilePath);
    }

    public void setIngredientBook(ReadOnlyEntryBook<Ingredient> ingredientBook) {
        this.ingredientBook.resetData(ingredientBook);
    }

    @Override
    public ReadOnlyEntryBook<Ingredient> getIngredientBook() {
        return this.ingredientBook;
    }

    @Override
    public boolean hasIngredient(Ingredient ingredient) {
        requireNonNull(ingredient);
        return this.ingredientBook.has(ingredient);
    }

    @Override
    public void deleteIngredient(Ingredient target) {
        this.ingredientBook.remove(target);
    }

    @Override
    public void addIngredient(Ingredient ingredient) {
        this.ingredientBook.add(ingredient);
        this.updateFilteredIngredientList(PREDICATE_SHOW_ALL_ENTRIES);
    }

    @Override
    public void setIngredient(Ingredient target, Ingredient editedIngredient) {
        requireAllNonNull(target, editedIngredient);

        this.ingredientBook.set(target, editedIngredient);
    }

    @Override
    public Optional<Ingredient> findIngredientWithName(String name) {
        return this.ingredientBook.getEntryList()
            .stream()
            .filter(ingredient -> ingredient.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    /**
     * Returns an unmodifiable view of the list of {@code Ingredient} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Ingredient> getFilteredIngredientList() {
        return this.filteredIngredients;
    }

    @Override
    public void updateFilteredIngredientList(Predicate<? super Ingredient> predicate) {
        requireNonNull(predicate);
        this.filteredIngredients.setPredicate(predicate);
    }

    @Override
    public ObservableList<Recipe> getRecommendedRecipeList() {
        return this.recommendedRecipes;
    }

    @Override
    public void updateRecommendedRecipeList() {
        this.recommendedRecipes.setPredicate(recipe -> recipe.getIngredients().stream()
                .allMatch(ingredientRef -> this.getIngredientBook().getEntryList().stream()
                        .anyMatch(ingredient -> ingredientRef.getName().equalsIgnoreCase(ingredient.getName()))));
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;

        return this.userPrefs.equals(other.userPrefs)
                && this.recipeBook.equals(other.recipeBook)
                && this.filteredRecipes.equals(other.filteredRecipes)
                && this.ingredientBook.equals(other.ingredientBook)
                && this.filteredIngredients.equals(other.filteredIngredients);
    }
}
