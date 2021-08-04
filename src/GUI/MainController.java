package GUI;

import GUI.TextOutput.TextOutputController;
import GUI.carForm.CarFormController;
import GUI.carsTable.CarsTableController;
import GUI.findCarForm.FindCarController;
import GUI.licenceForm.LicenceFormController;
import GUI.licenceTable.LicenceTableController;
import apk.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainController {

    private LicencesDatabase licencesDatabase;
    private CarsDatabase carsDatabase;

    public void initialize() {
        Main.getPrimaryStage().setOnHiding( e -> {
            saveAllHelperFiles();
            System.out.println("All data were saved");
        });
    }

    public void initDatabase(Settings settings, boolean useExistingDatabase) {
        if (useExistingDatabase) {
            licencesDatabase = new LicencesDatabase(settings);
            carsDatabase = new CarsDatabase(settings);
        }
        else {
            licencesDatabase = new LicencesDatabase();
            carsDatabase = new CarsDatabase();
        }
    }

    @FXML
    @SuppressWarnings("Duplicates")
    private void addLicence() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("licenceForm/LicenceFormView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add new licence");
            stage.setScene(new Scene(root, 400, 450));
            LicenceFormController form = loader.getController();
            stage.showAndWait();

            if (form.isConfirmed()) {
                Licence newLicence = form.getLicence();
                if (newLicence == null) {
                    throw new IllegalArgumentException("Invalid parameters.");
                }
                else if (licencesDatabase.add(newLicence)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("License was successfully added to database.");
                    alert.setContentText(newLicence.toString());
                    alert.showAndWait();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Licence with ID: " + newLicence.getId() + " already exists");
                    alert.setContentText("Cannot add licence with same ID");
                    alert.showAndWait();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Main.showDefaultErrorMessage(e);
        }
    }

    @FXML
    @SuppressWarnings("Duplicates")
    public void editLicence() {
        try {
            Licence licence = findLicence(true);
            if (licence == null) {
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("licenceForm/LicenceFormView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit licence");
            stage.setScene(new Scene(root, 400, 450));
            LicenceFormController form = loader.getController();
            form.fillForm(licence);
            stage.showAndWait();

            if (form.isConfirmed()) {
                Licence editedLicence = form.getLicence();
                if (editedLicence == null) {
                    throw new IllegalArgumentException("Invalid parameters.");
                }
                else if (licencesDatabase.edit(editedLicence)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("New license was successfully edited.");
                    alert.setContentText(editedLicence.toString());
                    alert.showAndWait();
                }
                else {
                    throw new Exception("Car wasn't edited.");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Main.showDefaultErrorMessage(e);
        }
    }

    @FXML
    @SuppressWarnings("Duplicates")
    public void updateLicence() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("licenceForm/LicenceFormView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Update licence");
            stage.setScene(new Scene(root));
            LicenceFormController form = loader.getController();
            form.changeButtonName("Update licence");
            stage.showAndWait();

            if (form.isConfirmed()) {
                Licence updated = form.getLicence();
                if (updated == null) {
                    throw new IllegalArgumentException("Invalid parameters.");
                }
                else if (licencesDatabase.update(updated)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("License was successfully updated.");
                    alert.setContentText(updated.toString());
                    alert.showAndWait();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Licence with ID: " + updated.getId() + " doesn't exist");
                    alert.setContentText("Cannot update licence.");
                    alert.showAndWait();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Main.showDefaultErrorMessage(e);
        }
    }

    @FXML
    @SuppressWarnings("Duplicates")
    private void showAllLicences() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("licenceTable/LicenceTableView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("All licences");
            stage.setScene(new Scene(root));
            LicenceTableController table = loader.getController();
            table.show(licencesDatabase);
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Main.showDefaultErrorMessage(e);
        }
    }

    /**
     * @return vrati licenciu ktoru vyhlada podla zadaneho kluca
     * Ak nasstane chyba vrati null a zobrazi okno s chybovou hlaskou.
     */
    @SuppressWarnings("Duplicates")
    private Licence findLicence(boolean edit) {
        Licence licence = null;
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Find licence");
            dialog.setHeaderText("Find licence");
            dialog.setContentText("Enter licence ID");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                int id = Integer.parseInt(result.get());
                licence = licencesDatabase.find(id, edit);
                if (licence == null) {
                    throw new IllegalArgumentException("There is no licence with ID: " + id);
                }
            }
        }
        catch (NumberFormatException nfe) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Licence not found");
            alert.setContentText("Licence ID must be integer");
            alert.showAndWait();
            nfe.printStackTrace();
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Licence not found");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
        return licence;
    }

    @FXML
    @SuppressWarnings("Duplicates")
    public void showLicence() {
        try {
            Licence licence = findLicence(false);
            if (licence == null) {
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("licenceTable/LicenceTableView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Found licence");
            stage.setScene(new Scene(root));
            LicenceTableController table = loader.getController();
            table.show(licence);
            stage.show();
        }
        catch (Exception e) {
            Main.showDefaultErrorMessage(e);
        }
    }

    @FXML
    public void addCar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("carForm/CarFormView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add new car");
            stage.setScene(new Scene(root));
            CarFormController form = loader.getController();
            stage.showAndWait();

            if (form.isConfirmed()) {
                Car newCar = form.getCar();
                if (newCar == null) {
                    throw new IllegalArgumentException("Invalid parameters.");
                }
                else if (carsDatabase.add(newCar)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("New car was successfully added to database.");
                    alert.setContentText(newCar.toString());
                    alert.showAndWait();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Car already exists");
                    alert.setContentText("Cannot add car with same VIN or ECV");
                    alert.showAndWait();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Main.showDefaultErrorMessage(e);
        }
    }

    /**
     * @return vrati auto ktore vyhlada podla zadanych klucov.
     * Ak nastane chyba vrati null a zobrazi okno s chybovou hlaskou.
     */
    @SuppressWarnings("Duplicates")
    private Car findCar(boolean edit) {
        Car car;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("findCarForm/FindCarView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Find car");
            stage.setScene(new Scene(root));
            FindCarController form = loader.getController();
            stage.showAndWait();
            if (form.isConfirmed()) {
                String ecv = form.getECV();
                if (ecv != null && ! ecv.equals("")) {
                    car = carsDatabase.findByECV(ecv, edit);
                    if (car != null) {
                        return car;
                    }
                    else {
                        throw new IllegalArgumentException("There is no car with ecv: " + ecv);
                    }

                }

                String vin = form.getVIN();
                if (vin != null && ! vin.equals("")) {
                    car = carsDatabase.findByVIN(vin, edit);
                    if (car != null) {
                        return car;
                    }
                    else {
                        throw new IllegalArgumentException("There is no car with vin: " + vin);
                    }
                }

                throw new IllegalArgumentException("Invalid values");
            }
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Car not found");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    public void editCar() {
        try {
            Car car = findCar(true);
            if (car == null) {
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("carForm/CarFormView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Edit car");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 400, 450));
            CarFormController form = loader.getController();
            form.fillForm(car);
            stage.showAndWait();

            if (form.isConfirmed()) {
                Car editedCar = form.getCar();
                if (editedCar == null) {
                    throw new IllegalArgumentException("Invalid parameters.");
                }
                else if (carsDatabase.edit(editedCar)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Car was successfully edited.");
                    alert.setContentText(editedCar.toString());
                    alert.showAndWait();
                }
                else {
                    throw new Exception("Licence wasn't edited.");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Main.showDefaultErrorMessage(e);
        }
    }

    @FXML
    @SuppressWarnings("Duplicates")
    public void updateCar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("carForm/CarFormView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Update car");
            stage.setScene(new Scene(root));
            CarFormController form = loader.getController();
            form.changeButtonName("Update car");
            stage.showAndWait();

            if (form.isConfirmed()) {
                Car newCar = form.getCar();
                if (newCar == null) {
                    throw new IllegalArgumentException("Invalid parameters.");
                }
                else if (carsDatabase.update(newCar)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Car was successfully updated.");
                    alert.setContentText(newCar.toString());
                    alert.showAndWait();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Car with this ECV or VIN doesn't exist");
                    alert.setContentText("Cannot update car.");
                    alert.showAndWait();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Main.showDefaultErrorMessage(e);
        }
    }



    @FXML
    @SuppressWarnings("Duplicates")
    public void showCar() {
        try {
            Car car = findCar(false);
            if (car == null) {
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("carsTable/CarsTableView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Found car");
            stage.setScene(new Scene(root));
            CarsTableController table = loader.getController();
            table.show(car);
            stage.show();
        }
        catch (Exception e) {
            Main.showDefaultErrorMessage(e);
        }
    }


    @FXML
    @SuppressWarnings("Duplicates")
    public void showAllCars() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("carsTable/CarsTableView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("All cars");
            stage.setScene(new Scene(root));
            CarsTableController table = loader.getController();
            table.show(carsDatabase);
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Main.showDefaultErrorMessage(e);
        }
    }

    private TextOutputController createTextOutput(String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TextOutput/TextOutputView.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
        return loader.getController();
    }

    @FXML
    public void showLicencesSequence() {
        try {
            TextOutputController outputController = createTextOutput("Licence blocks");
            outputController.show(licencesDatabase);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTextOutput(CarsDatabase database, String title, String structure) {
        try {
            TextOutputController outputController = createTextOutput(title);
            outputController.show(database, structure);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showHeapFileSequence() {
        showTextOutput(carsDatabase, "Heap file of cars", TextOutputController.CARS);
    }

    @FXML
    public void showVinsSequence() {
        showTextOutput(carsDatabase, "B-Tree of VINs", TextOutputController.VINs);
    }

    @FXML
    public void showPlatesSequence() {
        showTextOutput(carsDatabase, "B-Tree of ECVs", TextOutputController.ECVs);
    }

    @FXML
    public void showHelperFiles() {
        try {
            TextOutputController outputController = createTextOutput("Helper files");
            List<String> output = new ArrayList<>();
            output.addAll(licencesDatabase.helperData());
            output.addAll(carsDatabase.helperData());
            outputController.show(output);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    @SuppressWarnings("Duplicates")
    public void generateLicences() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Licences generator");
            dialog.setHeaderText("How many licences you wish generate?");
            dialog.setContentText("Enter number of licences");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                int numOfLicences = Integer.parseInt(result.get());
                licencesDatabase.generate(numOfLicences);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Success");
                alert.setHeaderText(numOfLicences + " licences was successfully generated.");
                alert.showAndWait();
            }
        }
        catch (Exception e) {
            Main.showDefaultErrorMessage(e);
        }
    }

    @FXML
    @SuppressWarnings("Duplicates")
    public void generateCars() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Cars generator");
            dialog.setHeaderText("How many cars you wish generate?");
            dialog.setContentText("Enter number of cars");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                int numOfCars = Integer.parseInt(result.get());
                carsDatabase.generate(numOfCars);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Success");
                alert.setHeaderText(numOfCars + " cars was successfully generated.");
                alert.showAndWait();
            }
        }
        catch (Exception e) {
            Main.showDefaultErrorMessage(e);
        }
    }

    @FXML
    public void saveAllHelperFiles() {
        licencesDatabase.save();
        carsDatabase.save();
    }

    @FXML
    public void deleteAllLicences() {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure that you want delete all licences?",
                ButtonType.YES,
                ButtonType.NO
        );
        alert.setHeaderText("Warning");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            licencesDatabase.clear();
        }
    }

    @FXML
    public void deleteAllCars() {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure that you want delete all cars?",
                ButtonType.YES,
                ButtonType.NO
        );
        alert.setHeaderText("Warning");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            carsDatabase.clear();
        }
    }


}
