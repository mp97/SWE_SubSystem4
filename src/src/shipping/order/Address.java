package src.shipping.order;

/**
 * An address, needed as a delivery information
 */
public class Address {

    //fields
    private final Continent region;
    private final int area;
    private final String address;

    //constructor
    public Address(Continent region, int area, String address) {
        this.region = region;
        this.area = area;
        this.address = address;
    }

    //to String
    @Override
    public String toString() {
        return "Region: " + region + ", Area: " + area + ", Address: " +address;
    }

    //getter
    public Continent getRegion() {
        return region;
    }

    public int getArea() {
        return area;
    }

    public String getAddress() {
        return address;
    }
}
