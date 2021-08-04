package apk;

import structures.BTree;
import structures.HeapFile;

import java.util.ArrayList;
import java.util.List;

public class CarsDatabase extends Database<String, Car> {

    private HeapFile<String, Car> cars;
    private BTree<String, Address> ecvs;
    private BTree<String, Address> vins;
    private Address addressOfEdited;

    public CarsDatabase(Settings settings) {
        this.cars = new HeapFile<>("carsData.bin", "carsHelper.bin", settings.getHeapFileCapacity(), new Car());
        this.ecvs = new BTree<>("ecvsData.bin", "ecvsHelper.bin", settings.getCarByECVCapacity(), new Address());
        this.vins = new BTree<>("vinsData.bin", "vinsHelper.bin", settings.getCarByVinCapacity(), new Address());
        this.addressOfEdited = null;
    }

    public CarsDatabase() {
        this.cars = new HeapFile<>("carsData.bin", "carsHelper.bin", new Car());
        this.ecvs = new BTree<>("ecvsData.bin", "ecvsHelper.bin", new Address());
        this.vins = new BTree<>("vinsData.bin", "vinsHelper.bin", new Address());
    }

    @Override
    public boolean add(Car car) {
        if (ecvs.findAndRememberBlocks(car.getPlateNumber())) { // zistim ci ECV je unikatne
            throw new IllegalArgumentException("Car with same ECV already exits");
        }

        if (vins.findAndRememberBlocks(car.getVin())) { // zistim ci VIN je unikatne
            throw new IllegalArgumentException("Car with same VIN already exits");
        }

        int addressToHeap = cars.insert(car); // ECV aj VIN su unikatne, mozem zaznam vlozit do heapu
        // prislusne bloky sa pri kontrole duplicity ulozili do stacku
        ecvs.insertWithRememberedBlocks(new Address(car.getPlateNumber(), addressToHeap));
        vins.insertWithRememberedBlocks(new Address(car.getVin(), addressToHeap));
        return true;
    }

    @Override
    public boolean edit(Car car) {
        if (addressOfEdited == null) {
            return false;
        }
        cars.edit(addressOfEdited.getAddress(), car);
        addressOfEdited = null;
        return true;
    }

    @Override
    public Car find(String key, boolean edit) {
        return findByECV(key, edit);
    }

    @Override
    public boolean update(Car newCar) {
        String ecv = newCar.getPlateNumber();
        String vin = newCar.getVin();
        Address address = null;
        if (ecv != null && !ecv.equals("")) {
            address = ecvs.find(ecv);
        }
        if ((address == null) && (vin != null) && !vin.equals("")) { // nepodarilo sa najist podla ecv alebo ecv nebolo zadane
            address = vins.find(vin);
        }

        if (address == null) {
            return false;
        }
        else {
            Car oldCar = cars.get(address.getAddress());
            newCar.setPlateNumber(oldCar.getPlateNumber());
            newCar.setVin(oldCar.getVin());
            cars.edit(address.getAddress(), newCar);
            return true;
        }
    }

    private Car find(Address address, boolean edit) {
        if (address != null) {
            addressOfEdited = (edit) ? address : null;
            return cars.get(address.getAddress());
        }
        else {
            return null;
        }
    }

    public Car findByECV(String ecv, boolean edit) {
        return find(ecvs.find(ecv), edit);
    }

    public Car findByVIN(String vin, boolean edit) {
        return find(vins.find(vin), edit);
    }

    @Override
    public void show(List<Car> carsList) {
        cars.readAllRecords(carsList);
    }

    @Override
    public void generate(int numOfData) {
        DataGenerator generator = new DataGenerator();
        int generated = 0;
        while (generated < numOfData) {
            Car car = generator.randomCar();
            try {
                if (add(car)) {
                    generated++;
                }
            }
            catch (Exception e) {
                System.out.println("Generated duplicate car: " + e.getMessage());
            }
        }
    }

    @Override
    public void allDataSequenceOutput(List<String> blocks) {
        cars.dataSequenceOutput(blocks);
    }

    public void vinsSequenceOutput(List<String> blocks) {
        vins.dataSequenceOutput(blocks);
    }

    public void ecvsSequenceOutput(List<String> blocks) {
        ecvs.dataSequenceOutput(blocks);
    }

    @Override
    public List<String> helperData() {
        List<String> output = new ArrayList<>();
        output.add("\nHeap file of cars:\n" + cars.helperFileOutput());
        output.add("\nCars address by ECV:\n" + ecvs.helperFileOutput());
        output.add("\nCars address by VIN:\n" + vins.helperFileOutput());
        return  output;
    }

    @Override
    public void save() {
        cars.saveHelperFile();
        ecvs.saveHelperFile();
        vins.saveHelperFile();
    }

    @Override
    public void clear() {
        cars.clear();
        ecvs.clear();
        vins.clear();
    }
}
