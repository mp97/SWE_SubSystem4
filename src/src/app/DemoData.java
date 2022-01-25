package src.app;

import src.database.DemoDB;
import src.exceptions.DroneException;
import src.exceptions.StorageAreaException;
import src.shipping.ShippingManager;
import src.shipping.deliverymethod.drones.CarrierDrone;
import src.shipping.deliverymethod.drones.DeliveryDrone;
import src.shipping.ditributionCenter.DistributionCenter;
import src.shipping.order.Address;
import src.shipping.order.Continent;
import src.shipping.order.Order;
import src.shipping.order.OrderStatus;
import src.warehouse.InventoryManager;
import src.warehouse.item.*;
import src.warehouse.item.Package;
import src.warehouse.storageArea.GeneralArea;
import src.warehouse.storageArea.IngredientArea;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Just generates some data for demo purpose
 */
public class DemoData {

    //demo for InventoryManager
    private static final Ingredient flour = new Ingredient(1, "Flour v1", "default flour type", 1.1, 2.2, LocalDate.now().plusDays(3), LocalDate.now().plusDays(10), "PL.....");
    private static final Ingredient tomatos = new Ingredient(2, "Tomato v1", "Cherry Tomatoes", 1.1, 2.2, LocalDate.now().plusDays(3), LocalDate.now().plusDays(10), "PL.....");
    private static final Ingredient cheese = new Ingredient(3, "Cheese v1", "grated cheddar", 1.1, 2.2, LocalDate.now().plusDays(3), LocalDate.now().plusDays(10), "PL.....");
    private static final Package standardPackage = new Package(4, "Package t1", "standard package type", 0.1, 0.2, new PackageDimensions(30, 5,5), PackageType.STANDARD);
    private static final int demoTarget = 100;

    /**
     * Adds some demoData to the database
     */
    public static void inventoryDBSetup(){
        DemoDB db = DemoDB.getInstance();

        db.add(flour, demoTarget);
        db.add(tomatos, demoTarget);
        db.add(cheese, demoTarget);
        db.add(standardPackage, demoTarget);
    }

    /**
     * Adds the StorageAreas needed for the demo to the system
     * @param demoManager the InventoryManager used in the demo
     */
    public static void storageAreaSetup(InventoryManager demoManager){
        demoManager.addArea(new IngredientArea(flour, demoTarget*2));
        demoManager.addArea(new IngredientArea(tomatos, demoTarget*3));
        demoManager.addArea(new IngredientArea(cheese, demoTarget*4));
        demoManager.addArea(new GeneralArea(standardPackage, demoTarget/2));
        demoManager.addArea(new GeneralArea(standardPackage, demoTarget));
    }

    /**
     * Simulates an order for the system over the demo data
     * @param demoManager the InventoryManager used in the demo
     * @throws StorageAreaException if a StorageArea get overfilled
     */
    public static void demoOrder(InventoryManager demoManager) throws StorageAreaException {
        demoManager.addToStorage(new Ingredient(1, "Flour v1", "default flour type", 1.1, 2.2, LocalDate.now().minusDays(1), LocalDate.now().minusDays(7), "PL....."),
                                demoTarget/2);
        demoManager.addToStorage(new Ingredient(1, "Flour v1", "default flour type", 1.1, 2.2, LocalDate.now().plusDays(3), LocalDate.now().plusDays(10), "PL....."),
                demoTarget/2-1);
        demoManager.addToStorage(new Ingredient(1, "Flour v1", "default flour type", 1.1, 2.2, LocalDate.now().plusDays(2), LocalDate.now().plusDays(9), "PL....."),
                1);
        demoManager.addToStorage(tomatos, demoTarget);
        demoManager.addToStorage(cheese, demoTarget);
        demoManager.addToStorage(standardPackage, demoTarget);
    }

    /**
     * runs the daily routine of the InventoryManager manually
     * since we can't wait till 6am for the demo
     * @param demoManager the InventoryManager used in the demo
     */
    public static void demoDailyCheck(InventoryManager demoManager){
        demoManager.dalyCheck();
    }

    //demo for the shippingManager
    /**
     * Sets up the shippingInfrastructure used in the demo
     * @param demoManager the ShippingManager used in the demo
     */
    public static void infrastructureSetup(ShippingManager demoManager) throws DroneException {
        //demo DistributionCenter
        DistributionCenter demoDC = new DistributionCenter(Continent.NA);
        demoManager.addDistributionCenter(demoDC);

        //add drones to each
        for(CarrierDrone cd : addDroneFleet(null, demoManager, 5,10)){
            demoManager.addCarrierDrone(cd);
        }
        for(CarrierDrone cd : addDroneFleet(demoDC, null, 5, 10)){
            demoDC.addCarrierDrone(cd);
        }

    }

    /**
     * Adds drone to the distribution facilities
     * @param dc the DistributionCenter to which to add drones
     * @param mainFacility the manin facility/InventoryManager
     * @param deliveryDrones the amount of the deliver-drones to be added per carrier drone
     * @param capacity the capacities of the deliver-drones
     * @return the whole drone fleet
     * @throws DroneException if there is an issue with the drones
     */
    private static List<CarrierDrone> addDroneFleet(DistributionCenter dc, ShippingManager mainFacility, int deliveryDrones, int capacity) throws DroneException {
        CarrierDrone cd1 = new CarrierDrone(1, dc, mainFacility);
        CarrierDrone cd2 = new CarrierDrone(2, dc, mainFacility);
        CarrierDrone cd3 = new CarrierDrone(3, dc, mainFacility);

        List<CarrierDrone> carrierDrones = new ArrayList<>();
        carrierDrones.add(cd1);
        carrierDrones.add(cd2);
        carrierDrones.add(cd3);

        for(CarrierDrone cd : carrierDrones){
            for(int i = 0; i < deliveryDrones; i++){
                cd.assignDrones(new DeliveryDrone(i+4, capacity));
            }
        }

        return carrierDrones;
    }

    /**
     * Adds some Orders to the Database for the demo
     */
    public static void dbSetupShipping(){
        DemoDB.getInstance().clearDB();
        DemoDB db = DemoDB.getInstance();

        int perType = 5;
        Address demoAddress;
        for(int i = 0; i < 4*perType; i++){
            if(i < perType){
                demoAddress = new Address(Continent.AS, i, "demo address"+i);
                db.add(new Order(i, demoAddress, OrderStatus.PACKAGED, true));
            }else if(i < 2*perType){
                demoAddress = new Address(Continent.EU, i, "demo address"+i);
                db.add(new Order(i, demoAddress, OrderStatus.PACKAGED, false));
            }else if(i < 3*perType){
                demoAddress = new Address(Continent.NA, i, "demo address"+i);
                db.add(new Order(i, demoAddress, OrderStatus.PACKAGED, false));
            }else{
                demoAddress = new Address(Continent.EU, i, "demo address"+i);
                db.add(new Order(i, demoAddress, OrderStatus.SCHEDULED, false));
            }


        }

    }

    /**
     * Generates an alternative delivery network and set of Orders for the demo
     * @param demoManager the ShippingManager used in the demo
     * @param runs the amount of runs
     * @param deliveryDroneCapacity the capacity of the delivery drones
     * @param deliveryDrones the amount of delivery drones
     * @throws DroneException if there are issues when loading the drones
     */
    public static void alternativeRun(ShippingManager demoManager, int runs, int deliveryDroneCapacity, int deliveryDrones) throws DroneException {
        infrastructureSetupAlt(demoManager, deliveryDroneCapacity, deliveryDrones);
        dbSetupOrders2(runs);
    }

    /**
     * Sets up the alternative delivery network
     * @param demoManager the ShippingManager used in the demo
     * @param deliveryDroneCapacity the capacity of the delivery drones
     * @param deliveryDrones the amount of delivery drones
     * @throws DroneException if there are issues when loading the drones
     */
    private static void infrastructureSetupAlt(ShippingManager demoManager, int deliveryDroneCapacity, int deliveryDrones) throws DroneException {
        //add drones to each
        CarrierDrone cd = new CarrierDrone(1, null, demoManager);
        for(int i = 0; i < deliveryDrones; i++){
            cd.assignDrones(new DeliveryDrone(i+1, deliveryDroneCapacity));
        }
        demoManager.addCarrierDrone(cd);

    }

    /**
     * Sets up the orders for the demo
     * @param runs the amount of runs/orders in this case
     */
    private static void dbSetupOrders2(int runs){
        DemoDB.getInstance().clearDB();
        DemoDB db = DemoDB.getInstance();
        Address demoAddress;
        for(int i = 100; i < runs+100; i++){
            demoAddress = new Address(Continent.EU, i, "demo address "+i);
            db.add(new Order(i, demoAddress, OrderStatus.PACKAGED, false));
        }

    }


}
