package chopchop.logic.commands;

import static chopchop.logic.commands.CommandTestUtil.assertCommandSuccess;
import static chopchop.logic.commands.CommandTestUtil.assertIngredientCommandFailure;
import static chopchop.testutil.TypicalIngredients.getTypicalIngredientBook;

import chopchop.model.EntryBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import chopchop.model.Model;
import chopchop.model.ModelManager;
import chopchop.model.UserPrefs;
import chopchop.model.ingredient.Ingredient;
import chopchop.testutil.IngredientBuilder;

public class AddIngredientCommandIntTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new EntryBook<>(), getTypicalIngredientBook(), new UserPrefs());
    }

    @Test
    public void execute_newIngredient_success() {
        Ingredient validIngredient = new IngredientBuilder().build();

        Model expectedModel = new ModelManager(new EntryBook<>(), model.getIngredientBook(), new UserPrefs());
        expectedModel.addIngredient(validIngredient);

        assertCommandSuccess(new AddIngredientCommand(validIngredient), model,
            String.format(AddIngredientCommand.MESSAGE_ADD_INGREDIENT_SUCCESS, validIngredient), expectedModel);
    }
<<<<<<< HEAD

    @Test
    public void execute_duplicateIngredient_throwsCommandException() {
        Ingredient indInList = model.getIngredientBook().getFoodEntryList().get(0);
        assertIngredientCommandFailure(new AddIngredientCommand(indInList), model,
            AddIngredientCommand.MESSAGE_DUPLICATE_INGREDIENT);
    }

=======
>>>>>>> b04c1647ff463527478c9337eb1f7248df163b1e
}
