package chopchop.logic.commands;

import static chopchop.logic.commands.CommandTestUtil.assertCommandSuccess;
import static chopchop.logic.commands.CommandTestUtil.showPersonAtIndex;
import static chopchop.testutil.TypicalReferences.INDEXED_FIRST;
import static chopchop.testutil.TypicalIngredients.getTypicalIngredientBook;

import chopchop.model.EntryBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import chopchop.model.Model;
import chopchop.model.ModelManager;
import chopchop.model.UserPrefs;

public class ListIngredientCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new EntryBook<>(), getTypicalIngredientBook(), new UserPrefs());
        expectedModel = new ModelManager(new EntryBook<>(), model.getIngredientBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListIngredientCommand(), model, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {

        showPersonAtIndex(model, INDEXED_FIRST);
        assertCommandSuccess(new ListIngredientCommand(), model, expectedModel);
    }
}
