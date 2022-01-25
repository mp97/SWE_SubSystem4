package test.warehouse.StorageAreaTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.exceptions.StorageAreaException;
import src.warehouse.item.Item;
import src.warehouse.storageArea.GeneralArea;
import src.warehouse.storageArea.StorageArea;

import static org.junit.jupiter.api.Assertions.*;

class StorageAreaTest {

    private GeneralArea testArea;
    private Item testItem;
    private final int testCapacity = 15;

    @BeforeEach
    void setUp() {
        testArea = new GeneralArea(testCapacity);
        testItem = new Item(1, "Test item", "...", 10.1, 2.4);
    }

    /**
     * Test if the constructor 1 behaves as intended
     */
    @Test
    void constructor1(){
        GeneralArea g1 = new GeneralArea(testCapacity);
        assertEquals(StorageArea.AreaState.EMPTY, g1.getState());
        assertEquals(testCapacity, g1.getCapacity());
    }

    /**
     * Test if the constructor 2 behaves as intended
     */
    @Test
    void constructor2(){
        GeneralArea g1 = new GeneralArea(testItem, testCapacity);
        assertEquals(StorageArea.AreaState.EMPTY, g1.getState());
        assertEquals(testCapacity, g1.getCapacity());
        assertEquals(testItem.getIID(), g1.getDesignated().getIID());

    }

    /**
     * Test if setting to FREEZE State works as intended
     */
    @Test
    void setToFreeze() throws StorageAreaException {
        assertFalse(testArea.setToFreeze());
        testArea.deposit(testItem);
        assertTrue(testArea.setToFreeze());
    }

    /**
     * Test if setting to FLUSH State works as intended
     */
    @Test
    void setToFlush() throws StorageAreaException {
        assertFalse(testArea.setToFlush());
        testArea.deposit(testItem);
        assertTrue(testArea.setToFlush());
    }

    /**
     * Test if setting the state back NORMAL works as intended
     */
    @Test
    void setToNormal() throws StorageAreaException {
        assertFalse(testArea.setToNormal());
        testArea.deposit(testItem);
        assertTrue(testArea.setToNormal());
    }
}