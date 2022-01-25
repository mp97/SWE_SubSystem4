package test.warehouse.StorageAreaTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.exceptions.StorageAreaException;
import src.warehouse.item.Ingredient;
import src.warehouse.storageArea.IngredientArea;
import src.warehouse.storageArea.StorageArea;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IngredientAreaTest {

    private IngredientArea testArea;
    private final int testCapacity = 10;

    @BeforeEach
    void setUp() {
        testArea = new IngredientArea(testCapacity);
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Generates test Ingredient with only an ID
     * @param id the id of the test Ingredient
     * @param timeOffset specifies if the Ingredient has passed its expiration Date or not
     *           negative spoiled before today; 0 spoils today; positive spoils in the future
     * @return the Package generated
     */
    private Ingredient testIngredient(int id, int timeOffset){
        LocalDate expirationDate = LocalDate.now().plusDays(timeOffset);

        return new Ingredient(id,"ID..", "description...", id+0.1, id+0.2,
                                expirationDate, expirationDate.minusDays(7), "PL.......");
    }

    /**
     * Fills the testArea up to the specified value
     * @param fill the amount of items to be put into the area
     * @param timeOffset see testIngredient methode for explanation
     */
    private void fill(int fill, int id, int timeOffset) throws StorageAreaException {
        for(int i = 0; i < fill; i++){
            testArea.deposit(testIngredient(id,timeOffset));
        }
    }

    /**
     * Fills the testArea up to the specified value
     * @param fill the amount of items to be put into the area
     * @param timeOffset see testIngredient methode for explanation
     */
    private void fill(int fill, int timeOffset) throws StorageAreaException {
        fill(fill, 0, timeOffset);
    }

    /**
     * Test if the daily routine will delete all items that expire today or earlier
     * and keeps the rest
     * @throws StorageAreaException
     */
    @Test
    void dalyCheck() throws StorageAreaException {
        //fill Area
        int split = testCapacity/3;
        fill(split, -1);   //spoiled a few days ago: should be removed
        fill(split, 0);    //spoiled today: should be removed
        fill(split, 1);    //good: should be kept

        //run daily check
        testArea.dalyCheck();

        //check that there are "int split" left with future expiration Date
        assertEquals(split, testArea.getStock());
        for(int i = 0; i < split; i++){
            assertTrue(testArea.take().getExpiryDate().isAfter(LocalDate.now()));
        }
    }

    /**
     * Test if when depositing the item that expires first is in the first position
     */
    @Test
    void deposit() throws StorageAreaException {
        int split = testCapacity/2;

        //Deposit into Empty
        fill(split,1);
        assertEquals(split, testArea.getStock());

        //Try to deposit invalid
        assertThrows(StorageAreaException.class, ()->{
            fill(split,2,1);
        });
        assertEquals(split, testArea.getStock());

        //Deposit another valid ones
        fill(split,1);
        assertEquals(split*2, testArea.getStock());

        //Deposit over max
        //make testArea full
        int filling = testArea.getCapacity()-testArea.getStock();
        fill(filling,1);
        //try deposit into full testArea
        assertThrows(StorageAreaException.class, ()->{
            fill(split,1);
        });
    }

    /**
     * Test if when taking an item the first one gets taken
     */
    @Test
    void take() throws StorageAreaException {
        //test on empty Area
        assertThrows(StorageAreaException.class, ()->{
            testArea.take();
        });

        //test take on a partially filled Area until its empty
        int fill = testCapacity/2;
        //fill with future dates
        for(int i = 0; i < fill; i++){
            fill(1,i);
        }

        //empty step by step by step
        Ingredient lastRemoval = null;
        for(int i = fill; i > 0; i--){
            assertEquals(i, testArea.getStock());
            lastRemoval = testArea.take();
            assertEquals(i-1, testArea.getStock());

            //check if last removed expires before the next in line first
            if(testArea.getStock() >0 && lastRemoval != null){
                assertTrue(testArea.peakAtFirst().isAfter(lastRemoval.getExpiryDate()));
            }

        }
        //check if empty
        assertEquals(0, testArea.getStock());
        assertTrue(testArea.removable());
    }

    /**
     * Test if we can get the expiry date of the first item in the list
     * @throws StorageAreaException
     */
    @Test
    void peakAtFirst() throws StorageAreaException {
        //fill with current Dates
        for(int i = 0; i < testCapacity-1; i++){
            fill(1,0);
        }
        //add a past date
        int timeOffset = -1000;
        fill(1,timeOffset);

        //check past date is firsts in list with peakAtFirst()
        assertEquals(LocalDate.now().plusDays(timeOffset), testArea.peakAtFirst());


    }

    /**
     * Tests if the postRemoveCheck works as intended
     * @throws StorageAreaException
     */
    @Test
    void postRemoveCheck() throws StorageAreaException {
        int split = testCapacity;

        //test on a GeneralArea with NORMAL state that still holds an item (just call method)
        //fill area
        fill(split, 1);
        //almost empty Area and check that its normal
        for(int i = split; i > 1; i--){
            testArea.take();
            assertEquals(StorageArea.AreaState.NORMAL, testArea.getState());
        }

        //test on a GeneralArea with NORMAL state that has been emptied by take method
        testArea.take();
        //check that AreaState is empty
        assertEquals(StorageArea.AreaState.EMPTY, testArea.getState());
    }

    /**
     * Just here to se what toString actually does
     */
    @Test
    void testToString() throws StorageAreaException {
        fill(testCapacity,1);
        System.out.println(testArea);
    }
}