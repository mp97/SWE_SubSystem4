package src.warehouse.item;

/**
 * The base class the Item category
 */
public class Item{

    //variables
    private int IID;
    private String name;
    private String description;
    private double weight;
    private double price;

    /**
     * The constructor of the Item Class
     * @param IID Item id
     * @param name name of the Item
     * @param description a brief description of the item
     * @param weight the weight of the Item
     * @param price the price of the item
     */
    public Item(int IID, String name, String description, double weight, double price) {
        this.IID = IID;
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.price = price;
    }


    //getter and setter
    public int getIID() {
        return IID;
    }

    public void setIID(int IID) {
        this.IID = IID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Item[" +
                "IID=" + IID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", weight=" + weight +
                ", price=" + price +
                ']';
    }

}
