package src.shipping.order;

import java.io.Serializable;

/**
 * The actual object to used in shipping
 */
public class Order implements Serializable {

    //fields
    private int OID;
    private Address address;
    private OrderStatus status;
    private boolean wormholeDelivery;

    //constructor
    public Order(int OID, Address address, OrderStatus status, boolean isWormhole) {
        this.OID = OID;
        this.address = address;
        this.status = status;
        this.wormholeDelivery = isWormhole;
    }

    //getter and setter
    public int getOID() {
        return OID;
    }

    public void setOID(int OID) {
        this.OID = OID;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public boolean isWormholeDelivery() {
        return wormholeDelivery;
    }

    public void setWormholeDelivery(boolean wormholeDelivery) {
        this.wormholeDelivery = wormholeDelivery;
    }

    @Override
    public String toString() {
        return "Order{" +
                "OID=" + OID +
                ", address=" + address +
                ", status=" + status +
                ", wormholeDelivery=" + wormholeDelivery +
                '}';
    }
}
