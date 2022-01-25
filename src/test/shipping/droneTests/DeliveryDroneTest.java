package test.shipping.droneTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.database.DemoDB;
import src.exceptions.DroneException;
import src.shipping.deliverymethod.drones.DeliveryDrone;
import src.shipping.order.Address;
import src.shipping.order.Order;
import src.shipping.order.OrderStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryDroneTest {

    DeliveryDrone testDrone;
    List<Order> orders;

    @BeforeEach
    void setUp() {
        testDrone = new DeliveryDrone(1, 4);
        orders = new ArrayList<>();
    }


    /**
     * Test if the delivery works as intended
     */
    @Test
    void deliver() throws DroneException {
        Address demo = new Address(null, 1, "DEMO");
        Order order = new Order(1, demo, OrderStatus.IN_DELIVERY, false);
        Order order2 = new Order(2, demo, OrderStatus.IN_DELIVERY, false);
        Order order3 = new Order(3, demo, OrderStatus.IN_DELIVERY, false);
        Order order4 = new Order(4, demo, OrderStatus.IN_DELIVERY, false);

        DemoDB db = DemoDB.getInstance();

        db.add(order);
        db.add(order2);
        db.add(order3);
        db.add(order4);

        orders.add(db.getOrderTable().get(1));
        orders.add(db.getOrderTable().get(2));
        orders.add(db.getOrderTable().get(3));
        orders.add(db.getOrderTable().get(4));
        testDrone.load(orders);

        //assert orders were added to drone
        assertEquals(4, testDrone.getOrders().size());
        assertFalse(testDrone.isDocking());

        //assert orders were removed from drone
        testDrone.deliver();
        assertEquals(0, testDrone.getOrders().size());
        assertTrue(testDrone.isDocking());

        //assert order status in Database was changed
        assertEquals(OrderStatus.DELIVERED, db.getOrderTable().get(1).getStatus());
        assertEquals(OrderStatus.DELIVERED, db.getOrderTable().get(1).getStatus());
        assertEquals(OrderStatus.DELIVERED, db.getOrderTable().get(1).getStatus());
        assertEquals(OrderStatus.DELIVERED, db.getOrderTable().get(1).getStatus());



    }
}