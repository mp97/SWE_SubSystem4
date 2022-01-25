package src.database;


import src.shipping.order.Order;
import src.warehouse.item.Ingredient;
import src.warehouse.item.Package;
import src.warehouse.item.Part;

import java.util.HashMap;
import java.util.Map;

/**
 * Pseudo Database used for demo of the Subsystem
 */
public class DemoDB{

    //tables
    private final Item_Table<Ingredient> ingredientTable;
    private final Item_Table<Package> packageTable;
    private final Item_Table<Part> partTable;


    private final Map<Integer, Order> orderTable;

    //singleton
    private static DemoDB instance = null;
    public static DemoDB getInstance(){
        if(instance == null){
            instance = new DemoDB();
        }
        return instance;
    }

    //constructer
    private DemoDB(){
        ingredientTable = new Item_Table<>();
        packageTable = new Item_Table<>();
        partTable = new Item_Table<>();

         orderTable = new HashMap<>();
    }

    /**
     * Delete instance of DemoDB
     */
    public void clearDB(){
        if(getInstance() != null){
            instance = null;
        }
    }


    //adds to DB
    public boolean add(Ingredient ingredient){
        return ingredientTable.add(ingredient);
    }
    public boolean add(Ingredient ingredient, int threshold){
        ingredientTable.add(ingredient);
        ingredientTable.setThreshold(ingredient.getIID(), threshold);
        return true;
    }

    public boolean add(Package pac){
        return packageTable.add(pac);
    }
    public boolean add(Package pac, int threshold){
        packageTable.add(pac);
        packageTable.setThreshold(pac.getIID(), threshold);
        return true;
    }

    public boolean add(Part part){
        return partTable.add(part);
    }
    public boolean add(Part part, int threshold){
        partTable.add(part);
        partTable.setThreshold(part.getIID(), threshold);
        return true;
    }

    public boolean add(Order order){
        int id = order.getOID();

        if(orderTable.containsKey(id)){
            return false;
        }
        orderTable.put(id, order);
        return true;
    }

    //deletes
    public boolean deleteIngredient(int id){
        return ingredientTable.remove(id);
    }

    public boolean deletePackage(int id){
        return packageTable.remove(id);
    }

    public boolean deletePart(int id){
        return partTable.remove(id);
    }

    public boolean deleteOrder(int id){
        if(orderTable.containsKey(id)){
            orderTable.remove(id);
            return true;
        }
        return false;
    }

    //updates
    public boolean update(Ingredient ingredient){
        return ingredientTable.update(ingredient);
    }

    public boolean update(Package pac){
        return packageTable.update(pac);
    }

    public boolean update(Part part){
        return partTable.update(part);
    }

    public boolean update(Order order){
        int id = order.getOID();

        if(orderTable.containsKey(id)){
            orderTable.remove(id);
            orderTable.put(id, order);
            return true;
        }

        return false;
    }

    //getter
    public Item_Table<Ingredient> getIngredientTable() {
        return ingredientTable;
    }

    public Item_Table<Package> getPackageTable() {
        return packageTable;
    }

    public Item_Table<Part> getPartTable() {
        return partTable;
    }

    public Map<Integer, Order> getOrderTable() {
        return orderTable;
    }


    //prints
    public void printOrders(){
        for (int i : orderTable.keySet()) {
            System.out.println(orderTable.get(i));
        }
    }

    public void printItems(){
        System.out.println("Items in BD:");
        System.out.println("Table: Ingredients");
        System.out.println(ingredientTable);
        System.out.println("Table: Package");
        System.out.println(packageTable);
        System.out.println("Table: Part");
        System.out.println(partTable);
    }




}

