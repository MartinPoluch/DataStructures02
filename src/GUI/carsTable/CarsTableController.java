package GUI.carsTable;

import apk.Car;
import apk.CarsDatabase;
import apk.Licence;
import apk.LicencesDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class CarsTableController {

    @FXML private TableView<Car> carsTableView;
    @FXML private TableColumn<Car, String> ecvCol;
    @FXML private TableColumn<Car, String> vinCol;
    @FXML private TableColumn<Car, Integer> axlesCol;
    @FXML private TableColumn<Car, Integer> weightCol;
    @FXML private TableColumn<Car, Boolean> wantedCol;
    @FXML private TableColumn<Car, LocalDate> endOfSTKCol;
    @FXML private TableColumn<Car, LocalDate> endOfEKCol;

    private ObservableList<Car> cars;

    public void initialize() {
        this.cars = FXCollections.observableArrayList();
        ecvCol.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        vinCol.setCellValueFactory(new PropertyValueFactory<>("vin"));
        axlesCol.setCellValueFactory(new PropertyValueFactory<>("axle"));
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weight"));
        wantedCol.setCellValueFactory(new PropertyValueFactory<>("wanted"));
        endOfSTKCol.setCellValueFactory(new PropertyValueFactory<>("endOfSTK"));
        endOfEKCol.setCellValueFactory(new PropertyValueFactory<>("endOfEK"));
    }

    public void show(CarsDatabase database) {
        database.show(cars);
        carsTableView.setItems(cars);
    }

    public void show(Car car) {
        cars.add(car);
        carsTableView.setItems(cars);
    }
}
