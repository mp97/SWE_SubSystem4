package test.DatabaseTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.database.Item_Table;
import src.warehouse.item.Ingredient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class Item_TableTest {

    private Item_Table<Ingredient> testTable;

    @BeforeEach
    void setUp() {
        testTable = new Item_Table<>();
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Generates test Ingredient with only an ID
     * @param id the id of the test Ingredient
     * @return the Ingredient generated
     */
    private Ingredient testIngredient(int id){

        return new Ingredient(id,"ID..", "description...", id+0.1, id+0.2,
                LocalDate.now(), LocalDate.now().minusDays(7), "PL.......");
    }

    /**
     * Test if adding an item to the Item_Table works as intended
     */
    @Test
    void add() {
        //Add a valid one
        assertTrue(testTable.add(testIngredient(1)));
        assertEquals(1, testTable.size());

        //Add an invalid one aka. same ID
        assertFalse(testTable.add(testIngredient(1)));
        assertEquals(1, testTable.size());

        //Add another valid one different ID
        assertTrue(testTable.add(testIngredient(2)));
        assertEquals(2, testTable.size());

    }

    /**
     * Test if removing an item from the Item_Table works as intended
     */
    @Test
    void remove() {
        //add a few entries
        assertTrue(testTable.add(testIngredient(1)));
        assertTrue(testTable.add(testIngredient(2)));
        assertTrue(testTable.add(testIngredient(3)));
        assertEquals(3, testTable.size());

        //remove an existing one
        assertTrue(testTable.remove(3));
        assertEquals(2, testTable.size());

        //try to remove a non-existing one
        assertFalse(testTable.remove(3));
        assertEquals(2, testTable.size());

    }

    /**
     * Test if updating an item from the Item_Table works as intended
     */
    @Test
    void update() {
        //add an entry to update later
        assertTrue(testTable.add(testIngredient(1)));
        assertEquals(1, testTable.size());

        //update existing one
        assertTrue(testTable.update(testIngredient(1)));
        assertEquals(1, testTable.size());

        //try to update non-existing one
        assertFalse(testTable.update(testIngredient(2)));
        assertEquals(1, testTable.size());

    }

    /**
     * Tests if size get counted as intended
     */
    @Test
    void size() {
        int test = 10;
        for(int i = 0; i < test; i++){
            testTable.add(testIngredient(i));
            assertEquals(i+1, testTable.size());
        }
    }

    /**
     * Tests the getter
     */
    @Test
    void getSetAmount() {
        //item to test on
        testTable.add(testIngredient(1));
        assertEquals(0, testTable.getAmount(1));

        //test add
        testTable.setAmount(1,10);
        assertEquals(10, testTable.getAmount(1));

        //test subtract
        testTable.setAmount(1,-4);
        assertEquals(6, testTable.getAmount(1));

    }

    /**
     * Tests the getter
     */
    @Test
    void getSetThreshold() {
        //item to test on
        testTable.add(testIngredient(2));
        assertEquals(0, testTable.getThreshold(2));

        //test add
        testTable.setThreshold(2,10);
        assertEquals(10, testTable.getThreshold(2));

        //test subtract
        testTable.setThreshold(2,-4);
        assertEquals(6, testTable.getThreshold(2));
    }


    /**
     * Tests the getter
     */
    @Test
    void getEntry() {
        //setup
        Ingredient in = testIngredient(1);
        testTable.add(in);
        Ingredient out = testTable.getEntry(1);

        assertEquals(in, out);

    }

    /**
     * Tests the getter
     */
    @Test
    void testToString() {
        //add a few entries
        testTable.add(testIngredient(1));
        testTable.add(testIngredient(2));
        testTable.add(testIngredient(3));

        System.out.println(testTable);
    }
}