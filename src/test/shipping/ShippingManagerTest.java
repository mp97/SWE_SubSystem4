package test.shipping;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.database.DemoDB;
import src.exceptions.DroneException;
import src.shipping.ShippingManager;
import src.shipping.deliverymethod.drones.CarrierDrone;
import src.shipping.deliverymethod.drones.DeliveryDrone;
import src.shipping.ditributionCenter.DistributionCenter;
import src.shipping.order.Address;
import src.shipping.order.Continent;
import src.shipping.order.Order;
import src.shipping.order.OrderStatus;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class ShippingManagerTest {

    ShippingManager testManager;
    DistributionCenter disC1;
    DistributionCenter disC2;
    CarrierDrone cd01;
    CarrierDrone cd02;
    CarrierDrone cd11;
    CarrierDrone cd12;
    CarrierDrone cd21;
    CarrierDrone cd22;

    @BeforeEach
    void setUp() throws DroneException {
        testManager = new ShippingManager();
        disC1 = new DistributionCenter(Continent.NA);
        disC2 = new DistributionCenter(Continent.AS);

        //Add DistributionCenters
        testManager.addDistributionCenter(disC1);
        testManager.addDistributionCenter(disC2);

        //create CarrierDrones
        cd01 = new CarrierDrone(1, null, testManager);
        cd02 = new CarrierDrone(1, null, testManager);
        cd11 = new CarrierDrone(1, disC1, null);
        cd12 = new CarrierDrone(1, disC1, null);
        cd21 = new CarrierDrone(1, disC2, null);
        cd22 = new CarrierDrone(1, disC2, null);

        //Add CarrierDrones
        testManager.addCarrierDrone(cd01);
        testManager.addCarrierDrone(cd02);
        disC1.addCarrierDrone(cd11);
        disC1.addCarrierDrone(cd12);
        disC2.addCarrierDrone(cd21);
        disC2.addCarrierDrone(cd22);


        //Add DeliCarrierDrones
        assignDeliveryDrone(cd01);
        assignDeliveryDrone(cd02);
        assignDeliveryDrone(cd11);
        assignDeliveryDrone(cd12);
        assignDeliveryDrone(cd21);
        assignDeliveryDrone(cd22);

    }

    /**
     * Assigns DeliveryDrone to the CarrierDrones
     * @param cd the CarrierDrone to which we assign the DeliveryDrones
     */
    void assignDeliveryDrone(CarrierDrone cd) throws DroneException {
        for(int i = 0; i < 3; i++){
           cd.assignDrones(new DeliveryDrone((100*cd.getId()+i), 5));
        }
    }

    @AfterEach
    void tearDown() {
        testManager.terminate();
    }

    /**
     * test if we can add distribution centers
     */
    @Test
    void addDistributionCenter() {
        //jus clear for test
        testManager.getDistributionCenters().clear();
        assertEquals(0, testManager.getDistributionCenters().size());

        //adding should result in gaining one DistributionCenter more each time
        testManager.addDistributionCenter(disC1);
        assertEquals(1, testManager.getDistributionCenters().size());
        testManager.addDistributionCenter(disC2);
        assertEquals(2, testManager.getDistributionCenters().size());
    }

    /**
     * Test if adding CarrierDrone works as intended
     */
    @Test
    void addCarrierDrone() {
        testManager.getCarrierDrones().clear();
        assertEquals(0, testManager.getCarrierDrones().size());

        testManager.addCarrierDrone(cd01);
        assertEquals(1, testManager.getCarrierDrones().size());
        testManager.addCarrierDrone(cd02);
        assertEquals(2, testManager.getCarrierDrones().size());
    }

    /**
     * Test if termination process works as intended
     */
    @Test
    void terminate() {
        testManager.terminate();

        assertFalse(testManager.isRunning());

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            //ignore just assert everything was shutdown
        }

        assertTrue(testManager.schedulerShutdown());
        assertTrue(testManager.distributionCenterPoolShutdown());

    }

    /**
     * test if all order will be shipped
     */
    @Test
    void run() {
        //add orders to DB
        addOrdersToDB(20);


        //setup test pool
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        pool.execute(testManager);

        //wait for full delivery
        try {
            sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DemoDB.getInstance().printOrders();


        //no facility should have orders left
        assertEquals(0, testManager.getGroupedOrders().size());
        assertTrue(disC1.getPendingOrders().isEmpty());
        assertTrue(disC2.getPendingOrders().isEmpty());

        //all orders in DB should have the delivered State
        Map<Integer, Order> orders = DemoDB.getInstance().getOrderTable();
        for(int i : DemoDB.getInstance().getOrderTable().keySet()){
            assertEquals(OrderStatus.DELIVERED, orders.get(i).getStatus());
        }


    }

    /**
     * Adds specified amount of orders to each facility present in testing
     * @param sizeEach the amount of orders Added MAX 99
     */
    private void addOrdersToDB(int sizeEach){
        if(sizeEach > 99){
            throw new IllegalArgumentException("sizeEach is to high (99 is MAX value)");
        }
        Address demo;
        for(int i = 0; i < sizeEach; i++){
            demo = new Address(Continent.EU, 1, "IN " + Continent.EU);
            DemoDB.getInstance().add(new Order(i, demo, OrderStatus.PACKAGED, false));
            DemoDB.getInstance().add(new Order(-i, demo, OrderStatus.PACKAGED, true));

            demo = new Address(disC1.getLocation(), 1, "IN " + disC1.getLocation());
            DemoDB.getInstance().add(new Order(100+i, demo, OrderStatus.PACKAGED, false));

            demo = new Address(disC2.getLocation(), 1, "IN " + disC2.getLocation());
            DemoDB.getInstance().add(new Order(200+i, demo, OrderStatus.PACKAGED, false));
        }
    }
}