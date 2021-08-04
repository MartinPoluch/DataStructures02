package apk;

public class Settings {

    private int licenceCapacity;
    private int heapFileCapacity;
    private int carByVinCapacity;
    private int carByECVCapacity;

    public Settings(int licenceCapacity, int heapFileCapacity, int carByVinCapacity, int carByECVCapacity) {
        this.licenceCapacity = licenceCapacity;
        this.heapFileCapacity = heapFileCapacity;
        this.carByVinCapacity = carByVinCapacity;
        this.carByECVCapacity = carByECVCapacity;
    }

    public void setLicenceCapacity(int licenceCapacity) {
        this.licenceCapacity = licenceCapacity;
    }

    public void setHeapFileCapacity(int heapFileCapacity) {
        this.heapFileCapacity = heapFileCapacity;
    }

    public void setCarByVinCapacity(int carByVinCapacity) {
        this.carByVinCapacity = carByVinCapacity;
    }

    public void setCarByECVCapacity(int carByECVCapacity) {
        this.carByECVCapacity = carByECVCapacity;
    }

    public int getLicenceCapacity() {
        return licenceCapacity;
    }

    public int getHeapFileCapacity() {
        return heapFileCapacity;
    }

    public int getCarByVinCapacity() {
        return carByVinCapacity;
    }

    public int getCarByECVCapacity() {
        return carByECVCapacity;
    }
}
