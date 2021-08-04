package test;

import apk.*;
import org.junit.Assert;
import structures.BTree;
import structures.Block;
import structures.HeapFile;
import structures.UnsortedBlock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Triedy:
 * - ByteArrayOutputStream
 * - OutputStream
 *
 * Do suboru zapisujeme binarne data.
 * metoda seek
 * pri stringoch treba dat pozor ci je jedno bajtovy alebo dvojbajtovy
 */
public class Test {

    public static final boolean DEBUG = false;

    public static void main(String[] args) {
        HeapFile<String, Address> bTree = new HeapFile<>("heapFileNEW.bin", "helperHeapFileNEW.bin", new Address());
        try {
//        System.out.println(bTree.insert(new Address("A", 1)));
//        System.out.println(bTree.insert(new Address("D", 4)));
//        System.out.println(bTree.insert(new Address("B", 2)));
//        System.out.println(bTree.insert(new Address("C", 3)));
//        System.out.println(bTree.insert(new Address("F", 5)));
//        System.out.println(bTree.insert(new Address("M", 99)));
//        System.out.println(bTree.insert(new Address("L", 20)));
//        System.out.println(bTree.insert(new Address("E", 8)));
//        System.out.println(bTree.insert(new Address("Z", 99)));
//        System.out.println(bTree.insert(new Address("X", 8)));
//        System.out.println(bTree.insert(new Address("Y", 20)));
//        System.out.println(bTree.insert(new Address("H", 88)));
//        System.out.println(bTree.insert(new Address("N", 200)));
//        System.out.println(bTree.insert(new Address("k", 111)));
//        System.out.println(bTree.insert(new Address("K", 999)));
//        System.out.println(bTree.insert(new Address("a", -1)));
//        System.out.println(bTree.insert(new Address("b", -2)));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\nBTree was deleted");

        }

        bTree.saveHelperFile();
        List<String> blocks = new ArrayList<>();
        bTree.dataSequenceOutput(blocks);

        System.out.println("--------------------------------------------");

        System.out.println(blocks.toString());
        System.out.println("--------------------------------------------");

//        System.out.println(bTree.get("A"));
//        System.out.println(bTree.get("B"));
//        System.out.println(bTree.get("C"));
//        System.out.println(bTree.get("D"));
//        System.out.println(bTree.get("F"));
//        bTree.showStructure();

//        bTree.saveHelperFile();





//        Licence licence1 = new Licence("Martin", "Poluch", 9,LocalDate.of(2020, 11, 9), false, 4 );
//        Licence licence2 = new Licence("Martin2", "Poluch", 10,LocalDate.of(2021, 11, 5), false, 5 );
//        Licence licence3 = new Licence("Martin3", "Poluch", 11,LocalDate.of(2022, 11, 10), false, 6 );
//        Licence licence4 = new Licence("Martin4", "Poluch", 13,LocalDate.of(2023, 11, 9), false, 7 );
//        Licence licence5 = new Licence("Martin99", "Poluch", 99,LocalDate.of(2023, 11, 9), false, 7 );
//        Licence licence6 = new Licence("Martin55", "Poluch", 55,LocalDate.of(2023, 11, 9), false, 7 );
//        Licence licence7 = new Licence("Martin77", "Poluch", 77,LocalDate.of(2023, 11, 9), false, 7 );
//        Licence licence8 = new Licence("Martin66", "Poluch", 66,LocalDate.of(2023, 11, 9), false, 7 );
//
//        BTree<Integer, Licence> bTree = new BTree<>("bTreeFile.bin", "helperFile.bin", 4, new Licence());
//
//        System.out.println(bTree.insert(licence5));
//        System.out.println(bTree.insert(licence6));
//        System.out.println(bTree.insert(licence1));
//        System.out.println(bTree.insert(licence2));
//        System.out.println(bTree.insert(licence3));

//        System.out.println(bTree.insert(licence4));
//        System.out.println(bTree.insert(licence7));
//
//        System.out.println(bTree.get(licence1.getId()));
//        System.out.println(bTree.get(licence2.getId()));
//        System.out.println(bTree.get(licence3.getId()));
//        System.out.println(bTree.get(licence4.getId()));
//        System.out.println(bTree.get(licence5.getId()));
//        bTree.showStructure();
//        bTree.saveHelperFile();
//        bTree.clear();


//        Block<Integer, Licence> block = new Block<Integer, Licence>(0, 4, new Licence());
//        System.out.println(block.getSize());
//        Licence licence1 = new Licence("Martin", "Poluch", 9,LocalDate.of(2020, 11, 9), false, 4 );
//        Licence licence2 = new Licence("Martin2", "Poluch", 9,LocalDate.of(2021, 11, 9), false, 5 );
//        Licence licence3 = new Licence("Martin3", "Poluch", 9,LocalDate.of(2022, 11, 9), false, 6 );
//        Licence licence4 = new Licence("Martin4", "Poluch", 9,LocalDate.of(2023, 11, 9), false, 7 );
//        block.addRecord(licence1);
//        block.addRecord(licence2);
//        block.addRecord(licence3);
//        block.addRecord(licence4);
//        byte[] bytes = block.getByteArray();
//        Block<Integer, Licence> inBlock = new Block<>(4, new Licence());
//        inBlock.fromByteArray(bytes);
//        System.out.println(block.equals(inBlock));

//        Address address = new Address("12345678901234567", 2);
//        byte[] b = address.getByteArray();
//        Address inAddress = new Address();
//        inAddress.fromByteArray(b);
//        System.out.println(address.equals(inAddress));



//        Car car = new Car("PE726", "12345678901234567", 1000, 2, false,
//                LocalDate.of(2019, 12, 12), LocalDate.of(2020, 11, 9), CarKey.PLATE_NUMBER_KEY);
//        Car car1 = new Car("TO444oo", "123456789012345ab", 1999, 2, true,
//                LocalDate.of(2019, 11, 11), LocalDate.of(2022, 9, 9), CarKey.PLATE_NUMBER_KEY);
//
//        Car inCar = new Car();
//        byte[] bytes = car1.getByteArray();
//        inCar.fromByteArray(bytes);
//        System.out.println(car1.equals(inCar));
//
//        Licence licence = new Licence("Martin", "Poluch", 9,LocalDate.of(2020, 11, 9), false, 4 );
//        byte[] licenceBytes = licence.getByteArray();
//        Licence inLicence = new Licence();
//        inLicence.fromByteArray(licenceBytes);
//
//        System.out.println(licence.equals(inLicence));




    }
}
