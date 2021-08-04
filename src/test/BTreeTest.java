package test;

import apk.Address;
import apk.DataGenerator;
import org.junit.*;
import org.junit.Test;
import structures.BTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BTreeTest {

    private static final int NUMBER_OF_INSERTS = 10000;
    private static final int NUMBER_OF_FINDS = 5000;
    private static final int BLOCK_CAPACITY = 5;

    private static BTree<String, Address> bTree;
    private static List<Address> usedAddresses;

    @BeforeClass
    public static void setUp() throws Exception {
        bTree = new BTree<>("bTreeTest.bin", "helperTest.bin", BLOCK_CAPACITY, new Address());
        usedAddresses = new ArrayList<>(NUMBER_OF_INSERTS);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        bTree.clear();
        usedAddresses.clear();
    }

    @Test
    public void insert() {
        DataGenerator generator = new DataGenerator();
        for (int insert = 0; insert < NUMBER_OF_INSERTS; insert++) {
            Address address = generator.randomAddress(); // dostanem nahodnu instanciu s unikatnym klucom
            usedAddresses.add(address);
            Assert.assertTrue(bTree.insert(address));
            Address foundAddress = bTree.find(address.getPublicKey());
            Assert.assertEquals(address, foundAddress);
        }

        List<Address> addressesInBTree = new ArrayList<>(usedAddresses.size());
        bTree.levelOrder(addressesInBTree); // naplni List adresamy
        Assert.assertEquals(usedAddresses.size(), addressesInBTree.size());
        for (Address usedAddress : usedAddresses) {
            Assert.assertTrue(addressesInBTree.contains(usedAddress)); // ci sa kazda pouzita adresa naozaj nachadza v B-strome
        }
    }

    @Test
    public void find() {
        Random random = new Random();
        for (int find = 0; find < NUMBER_OF_FINDS; find++) {
            int randomIndex = random.nextInt(usedAddresses.size());
            Address address = usedAddresses.get(randomIndex);
            Address found = bTree.find(address.getPublicKey());
            Assert.assertEquals(address, found);
        }
    }

}