package test.warehouse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.database.DemoDB;
import src.exceptions.StorageAreaException;
import src.warehouse.InventoryManager;
import src.warehouse.item.Ingredient;
import src.warehouse.item.Package;
import src.warehouse.item.PackageDimensions;
import src.warehouse.storageArea.GeneralArea;
import src.warehouse.storageArea.IngredientArea;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InventoryManagerTest {

    InventoryManager testManager;

    @BeforeEach
    void setUp() {
        testManager = new InventoryManager();
    }

    @AfterEach
    void tearDown() {
        testManager.terminate();
        testManager = null;
        DemoDB.getInstance().clearDB();
    }

    /**
     * Check if the Main and scheduled task have been shutdown properly
     */
    @Test
    void terminate() {
        testManager.terminate();

        assertFalse(testManager.isRunning());
        assertTrue(testManager.schedulerShutdown());
    }

    /**
     * test if adding storage areas works as intended
     */
    @Test
    void addArea() {
        assertEquals(0, testManager.getGeneralAreasNr());
        assertEquals(0, testManager.getIngredientAreasNr());

        int runs = 8;
        addAreas(runs*2, runs);

        assertEquals(runs*2, testManager.getGeneralAreasNr());
        assertEquals(runs, testManager.getIngredientAreasNr());
    }

    /**
     * Adds a few Storage Areas for testing
     * @param general amount of GeneralAreas added
     * @param ingredient amount of IngredientAreas added
     */
    private void addAreas(int general, int ingredient){
        for(int i = 0; i < general; i++){
            testManager.addArea(new GeneralArea(10));
        }
        for(int i = 0; i < ingredient; i++){
            testManager.addArea(new IngredientArea(10));
        }
    }

    /**
     * Test if the removal of a StorageArea works as intended
     */
    @Test
    void removeArea() {
        //add areas
        int runs = 8;
        addAreas(runs, runs);

        //remove these areas
        assertTrue(testManager.removeArea(0));
        assertTrue(testManager.removeArea(1));
        assertTrue(testManager.removeArea(runs));

        assertEquals(runs-2, testManager.getGeneralAreasNr());
        assertEquals(runs-1, testManager.getIngredientAreasNr());

        //try to remove non-existing one
        assertFalse(testManager.removeArea(0));
    }

    /**
     * Test if areas can be set to FREEZE and if the amount of anomalies goes up as intended
     */
    @Test
    void setToFreeze() {
        //add areas
        testManager.addArea(new IngredientArea(10));
        testManager.addArea(new GeneralArea(10));

        //FREEZE
        assertTrue(testManager.setToFreeze(0));
        assertEquals(1, testManager.getStorageAreaAnomaliesNr());
        assertTrue(testManager.setToFreeze(1));
        assertEquals(2, testManager.getStorageAreaAnomaliesNr());

        //FREEZE A NON EXISTING ONE
        assertFalse(testManager.setToFreeze(99999));
        assertEquals(2, testManager.getStorageAreaAnomaliesNr());

    }

    /**
     * Test if areas can be set to FLUSH and if the amount of anomalies goes up as intended
     */
    @Test
    void setToFlush() {
        //add areas
        testManager.addArea(new IngredientArea(10));
        testManager.addArea(new GeneralArea(10));

        //FREEZE
        assertTrue(testManager.setToFlush(0));
        assertEquals(1, testManager.getStorageAreaAnomaliesNr());
        assertTrue(testManager.setToFlush(1));
        assertEquals(2, testManager.getStorageAreaAnomaliesNr());

        //FREEZE A NON EXISTING ONE
        assertFalse(testManager.setToFreeze(99999));
        assertEquals(2, testManager.getStorageAreaAnomaliesNr());
    }

    /**
     * Test if areas can be set to back to NORMAL and if the amount of anomalies goes down as intended
     */
    @Test
    void setToNormal() {
        //add areas
        testManager.addArea(new IngredientArea(10));
        testManager.addArea(new GeneralArea(10));

        //FREEZE and FLUSH
        assertTrue(testManager.setToFreeze(0));
        assertEquals(1, testManager.getStorageAreaAnomaliesNr());
        assertTrue(testManager.setToFlush(1));
        assertEquals(2, testManager.getStorageAreaAnomaliesNr());

        //SET TO NORMAL
        assertTrue(testManager.setToNormal(1));
        assertEquals(1, testManager.getStorageAreaAnomaliesNr());
        assertTrue(testManager.setToNormal(0));
        assertEquals(0, testManager.getStorageAreaAnomaliesNr());

        //SET NORMAL ARE TO NORMAL
        assertTrue(testManager.setToNormal(1));
        assertEquals(0, testManager.getStorageAreaAnomaliesNr());

        //SET NON-EXISTING AREA TO NORMAL
        assertFalse(testManager.setToNormal(999));
        assertEquals(0, testManager.getStorageAreaAnomaliesNr());

    }

    /**
     * jus a println not actually needed for testing just here to see output
     */
    @Test
    void listAreas() {
        addAreas(10,5);
        testManager.listAreas();
    }

    /*
     * Not needed, done with a combination of set to NORMAL and FREEZE/FLUSH testing
    @Test
    void AnomaliesManagement() {
    }*/

    /**
     * Test if the not enough space warning track correctly
     */
    @Test
    void WarningsManagement() throws StorageAreaException {

        //add a new Ingredient
        int threshold = 10000;
        DemoDB db = DemoDB.getInstance();
        Ingredient testIngredient = new Ingredient(1,"Name", "description", 0.1, 0.2,
                LocalDate.now(), LocalDate.now(), "PL________");
        db.add(testIngredient, threshold);

        //call to orderingProcess() is used to force the check,
        //since testManager wasn't actually started so its must be done manually


        System.out.println("_>>>>>>>>Nothing");
        testManager.orderingProcess(db.getIngredientTable(),true);
        //now there should be a warning
        assertEquals(1, testManager.getInventoryWarningsNr());
        testManager.listWarnings();
        testManager.listAreas();



        //by adding a StorageArea with only half capacity of what's missing the warning should remain
        System.out.println("_>>>>>>>>HALF");
        testManager.addArea(new IngredientArea(testIngredient, threshold/2));
        //testManager.orderingProcess(db.getIngredientTable(),true);
        testManager.addToStorage(testIngredient, threshold/2);
        assertEquals(1, testManager.getInventoryWarningsNr());
        testManager.listWarnings();
        testManager.listAreas();

        //by adding a StorageArea with enough capacity the warning should go away
        System.out.println("_>>>>>>>>double");
        testManager.addArea(new IngredientArea(testIngredient, threshold));
        testManager.listAreas();
        assertEquals(1, testManager.getInventoryWarningsNr());

    }

    /**
     * Test if using the addToStorage adds to db and StorageAreas
     */
    @Test
    void addToStorage() throws StorageAreaException {
        //create a new Ingredient
        int id = 1;
        int threshold = 10000;
        DemoDB db = DemoDB.getInstance();
        Ingredient testIngredient = new Ingredient(id,"Name", "description", 0.1, 0.2,
                LocalDate.now(), LocalDate.now(), "PL________");
        db.add(testIngredient, threshold);

        //create test Areas
        IngredientArea testArea = new IngredientArea(testIngredient, 10);
        testManager.addArea(testArea);
        IngredientArea testArea2 = new IngredientArea(testIngredient, 1000);
        testManager.addArea(testArea2);

        //assert in DB
        assertEquals(0, db.getIngredientTable().getAmount(id));
        testManager.addToStorage(testIngredient, 1010);
        assertEquals(1010, db.getIngredientTable().getAmount(id));

        //assert in Storage Areas
        testManager.listAreas();
        assertEquals(10, testManager.getIngredientAreas().get(0).getStock());
        assertEquals(1000, testManager.getIngredientAreas().get(1).getStock());

    }

    /**
     * Test if using the removeFromStorage removes from db and StorageAreas
     */
    @Test
    void removeFromStorage() throws StorageAreaException {
        //add a package and an ingredient to the system

        int threshold = 510;
        int remove = 50;
        int capacity1 = 10;
        int capacity2 = 1000;
        DemoDB db = DemoDB.getInstance();
        Ingredient testIngredient = new Ingredient(1,"INg", "description", 0.1, 0.2,
                LocalDate.now(), LocalDate.now(), "PL________");
        Package testPackage = new Package(2, "PAck", "description", 0.1, 0.2, new PackageDimensions(1,2,3));
        db.add(testIngredient, threshold);
        db.add(testPackage, threshold);

        //create test Areas
        IngredientArea testArea = new IngredientArea(testIngredient, capacity1);
        testManager.addArea(testArea);
        IngredientArea testArea2 = new IngredientArea(testIngredient, capacity2);
        testManager.addArea(testArea2);
        GeneralArea testArea3 = new GeneralArea(testIngredient, capacity1);
        testManager.addArea(testArea3);
        GeneralArea testArea4 = new GeneralArea(testIngredient, capacity2);
        testManager.addArea(testArea4);

        //add to testAreas
        testManager.addToStorage(testIngredient, threshold);
        testManager.addToStorage(testPackage, threshold);
        assertEquals(510, db.getIngredientTable().getAmount(1));
        assertEquals(510, db.getPackageTable().getAmount(2));

        //remove
        testManager.removeFromStorage(testIngredient, remove);
        testManager.removeFromStorage(testPackage, remove);

        //assert in DB
        assertEquals(threshold-remove, db.getIngredientTable().getAmount(1));
        assertEquals(threshold-remove, db.getPackageTable().getAmount(2));

        //assert in Storage Areas
        testManager.listAreas();

        assertEquals(0, testManager.getIngredientAreas().get(0).getStock());
        assertEquals((threshold-capacity1)-(remove-capacity1), testManager.getIngredientAreas().get(1).getStock());
        assertEquals(460, testManager.getIngredientAreas().get(1).getStock());
        assertEquals(0, testManager.getGeneralAreas().get(2).getStock());


    }
}