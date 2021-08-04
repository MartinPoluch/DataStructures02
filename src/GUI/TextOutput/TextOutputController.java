package GUI.TextOutput;

import apk.CarsDatabase;
import apk.Database;
import apk.LicencesDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class TextOutputController {

    public static final String CARS = "cars";
    public static final String ECVs = "ECVs";
    public static final String VINs = "VINs";

    @FXML private ListView<String> listView;

    public void show(LicencesDatabase licences) {
        ObservableList<String> list = FXCollections.observableArrayList();
        licences.allDataSequenceOutput(list);
        listView.setItems(list);
    }

    public void show(CarsDatabase carsDatabase, String structure) {
        ObservableList<String> list = FXCollections.observableArrayList();
        switch (structure) {
            case CARS:
            {
                carsDatabase.allDataSequenceOutput(list);
                break;
            }
            case ECVs:
            {
                carsDatabase.ecvsSequenceOutput(list);
                break;
            }
            case VINs:
            {
                carsDatabase.vinsSequenceOutput(list);
                break;
            }
        }
        listView.setItems(list);
    }

    public void show(List<String> items) {
        ObservableList<String> list = FXCollections.observableArrayList(items);
        listView.setItems(list);
    }
}
