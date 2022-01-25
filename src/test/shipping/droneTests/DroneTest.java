package test.shipping.droneTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.exceptions.DroneException;
import src.shipping.deliverymethod.drones.DeliveryDrone;
import src.shipping.order.Address;
import src.shipping.order.Order;
import src.shipping.order.OrderStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DroneTest {

    DeliveryDrone testDrone;
    List<Order> orders;

    @BeforeEach
    void setUp() {
        testDrone = new DeliveryDrone(1, 4);
        orders = new ArrayList<>();
    }

    /**
     * Loads the done order by order, but always valid
     */
    @Test
    void loadValidSingle() throws DroneException {
        Address demo = new Address(null, 1, "DEMO");

        Order order = new Order(1, demo, OrderStatus.IN_DELIVERY, false);
        testDrone.load(order);
        assertEquals(1, testDrone.getOrders().size());

        order = new Order(2, demo, OrderStatus.IN_DELIVERY, false);
        testDrone.load(order);
        assertEquals(2, testDrone.getOrders().size());

        order = new Order(3, demo, OrderStatus.IN_DELIVERY, false);
        testDrone.load(order);
        assertEquals(3, testDrone.getOrders().size());
    }

    /**
     * loads the drone multiple at a time, but always valid
     */
    @Test
    void loadValidMultiple() throws DroneException {
        Address demo = new Address(null, 1, "DEMO");
        Order order = new Order(1, demo, OrderStatus.IN_DELIVERY, false);
        Order order2 = new Order(2, demo, OrderStatus.IN_DELIVERY, false);
        Order order3 = new Order(3, demo, OrderStatus.IN_DELIVERY, false);
        Order order4 = new Order(4, demo, OrderStatus.IN_DELIVERY, false);

        orders.add(order);
        orders.add(order2);
        testDrone.load(orders);
        assertEquals(2, testDrone.getOrders().size());

        orders.clear();
        orders.add(order3);
        orders.add(order4);
        testDrone.load(orders);
        assertEquals(4, testDrone.getOrders().size());

    }

    /**
     * overloads th drone to cause an exception
     * includes both valid and invalid single/multi insertions
     */
    @Test
    void loadInvalid() throws DroneException {
        Address demo = new Address(null, 1, "DEMO");
        Order order = new Order(1, demo, OrderStatus.IN_DELIVERY, false);
        Order order2 = new Order(2, demo, OrderStatus.IN_DELIVERY, false);
        Order order3 = new Order(3, demo, OrderStatus.IN_DELIVERY, false);
        Order order4 = new Order(4, demo, OrderStatus.IN_DELIVERY, false);
        Order order5 = new Order(5, demo, OrderStatus.IN_DELIVERY, false);

        //valid
        orders.add(order);
        orders.add(order2);
        orders.add(order3);
        testDrone.load(orders);

        //invalid
        orders.clear();
        orders.add(order4);
        orders.add(order5);
        assertThrows(DroneException.class, ()->{
            testDrone.load(orders);
        });

        //valid
        testDrone.load(order4);

        //invalid
        assertThrows(DroneException.class, ()->{
            testDrone.load(order5);
        });



    }
}