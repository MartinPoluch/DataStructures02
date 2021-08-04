package apk;

import javafx.scene.control.DatePicker;
import structures.BTree;
import structures.Record;

import java.util.ArrayList;
import java.util.List;

public class LicencesDatabase extends Database<Integer, Licence> {

    private BTree<Integer, Licence> licences;

    public LicencesDatabase(Settings settings) {
        this.licences = new BTree<>("licencesData.bin", "licencesHelper.bin", settings.getLicenceCapacity(), new Licence());
    }

    public LicencesDatabase() {
        this.licences = new BTree<>("licencesData.bin", "licencesHelper.bin", new Licence());
    }

    @Override
    public boolean add(Licence licence) {
        return licences.insert(licence);
    }

    @Override
    public boolean edit(Licence licence) {
        return licences.edit(licence);
    }

    @Override
    public boolean update(Licence licence) {
        return licences.update(licence);
    }

    @Override
    public Licence find(Integer key, boolean edit) {
        return (edit) ? licences.findRecordForEdit(key) : licences.find(key);
    }

    @Override
    public void generate(int numOfData) {
        DataGenerator generator = new DataGenerator();
        int generated = 0;
        while (generated < numOfData) {
            Licence licence = generator.randomLicence();
            if (add(licence)) {
                generated++;
            }
            else {
                System.out.println("Generated licence with duplicate ID");
            }
        }
    }

    @Override
    public void show(List<Licence> records) {
        licences.levelOrder(records);
    }

    @Override
    public void allDataSequenceOutput(List<String> blocks) {
        licences.dataSequenceOutput(blocks);
    }

    @Override
    public List<String> helperData() {
        List<String> output = new ArrayList<>();
        output.add("Licences: \n" + licences.helperFileOutput());
        return output;
    }

    @Override
    public void save() {
        licences.saveHelperFile();
    }

    @Override
    public void clear() {
        licences.clear();
    }
}
