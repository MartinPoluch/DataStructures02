package apk;

import structures.Record;

import java.util.Arrays;
import java.util.Objects;

public class Address extends Record<String, Address> {

    private String key;
    private int address;

    public Address(String key, int address) {
        super();
        if (key.length() > Car.MAX_VIN_SIZE) {
            throw new IllegalArgumentException("Key of address is too long!");
        }
        this.key = key;
        this.address = address;
    }

    public Address() {
        super();
    }

    /**
     * metoda getKey() je protected
     */
    public String getPublicKey() {
        return key;
    }

    public int getAddress() {
        return address;
    }

    @Override
    public Record<String, Address> newInstance() {
        return new Address();
    }

    @Override
    public byte[] getByteArray() {
        byte[] output = new byte[getSize()];
        output[0] = BitConverter.toByte(isValid());
        BitConverter.addBytes( BitConverter.toByteArray(address), output, 1);
        BitConverter.addBytes(BitConverter.toByteArray(key), output, 1 + Integer.BYTES);
        return output;
    }

    @Override
    public int getSize() {
        return 1 + Integer.BYTES + Car.MAX_VIN_SIZE;
    }

    @Override
    public void fromByteArray(byte[] bytes) {
        boolean valid = BitConverter.fromBytesToBool(bytes[0]);
        setValid(valid);
        address = BitConverter.fromByteArrayToInt(Arrays.copyOfRange(bytes, 1, 1 + Integer.BYTES));
        key = BitConverter.fromByteArrayToString(Arrays.copyOfRange(bytes, 1 + Integer.BYTES, getSize())).trim();
    }

    @Override
    protected String getKey() {
        return key;
    }

    @Override
    public Address getInstance() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address1 = (Address) o;
        return getAddress() == address1.getAddress() &&
                Objects.equals(getKey(), address1.getKey());
    }

    @Override
    public String toString() {
        return "(key: " + key + ", address: " + address + ")";
    }
}
