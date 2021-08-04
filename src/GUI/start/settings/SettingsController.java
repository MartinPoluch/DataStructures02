package GUI.start.settings;

import apk.Settings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class SettingsController {

    @FXML private TextField licencesInput;
    @FXML private TextField carsHeapFileInput;
    @FXML private TextField carsByVINInput;
    @FXML private TextField carsByECVInput;

    private Settings settings;

    public void setSettings(Settings settings) {
        this.settings = settings;
        licencesInput.setText(Integer.toString(settings.getLicenceCapacity()));
        carsHeapFileInput.setText(Integer.toString(settings.getHeapFileCapacity()));
        carsByVINInput.setText(Integer.toString(settings.getCarByVinCapacity()));
        carsByECVInput.setText(Integer.toString(settings.getCarByECVCapacity()));
    }

    /**
     * Pokusi sa prepisat aktualne nastavnia novymi zadanymi hodnotami vo formulary.
     * Ak nastane chyba tak ponecha povodne nastavenia a vypise chybovu hlasku.
     * Zatvori okno
     */
    @FXML
    public void save() {
        try {
            settings.setLicenceCapacity(checkInput(licencesInput));
            settings.setHeapFileCapacity(checkInput(carsHeapFileInput));
            settings.setCarByVinCapacity(checkInput(carsByVINInput));
            settings.setCarByECVCapacity(checkInput(carsByECVInput));
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Wrong input");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }

        Stage stage = (Stage) licencesInput.getScene().getWindow();
        stage.close();
    }

    private int checkInput(TextField input) {
        int value = Integer.parseInt(input.getText());
        final int MINIMUM_SIZE = 1;
        if (value < MINIMUM_SIZE) {
            throw new IllegalArgumentException("Block capacity must be bigger that " + MINIMUM_SIZE);
        }
        else {
            return value;
        }
    }






}
