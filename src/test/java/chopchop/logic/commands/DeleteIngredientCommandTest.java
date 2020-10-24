package chopchop.logic.commands;

import chopchop.model.EntryBook;
import chopchop.model.Model;
import chopchop.model.ModelManager;
import chopchop.model.UserPrefs;
import chopchop.logic.parser.ItemReference;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static chopchop.logic.commands.CommandTestUtil.assertCommandFailure;
import static chopchop.logic.commands.CommandTestUtil.assertCommandSuccess;
import static chopchop.logic.commands.CommandTestUtil.showPersonAtIndex;
import static chopchop.testutil.TypicalReferences.INDEXED_FIRST;
import static chopchop.testutil.TypicalReferences.INDEXED_SECOND;
import static chopchop.testutil.TypicalIngredients.getTypicalIngredientBook;

public class DeleteIngredientCommandTest {

    private Model model = new ModelManager(new EntryBook<>(), getTypicalIngredientBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        var indToDelete = model.getFilteredIngredientList().get(INDEXED_FIRST.getZeroIndex());
        var deleteCommand = new DeleteIngredientCommand(INDEXED_FIRST);

        var expectedModel = new ModelManager(new EntryBook<>(), model.getIngredientBook(), new UserPrefs());
        expectedModel.deleteIngredient(indToDelete);

        assertCommandSuccess(deleteCommand, model, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        var outOfBoundIndex = ItemReference.ofOneIndex(model.getFilteredIngredientList().size() + 1);
        var deleteCommand = new DeleteIngredientCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEXED_FIRST);

        var indToDelete = model.getFilteredIngredientList().get(INDEXED_FIRST.getZeroIndex());
        var deleteCommand = new DeleteIngredientCommand(INDEXED_FIRST);

        var expectedModel = new ModelManager(new EntryBook<>(), model.getIngredientBook(), new UserPrefs());
        expectedModel.deleteIngredient(indToDelete);
        showNoIngredient(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEXED_FIRST);

        var outOfBoundIndex = INDEXED_SECOND;
        // ensures that outOfBoundIndex is still in bounds of ingredient book list
        assertTrue(outOfBoundIndex.getZeroIndex() < model.getIngredientBook().getEntryList().size());

        var deleteCommand = new DeleteIngredientCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model);
    }

    @Test
    public void equals() {
        var deleteFirstCommand = new DeleteIngredientCommand(INDEXED_FIRST);
        var deleteSecondCommand = new DeleteIngredientCommand(INDEXED_SECOND);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        var deleteFirstCommandCopy = new DeleteIngredientCommand(INDEXED_FIRST);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoIngredient(Model model) {
        model.updateFilteredIngredientList(p -> false);

        assertTrue(model.getFilteredIngredientList().isEmpty());
    }
}
