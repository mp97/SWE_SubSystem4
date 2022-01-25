package src.shipping.deliverymethod.drones;

import src.exceptions.DroneException;
import src.shipping.ShippingManager;
import src.shipping.ditributionCenter.DistributionCenter;
import src.shipping.order.Continent;

import java.util.ArrayList;
import java.util.List;

public class CarrierDrone extends Drone{
    private static final int CAPACITY = 100;
    private static final int DRONE_SPACE = 10;

    private final List<DeliveryDrone> deliveryDrones;

    private DistributionCenter basis;
    private ShippingManager mainHub;

    public CarrierDrone(int id, DistributionCenter dc, ShippingManager sm) {
        super(id, CAPACITY);
        deliveryDrones = new ArrayList<>();
        if(dc == null && sm != null){
            identifier = "Main Facility CarrierDrone: " + id;
            location = Continent.EU;
        } else {
            identifier = dc.getLocation() + " CarrierDrone: " + id;
            location = dc.getLocation();
        }
    }

    /**
     * Assigns a DeliveryDrone to the CarrierDrone
     * @param drone the DeliveryDrone to be assigned
     */
    public void assignDrones(DeliveryDrone drone) throws DroneException{
        if(deliveryDrones.size() < DRONE_SPACE){
            drone.dock = this;
            deliveryDrones.add(drone);
            drone.identifier = this.identifier + " DeliveryDrone: " + drone.id;
        } else {
            throw new DroneException("MAX DeliveryDrone limit surpassed");
        }
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void deliver() throws DroneException {
        if(!orders.isEmpty()){
            for(DeliveryDrone dd : deliveryDrones){
                if(dd.isDocking){
                    while(!orders.isEmpty() && dd.orders.size() < dd.capacity){
                        dd.load(orders.get(0));
                        orders.remove(0);
                    }
                }
            }
        }

        //must be done separate from if(!orders.isEmpty()) because it could change in the while loop
        if(orders.isEmpty()){
            isDocking = true;
        }

    }

    //getter
    public int getDroneSpace(){
        return DRONE_SPACE;
    }

    public List<DeliveryDrone> getDeliveryDrones() {
        return deliveryDrones;
    }

}
