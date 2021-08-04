package structures;

import apk.BitConverter;

import java.util.List;

public class HeapFile<K extends Comparable<K>, V extends Record<K, V>> {

    private FileHandler heap;
    private FileHandler helper;
    private int capacityOfBlock;
    private Record<K, V> defaultRecord;
    private int addressOfLastBlock;

    public HeapFile(String pathToHeap, String pathToHelper, int capacityOfBlock, Record<K, V> defaultRecord) {
        this.helper = new FileHandler(pathToHelper);
        this.capacityOfBlock = capacityOfBlock;
        this.heap = new FileHandler(pathToHeap);
        this.defaultRecord = defaultRecord;
        initForFirstTime();
    }

    public HeapFile(String pathToHeap, String pathToHelper, Record<K, V> defaultRecord) {
        this.heap = new FileHandler(pathToHeap);
        this.helper = new FileHandler(pathToHelper);
        this.defaultRecord = defaultRecord;
        initFromHelperFile();
    }

    private void initFromHelperFile() {
        //Struktura pomocneho suboru:
        // + 4 bajty => adresa prveho bloku
        // + bajty => kapacita bloku
        this.addressOfLastBlock = BitConverter.fromByteArrayToInt(helper.read(0, Integer.BYTES));
        assert addressOfLastBlock >= 0 : "Invalid address of last block";
        // kapacita v bloku v pomocnom subore sa moze lisit od kapacity zadanej v konstruktore, v takomto pripade pouzivam kapacitu ktoru som nacital zo suboru
        this.capacityOfBlock = BitConverter.fromByteArrayToInt(helper.read(Integer.BYTES, Integer.BYTES));
    }

    private void initForFirstTime() {
        this.addressOfLastBlock = 0;
        helper.write(0, BitConverter.toByteArray(addressOfLastBlock));
        helper.write(Integer.BYTES, BitConverter.toByteArray(capacityOfBlock));
        Block<K, V> root = new Block<>(Block.NULL_ADDRESS, capacityOfBlock, defaultRecord);
        heap.write(0, root.getByteArray());
    }

    private UnsortedBlock<K, V> allocateNewBlock() {
        UnsortedBlock<K, V> block = new UnsortedBlock<>(capacityOfBlock, defaultRecord);
        block.setAddress(addressOfLastBlock + block.getSize());
        addressOfLastBlock = block.getAddress();
        return block;
    }

    public int insert(V record) {
        UnsortedBlock<K, V> block = new UnsortedBlock<>(capacityOfBlock, defaultRecord);
        byte[] blockBytes = heap.read(addressOfLastBlock, block.getSize());
        block.fromByteArray(blockBytes);
        block.setAddress(addressOfLastBlock);

        if (block.numberOfRecords() >= block.getCapacity()) {
            block = allocateNewBlock();
        }

        int recordAddress = block.getAddress() + (block.numberOfRecords() * record.getSize());
        block.addRecord(record);
        heap.write(block.getAddress(), block.getByteArray());
        return recordAddress;
    }

    public void edit(int position, V editedRecord) {
        heap.write(position, editedRecord.getByteArray());
    }

    public V get(int address) {
        byte[] recordBytes = heap.read(address, defaultRecord.getSize());
        Record<K, V> record = defaultRecord.newInstance();
        record.fromByteArray(recordBytes);
        return record.getInstance();
    }

    public void readAllRecords(List<V> records) {
        UnsortedBlock<K, V> defaultBlock = new UnsortedBlock<K, V>(capacityOfBlock, defaultRecord);
        for (int address = 0; address < addressOfLastBlock + defaultBlock.getSize(); address += defaultBlock.getSize()) {
            UnsortedBlock<K, V> actual = new UnsortedBlock<>(defaultBlock.getCapacity(), defaultRecord);
            actual.fromByteArray(heap.read(address, defaultBlock.getSize()));
            actual.setAddress(address);
            for (Record<K, V> record : actual.getRecords()) {
                records.add(record.getInstance());
            }
        }
    }

    public void dataSequenceOutput(List<String> blocks) {
        UnsortedBlock<K, V> defaultBlock = new UnsortedBlock<K, V>(capacityOfBlock, defaultRecord);
        for (int address = 0; address < addressOfLastBlock + defaultBlock.getSize(); address += defaultBlock.getSize()) {
            UnsortedBlock<K, V> actual = new UnsortedBlock<>(defaultBlock.getCapacity(), defaultRecord);
            actual.fromByteArray(heap.read(address, defaultBlock.getSize()));
            actual.setAddress(address);
            blocks.add(actual.toString());
        }
    }

    public String helperFileOutput() {
        StringBuilder builder = new StringBuilder();
        builder.append("Address of last block: ");
        builder.append(BitConverter.fromByteArrayToInt(helper.read(0, Integer.BYTES)));
        builder.append("\nCapacity of block: ");
        builder.append(BitConverter.fromByteArrayToInt(helper.read(Integer.BYTES, Integer.BYTES)));
        return builder.toString();
    }

    public void saveHelperFile() {
        helper.write(0, BitConverter.toByteArray(addressOfLastBlock));
        helper.write(Integer.BYTES, BitConverter.toByteArray(capacityOfBlock));
    }

    public void clear() {
        heap.clear();
        helper.clear();
        initForFirstTime();
    }
}
