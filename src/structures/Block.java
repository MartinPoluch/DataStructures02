package structures;

import apk.BitConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Block<K extends Comparable<K>, V extends Record<K, V>> implements WritableAndReadable, Comparable<Block<K, V>> {

    public static final int NULL_ADDRESS = -1;

    private List<Record<K, V>> records;
    private List<Integer> sonsAddresses;
    private final int capacity;
    private Record<K, V> defaultRecord;

    private int address; //toto sa neserializuje

    /**
     * Vytvorenie noveho prazdneho bloku.
     */
    public Block(int parentAddress, int capacity, Record<K, V> defaultRecord) {
        this.address = NULL_ADDRESS;
        this.defaultRecord = defaultRecord;
        this.records = new ArrayList<>(); // moze byt aj linked list
        this.capacity = capacity;
        this.initSonsAddress();
    }

    /**
     * Pouziva sa na vytvorenie instancie nad ktorou sa zavola metoda fromByteArray. Vyuzitie pri serializacii.
     */
    public Block(int capacity, Record<K, V> defaultRecord) {
        // adresa rodica +  adresa potomka * pocet potomkov + data potomka * pocet potomkov
        this.address = NULL_ADDRESS;
        this.capacity = capacity;
        this.defaultRecord = defaultRecord;
        this.records = new ArrayList<>();
        this.initSonsAddress();
    }

    private void initSonsAddress() {
        this.sonsAddresses = new ArrayList<>();
        for (int i = 0; i < capacity + 1; i++) {
            sonsAddresses.add(NULL_ADDRESS);
        }
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

    public List<Integer> getSonsAddresses() {
        return sonsAddresses;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public Record<K, V> getDefaultRecord() {
        return defaultRecord;
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
        position = defaultRecord.getSize() * capacity; // padding outputu

        assert sonsAddresses.size() <= capacity + 1 : "You cannot have more son address then block capacity + 1";
        for (int sonAddress : sonsAddresses) {
            position = BitConverter.addBytes(BitConverter.toByteArray(sonAddress), output, position);
        }

        return output;
    }

    @Override
    public int getSize() {
        //(data potomka * pocet potomkov) + (adresa potomka * pocet potomkov)
        return (defaultRecord.getSize() * capacity) + (Integer.BYTES * (capacity + 1));
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

        assert sonsAddresses.size() == capacity + 1 : "Too many son addresses";
        for (int i = 0; i < capacity + 1; i++) {
            int sonAddress = BitConverter.fromByteArrayToInt(Arrays.copyOfRange(input, position, position + Integer.BYTES));
            position += Integer.BYTES;
            sonsAddresses.set(i, sonAddress);
        }
    }

    /**
     * Vzdy prida novy record, bez ohladu ci sme prekrocili kapacitu daneho bloku.
     * Ak prekrocime kapacitu daneho bloku tak sa to bude riesit v B-strome
     * V specifickych pripadoch moze POCAS INSERTU blok obsahovat o jeden record viac ako je jeho kapacita (ale nie uz po vykonani insertu)
     * Moze docasne zvacsit pole smernikov na synov o 1.
     * @param record novy record
     */
    public void addRecord(Record<K, V> record) {
        if (records.size() == capacity) {
            sonsAddresses.add(NULL_ADDRESS); // docasne zvacsim pole smernikov na synov
        }

        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).compareTo(record) >= 1) {
                records.add(i, record); // pridam novy record na poziciu aktualneho recordu
                return;
            }
        }
        records.add(record); // pridam nakoniec, ak novy record ma najvacsiu hodnotu v bloku
    }

    public void replaceRecord(Record<K, V> record) {
       for (int i = 0; i < records.size(); i++) {
           if (records.get(i).getKey().equals(record.getKey())) {
               records.set(i, record);
           }
       }
    }

    /**
     * Vlozi adresu bloku vkladaneho ako parameter na prislusne miesto do pola smernikov na synov.
     */
    public void addSonAddress(Block<K, V> son) {
        assert ! son.records.isEmpty() : "Cannot add son address because son has no records!";
        assert ! this.records.isEmpty() : "Cannot add son address to block because block has no records!";

        for (int i = 0; i < records.size(); i++) {
            Record<K, V> record = records.get(i);
            Record<K, V> sonLowestRecord = son.records.get(0);
            if (record.compareTo(sonLowestRecord) >= 1) {
                if (sonsAddresses.get(i) != son.getAddress()) { // ak uz sa tu tato adresa nachadza tak ju znovu nepriradzam
                    sonsAddresses.add(i, son.getAddress()); // vlozi na dany index a posunie ostatne adresy o jednu poziciu
                    assert sonsAddresses.get(sonsAddresses.size() - 1) == NULL_ADDRESS : "Cannot remove last son address, because last address is not NULL ADDRESS!";
                    sonsAddresses.remove(sonsAddresses.size() - 1); // kapacita bloku musi ostat rovnaka, takze zmazene poslednu -1
                }
                return;
            }
        }
        sonsAddresses.set(records.size(), son.getAddress()); // syn je priradeny na posledny smernik
    }

    /**
     * Presunie polovicu recordov a smernikov na synov do druheho bloku.
     * V pripade ze list adries synov bol docasne zvacseny tak tu ho zmensim na povodnu hodnotu.
     * @param higherThenMedian prazdny novovytvoreny blok do ktoreho presuvam recordy a adresy na synov
     * @return median, v pripade dvoch medianov vraciam prvu hodnotu
     */
    public Record<K, V> split(Block<K, V> higherThenMedian) {
        assert higherThenMedian.records.isEmpty() :  "Block records should be empty (it just has been allocated)";
        assert !higherThenMedian.sonsAddresses.isEmpty() && higherThenMedian.sonsAddresses.get(0) == NULL_ADDRESS : "Block addresses should be empty (it just has been allocated)";

        int medianIndex = medianIndex();
        Record<K, V> median = records.get(medianIndex); // treba si ulozit median, pretoze ho budem z tohto bloku odstranovat
        for (int i = medianIndex + 1; i < records.size(); i++) {
            higherThenMedian.addRecord(records.get(i)); // urcite pojde pridat lebo, lebo novy blok je novo vytvoreny (bol prazdny)
            int newAddressIndex = i % (medianIndex + 1); // do dalsieho bloku treba pridavat od nuly (mapovanie indexu)
            higherThenMedian.sonsAddresses.set(newAddressIndex, sonsAddresses.get(i));
            sonsAddresses.set(i, NULL_ADDRESS);
        }

        int lastIndexAddress = higherThenMedian.records.size();
        int lastSonAddress = this.sonsAddresses.get(this.records.size());
        higherThenMedian.sonsAddresses.set(lastIndexAddress, lastSonAddress); // tato adresa nebola priradena vo for cykle, treba ju priradit explicitne
        sonsAddresses.set(this.records.size(), NULL_ADDRESS); // znulujem poslednu adresu ktoru som presunul do noveho bloku
        this.records = records.subList(0, medianIndex); // polovica recordov ostane v aktualnom bloku

        if (sonsAddresses.size() > capacity + 1) { // zistim ci kapacita poctu synov nebola docasne navrsena
            assert sonsAddresses.size() == capacity + 2 : "Wrong size of son addresses";
            assert sonsAddresses.get(capacity + 1) == NULL_ADDRESS : "Cannot delete address which is not null address";
            sonsAddresses.remove(capacity + 1); // znizim kapacitu na povodnu hodnotu
        }

        return median;
    }

    private int medianIndex() {
        int index = records.size() / 2;
        if (records.size() % 2 == 0) {
            index--;
        }
        return index;
    }

    public V findRecord(K key) {
        for (Record<K, V> record : records) {
            if (record.getKey().equals(key)) {
                return record.getInstance();
            }
        }
        return null;
    }

    /**
     * @param key hladany kluc
     * @return Vrati adresu bloku v ktorom by sa mohol nachadzat hladany kluc. Ak vrati -1 tak dany blok je list.
     */
    public int nextAddress(K key) {
        for (int i = 0; i < records.size(); i++) {
            Record<K, V> record = records.get(i);
            if (record.getKey().compareTo(key) >= 1) {
                return sonsAddresses.get(i); // kluc je mensi ako aktualny reccord
            }
        }
        return sonsAddresses.get(records.size()); // posledna adresa, kluc je vacsi ako ktory kolvek record
    }

    private boolean isLeaf() {
        return sonsAddresses.get(0) == NULL_ADDRESS;
    }

    @Override
    public int compareTo(Block<K, V> other) {
        Record<K, V> thisKey = records.get(0);
        Record<K, V> otherKey = other.records.get(0);
        return thisKey.compareTo(otherKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;
        Block<K, V> block = (Block<K, V>) o;
        return capacity == block.capacity &&
                //parentAddress == block.parentAddress  &&
                Objects.equals(records, block.records) &&
                Objects.equals(sonsAddresses, block.sonsAddresses);
    }

    @Override
    public String toString() {
        StringBuilder recordsOutput = new StringBuilder();
        for (Record<K, V> record : records) {
            recordsOutput.append("\n");
            recordsOutput.append(record);
        }
        return "\nBlock address(" + address + "):" +
                "\nrecords: " + recordsOutput.toString() +
                "\nsonsAddresses: " + sonsAddresses.toString();
    }
}
