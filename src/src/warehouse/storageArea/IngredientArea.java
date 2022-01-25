package src.warehouse.storageArea;

import src.exceptions.StorageAreaException;
import src.warehouse.item.Ingredient;

import java.time.LocalDate;
import java.util.*;

public class IngredientArea extends StorageArea<Ingredient>{

    //the Ingredients inside this IngredientArea
    private final SortedSet<SetEntry> stock = new TreeSet<>();

    //constructor 1
    public IngredientArea(Ingredient designated, int capacity) {
        super(designated, capacity);
    }

    //constructor 2
    public IngredientArea(int capacity) {
        super(capacity);
    }

    /**
     * Removes all items that have expired as of now, form the StorageArea
     * @return the amount of Ingredients that hae expired
     */
    public int dalyCheck(){
        int remove = 0;
        List<SetEntry> toDelete = new ArrayList<>();
        for(SetEntry i : stock){
            if(!i.getIngredient().getExpiryDate().isAfter(LocalDate.now()) || !i.getIngredient().isGood()){
                toDelete.add(i);
                remove++;
            }
        }
        stock.removeAll(toDelete);
        return remove;
    }


    /**
     * {@inheritdoc}
     */
    @Override
    public void deposit(Ingredient item) throws StorageAreaException {
        if (state.equals(AreaState.EMPTY)) {
            designated = item;
            state = AreaState.NORMAL;
            //stock.add(item);
        } else if(state.equals(AreaState.FREEZE) || state.equals(AreaState.FLUSH)){
            throw new StorageAreaException("StorageArea state: " + state);
        }else if(!(item.getIID() == designated.getIID())){
            throw new StorageAreaException("Wrong Item Type!");
        } else if(stock.size() +1 > capacity) {
            throw new StorageAreaException("StorageArea full!");
        }

        stock.add(new SetEntry(String.valueOf(System.nanoTime()), item));
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public Ingredient take() throws StorageAreaException {
        if(stock.isEmpty()){
            throw new StorageAreaException("StorageArea empty!");
        } else{

            SetEntry temp = stock.first();
            stock.remove(temp);
            postRemoveCheck();
            return temp.getIngredient();

        }
    }

    /**
     * Shows the earliest Expiry Date present in the Offers StorageArea
     * @return the Date in question
     */
    public LocalDate peakAtFirst(){
        return stock.first().getIngredient().getExpiryDate();
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void postRemoveCheck() {
        if(stock.isEmpty()){
            state = AreaState.EMPTY;
        }
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public String toString() {
        StringBuilder sB = new StringBuilder();
        sB.append(String.format("StorageArea holding %3d of: %10s:\n", stock.size(), designated.hideDetails()));


        for(SetEntry i : stock){
            sB.append(String.format("Expiration date %10s\t",i.getIngredient().getExpiryDate()));
            sB.append(String.format("Production date %10s\t",i.getIngredient().getProductionDate()));
            sB.append(String.format("Production lot %10s\n",i.getIngredient().getProductionLot()));
        }

        return sB.toString();
    }

    /**
     * Pseudo getter shows how many items are in stock,
     * since your not supposed tho get the actual Sorted Set
     * @return the amount of items in stock
     */
    @Override
    public int getStock(){
        return stock.size();
    }

    /**
     * Internal Class used for the sored set to allow duplicates
     */
    private class SetEntry implements Comparable<SetEntry>{

        private String UID;
        private Ingredient ingredient;

        public SetEntry(String UID, Ingredient ingredient) {
            this.UID = UID;
            this.ingredient = ingredient;
        }

        //getters
        public String getUID() {
            return UID;
        }

        public Ingredient getIngredient() {
            return ingredient;
        }

        //methods for sorting by field usd for compareTo
        private LocalDate sort_1st(){
            return ingredient.getExpiryDate();
        }
        private LocalDate sort_2nd(){
            return ingredient.getProductionDate();
        }

        @Override
        public int compareTo(SetEntry o) {
            return Comparator.comparing(SetEntry::sort_1st)
                    .thenComparing(SetEntry::sort_2nd)
                    .thenComparing(SetEntry::getUID)
                    .compare(this, o);
        }
    }
}
