package chopchop.model;

import static chopchop.commons.util.CollectionUtil.requireAllNonNull;
import static java.util.Objects.requireNonNull;
import java.util.Iterator;
import java.util.List;
import chopchop.model.exceptions.DuplicateEntryException;
import chopchop.model.exceptions.EntryNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UniqueFoodEntryList implements Iterable<Item> {

    private final ObservableList<Item> internalList = FXCollections.observableArrayList();
    private final ObservableList<Item> internalUnmodifiableList =
        FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent food as the given argument.
     */
    public boolean contains(Item toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::equals);
    }

    /**
     * Adds a FoodEntry to the list.
     * The FoodEntry must not already exist in the list.
     */
    public void add(Item toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateEntryException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the FoodEntry {@code target} in the list with {@code editedFoodEntry}.
     * {@code target} must exist in the list.
     * The FoodEntry identity of {@code editedFoodEntry} must not be the same as another existing
     * FoodEntry in the list.
     */
    public void setEntry(Item target, Item editedFoodEntry) {
        requireAllNonNull(target, editedFoodEntry);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new EntryNotFoundException();
        }

        if (!target.equals(editedFoodEntry) && contains(editedFoodEntry)) {
            throw new DuplicateEntryException();
        }

        internalList.set(index, editedFoodEntry);
    }

    /**
     * Removes the equivalent FoodEntry from the list.
     * The FoodEntry must exist in the list.
     */
    public void remove(Item toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new EntryNotFoundException();
        }
    }

    public void setFoodEntries(UniqueFoodEntryList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code FoodEntries}.
     * {@code FoodEntries} must not contain duplicate FoodEntries.
     */
    public void setFoodEntries(List<? extends Item> entries) {
        requireAllNonNull(entries);
        //if (!ingredientsAreUnique(entries)) {
        if (!foodEntriesAreUnique(entries)) {
            throw new DuplicateEntryException();
        }

        internalList.setAll(entries);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Item> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Item> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof UniqueFoodEntryList // instanceof handles nulls
            && internalList.equals(((UniqueFoodEntryList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code FoodEntries} contains only unique FoodEntries.
     */
    private boolean ingredientsAreUnique(List<? extends Item> ingredients) {
        for (int i = 0; i < ingredients.size() - 1; i++) {
            for (int j = i + 1; j < ingredients.size(); j++) {
                if (ingredients.get(i).equals(ingredients.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns true if {@code FoodEntries} contains only unique FoodEntries.
     */
    private boolean recipesAreUnique(List<? extends Item> recipes) {
        for (int i = 0; i < recipes.size() - 1; i++) {
            for (int j = i + 1; j < recipes.size(); j++) {
                if (recipes.get(i).equals(recipes.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns true if {@code FoodEntries} contains only unique FoodEntries.
     */
    private boolean foodEntriesAreUnique(List<? extends Item> foodEntries) {
        for (int i = 0; i < foodEntries.size() - 1; i++) {
            for (int j = i + 1; j < foodEntries.size(); j++) {
                if (foodEntries.get(i).equals(foodEntries.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

}
