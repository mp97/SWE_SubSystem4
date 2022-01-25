package src.warehouse.storageArea;

import src.exceptions.StorageAreaException;
import src.warehouse.item.Item;

public abstract class StorageArea<T extends Item> {

    protected T designated;
    protected int capacity;
    protected AreaState state;

    /**
     * Class constructor when providing a Type of Item and a capacity.
     * @param designated the type of item for which the StorageArea is reserved
     * @param capacity the maximum capacity of the StorageArea
     */
    public StorageArea(T designated, int capacity){
        this.designated = designated;
        this.capacity = capacity;
        this.state = AreaState.EMPTY;
    }

    /**
     * Class constructor when providing ONLY a capacity.
     * @param capacity the maximum capacity of the StorageArea
     */
    public StorageArea(int capacity){
        this.capacity = capacity;
        this.state = AreaState.EMPTY;
    }

    /**
     * Adds an item to the StorageArea if possible
     * @param item the Item in question
     */
    public abstract void deposit(T item) throws StorageAreaException;

    /**
     * Retrieves an item from StorageArea, if it is an ingredient the one with the earliest expiryDate
     */
    public abstract T take() throws StorageAreaException;

    /**
     * Check if a StorageArea was emptied after a removal
     */
    public abstract void postRemoveCheck();

    /**
     * Check if a StorageArea cam be deleted
     * @return true if possible
     */
    public boolean removable(){
        //return state.equals(AreaState.EMPTY) ? true : false;
        return state.equals(AreaState.EMPTY);
    }

    /**
     * Set the StorageArea to the FREEZE state
     * Returns false if AreaState is FREE
     */
    public boolean setToFreeze() {
        if(state.equals(AreaState.EMPTY)){
            return false;
        }
        state = AreaState.FREEZE;
        return true;
    }

    /**
     * Set the StorageArea to the FLUSH state
     * Returns false if AreaState is EMPTY
     */
    public boolean setToFlush(){
        if(state.equals(AreaState.EMPTY)){
            return false;
        }
        state = AreaState.FLUSH;
        return true;
    }

    /**
     * Set the StorageArea to the NORMAL state
     * Returns false if AreaState is EMPTY
     */
    public boolean setToNormal(){
        if(state.equals(AreaState.EMPTY)){
            return false;
        }
        state = AreaState.NORMAL;
        return true;
    }

    //Getters and setters
    public T getDesignated() {
        return designated;
    }

    public void setDesignated(T designated) {
        this.designated = designated;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public AreaState getState() {
        return state;
    }

    abstract public int getStock();

    /**
     * Internal Class AreaState
     * defines the different type of States of the StorageArea
     */
    public enum AreaState {
        NORMAL("StorageArea contains items."),
        FREEZE("StorageArea is set to the FREEZE state."),
        FLUSH("StorageArea is set to the FLUSH state."),
        EMPTY("StorageArea does not contain any items currently.");

        private final String description;

        AreaState(String description){
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
