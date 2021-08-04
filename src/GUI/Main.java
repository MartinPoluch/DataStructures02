package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.*;

/**
 * author: Martin Poluch
 */
public class Main extends Application {

    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("start/StartView.fxml"));
        primaryStage.setTitle("Application");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void showDefaultErrorMessage(Exception error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(error.getMessage());
        alert.showAndWait();
    }

    public static Stage getPrimaryStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
