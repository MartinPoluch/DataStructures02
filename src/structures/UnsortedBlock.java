package structures;

import apk.BitConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UnsortedBlock<K extends Comparable<K>, V extends Record<K, V>> implements WritableAndReadable {

    private List<Record<K, V>> records;
    private final int capacity;
    private Record<K, V> defaultRecord;

    private int address; //toto sa neserializuje

    /**
     * Vytvorenie noveho prazdneho bloku.
     */
    public UnsortedBlock(int capacity, Record<K, V> defaultRecord) {
        this.address = Block.NULL_ADDRESS;
        this.defaultRecord = defaultRecord;
        this.records = new ArrayList<>(); // moze byt aj linked list
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public int numberOfRecords() {
        return records.size();
    }

    public int getAddress() {
        return address;
    }

    public List<Record<K, V>> getRecords() {
        return records;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public byte[] getByteArray() {
        // adresa rodica(4) + data potomkov + adresy potomkov
        byte[] output = new byte[getSize()];
        int position = 0;

        assert records.size() <= capacity : "You cannot have more records then block capacity";
        for (Record record : records) {
            if (record.isValid()) {
                position = BitConverter.addBytes(record.getByteArray(), output, position);
            }
        }
        return output;
    }

    @Override
    public int getSize() {
        //(data potomka * pocet potomkov)
        return defaultRecord.getSize() * capacity;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void fromByteArray(byte[] input) {
        // adresa rodica(4) + data potomkov + adresy potomkov
        assert input.length == getSize() : "Input has invalid length!";

        int position = 0;

        this.records.clear();
        for (int i = 0; i < capacity; i++) {
            Record<K, V> record = defaultRecord.newInstance();
            boolean validRecord = (input[position] == 1); // prvy bit recordu urcuje jeho validitu
            if (validRecord) {
                record.fromByteArray(Arrays.copyOfRange(input, position, position + record.getSize()));
                records.add(record);
            }
            position += record.getSize();
        }
    }


    public void addRecord(Record<K, V> record) {
        records.add(record);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnsortedBlock)) return false;
        UnsortedBlock<K, V> block = (UnsortedBlock<K, V>) o;
        return capacity == block.capacity &&
                Objects.equals(records, block.records);
    }

    @Override
    public String toString() {
        StringBuilder recordsOutput = new StringBuilder();
        for (Record<K, V> record : records) {
            recordsOutput.append("\n");
            recordsOutput.append(record);
        }
        return "\nBlock address(" + address + "):" +
                "\nrecords: " + recordsOutput.toString();
    }
}
