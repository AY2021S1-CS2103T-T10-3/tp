package chopchop.model.attributes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

public class StepTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Step(null));
    }

    @Test
    public void constructor_invalidStep_throwsIllegalArgumentException() {
        String invalidStep = "";
        assertThrows(IllegalArgumentException.class, () -> new Step(invalidStep));
    }

    @Test
    public void isValidStep() {
        // null Step
        assertThrows(NullPointerException.class, () -> Step.isValidStep(null));

        // invalid Step
        assertFalse(Step.isValidStep("")); // empty string
        assertFalse(Step.isValidStep(" ")); // spaces only
        assertFalse(Step.isValidStep("^")); // only non-alphanumeric characters

        // valid Step
        assertTrue(Step.isValidStep("recipe*")); // contains non-alphanumeric characters
        assertTrue(Step.isValidStep("rec recipe")); // alphabets only
        assertTrue(Step.isValidStep("12345")); // numbers only
        assertTrue(Step.isValidStep("recipe number seven")); // alphanumeric characters
        assertTrue(Step.isValidStep("The Most Unhealthy Recipe")); // with capital letters
        assertTrue(Step.isValidStep("First, place eggs in a large saucepan and cover them with cool water by 1 inch.")); // long Step
    }
}