package chopchop.logic.commands;

import static chopchop.logic.commands.CommandTestUtil.assertCommandSuccess;
import static chopchop.testutil.TypicalIngredients.getTypicalIngredientBook;

import chopchop.model.EntryBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import chopchop.model.Model;
import chopchop.model.ModelManager;
import chopchop.model.UserPrefs;
import chopchop.testutil.IngredientBuilder;

public class AddIngredientCommandIntTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new EntryBook<>(), getTypicalIngredientBook(), new UserPrefs());
    }

    @Test
    public void execute_newIngredient_success() {
        var validIngredient = new IngredientBuilder().build();

        var expectedModel = new ModelManager(new EntryBook<>(), model.getIngredientBook(), new UserPrefs());
        expectedModel.addIngredient(validIngredient);

        assertCommandSuccess(new AddIngredientCommand(validIngredient), model, expectedModel);
    }
}
