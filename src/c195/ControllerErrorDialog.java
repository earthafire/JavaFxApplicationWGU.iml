package c195;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Error dialog logic
 * @author Addison Ashworth
 */
public class ControllerErrorDialog {

    @FXML
    private Text text;

    @FXML
    private Text boldTitle;

    /**
     * closes window
     */
    public void OKButtonPressed() {
        Stage stage = (Stage) text.getScene().getWindow();
        stage.close();
    }

    /**
     * changes text in dialog
     * @param text String to display
     */
    public void setErrorMessage(String text) {
        this.text.setText(text);
    }

    /**
     * changes bold text in dialog
     * @param text String to display
     */
    public void setBoldTitle(String text) {
        this.boldTitle.setText(text);
    }

}
