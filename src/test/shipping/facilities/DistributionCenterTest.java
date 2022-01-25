package test.shipping.facilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.exceptions.DroneException;
import src.shipping.deliverymethod.drones.CarrierDrone;
import src.shipping.deliverymethod.drones.DeliveryDrone;
import src.shipping.ditributionCenter.DistributionCenter;
import src.shipping.order.Address;
import src.shipping.order.Continent;
import src.shipping.order.Order;
import src.shipping.order.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class DistributionCenterTest {

    Continent location = Continent.AS;
    DistributionCenter testCenter;
    ThreadPoolExecutor pool;

    @BeforeEach
    void setUp() {
        testCenter = new DistributionCenter(location);

    }

    @AfterEach
    void tearDown() {
        testCenter.terminate();
    }

    /**
     * Test if all received Orders are actually being shipped
     */
    @Test
    void run() throws DroneException {
        //SETUP POOL
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        pool.execute(testCenter);


        int size = 100;

        //Need drones to actually run
        CarrierDrone cd = new CarrierDrone(1, testCenter, null);
        cd.assignDrones(new DeliveryDrone(2, 10));
        cd.assignDrones(new DeliveryDrone(3, 10));
        testCenter.addCarrierDrone(cd);


        testCenter.receiveOrders(generateOrders(size));
        assertEquals(size, testCenter.getPendingOrders().size());


        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(0, testCenter.getPendingOrders().size());


        //shutdown pool
        pool.shutdown();
        try {
            if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }

    /**
     * Test if adding carrierDrones works as intended
     */
    @Test
    void addCarrierDrone() {
        assertTrue(testCenter.getCarrierDrones().isEmpty());
        testCenter.addCarrierDrone(new CarrierDrone(1, testCenter, null));
        assertEquals(1, testCenter.getCarrierDrones().size());
        testCenter.addCarrierDrone(new CarrierDrone(2, testCenter, null));
        testCenter.addCarrierDrone(new CarrierDrone(3, testCenter, null));
        testCenter.addCarrierDrone(new CarrierDrone(4, testCenter, null));
        testCenter.addCarrierDrone(new CarrierDrone(5, testCenter, null));
        assertEquals(5, testCenter.getCarrierDrones().size());
    }

    /**
     * Test if adding receiving orders works as intended
     */
    @Test
    void receiveOrders() {
        assertTrue(testCenter.getPendingOrders().isEmpty());
        int size = 150;
        testCenter.receiveOrders(generateOrders(size));
        assertEquals(size, testCenter.getPendingOrders().size());
    }

    private List<Order> generateOrders(int size){
        Address demo = new Address(null, 1, "DEMO");
        List<Order> orders = new ArrayList<>();
        for(int i = 0; i < size; i++){
            orders.add(new Order(i, demo, OrderStatus.IN_DELIVERY, false));
        }
        return orders;
    }
}