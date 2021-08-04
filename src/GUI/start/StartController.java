package GUI.start;

import GUI.Main;
import GUI.MainController;
import GUI.start.settings.SettingsController;
import apk.Settings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class StartController {

    private Settings settings;

    @FXML
    public void initialize() {
        this.settings = new Settings(5, 3, 10, 10);
    }

    @FXML
    public void createNewDatabase() {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "If you create new database all existing data will be lost. Are you sure that you want delete all data?",
                ButtonType.YES,
                ButtonType.NO
        );
        alert.setHeaderText("Warning");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            setUpDatabase(true);
        }
    }

    @FXML
    public void useExistingDatabase() {
        setUpDatabase(false);
    }

    public void setUpDatabase(boolean useExistingData) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/MainView.fxml"));
            Parent root = loader.load();
            Stage primaryStage = Main.getPrimaryStage();
            MainController controller = loader.getController();
            controller.initDatabase(settings, useExistingData);
            primaryStage.setScene(new Scene(root));
        }
        catch (Exception e) {
            Main.showDefaultErrorMessage(e);
            e.printStackTrace();
        }
    }

    @FXML
    public void settings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings/SettingsView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            SettingsController controller = loader.getController();
            controller.setSettings(settings); // vlozim referenciu na nastavenia, toto umoznuje editaciu nastaveni
            stage.showAndWait();
        }
        catch (Exception e) {
            Main.showDefaultErrorMessage(e);
            e.printStackTrace();
        }
    }
}
