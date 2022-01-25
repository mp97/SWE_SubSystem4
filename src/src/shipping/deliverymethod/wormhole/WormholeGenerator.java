package src.shipping.deliverymethod.wormhole;

import src.database.DemoDB;
import src.shipping.order.Order;
import src.shipping.order.OrderStatus;



public class WormholeGenerator {

    //constructor
    public WormholeGenerator() {
        super();
    }

    /**
     * Send an order via the WormholeGenerator
     * @param order the order to be sent
     * @throws InterruptedException if the gets interrupted
     */
    public void sendOrder(Order order) throws InterruptedException {
        DemoDB.getInstance().getOrderTable().get(order.getOID()).setStatus(OrderStatus.DELIVERED);
        System.out.println("Order: " + order + " has been Sent via WormholeGenerator.");
    }
}
