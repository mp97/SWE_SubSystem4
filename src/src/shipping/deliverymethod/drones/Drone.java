package src.shipping.deliverymethod.drones;

import src.exceptions.DroneException;
import src.shipping.order.Address;
import src.shipping.order.Continent;
import src.shipping.order.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class of the different Drone-Types used by the system
 */
public abstract class Drone {


    //id of the drone
    protected int id;

    //identifier of drone
    protected String identifier;

    //load-capacity of drone
    protected final int capacity;
    //list of orders carried by the drone
    protected final List<Order> orders;
    //current position of drone
    protected Continent location;
    //if drone is currently docked/available
    protected boolean isDocking;

    //constructor
    public Drone(int id, int capacity){
        this.capacity = capacity;
        this.orders = new ArrayList<>();
        this.isDocking = true;
        this.id = id;
    }


    /**
     * Drone will go to specified address
     * @param address the specified address
     */
    public void goTo(Address address){
        System.out.println(identifier + " is going to -> " + address + ".");
    }

    /**
     * Load the drone with all the orders it has to deliver
     * @param orders a list of orders
     */
    public void load(List<Order> orders) throws DroneException {
        if(orders.size() + this.orders.size() > capacity){
            throw new DroneException("Trying to overfill a drone: " + identifier);
        }
        this.orders.addAll(orders);
        isDocking = false;
    }

    /**
     * Load the drone with all the orders it has to deliver
     * @param order a single order
     */
    public void load(Order order) throws DroneException {
        if(orders.size() + 1 > capacity){
            throw new DroneException("Trying to overfill a drone: " + identifier);
        }
        orders.add(order);
        isDocking = false;
    }

    /**
     * Defines the next steps a drone has to take:
     * CarrierDrone: distributes Orders among DeliveryDrones
     * DeliveryDrone: delivers the Orders it has loaded
     */
    public abstract void deliver() throws DroneException;

    //getter
    public List<Order> getOrders() {
        return orders;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public Continent getLocation() {
        return location;
    }

    public boolean isDocking() {
        return isDocking;
    }
}
