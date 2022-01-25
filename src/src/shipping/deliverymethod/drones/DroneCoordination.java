package src.shipping.deliverymethod.drones;

import src.exceptions.DroneException;
import src.shipping.order.Order;

import java.util.List;

/**
 * Coordinate the shipping of the orders via the drone system
 * it decides how and when CarrierDrones are loaded
 * Used by the ShippingManager (main facility) and the DistributionCenters (secondary facilities)
 */
public class DroneCoordination {


    /**
     * Coordinates the various CarrierDrones of the different facilities with their respective orders
     * Goes over each CarrierDrone and tries to load it as long as there are orders to load onto it
     * @param carrierDrones the CarrierDrones of the facility calling this method
     * @param orders the pending orders of the facility calling this method
     */
    public static void carrierDronesHandling(List<CarrierDrone> carrierDrones, List<Order> orders) throws DroneException {
        if(!orders.isEmpty()){
            for(CarrierDrone cd : carrierDrones){
                if(cd.isDocking){
                    while(!orders.isEmpty() && cd.orders.size() < cd.capacity){
                        cd.load(orders.get(0));
                        orders.remove(0);
                    }
                }
            }
        }
    }

    /**
     * Coordinates all the various Drones types of the different facilities with their respective orders
     * Goes over each CarrierDrone completes the loading process and then maks all DeliveryDrones finish the delivery
     * @param carrierDrones the CarrierDrones of the facility calling this method
     * @param orders the pending orders of the facility calling this method
     */
    public static void completeHandling(List<CarrierDrone> carrierDrones, List<Order> orders) throws DroneException {
        while(!orders.isEmpty()){
            //fills the carrier drones
            carrierDronesHandling(carrierDrones,orders);
            for(CarrierDrone cd : carrierDrones){
                //run until CarrierDrone are idling
                while (!cd.isDocking){
                    //makes the CarrierDrones distribute among the DeliveryDrones
                    cd.deliver();
                    for(DeliveryDrone dd : cd.getDeliveryDrones()){
                        //makes all the DeliveryDrones finish their delivery
                        dd.deliver();
                    }
                }
            }
        }
    }
}
