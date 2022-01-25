package test.warehouse.StorageAreaTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.exceptions.StorageAreaException;
import src.warehouse.item.Package;
import src.warehouse.item.PackageDimensions;
import src.warehouse.storageArea.GeneralArea;
import src.warehouse.storageArea.StorageArea;

import static org.junit.jupiter.api.Assertions.*;

class GeneralAreaTest {

    private GeneralArea testArea;
    private int testCapacity = 2;

    /**
     * Generates a package with only an ID as input
     * @param id the id of the package
     * @return the Package generated
     */
    private Package testPackage(int id){
        PackageDimensions dim = new PackageDimensions(1,1,1);
        return new Package(id, "ID..", "description...",id+0.1, id+0.2, dim);
    }

    @BeforeEach
    void setUp() {
        testArea = new GeneralArea(null, testCapacity);
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Tests if depositing works as intended
     */
    @Test
    void deposit() throws StorageAreaException {
        Package p1 = testPackage(1);
        Package p2 = testPackage(2);

        //Deposit into Empty
        testArea.deposit(p1);
        assertEquals(1, testArea.getStock());

        //Try to deposit invalid
        assertThrows(StorageAreaException.class, () -> {
            testArea.deposit(p2);
        });

        //Deposit another valid one
        testArea.deposit(p1);
        assertEquals(2, testArea.getStock());

        //Deposit over max
        assertThrows(StorageAreaException.class, () -> {
            testArea.deposit(p1);
        });
    }

    /**
     * Tests removing an item works as intended
     */
    @Test
    void take() throws StorageAreaException {
        //test on empty Area
        assertThrows(StorageAreaException.class, () ->{
           testArea.take();
        });

        //test on a partially filled Area, that still has some stock left after removal
        Package p1 = testPackage(1);
        testArea.deposit(p1);
        testArea.deposit(p1);
        testArea.take();
        assertTrue(testArea.getStock() >= 1);

        //test on a partially filled Area, that should be empty afterwards
        testArea.take();
        assertEquals(0, testArea.getStock());
        assertTrue(testArea.removable());

    }

    /**
     * Thest if the postRemoveCheck behaves correctly
     * @throws StorageAreaException
     */
    @Test
    void postRemoveCheck() throws StorageAreaException {
        Package p1 = testPackage(1);

        //test on a GeneralArea with NORMAL state that still holds an item (just call method)
        testArea.deposit(p1);
        testArea.postRemoveCheck();
        assertEquals(StorageArea.AreaState.NORMAL, testArea.getState());

        //test on a GeneralArea with NORMAL state that has been emptied by take method
        testArea.take();
        assertEquals(StorageArea.AreaState.EMPTY, testArea.getState());

    }

    /**
     * Just here to se what toString actually does
     */
    @Test
    void testToString() throws StorageAreaException {
        GeneralArea testArea2 = new GeneralArea(100);
        Package p1 = testPackage(1);
        for(int i = 0; i < 10; i++){
            testArea2.deposit(p1);
        }
        System.out.println(testArea2.toString());
    }
}