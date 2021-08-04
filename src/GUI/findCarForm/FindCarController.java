package GUI.findCarForm;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FindCarController {

    @FXML private TextField ecvInput;
    @FXML private TextField vinInput;

    private boolean confirmed;

    public void initialize() {
        confirmed = false;
    }

    public void confirm() {
        confirmed = true;
        close();
    }

    public void close() {
        Stage stage = (Stage) ecvInput.getScene().getWindow();
        stage.close();
    }

    public String getECV() {
        return ecvInput.getText();
    }

    public String getVIN() {
        return vinInput.getText();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
