package chopchop.model.recipe;

import static chopchop.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

import chopchop.model.Entry;
import chopchop.model.attributes.Step;
import chopchop.model.ingredient.IngredientReference;

public class Recipe extends Entry {
    private final List<IngredientReference> ingredients = new ArrayList<>();
    private final List<Step> steps = new ArrayList<>();

    /**
     * Every field must be present and not null.
     */
    public Recipe(String name, List<IngredientReference> ingredients, List<Step> steps) {
        super(name);
        requireAllNonNull(ingredients, steps);
        this.ingredients.addAll(ingredients);
        this.steps.addAll(steps);
    }

    /**
     * Returns an immutable ingredient list, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public List<IngredientReference> getIngredients() {
        return Collections.unmodifiableList(this.ingredients);
    }

    /**
     * Returns an immutable step list, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public List<Step> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    @Override
    public boolean isSame(Entry other) {
        return other == this
                || (other instanceof Recipe
                && this.name.equals(((Recipe) other).name));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof Recipe
                && this.name.equals(((Recipe) other).name)
                && this.ingredients.equals(((Recipe) other).ingredients)
                && this.steps.equals(((Recipe) other).steps));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, ingredients, steps);
    }

    @Override
    public String toString() {
        var recipeJoiner = new StringJoiner(" ");
        var ingredientJoiner = new StringJoiner(", ");
        var stepJoiner = new StringJoiner(", ");
        var counter = new AtomicInteger(1);

        this.getIngredients().forEach(ingredient -> ingredientJoiner.add(ingredient.toString()));
        this.getSteps().forEach(step -> stepJoiner.add(counter.getAndIncrement() + ". " + step.toString()));

        recipeJoiner.add(this.getName())
                .add("Ingredients:")
                .add(ingredientJoiner.toString())
                .add("Steps:")
                .add(stepJoiner.toString());

        return recipeJoiner.toString();
    }
}

