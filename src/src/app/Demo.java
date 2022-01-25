package src.app;

import src.database.DemoDB;
import src.exceptions.DroneException;
import src.exceptions.StorageAreaException;
import src.shipping.ShippingManager;
import src.shipping.deliverymethod.drones.CarrierDrone;
import src.shipping.deliverymethod.drones.DeliveryDrone;
import src.shipping.ditributionCenter.DistributionCenter;
import src.warehouse.InventoryManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static src.common.Constants.*;
import static src.app.DemoData.*;

/**
 * This is a demo application for the subsystem
 */
public class Demo {

    private static ThreadPoolExecutor pool;
    private static InventoryManager inventoryManager;
    private static ShippingManager shippingManager;


    public static void main(String[] args) throws StorageAreaException, DroneException {

        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);



        //runs respective parts of the demo
        demoStorage();
        demoShipping();

        //terminates the pool
        terminate();
    }

    /**
     * Runs the storage part of the demo
     */
    private static void demoStorage() throws StorageAreaException {

        inventoryManager = new InventoryManager();
        inventoryColor("Demo for storage part");
        //prepare DB for demo
        inventoryDBSetup();
        DemoDB db = DemoDB.getInstance();
        inventoryColor("\nLook at DB tables of Items");
        db.printItems();


        //prepare StorageAreas
        storageAreaSetup(inventoryManager);
        //NOTE: inventoryManger should be started like this:
        //pool.execute(inventoryManager);
        //it just doesn't get done to allow the demo to showcase certain scenarios
        //the process would otherwise just correct before it can be shown
        inventoryColor("\nLook at storage Areas");
        inventoryManager.listAreas();

        //proceed to fill storage up to threshold
        inventoryColor("\nFill storage up to threshold");
        demoOrder(inventoryManager);
        inventoryManager.listAreas();

        //delete spoiled ingredients by running daily routine
        //normally ordering should not get soiled items but here id was a setup for the demo
        inventoryColor("\nLook at Date of StorageArea ID: 0");
        System.out.println(inventoryManager.getIngredientAreas().get(0));
        //inventoryManager.dalyCheck();
        demoDailyCheck(inventoryManager);
        inventoryColor("\nRun daily routine now (should only run once a day (exception for the demo))");
        System.out.println(inventoryManager.getIngredientAreas().get(0));





        inventoryManager.terminate();
    }


    /**
     * Runs the shipping part of the demo
     */
    private static void demoShipping() throws DroneException {
        storageColor("\n\n\nDemo for storage part");
        shippingManager = new ShippingManager();

        //Setup of delivery network
        storageColor("\nSetup of delivery network");
        infrastructureSetup(shippingManager);
        for(CarrierDrone cd : shippingManager.getCarrierDrones()){
            System.out.println(cd.getIdentifier());
            for(DeliveryDrone dd : cd.getDeliveryDrones()){
                System.out.println("\t" + dd.getIdentifier());
            }
        }
        for(DistributionCenter dc : shippingManager.getDistributionCenters()){
            for(CarrierDrone cd : dc.getCarrierDrones()){
                System.out.println(cd.getIdentifier());
                for(DeliveryDrone dd : cd.getDeliveryDrones()){
                    System.out.println("\t" + dd.getIdentifier());
                }
            }
        }

        //Setup of demo Orders
        storageColor("\nSetup of demo Orders");
        dbSetupShipping();
        DemoDB db = DemoDB.getInstance();
        db.printOrders();

        //run delivery
        storageColor("\nRun Delivery and give it some time to run");
        //shippingManager.run();
        pool.execute(shippingManager);
        //wait to allow delivery to run
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check order Status once again
        storageColor("\nCheck order Status once again");
        db.printOrders();


        //alternative run with many orders and only one facility no wormhole-delivery
        //to showcase how a facility behaves if it has more pending orders than delivery capacity
        int runs  = 25;
        storageColor("\nAlternative run with " + runs + " orders only over main facility no wormhole-delivery");
        //create new shipping manager with new delivery network and terminate old one
        shippingManager.terminate();
        shippingManager = new ShippingManager();
        //setup alternative delivery network
        int deliveryDroneCapacity = 5;
        int deliveryDrones = 2;
        alternativeRun(shippingManager, runs, deliveryDroneCapacity, deliveryDrones);
        //show current delivery network
        storageColor("\nShow alternative delivery network ["
                + deliveryDrones + " DeliveryDrone with capacity: " + deliveryDroneCapacity + "]");
        for(CarrierDrone cd : shippingManager.getCarrierDrones()){
            System.out.println(cd.getIdentifier());
            for(DeliveryDrone dd : cd.getDeliveryDrones()){
                System.out.println("\t" + dd.getIdentifier());
            }
        }
        //show Orders for this run
        db = DemoDB.getInstance();
        storageColor("\nCheck order Status once again");
        db.printOrders();

        pool.execute(shippingManager);
        //showcase Drone behaviour
        storageColor("\nDrone behaviour");

        //wait to allow delivery to run
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        storageColor("\nCheck order Status a final time");
        db.printOrders();
    }

    /**
     * terminates the pool
     */
    private static void terminate(){
        inventoryManager.terminate();
        shippingManager.terminate();

        System.out.println(TEXT_YELLOW + "\n\n\nTrying to end running tasks" + TEXT_RESET);
        pool.shutdown();
        try {
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println(TEXT_GREEN + "All Tasks are Closed" + TEXT_RESET);
    }

    private static void inventoryColor(String text){
        System.out.println(TEXT_CYAN + text + TEXT_RESET);
    }

    private static void storageColor(String text){
        System.out.println(TEXT_PURPLE + text + TEXT_RESET);
    }
}
