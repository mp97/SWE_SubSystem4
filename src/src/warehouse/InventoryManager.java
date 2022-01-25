package src.warehouse;

import src.common.TaskRequirements;
import src.database.DemoDB;
import src.database.Item_Table;
import src.exceptions.StorageAreaException;
import src.warehouse.item.Ingredient;
import src.warehouse.item.Item;
import src.warehouse.item.Package;
import src.warehouse.storageArea.GeneralArea;
import src.warehouse.storageArea.IngredientArea;
import src.warehouse.storageArea.StorageArea;


import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static src.common.Constants.*;

/**
 * The InventoryManager is one of the main component of the subsystem and its responsible for the inventory management.
 * Its needs to ensure that are always enough of each item(ingredient/package/part) left.
 * The storage system used two types of StorageAreas one for ingredients (can spoil)
 * and a general one for stuff that doesn't spoil.
 * The InventoryManager handles the distribution of the items amongst the StorageAreas.
 */
public class InventoryManager extends TaskRequirements implements Runnable{

    //hold information about the existing storageAreas
    private final Map<Integer, GeneralArea> generalAreas;
    private final Map<Integer, IngredientArea> ingredientAreas;
    private int id;

    //hold information about storageArea state anomalies (state!=NORMAL)
    private final Map<Integer, Warnings> storageAreaAnomalies;

    //list of warnings (not space to accommodate the needed amount of ITEMS of a certain type)
    private final Map<Item, Warnings> inventoryWarnings;


    //task running once a day at given time
    private final ScheduledExecutorService scheduler;
    private final int[] TIME = new int[]{6,0,0};


    /**
     * Constructor for class
     */
    public InventoryManager() {
        this.generalAreas = new TreeMap<>();
        this.ingredientAreas = new TreeMap<>();

        this.id = 0;
        this.storageAreaAnomalies = new TreeMap<>();

        this.inventoryWarnings = new TreeMap<>();

        //scheduled task running a 6am each day (daly check of IngredientAreas)
        this.scheduler = Executors.newScheduledThreadPool(1);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Vienna"));
        ZonedDateTime nextRun = now.withHour(TIME[0]).withMinute(TIME[1]).withSecond(TIME[2]);
        if(now.compareTo(nextRun) > 0 ){
            nextRun = nextRun.plusDays(1);
        }
        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();
        scheduler.scheduleAtFixedRate(new DailyCheckupTask(),
                initialDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);
    }

    /**
     * Terminates the main task and the ScheduledExecutorService
     */
    @Override
    public void terminate(){
        //shutdown for InventoryManager task
        super.terminate();

        //shutdown of daily check method task
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException ex) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Adds a new StorageArea to the system
     * @param area the StorageArea to be added
     * @return true if successful
     */
    public boolean addArea(StorageArea area){
        if(area instanceof GeneralArea){
            generalAreas.put(id, (GeneralArea) area);
        }else if(area instanceof IngredientArea){
            ingredientAreas.put(id, (IngredientArea) area);
        } else {
            return false;
        }

        id++;
        return true;
    }

    /**
     * Removes a StorageArea
     * @param id the ide of the area to be removed
     * @return true if successful
     */
    public boolean removeArea(int id){
        if(generalAreas.containsKey(id)){
            if(generalAreas.get(id).removable()){
                generalAreas.remove(id);
            }
        }else if(ingredientAreas.containsKey(id)){
            if(ingredientAreas.get(id).removable()){
                ingredientAreas.remove(id);
            }
        }else{
            return false;
        }
        return true;
    }

    /**
     * Sets a Storage Area to its normal State
     * @param id the ide of the area to be removed
     * @return true if successful
     */
    public boolean setToNormal(int id){
        if(generalAreas.containsKey(id)){
            generalAreas.get(id).setToNormal();
        }else if(ingredientAreas.containsKey(id)){
            ingredientAreas.get(id).setToNormal();
        }else{
            return false;
        }
        if(storageAreaAnomalies.containsKey(id)){
            storageAreaAnomalies.remove(id);
        }
        return true;
    }

    /**
     * Sets a Storage Area to its FREEZE State
     * @param id the ide of the area to be removed
     * @return true if successful
     */
    public boolean setToFreeze(int id){
        if(generalAreas.containsKey(id)){
            generalAreas.get(id).setToFreeze();
        }else if(ingredientAreas.containsKey(id)){
            ingredientAreas.get(id).setToFreeze();
        }else{
            return false;
        }
        storageAreaAnomalies.put(id, new Warnings(TEXT_CYAN, "AREA Nr: " + id + " is Set to FREEZE" + TEXT_RESET));
        return true;
    }

    /**
     * Sets a Storage Area to its FLUSH State
     * @param id the ide of the area to be removed
     * @return true if successful
     */
    public boolean setToFlush(int id){
        if(generalAreas.containsKey(id)){
            generalAreas.get(id).setToFlush();
        }else if(ingredientAreas.containsKey(id)){
            ingredientAreas.get(id).setToFlush();
        }else{
            return false;
        }
        storageAreaAnomalies.put(id, new Warnings(TEXT_YELLOW, "AREA Nr: " + id + " is Set to FLUSH" + TEXT_RESET));
        return true;
    }

    /**
     * runs the daily check method of the ingredient area
     * will be called onc a day by the InventoryManager
     *
     * "can be called by the MAIN program" but only for demo purposes
     */
    public void dalyCheck(){
        for(Integer i : ingredientAreas.keySet()){
            int removeCount = 0;
            removeCount += ingredientAreas.get(i).dalyCheck();
            if(removeCount != 0){
                DemoDB.getInstance().getIngredientTable().setThreshold(ingredientAreas.get(i).getDesignated().getIID(), -removeCount);
            }
        }
    }

    /**
     * Prints a List of all the StorageAreas
     * first goes over the ones containing Ingredients (can spoil)
     * and afterwords the rest (can't spoil)
     */
    public void listAreas(){
        for(Integer i : ingredientAreas.keySet()){
            String ifNull;
            if(ingredientAreas.get(i).getDesignated() == null){
                ifNull = " is empty";
            } else {
                ifNull = String.valueOf(ingredientAreas.get(i).getDesignated().hideDetails());
            }
            System.out.printf("IA ID: %4s\t is holding %4d/%d of %s\n", i,  ingredientAreas.get(i).getStock(), ingredientAreas.get(i).getCapacity(), ifNull);
        }
        for(Integer i : generalAreas.keySet()){
            System.out.printf("GA ID: %4s\t is holding %4d/%d of %s\n", i,  generalAreas.get(i).getStock(), generalAreas.get(i).getCapacity(), generalAreas.get(i).getDesignated());
        }
    }

    /**
     * Prints a List of all the StorageAreas in full detail
     * first goes over the ones containing Ingredients (can spoil)
     * and afterwords the rest (can't spoil)
     */
    public void listAreas2(){
        for(Integer i : ingredientAreas.keySet()){
            System.out.println(ingredientAreas.get(i));
        }
        for(Integer i : generalAreas.keySet()){
            System.out.println(generalAreas.get(i));
        }
    }

    /**
     * Take a request from other subsystems
     * @param itemName the name of the item needed
     * @param amount the amount of the Item
     * @throws StorageAreaException if not enough items ar available
     */
    public void request(String itemName, int amount) throws StorageAreaException {
        DemoDB db = DemoDB.getInstance();
        //find the item based on id by going over all tables
        Item_Table<Ingredient> iTab = db.getIngredientTable();
        for(int i : iTab.getAll().keySet()){
            if(iTab.getEntry(i).getName().equals(itemName)){
                if(iTab.getAmount(i) >= amount){
                    send(iTab.getEntry(i), amount);
                } else {
                    throw new StorageAreaException("NOT ENOUGH of: " + itemName + "available");
                }
            }
        }
    }

    /**
     * Sends the request Item
     * @param item the item requested
     * @param amount the amount of the item
     * @return s
     */
    private String send(Item item, int amount) throws StorageAreaException {
        removeFromStorage(item, amount);
        return "Sent " + amount + " of " + item.getName();
    }


    /**
     * distributes item between storage areas
     * @param item the item to be distributed
     * @param amount the amount of that item
     */
    public void addToStorage(Item item, int amount) throws StorageAreaException {

        //add to DB
        if(item instanceof Ingredient){
            DemoDB.getInstance().getIngredientTable().setAmount(item.getIID(), amount);
        }else if (item instanceof Package){
            DemoDB.getInstance().getPackageTable().setAmount(item.getIID(), amount);
        } else{
            DemoDB.getInstance().getPartTable().setAmount(item.getIID(), amount);
        }

        //distribute among Storage Areas
        StorageArea currentArea;
        if(item instanceof Ingredient){
            for(int i : ingredientAreas.keySet()){
                currentArea = ingredientAreas.get(i);
                amount = distributeInAreas(currentArea, item, amount);
                if(amount == 0){
                    return;
                }

            }
        }else {//item is Part or Package
            for(int i : generalAreas.keySet()){
                currentArea = generalAreas.get(i);
                amount = distributeInAreas(currentArea, item, amount);
                if(amount == 0){
                    return;
                }

            }
        }

    }

    /**
     * Fills a single storage Area to the max
     * @param currentArea the storage Area in question
     * @param item the item going into it
     * @param amount the amount going into it
     * @return new value for Amount
     * @throws StorageAreaException if StorageArea gets overfilled
     */
    private int distributeInAreas(StorageArea currentArea, Item item, int amount) throws StorageAreaException {
        if(currentArea.getDesignated().getName().equals(item.getName())){
            if(currentArea.getCapacity() >= amount){
                for(int j = 0; j < amount; j++){
                    currentArea.deposit(item);
                }
                return 0;
            }else{
                for(int j = 0; j < currentArea.getCapacity(); j++){
                    currentArea.deposit(item);
                    amount--;
                }
            }
        }
        return amount;
    }

    /**
     * takes an item from a storage area
     * @param item the item to be taken
     * @param amount the amount ot be taken
     */
    public void removeFromStorage(Item item, int amount) throws StorageAreaException {
        //remove from DB
        if(item instanceof Ingredient){
            DemoDB.getInstance().getIngredientTable().setAmount(item.getIID(), -amount);
        }else if (item instanceof Package){
            DemoDB.getInstance().getPackageTable().setAmount(item.getIID(), -amount);
        } else{
            DemoDB.getInstance().getPartTable().setAmount(item.getIID(), -amount);
        }

        //remove from storage areas
        for(int i = 0; i < amount; i++){
            if(item instanceof Ingredient){
                //identify earliest expiring product from storage areas and take it
                IngredientArea earliestExpiry = null;
                for(int j : ingredientAreas.keySet()){
                    IngredientArea currentArea = ingredientAreas.get(j);
                    if(currentArea.getDesignated().equals(item)){
                        if(currentArea.getStock() > 0){
                            if(earliestExpiry == null){
                                earliestExpiry = currentArea;
                            } else if(earliestExpiry.peakAtFirst().isAfter(currentArea.peakAtFirst())){
                                earliestExpiry = currentArea;
                            }
                        }
                    }
                }
                //take identify earliest expiring
                if(earliestExpiry != null) {
                    earliestExpiry.take();
                }

            } else{//item instanceof Part/Package
                //simply take first match
                for(int j : generalAreas.keySet()){
                    GeneralArea currentArea = generalAreas.get(j);
                    if(currentArea.getStock() > 0 && currentArea.getDesignated().equals(item)){
                       currentArea.take();
                       break;
                    }
                }
            }
        }
    }


    /**
     * Prints a list of al the storageAreaAnomalies
     */
    public void listAnomalies(){
        for (Integer i : storageAreaAnomalies.keySet()){
            System.out.println(storageAreaAnomalies.get(i));
        }
    }

    /**
     * Prints a list of al the inventoryWarnings
     */
    public void listWarnings(){
        for (Item i : inventoryWarnings.keySet()){
            System.out.println(inventoryWarnings.get(i));
        }
    }




    /**
     * Keeps inventory full
     */
    @Override
    public void run() {
       while (this.isRunning()){
            //needs to sleep
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //run orders
            try {
               orderingProcess(DemoDB.getInstance().getIngredientTable(), true);
               orderingProcess(DemoDB.getInstance().getPackageTable(), false);
               orderingProcess(DemoDB.getInstance().getPartTable(), false);
            } catch (StorageAreaException e) {
               e.printStackTrace();
            }


       }
    }

    /**
     * controllers the ordering process to keep the system stocked up
     * @param table the Table of the Database it's focusing on
     * @param isIngredient true if table<Ingredient>
     */
    public void orderingProcess(Item_Table table, boolean isIngredient) throws StorageAreaException {
        //go over content of table
        for(Object i : table.getAll().keySet()){
            //min amount of each ingredient
            int threshold = table.getThreshold((Integer) i);

            //get amount of free capacity
            int free = 0;
            if(isIngredient){
                for(Integer j : ingredientAreas.keySet()){
                    IngredientArea currentArea = ingredientAreas.get(j);
                    if(currentArea.getDesignated() == table.getEntry((Integer) i)){
                        free += (currentArea.getCapacity()-currentArea.getStock());
                    }
                }
            }else {
                for (Integer j : generalAreas.keySet()) {
                    GeneralArea currentArea = generalAreas.get(j);
                    if(currentArea.getDesignated() == table.getEntry((Integer) i)){
                        free += (currentArea.getCapacity()-currentArea.getStock());
                    }
                }
            }
            //get current amount stored
            int amount = table.getAmount((Integer) i);
            //if amount < threshold place an order
            Item currentItem = table.getEntry((Integer) i);
            if(amount < threshold){

                //add a warning if there isn't enough space to satisfy threshold requirement
                if(amount+free < threshold){
                    inventoryWarnings.put(currentItem, new Warnings(TEXT_RED, "Not enough space for "
                            + currentItem.getName() + " ID: " + currentItem.getIID() +
                            "; current capacity is " + (threshold-amount-free) + " to low!"
                            + "\nFREE: " +free+" Amount: "+amount+" Threshold: "+threshold));
                } else {//remove the warning if the situation is solved
                    if(inventoryWarnings.containsKey(currentItem)){
                        inventoryWarnings.remove(currentItem);
                        inventoryWarnings.put(currentItem,
                                new Warnings(TEXT_GREEN, "Not enough space for "
                                        + currentItem.getName() + " ID: " + currentItem.getIID() +
                                        "; current capacity is " + ((threshold-amount-free)) + " to low!"
                                        + "\nFREE: " +free+" Amount: "+amount+" Threshold: "+threshold));
                    }
                }

                //either case orders as much a fits
                System.out.println("ordering: "+free);
                addToStorage(currentItem, free);
            }
        }


    }


    /**
     * Extra Task that only runs the daly check Method once a day a given time
     * see constructor of Inventory Manager for exact daytime
     */
    private class DailyCheckupTask implements Runnable{

        public DailyCheckupTask() {
        }

        @Override
        public void run() {
            dalyCheck();
        }
    }


    //getter
    public Map<Integer, GeneralArea> getGeneralAreas() {
        return generalAreas;
    }

    public Map<Integer, IngredientArea> getIngredientAreas() {
        return ingredientAreas;
    }

    public int getGeneralAreasNr() {
        return generalAreas.size();
    }

    public int getIngredientAreasNr() {
        return ingredientAreas.size();
    }

    public int getStorageAreaAnomaliesNr() {
        return storageAreaAnomalies.size();
    }

    public int getInventoryWarningsNr() {
        return inventoryWarnings.size();
    }

    public boolean schedulerShutdown(){
        return scheduler.isShutdown();
    }
}
