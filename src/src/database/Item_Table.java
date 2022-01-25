package src.database;


import src.warehouse.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * A Table containing subclasses of Ingredient used for the pseudo Database
 * @param <T> the entries Table
 */
public class Item_Table<T extends Item> {

    private final Map<Integer, TableEntry> content;

    public Item_Table() {
        super();
        content = new HashMap<>();
    }

    /**
     * Adds a new entry to the Table
     * @param newEntry the new element to be added
     * @return if operation was successful
     */
    public boolean add(T newEntry){
        int id = newEntry.getIID();

        if(content.containsKey(id)){
            return false;
        }
        content.put(id, new TableEntry(newEntry));
        return true;
    }

    /**
     * Removes an entry from the Table
     * @param id the id of the entry to be removed
     * @return if operation was successful
     */
    public boolean remove(int id){
        if(content.containsKey(id)){
            content.remove(id);
            return true;
        }
        return false;
    }

    /**
     * Updates an entry from the Table
     * @param updatedEntry the entry with updated values
     * @return if operation was successful
     */
    public boolean update(T updatedEntry){
        int id = updatedEntry.getIID();

        if(content.containsKey(id)){
            TableEntry temp = new TableEntry(updatedEntry);
            temp.amount = content.get(id).amount;
            temp.threshold = content.get(id).threshold;

            content.remove(id);
            content.put(id, temp);

            return true;
        }
        return false;
    }

    /**
     * Gets the size of the Table
     * @return number of elements contained in table
     */
    public int size(){
        return content.size();
    }

    /**
     * Gets the current amount of a specified Item
     * @param id the ID of the Item in question
     */
    public int getAmount(int id){
        return content.get(id).amount;
    }

    /**
     * Change the value amount of a Table Entry
     * @param id the ID of the Item in question
     * @param diff the change to amount
     * @return if operation was successful
     */
    public boolean setAmount(int id, int diff){
        if(content.containsKey(id)){
            content.get(id).amount += diff;
            return true;
        }
        return false;
    }

    /**
     * Gets the threshold of a specified Item
     * @param id the ID of the Item in question
     */
    public int getThreshold(int id){
        return content.get(id).threshold;
    }

    /**
     * Change the value threshold of a Table Entry
     * @param id the ID of the Item in question
     * @param diff the change to threshold
     * @return if operation was successful
     */
    public boolean setThreshold(int id, int diff){
        if(content.containsKey(id)){
            content.get(id).threshold += diff;
            return true;
        }
        return false;
    }

    /**
     * Gets a copy the specified Item
     * @param id the ID of the Item in question
     */
    public T getEntry(int id){
        return content.get(id).entry;
    }

    /**
     * Gets the whole content
     * @return the content itself
     */
    public Map<Integer, TableEntry> getAll(){
        return content;
    }


    /**
     * Prints a visual representation of the Table
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i : content.keySet()){
            sb.append(content.get(i) + "\n");
        }
        return sb.toString();
    }


    /**
     * Internal class that gets actually used by Item_Table
     */
    private class TableEntry{
        private T entry;
        private int amount;
        private int threshold;

        public TableEntry(T entry) {
            this.entry = entry;
            this.amount = 0;
            this.threshold = 0;
        }

        @Override
        public String toString() {
            return entry +
                    ", amount=" + amount +
                    ", threshold=" + threshold;
        }
    }
}
