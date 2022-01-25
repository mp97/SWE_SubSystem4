package src.warehouse.item;

import java.time.LocalDate;
import java.util.Comparator;

public class Ingredient extends Item implements Comparable<Ingredient>{

    //variables
    private final LocalDate expiryDate;
    private final LocalDate productionDate;
    private final String productionLot;
    private boolean isGood;

    /**
     * Constructor of Ingredient Class
     * @param IID Item id
     * @param name name of the Item
     * @param description a brief description of the item
     * @param weight the weight of the Item
     * @param price the price of the item
     * @param expiryDate the best before date of the ingredient
     * @param productionDate the production Date of the ingredient
     * @param productionLot the production lot of the ingredient
     */
    public Ingredient(int IID, String name, String description, double weight, double price, LocalDate expiryDate, LocalDate productionDate, String productionLot) {
        super(IID, name, description, weight, price);
        this.expiryDate = expiryDate;
        this.productionDate = productionDate;
        this.productionLot = productionLot;
        this.isGood = true;
    }

    /**
     * Turns an Ingredient back into Item to hide details
     * @return an Item with some field of the Ingredient
     */
    public Item hideDetails(){
        return new Item(this.getIID(), this.getName(),this.getDescription(), this.getWeight(), this.getPrice());
    }

    //getters
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public String getProductionLot() {
        return productionLot;
    }

    public boolean isGood() {
        return isGood;
    }

    /**
     * Flags an ingredient as spoiled
     */
    public void spoil(){
        isGood = false;
    }

    @Override
    public String toString() {
        return super.toString() + " with [" +
                "expiryDate=" + expiryDate +
                ", productionDate=" + productionDate +
                ", productionLot='" + productionLot + '\'' +
                ", isGood=" + isGood +
                ']';
    }

    @Override
    public int compareTo(Ingredient o) {
        //return expiryDate.compareTo(o.getExpiryDate());
        return Comparator.comparing(Ingredient::getExpiryDate)
                .thenComparing(Ingredient::getProductionDate)
                .compare(this, o);
    }
}
