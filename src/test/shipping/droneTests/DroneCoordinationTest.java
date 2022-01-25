package test.shipping.droneTests;

import org.junit.jupiter.api.*;
import src.exceptions.DroneException;
import src.shipping.deliverymethod.drones.CarrierDrone;
import src.shipping.deliverymethod.drones.DeliveryDrone;
import src.shipping.deliverymethod.drones.DroneCoordination;
import src.shipping.ditributionCenter.DistributionCenter;
import src.shipping.order.Address;
import src.shipping.order.Continent;
import src.shipping.order.Order;
import src.shipping.order.OrderStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DroneCoordinationTest {

    static final int ORDERS = 1500;
    static final int CARRIER_DRONES = 5;
    static final int DELIVERY_DRONES = 5;
    static final int DELIVERY_DRONES_CAPACITY = 10;

    static List<Order> orders;
    static List<CarrierDrone> carrierDrones;

    @BeforeEach
    void setUp() throws DroneException {
        orders = new ArrayList<>();
        carrierDrones = new ArrayList<>();

        generateOrders(ORDERS);
        generateCarrierDrones();

        //generate DeliveryDrones
        for(CarrierDrone cd : carrierDrones){
            generateDeliveryDrones(cd);
        }
    }

    /**
     * In multiple phases test if the order distribution across the CarrierDrones is correct
     * @throws DroneException if a CarrierDrone gets loaded to much
     */
    @Test
    void carrierDronesHandling() throws DroneException {
        //with current amount of orders all CarrierDrone should be full
        DroneCoordination.carrierDronesHandling(carrierDrones, orders);
        for(CarrierDrone cd : carrierDrones){
            assertEquals(cd.getCapacity(),cd.getOrders().size());
        }

        //make all CarrierDrones Deliver to their DeliveryDrones
        //since the DeliveryDrones don't deliver jet there should still be some left in the carrier drones
        int expectedValue = carrierDrones.get(0).getCapacity() - DELIVERY_DRONES * DELIVERY_DRONES_CAPACITY;
        for(CarrierDrone cd : carrierDrones){
            cd.deliver();
            assertEquals(expectedValue, cd.getOrders().size());
        }

        //make all the DeliveryDrones deliver and then make the CarrierDrones redistribute among the DeliveryDrones
        //this should now have all the CarrierDrones empty
        for(CarrierDrone cd : carrierDrones){
            for(DeliveryDrone dd : cd.getDeliveryDrones()){
                dd.deliver();
            }
            cd.deliver();
            assertEquals(0, cd.getOrders().size());
        }

        //for good measure the DeliveryDrone deliver again
        for(CarrierDrone cd : carrierDrones){
            for(DeliveryDrone dd : cd.getDeliveryDrones()){
                dd.deliver();
            }
        }

        //generate only a few Orders
        //now most CarrierDrones should be empty, only the first should be partially filled
        orders = new ArrayList<>();
        int smallOrder = 50;
        generateOrders(smallOrder);
        DroneCoordination.carrierDronesHandling(carrierDrones,orders);
        assertEquals(smallOrder, carrierDrones.get(0).getOrders().size());
        for (int i = 1; i < CARRIER_DRONES; i++){
            assertEquals(0, carrierDrones.get(i).getOrders().size());
        }


    }

    /**
     * Delivers all orders
     * @throws DroneException if any drone gets overfilled
     */
    @Test
    void completeHandling() throws DroneException {

        DroneCoordination.completeHandling(carrierDrones, orders);

        //assert every Order has been delivered
        for(Order order : orders){
            assertEquals(OrderStatus.DELIVERED, order.getStatus());
        }

        //assert that every drone is empty (multiple asserts jus for safety)
        for(CarrierDrone cd : carrierDrones){
            assertEquals(0, cd.getOrders().size());
            assertTrue(cd.isDocking());
            for(DeliveryDrone dd : cd.getDeliveryDrones()){
                assertEquals(0, dd.getOrders().size());
                assertTrue(dd.isDocking());
            }
        }

        //assert that order was delivered
        for(Order order : orders){
            assertEquals(OrderStatus.DELIVERED, order.getStatus());
        }
    }

    /**
     * Generates a lot of Orders to test with
     */
    private void generateOrders(int amount){
        Address demo = new Address(Continent.EU, 1, "Demo Street __");
        for(int i = 0; i < amount; i++){
            orders.add(new Order(i, demo, OrderStatus.IN_DELIVERY, false));
        }
    }

    /**
     * Generates CarrierDrones to test with
     */
    private void generateCarrierDrones(){
        for(int i = 0; i < CARRIER_DRONES; i++){
            carrierDrones.add(new CarrierDrone(i, new DistributionCenter(Continent.RU), null));
        }
    }

    /**
     * Generates DeliveryDrones to test with
     */
    private void generateDeliveryDrones(CarrierDrone cd) throws DroneException {
        for(int i = 0; i < DELIVERY_DRONES; i++){
           cd.assignDrones(new DeliveryDrone(i, DELIVERY_DRONES_CAPACITY));
        }
    }
}