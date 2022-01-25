package src.warehouse.storageArea;

import src.exceptions.StorageAreaException;
import src.warehouse.item.Item;

public class GeneralArea extends StorageArea<Item> {

    //the amount of items in this StorageArea
    private int stock;

    //constructor: 1
    public GeneralArea(Item designated, int capacity) {
        super(designated, capacity);
    }

    //constructor: 2
    public GeneralArea(int capacity) {
        super(capacity);
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void deposit(Item item) throws StorageAreaException {
        if (state.equals(AreaState.EMPTY)) {
            designated = item;
            state = AreaState.NORMAL;
            //stock++;
        } else if(state.equals(AreaState.FREEZE) || state.equals(AreaState.FLUSH)){
            throw new StorageAreaException("StorageArea state: " + state);
        }else if(!(designated.getIID() == item.getIID())){
            throw new StorageAreaException("Wrong Item Type!");
        } else if(stock + 1 > capacity){
            throw new StorageAreaException("StorageArea full!");
        }
        stock++;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public Item take() throws StorageAreaException {
        if(stock == 0){
            throw new StorageAreaException("StorageArea empty!");
        } else{
            stock--;
            postRemoveCheck();
            return designated;
        }
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void postRemoveCheck() {
        if(stock == 0){
            state = AreaState.EMPTY;
        }
    }

    @Override
    public String toString() {
        return String.format("StorageArea holding %3d of: %10s.", stock, designated);
    }

    //getter
    @Override
    public int getStock() {
        return stock;
    }
}
