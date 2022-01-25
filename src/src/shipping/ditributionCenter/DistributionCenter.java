package src.shipping.ditributionCenter;

import src.common.TaskRequirements;
import src.exceptions.DroneException;
import src.shipping.deliverymethod.drones.CarrierDrone;
import src.shipping.deliverymethod.drones.DroneCoordination;
import src.shipping.order.Continent;
import src.shipping.order.Order;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * A secondary type of facility used by the Shipping Manager to handle shipping abroad
 */
public class DistributionCenter extends TaskRequirements implements Runnable{

    //a list of pending orders the DistributionCenter still has to process
    private final List<Order> pendingOrders;
    //a list of large CarrierDrones associated with the DistributionCenter
    private final List<CarrierDrone> carrierDrones;
    //the location the DistributionCenter is based in
    private final Continent location;

    /**
     * Constructor
     * @param location the location the DistributionCenter is based in
     */
    public DistributionCenter(Continent location) {
        this.pendingOrders = new ArrayList<>();
        this.carrierDrones = new ArrayList<>();
        this.location = location;
    }

    /**
     * Loads all the pending orders onto the associated CarrierDrones
     */
    @Override
    public void run() {
        while (this.isRunning()){
            //just to slow it down
            try {
                sleep(100);
            } catch (InterruptedException e) {
                //ignore
            }

            //actual delivery
            try {
                DroneCoordination.completeHandling(carrierDrones, pendingOrders);
            } catch (DroneException e) {
                e.printStackTrace();
            }

            //clear pending Orders
            pendingOrders.clear();
        }
    }

    /**
     * Adds a new carrier Drone to the DistributionCenter
     * @param cd The CarrierDrone to be added
     */
    public void addCarrierDrone(CarrierDrone cd){
        carrierDrones.add(cd);
    }

    /**
     * Receives the Orders from the ShippingManager (main facility)
     * @param orders the orders we received / have to send
     */
    public void receiveOrders(List<Order> orders){
        this.pendingOrders.addAll(orders);
    }

    //getter and setter
    public Continent getLocation() {
        return location;
    }

    public List<Order> getPendingOrders() {
        return pendingOrders;
    }

    public List<CarrierDrone> getCarrierDrones() {
        return carrierDrones;
    }
}
