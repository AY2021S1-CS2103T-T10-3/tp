// RecipeEditDescriptor.java

package chopchop.logic.edit;

import java.util.List;
import java.util.Optional;

public class RecipeEditDescriptor {

    private final List<IngredientEditDescriptor> ingredientEdits;
    private final List<StepEditDescriptor> stepEdits;
    private final List<TagEditDescriptor> tagEdits;
    private final Optional<String> nameEdit;

    /**
     * Creates a RecipeEditDescriptor to edit a recipe.
     *
     * @param ingredientEdits   the list of edit descriptors for ingredients
     * @param stepEdits         the list of edit descriptors for steps
     * @param tagEdits          the list of edit descriptors for tags
     * @param nameEdit          the edited name (if present)
     */
    public RecipeEditDescriptor(Optional<String> nameEdit, List<IngredientEditDescriptor> ingredientEdits,
        List<StepEditDescriptor> stepEdits, List<TagEditDescriptor> tagEdits) {

        this.ingredientEdits = ingredientEdits;
        this.stepEdits = stepEdits;
        this.tagEdits = tagEdits;
        this.nameEdit = nameEdit;
    }

    public List<IngredientEditDescriptor> getIngredientEdits() {
        return this.ingredientEdits;
    }

    public List<StepEditDescriptor> getStepEdits() {
        return this.stepEdits;
    }

    public List<TagEditDescriptor> getTagEdits() {
        return this.tagEdits;
    }

    public Optional<String> getNameEdit() {
        return this.nameEdit;
    }
}
