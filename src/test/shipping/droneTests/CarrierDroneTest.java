package test.shipping.droneTests;

import org.junit.jupiter.api.AfterAll;
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

import static org.junit.jupiter.api.Assertions.*;

class CarrierDroneTest {

    CarrierDrone testDrone;
    static DistributionCenter dc;

    @BeforeEach
    void setUp() throws DroneException {
        dc = new DistributionCenter(Continent.NA);
        testDrone = new CarrierDrone(1, dc, null);
        testDrone.assignDrones(new DeliveryDrone(2, 2));
        testDrone.assignDrones(new DeliveryDrone(3, 5));
        testDrone.assignDrones(new DeliveryDrone(4, 9));
    }

    @AfterAll
    static void tearDown() {
        dc.terminate();
    }

    /**
     * Test if the assigment process for the subordinate deliveryDrones works as intended
     */
    @Test
    void assign() throws DroneException {

        //assign one below the drone limit
        int runs = testDrone.getDroneSpace()-testDrone.getDeliveryDrones().size()-1;
        for(int i = 0; i < runs; i++){
            testDrone.assignDrones(new DeliveryDrone(-i, 4));
        }
        assertEquals(testDrone.getDroneSpace()-1, testDrone.getDeliveryDrones().size());

        //assign exactly the drone limit
        testDrone.assignDrones(new DeliveryDrone(Integer.MIN_VALUE, 4));
        assertEquals(testDrone.getDroneSpace(), testDrone.getDeliveryDrones().size());

        //go over the drone limit
        assertThrows(DroneException.class, ()->{
            testDrone.assignDrones(new DeliveryDrone(Integer.MAX_VALUE, 4));
        });

        //check that assigment process has worked in both directions
        for(DeliveryDrone dd : testDrone.getDeliveryDrones()){
            assertEquals(testDrone, dd.getCarrierDrone());
        }
    }

    /**
     * Test if the distribution of items between the subordinate deliveryDrones works as intended
     */
    @Test
    @SuppressWarnings("Duplicates")
    void deliver() throws DroneException {
        //fully load the drone
        Address demo = new Address(null, 1, "DEMO");
        for(int i = 0; i < testDrone.getCapacity(); i++){
            testDrone.load(new Order(i, demo, OrderStatus.IN_DELIVERY, false));
        }
        assertEquals(testDrone.getCapacity(), testDrone.getOrders().size());

        //run deliver to see how it behaved (case: all drones can be fully field in this run)
        testDrone.deliver();
        for(DeliveryDrone dd : testDrone.getDeliveryDrones()){
           assertEquals(dd.getCapacity(), dd.getOrders().size());
        }

        //empty the drones and run delivery again
        for(DeliveryDrone dd : testDrone.getDeliveryDrones()){
            dd.deliver(); //empty DeliveryDrones / make dem deliver themselves
            assertEquals(0, dd.getOrders().size());
        }
        testDrone.deliver();
        for(DeliveryDrone dd : testDrone.getDeliveryDrones()){
            assertEquals(dd.getCapacity(), dd.getOrders().size());
        }

        //empty again
        for(DeliveryDrone dd : testDrone.getDeliveryDrones()){
            dd.deliver(); //empty DeliveryDrones / make dem deliver themselves
            assertEquals(0, dd.getOrders().size());
        }

        //run delivery again but this time add a non drone that can't possibly be filled
        //i.e. drones capacity is higher than the CarrierDrone one's
        //so this time at least one drone has to be only partially full
        testDrone.assignDrones(new DeliveryDrone(5, testDrone.getCapacity()));
        testDrone.deliver();
        for(DeliveryDrone dd : testDrone.getDeliveryDrones()){
            assertTrue(dd.getOrders().size() <= dd.getCapacity());
        }

        //empty again
        for(DeliveryDrone dd : testDrone.getDeliveryDrones()){
            dd.deliver(); //empty DeliveryDrones / make dem deliver themselves
            assertEquals(0, dd.getOrders().size());
        }

        //check if CarrierDrone is empty
        assertTrue(testDrone.isDocking());

    }

    /**
     * Generates a bunch of orders for testing
     * @return a list of the generated orders
     */
    private List<Order> generateOrders() throws DroneException {
        Address testAddress = new Address(Continent.NA, 0, "Test Address");
        List<Order> orders = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            orders.add(new Order(i, testAddress, OrderStatus.IN_DELIVERY, false));
        }
        return orders;
    }


}