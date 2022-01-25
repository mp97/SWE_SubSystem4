package src.warehouse.item;

import java.util.ArrayList;
import java.util.List;

public class Part extends Item {

    private final List<Object> compatibleMachines;

    /**
     * The constructor of the Item Class
     *
     * @param IID         Item id
     * @param name        name of the Item
     * @param description a brief description of the item
     * @param weight      the weight of the Item
     * @param price       the price of the item
     */
    public Part(int IID, String name, String description, double weight, double price) {
        super(IID, name, description, weight, price);

        compatibleMachines = new ArrayList<>();
    }

    /**
     * Adds a compatible machine to the list
     */
    private void addCompatibleMachine(Object machine){
        compatibleMachines.add(machine);
    }

    /**
     * Getter
     */
    private List<Object> getCompatibleMachines(){
        return compatibleMachines;
    }

    @Override
    public String toString() {
        return super.toString() + "\ncompatible with [" +
                 compatibleMachines + "]";
    }
}
