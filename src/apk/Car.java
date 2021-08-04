package apk;

import structures.Record;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

public class Car extends Record<String, Car> {

    private String plateNumber;
    private String vin;
    private int weight;
    private int axle;
    private boolean wanted;
    private LocalDate endOfSTK;
    private LocalDate endOfEK;

    public static final int MAX_PLATE_NUMBER_SIZE = 7;
    public static final int MAX_VIN_SIZE = 17;

    public Car(String plateNumber, String vin, int weight, int axle, boolean wanted, LocalDate endOfSTK, LocalDate endOfEK) {
        super();
        this.setPlateNumber(plateNumber);
        this.setVin(vin);
        this.weight = weight;
        this.axle = axle;
        this.wanted = wanted;
        this.endOfSTK = endOfSTK;
        this.endOfEK = endOfEK;
    }

    public Car() {
        super();
    }


    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        if (plateNumber.length() <= MAX_PLATE_NUMBER_SIZE) {
            this.plateNumber = plateNumber;
        }
        else {
            throw new IllegalArgumentException("Plate number has illegal length." +
                    "\nactual: " + plateNumber.length() +
                    "\nmax: " + MAX_PLATE_NUMBER_SIZE);
        }
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        if (vin.length() <= MAX_VIN_SIZE) {
            this.vin = vin;
        }
        else {
            throw new IllegalArgumentException("VIN code has illegal length.\nactual: " + vin.length() + "\nmax: " + MAX_VIN_SIZE);
        }
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getAxle() {
        return axle;
    }

    public void setAxle(int axle) {
        this.axle = axle;
    }

    public boolean isWanted() {
        return wanted;
    }

    public void setWanted(boolean wanted) {
        this.wanted = wanted;
    }

    public LocalDate getEndOfSTK() {
        return endOfSTK;
    }

    public void setEndOfSTK(LocalDate endOfSTK) {
        this.endOfSTK = endOfSTK;
    }

    public LocalDate getEndOfEK() {
        return endOfEK;
    }

    public void setEndOfEK(LocalDate endOfEK) {
        this.endOfEK = endOfEK;
    }

    @Override
    public Record<String, Car> newInstance() {
        return new Car();
    }

    @Override
    public Car getInstance() {
        return this;
    }

    /**
     * Vsetky atributy serializuje na sekvenciu bajtov.
     */
    @Override
    @SuppressWarnings("Duplicates")
    public byte[] getByteArray() {
        // valid  + plateNumber + vin + weight + axle + endOfSTK + endOfEK + wanted
        int position = 0;
        byte[] output = new byte[getSize()];
        output[position++] = BitConverter.toByte(isValid()); // prvy bit urcuje ci je dany zaznam validny

        //output[position++] = (byte) plateNumber.length(); // bit ktory urcuje dlzku stringu
        byte[] plateNumberBytes = new byte[MAX_PLATE_NUMBER_SIZE]; // inicializujeme pole o maximalnej dlzke kvoli paddingu
        System.arraycopy(BitConverter.toByteArray(plateNumber),0, plateNumberBytes, 0, plateNumber.length());
        position = BitConverter.addBytes(plateNumberBytes, output, position);

        //output[position++] = (byte) vin.length(); // bit ktory urcuje dlzku stringu
        byte[] vinBytes = new byte[MAX_VIN_SIZE];
        System.arraycopy(BitConverter.toByteArray(vin), 0, vinBytes, 0, vin.length());
        position = BitConverter.addBytes(vinBytes, output, position);

        position = BitConverter.addBytes(BitConverter.toByteArray(weight), output, position);
        position = BitConverter.addBytes(BitConverter.toByteArray(axle), output, position);
        position = BitConverter.addBytes(BitConverter.toByteArray(endOfSTK), output, position);
        position = BitConverter.addBytes(BitConverter.toByteArray(endOfEK), output, position);
        output[position] = BitConverter.toByte(wanted);
        return output;
    }

    @Override
    public int getSize() {
        // valid  + 1 + plateNumber + 1 + vin + weight + axle + endOfSTK + endOfEK + wanted
        return 1 + 1 + MAX_PLATE_NUMBER_SIZE + 1 + MAX_VIN_SIZE + Integer.BYTES + Integer.BYTES + BitConverter.DATE_BYTE_SIZE + BitConverter.DATE_BYTE_SIZE  + 1;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void fromByteArray(byte[] input) {
        // valid  + plateNumber + vin + weight + axle + endOfSTK + endOfEK + wanted
        int position = 0; // smernik na bajt ktory sa ma aktualne precitat
        boolean isValid = BitConverter.fromBytesToBool(input[position++]);
        setValid(isValid);
        //int sizeOfPlateNumber = input[position++];
        plateNumber = BitConverter.fromByteArrayToString(Arrays.copyOfRange(input, position, position + MAX_PLATE_NUMBER_SIZE)).trim();
        position += MAX_PLATE_NUMBER_SIZE;
        //int sizeOfVin = input[position++];
        vin = BitConverter.fromByteArrayToString(Arrays.copyOfRange(input, position, position + MAX_VIN_SIZE)).trim();
        position += MAX_VIN_SIZE;
        weight = BitConverter.fromByteArrayToInt(Arrays.copyOfRange(input, position, position + Integer.BYTES));
        position += Integer.BYTES;
        axle = BitConverter.fromByteArrayToInt(Arrays.copyOfRange(input, position, position + Integer.BYTES));
        position += Integer.BYTES;
        endOfSTK = BitConverter.fromByteArrayToDate(Arrays.copyOfRange(input, position, position + BitConverter.DATE_BYTE_SIZE));
        position += BitConverter.DATE_BYTE_SIZE;
        endOfEK = BitConverter.fromByteArrayToDate(Arrays.copyOfRange(input, position, position + BitConverter.DATE_BYTE_SIZE));
        position += BitConverter.DATE_BYTE_SIZE;
        wanted = BitConverter.fromBytesToBool(input[position]);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        Car car = (Car) o;
        return getWeight() == car.getWeight() &&
                getAxle() == car.getAxle() &&
                isWanted() == car.isWanted() &&
                Objects.equals(getPlateNumber(), car.getPlateNumber()) &&
                Objects.equals(getVin(), car.getVin()) &&
                Objects.equals(getEndOfSTK(), car.getEndOfSTK()) &&
                Objects.equals(getEndOfEK(), car.getEndOfEK());
    }

    @Override
    protected String getKey() {
        return plateNumber;
    }

    @Override
    public String toString() {
        return  "ECV: " + plateNumber +
                "  VIN: " + vin +
                "  Weight: " + weight +
                "  Axle: " + axle +
                "  Wanted: " + wanted +
                "  End of STK: " + endOfSTK +
                "  End of EK: " + endOfEK;
    }
}
