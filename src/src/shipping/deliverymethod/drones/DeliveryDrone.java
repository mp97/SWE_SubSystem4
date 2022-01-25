package src.shipping.deliverymethod.drones;

import src.shipping.order.Order;
import src.shipping.order.OrderStatus;

public class DeliveryDrone extends Drone{

    //the carrier drone to which it docks
    protected CarrierDrone dock;

    //constructor
    public DeliveryDrone(int id, int capacity) {
        super(id, capacity);
        this.identifier = "NOT ASSIGNED " + this.id;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void deliver() {
        while (!orders.isEmpty()){
            Order next = orders.get(0);
            goTo(next.getAddress());
            next.setStatus(OrderStatus.DELIVERED);
            orders.remove(0);
        }
        isDocking = true;
    }

    //getter
    public CarrierDrone getCarrierDrone(){
        return dock;
    }
}
