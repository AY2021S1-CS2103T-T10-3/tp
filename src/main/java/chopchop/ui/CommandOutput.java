package chopchop.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandOutput extends UiPart<Region> {

    private static final String FXML = "CommandOutput.fxml";

    private static final Font boldFont = Font.font(Font.getDefault().getFamily(),
        FontWeight.BOLD, Font.getDefault().getSize());

    @FXML
    private TextFlow displayBox;

    /**
     * Constructs {@code CommandBox}
     */
    public CommandOutput() {
        super(FXML);
    }

    /**
     * Displays the commandResult to the user.
     *
     * @param feedbackToUser
     */
    public void setFeedbackToUser(String message, boolean isError) {
        requireNonNull(message);

        var texts = displayBox.getChildren();
        texts.clear();

        if (isError) {
            var foo = new Text("Error: ");
            foo.setStyle("-fx-font-weight: bolder");
            foo.setFill(Color.RED);

            texts.add(foo);

            // a little dirty, but... whatever.
            if (message.length() > 0) {
                message = message.substring(0, 1).toLowerCase() + message.substring(1);
            }
        }

        texts.add(new Text(message));
    }
}
