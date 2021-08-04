package GUI.carForm;

import apk.Car;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class CarFormController {

    @FXML private TextField plateNumberInput;
    @FXML private TextField vinInput;
    @FXML private TextField axlesInput;
    @FXML private TextField weighInput;
    @FXML private DatePicker endOfSTKInput;
    @FXML private DatePicker endOfEKInput;
    @FXML private CheckBox wantedInput;
    @FXML private Button okBtn;

    private boolean confirmed;
    private Car car;

    public void initialize() {
        confirmed = false;
    }

    @FXML
    public void fillForm(Car car) {
        okBtn.setText("Edit car");
        plateNumberInput.setText(car.getPlateNumber());
        plateNumberInput.setDisable(true);
        vinInput.setText(car.getVin());
        vinInput.setDisable(true);
        axlesInput.setText(Integer.toString(car.getAxle()));
        weighInput.setText(Integer.toString(car.getWeight()));
        endOfSTKInput.setValue(car.getEndOfSTK());
        endOfEKInput.setValue(car.getEndOfEK());
        wantedInput.setSelected(car.isWanted());
    }

    public void changeButtonName(String name) {
        okBtn.setText(name);
    }

    @FXML
    public void save() {
        try {
            LocalDate endOfSTK =  endOfSTKInput.getValue();
            if (endOfSTK == null) {
                throw new IllegalArgumentException("You need to specified end of STK.") ;
            }

            LocalDate endOfEK =  endOfEKInput.getValue();
            if (endOfEK== null) {
                throw new IllegalArgumentException("You need to specified end of EK.") ;
            }

            car = new Car(
                    plateNumberInput.getText(),
                    vinInput.getText(),
                    Integer.parseInt(weighInput.getText()),
                    Integer.parseInt(axlesInput.getText()),
                    wantedInput.isSelected(),
                    endOfSTK,
                    endOfEK
            );
            confirmed = true;
            closeWindow(); // sem sa dostanem len ak nenastane vynimka
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot save new car");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Car getCar() {
        return car;
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) plateNumberInput.getScene().getWindow();
        stage.close();
    }
}
