package GUI.licenceForm;

import apk.Licence;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class LicenceFormController {

    @FXML private TextField firstNameInput;
    @FXML private TextField lastNameInput;
    @FXML private TextField idInput;
    @FXML private DatePicker dateInput;
    @FXML private TextField numberOfFinesInput;
    @FXML private CheckBox banInput;
    @FXML private Button okBtn;

    private Licence licence;
    private boolean confirmed;

    public void initialize() {
        confirmed = false;
    }

    /**
     * Naplni formular podla atributov licencie ktoru dostane ako parameter.
     * @param existing licencia ktora uz existuje v databaze
     */
    public void fillForm(Licence existing) {
        okBtn.setText("Edit licence");
        firstNameInput.setText(existing.getFirstName());
        lastNameInput.setText(existing.getLastName());
        idInput.setText(Integer.toString(existing.getId()));
        idInput.setDisable(true); // klucovy atribut nie je mozne editovat
        dateInput.setValue(existing.getEnd());
        numberOfFinesInput.setText(Integer.toString(existing.getFines()));
        banInput.setSelected(existing.isBan());
    }

    public void changeButtonName(String name) {
        okBtn.setText(name);
    }

    @FXML
    private void save() {
        try {
            LocalDate endOfLicence =  dateInput.getValue();
            if (endOfLicence == null) {
                throw new IllegalArgumentException("You need to specified date of licence end.") ;
            }
            licence = new Licence(
                    firstNameInput.getText(),
                    lastNameInput.getText(),
                    Integer.parseInt(idInput.getText()),
                    endOfLicence,
                    banInput.isSelected(),
                    Integer.parseInt(numberOfFinesInput.getText())
            );
            confirmed = true;
            closeWindow(); // sem sa dostanem len ak nenastane vynimka
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot save new licence");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Licence getLicence() {
        return licence;
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) firstNameInput.getScene().getWindow();
        stage.close();
    }

}
