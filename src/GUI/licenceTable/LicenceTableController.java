package GUI.licenceTable;

import apk.Licence;
import apk.LicencesDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class LicenceTableController {

    @FXML private TableView<Licence> licenceTableView;
    @FXML private TableColumn<Licence, Integer> idCol;
    @FXML private TableColumn<Licence, String> firstNameCol;
    @FXML private TableColumn<Licence, String> lastNameCol;
    @FXML private TableColumn<Licence, LocalDate> endCol;
    @FXML private TableColumn<Licence, Boolean> banCol;
    @FXML private TableColumn<Licence, Integer> finesCol;

    private ObservableList<Licence> licences;

    public void initialize() {
        this.licences = FXCollections.observableArrayList();
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        banCol.setCellValueFactory(new PropertyValueFactory<>("ban"));
        finesCol.setCellValueFactory(new PropertyValueFactory<>("fines"));
    }

    public void show(LicencesDatabase database) {
        database.show(licences);
        licenceTableView.setItems(licences);
    }

    public void show(Licence licence) {
        licences.add(licence);
        licenceTableView.setItems(licences);
    }
}
