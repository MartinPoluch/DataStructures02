package structures;

import apk.BitConverter;
import test.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 *
 */
public class BTree<K extends Comparable<K>, V extends Record<K, V>> {

    private int capacityOfBlock;
    private Record<K, V> defaultRecord;
    private int rootAddress;
    private int addressToEndOfFile;
    private FileHandler primaryFile;
    private FileHandler helperFile;

    private Stack<Block<K, V>> stack;
    private Block<K, V> blockForEdit;

    /**
     * Konstruktor pre vytovrenie instancie ktora bude pracovat uz s existujucimi naplnenymi subormi.
     * Nie je potrebne zadavat kapacitu bloku pretoze ta sa nachadza v pomocnom subore.
     */
    public BTree(String btreeFile, String pathToHelperFile, Record<K, V> defaultRecord) {
        this.helperFile = new FileHandler(pathToHelperFile); // nasledujuca metoda vyuziva helperFile
        this.initFromHelperFile();
        this.initCommonAttributes(btreeFile, defaultRecord);
    }

    /**
     * Konstruktor pre vytovrenie instancie ktora bude pracovat s prazdnymi subormi.
     * Zmaze vsetky existujuce data.
     * Je potrebne zadat kapacitu.
     */
    public BTree(String pathToPrimaryFile, String pathToHelperFile,  int blockCapacity, Record<K, V> defaultRecord) {
        this.helperFile = new FileHandler(pathToHelperFile);
        this.capacityOfBlock = blockCapacity;
        this.initCommonAttributes(pathToPrimaryFile, defaultRecord);
        this.primaryFile.clear(); // zmaze obsah suboru
        this.helperFile.clear();
        this.initForFirstTime();
    }

    private void initCommonAttributes(String btreeFile, Record<K, V> defaultRecord) {
        this.primaryFile = new FileHandler(btreeFile);
        this.stack = new Stack<>();
        this.defaultRecord = defaultRecord;
    }

    private void initFromHelperFile() {
        //Struktura pomocneho suboru:
        // + 4 bajty => koniec suboru
        // + 4 bajty => adresa korena
        // + bajty => kapacita bloku
        this.addressToEndOfFile = BitConverter.fromByteArrayToInt(helperFile.read(0, Integer.BYTES));
        assert addressToEndOfFile > 0 : "Invalid address to end of file";
        this.rootAddress =  BitConverter.fromByteArrayToInt(helperFile.read(Integer.BYTES, Integer.BYTES));
        assert rootAddress > 0 :  "Invalid root address";
        // kapacita v bloku v pomocnom subore sa moze lisit od kapacity zadanej v konstruktore, v takomto pripade pouzivam kapacitu ktoru som nacital zo suboru
        this.capacityOfBlock = BitConverter.fromByteArrayToInt(helperFile.read(2 * Integer.BYTES, Integer.BYTES));
        assert rootAddress > 0 : "Capacity of block must be bigger than 0";
    }

    private void initForFirstTime() {
        Block<K, V> root = new Block<>(Block.NULL_ADDRESS, capacityOfBlock, defaultRecord);
        this.addressToEndOfFile = root.getSize();
        primaryFile.write(0, root.getByteArray());
        helperFile.write(0, BitConverter.toByteArray(addressToEndOfFile));
        helperFile.write(Integer.BYTES, new byte[Integer.BYTES]); // adresa roota je 0
        helperFile.write(2 * Integer.BYTES, BitConverter.toByteArray(capacityOfBlock));
        this.rootAddress = 0;
    }


    public V find(K key) {
        Block<K, V> block = new Block<>(capacityOfBlock, defaultRecord);
        return find(key, block, false);
    }

    /**
     * Zisti ci v strome existuje zaznam s danym klucom, a ak je kluc unikatny tak do stacku si ulozi vsetky bloky ktore boli prehladane.
     * @return ak vrati true znamena to ze zaznam s danym klucom v strome existuje
     */
    public boolean findAndRememberBlocks(K key) {
        stack.clear();
        Block<K, V> block = new Block<>(capacityOfBlock, defaultRecord);
        boolean recordExists = find(key, block, true) != null;
        if (recordExists) {
            stack.clear(); // v pripade ze record s danym klucom uz existuje tak nema zmysel si pamatat bloky, record nebude mozne v buducnosti vlozit
        }
        return recordExists;
    }

    /**
     * @param key kluc podla ktoreho hladam
     * @param actualBlock blok v ktorom sa skoncilo hladanie, do tohto bloku dany record patri
     * @param insertToStack ak true tak pocas prehladavania bude vkladat vsetky bloky cez ktore prejde do stacku
     * @return hladany record, ak vrati null tak record s danym klucom neexistuje
     */
    private V find(K key, Block<K, V> actualBlock, boolean insertToStack) {
        int address = rootAddress; // adresa na ktorej by mohol byt hladany prvok
        while (true) {
            byte[] blockBytes = primaryFile.read(address, actualBlock.getSize()); // nacitavame data ktore reprezentuju blok
            actualBlock.fromByteArray(blockBytes);
            actualBlock.setAddress(address);

            if (insertToStack) {
                Block<K, V> copyOfActual = new Block<>(capacityOfBlock, defaultRecord);
                copyOfActual.fromByteArray(blockBytes);
                copyOfActual.setAddress(address);
                stack.push(copyOfActual);
            }

            V record = actualBlock.findRecord(key);
            if (record != null) {
                return record; // nasiel sa hladany prvok
            }
            else {
                address = actualBlock.nextAddress(key);
                if (address == Block.NULL_ADDRESS) {
                    return null; // aktualny blok je list a neobsahuje hladany prvok
                }
            }
        }
    }

    /**
     * Vlozi zaznam do stromu. Metoda predpoklada ze record ma unikatny kluc (duplicita bola skontrolovana pomocou inej metody).
     * V stacku musia byt vopred ulozene vsetky bloky ktore boli prehladane pri kontrole duplicity.
     */
    public void insertWithRememberedBlocks(V value) {
        assert ! stack.isEmpty() : "Stack cannot be empty. Make sure that helper method was called.";
        Block<K, V> actualBlock = stack.pop(); // posledny najdeny blok, do tohto bloku patri zaznam
        insertToBlock(actualBlock, value); // kontrola duplicity bola skontrolovana inou metodou
    }

    public boolean insert(V value) {
        K key = value.getKey();
        stack.clear();
        Block<K, V> actualBlock = new Block<>(capacityOfBlock, defaultRecord);
        V existingValue = find(key, actualBlock, true);
        if (existingValue != null) {
            return false; // chyba duplicity, kluc sa uz v strome nachadza
        }

        if (! stack.isEmpty()) {
            stack.pop(); // odstrani aktualny blok (posledny)
        }
        else {
            assert false : "Stack should never be empty"; // vzdy by tam mal byt minimalne koren
        }

        insertToBlock(actualBlock, value);

        if (Test.DEBUG) {
            System.out.println("\n------------------------------------------------------------\n");
            System.out.println(this.showStructure());
        }
        return true;
    }

       private void insertToBlock(Block<K, V> actualBlock, Record<K, V> record) {
        actualBlock.addRecord(record);
        while (actualBlock.numberOfRecords() > actualBlock.getCapacity()) {
            Block<K, V> parent;
            if (stack.isEmpty()) {
                parent = allocateNewBlock(); // aktualny block je koren, treba zvysit vysku stromu o 1
                rootAddress = parent.getAddress(); // parent bude novy root
            }
            else {
                parent = stack.pop(); // ziskam si zo zasobnika rodica
            }
            Block<K, V> second = allocateNewBlock();
            Record<K, V> median = actualBlock.split(second);

            parent.addRecord(median); //najskor treba pridat record az potom mozem pridavat adresy, pretoze pozicia adresy sa urcuje podla pridaneho recordu
            parent.addSonAddress(actualBlock);
            parent.addSonAddress(second);

            primaryFile.write(actualBlock);
            primaryFile.write(second);

            actualBlock = parent;
        }
        primaryFile.write(actualBlock);
        stack.clear();
    }

    /**
     * D atributu si ulozi referenciu na block v ktorom sa dany record nachadza.
     * @param key kluc hladaneho recordu
     * @return vrati hladany record
     */
    public V findRecordForEdit(K key) {
        blockForEdit = new Block<>(capacityOfBlock, defaultRecord);
        return find(key, blockForEdit, false);
    }

    /**
     * Pred volanym tejto metody je potrebne najskor zavolat metodu findRecordForEdit() ktora nam spristupni record a
     * do atributu ulozi referenciu na blok v ktorom sa editovany reocrd nachadza.
     */
    public boolean edit(V editedRecord) {
        boolean canEdit = (blockForEdit != null);
        if (canEdit) {
            blockForEdit.replaceRecord(editedRecord);
            primaryFile.write(blockForEdit);
            blockForEdit = null;
        }
        return canEdit;
    }

    public boolean update(V editedRecord) {
        Block<K, V> editedBlock = new Block<>(capacityOfBlock, defaultRecord);
        Record<K, V> oldRecord = find(editedRecord.getKey(), editedBlock, false);
        if (oldRecord != null) {
            editedBlock.replaceRecord(editedRecord);
            primaryFile.write(editedBlock);
            return true;
        }
        else {
            return false;
        }
    }

    private Block<K, V> allocateNewBlock() {
        Block<K, V> block = new Block<>(Block.NULL_ADDRESS, capacityOfBlock, defaultRecord);
        block.setAddress(addressToEndOfFile);
        addressToEndOfFile += block.getSize();
        return block;
    }

    /**
     * Treba ulozit referenciu na koren, dlzku suboru a velkost bloku.
     */
    public void saveHelperFile() {
        helperFile.write(0, BitConverter.toByteArray(addressToEndOfFile));
        helperFile.write(Integer.BYTES, BitConverter.toByteArray(rootAddress));
        helperFile.write(2 * Integer.BYTES, BitConverter.toByteArray(capacityOfBlock));
    }

    public void clear() {
        primaryFile.clear();
        helperFile.clear();
        initForFirstTime();

    }

    public String showStructure() {
        StringBuilder builder = new StringBuilder();
        LinkedList<Integer> queue = new LinkedList<>();
        int address = rootAddress;
        queue.addFirst(address);
        Block<K, V> actual = new Block<>(capacityOfBlock, defaultRecord);
        while (! queue.isEmpty()) {
            address = queue.pop();
            byte[] blockBytes = primaryFile.read(address, actual.getSize());
            actual.fromByteArray(blockBytes);
            actual.setAddress(address);
            builder.append(actual.toString());
            builder.append("\n");

            for (int sonAddress : actual.getSonsAddresses()) {
                if (sonAddress != Block.NULL_ADDRESS) {
                    queue.addLast(sonAddress);
                }
            }

        }
        return builder.toString();
    }

    public void dataSequenceOutput(List<String> blocks) {
        Block<K, V> defaultBlock = new Block<>(Block.NULL_ADDRESS, capacityOfBlock, defaultRecord);
        for (int address = 0; address < addressToEndOfFile; address += defaultBlock.getSize()) {
            Block<K, V> actual = new Block<>(Block.NULL_ADDRESS, defaultBlock.getCapacity(), defaultRecord);
            actual.fromByteArray(primaryFile.read(address, defaultBlock.getSize()));
            actual.setAddress(address);
            blocks.add(actual.toString());
        }
    }

    public String helperFileOutput() {
        StringBuilder builder = new StringBuilder();
        builder.append("Length of file: ");
        builder.append(BitConverter.fromByteArrayToInt(helperFile.read(0, Integer.BYTES)));
        builder.append("\nAddress of root: ");
        builder.append(BitConverter.fromByteArrayToInt(helperFile.read(Integer.BYTES, Integer.BYTES)));
        builder.append("\nCapacity of block: ");
        builder.append(BitConverter.fromByteArrayToInt(helperFile.read(2 * Integer.BYTES, Integer.BYTES)));
        return builder.toString();
    }

    public void levelOrder(List<V> output) {
        LinkedList<Integer> queue = new LinkedList<>();
        int address = rootAddress;
        queue.addFirst(address);
        Block<K, V> actual = new Block<>(capacityOfBlock, defaultRecord);
        while (! queue.isEmpty()) {
            address = queue.pop();
            byte[] blockBytes = primaryFile.read(address, actual.getSize());
            actual.fromByteArray(blockBytes);
            actual.setAddress(address);

            for (Record<K, V> record : actual.getRecords()) {
                output.add(record.getInstance());
            }

            for (int sonAddress : actual.getSonsAddresses()) {
                if (sonAddress != Block.NULL_ADDRESS) {
                    queue.addLast(sonAddress);
                }
            }
        }
    }
}
