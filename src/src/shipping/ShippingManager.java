package src.shipping;

import src.common.TaskRequirements;
import src.database.DemoDB;
import src.exceptions.DroneException;
import src.shipping.deliverymethod.drones.CarrierDrone;
import src.shipping.deliverymethod.drones.DroneCoordination;
import src.shipping.deliverymethod.wormhole.WormholeGenerator;
import src.shipping.ditributionCenter.DistributionCenter;
import src.shipping.order.Continent;
import src.shipping.order.Order;
import src.shipping.order.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * The ShippingManager is responsible for taking all the orders that are ready to be shipped
 * and then sends them off to their destination.
 */
public class ShippingManager extends TaskRequirements implements Runnable {

    //grouped orders
    private final List<Order> groupedOrders;

    //carrier Drones
    private final List<CarrierDrone> carrierDrones;

    //Wormhole-Generator
    private final WormholeGenerator wormholeGenerator;

    //task running once each set amount
    private final ScheduledExecutorService scheduler;
    private final int INTERVALL = 3000;

    //DistributionCenter tasks
    private final List<DistributionCenter> distributionCenters;
    private final ThreadPoolExecutor DistributionCenterPool;

    //Constructor
    public ShippingManager(){

        groupedOrders = new ArrayList<>();
        wormholeGenerator = new WormholeGenerator();
        carrierDrones = new ArrayList<>();
        distributionCenters = new ArrayList<>();

        //schedule grouping Task
        this.scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new sendPeriodically(),
                INTERVALL, INTERVALL, TimeUnit.MILLISECONDS);

        //setup DistributionCenter pool
        DistributionCenterPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    }

    /**
     * Adds a new DistributionCenter to the system
     * and runs the DistributionCenter tasks
     * @param disC the newly added DistributionCenter
     */
    public void addDistributionCenter(DistributionCenter disC){
        distributionCenters.add(disC);
        DistributionCenterPool.execute(disC);
    }


    /**
     * Adds a new carrier Drone to the main building managed directly by the ShippingManager
     * @param cd The CarrierDrone to be added
     */
    public void addCarrierDrone(CarrierDrone cd){
        carrierDrones.add(cd);
    }

    /**
     * Terminates the ShippingManager, the periodically running task
     * an all the treads of the subordinated DistributionCenters
     */
    @Override
    public void terminate(){
        //shutdown for ShippingManager task
        super.terminate();

        //shutdown of grouping task
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException ex) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        //shutdown of DistributionCenter tasks and their respective pool
        for(DistributionCenter disC : distributionCenters){
            //shutdown the centers
            disC.terminate();
        }
        //shutdown the pool
        DistributionCenterPool.shutdown();
        try {
            if (!DistributionCenterPool.awaitTermination(1, TimeUnit.SECONDS)) {
                DistributionCenterPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            DistributionCenterPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }


    /**
     * Manages all the orders with the status Prepared
     * All orders that require wormhole delivery will be sent of immediately,
     * Orders that go to a DistributionCenter will be sent there and the remaining ones will be grouped to be sent all at once later
     */
    @Override
    public void run() {
        while (this.isRunning()){
            //needs to sleep
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //check order DB orders with status PREPARED
            DemoDB db = DemoDB.getInstance();
            Map<Integer, Order> orderTable = db.getOrderTable();
            List<Integer> keys = new ArrayList<>();
            for(int i : orderTable.keySet()){
                if(orderTable.get(i).getStatus().equals(OrderStatus.PACKAGED)){
                    keys.add(i);
                    //change Status of selected orders to IN_DELIVERY
                    orderTable.get(i).setStatus(OrderStatus.IN_DELIVERY);
                }
            }

            //send wormhole orders
            for(int key : keys){
                Order order = orderTable.get(key);
                if(order.isWormholeDelivery()){
                    //send all wormhole deliveries instantly
                    try {
                        wormholeGenerator.sendOrder(order);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Continent location = order.getAddress().getRegion();
                    if(location.equals(Continent.EU)){
                        groupedOrders.add(order);
                    } else { //send to DistributionCenters
                        for(DistributionCenter disC : distributionCenters){
                            if(location.equals(disC.getLocation())){
                                List<Order> disCOrders = new ArrayList<>();
                                disCOrders.add(order);
                                disC.receiveOrders(disCOrders);
                                break;
                            }
                        }

                    }
                }
            }

        }
    }

    /**
     * private class that periodically loads all the grouped order onto the CarrierDrones
     *
     */
    private class sendPeriodically implements Runnable{

        public sendPeriodically() {
        }

        /**
         * periodically loads all the grouped order onto the CarrierDrones
         */
        @Override
        public void run() {

            //send remaining grouped orders
            try {
                DroneCoordination.completeHandling(carrierDrones, groupedOrders);
            } catch (DroneException e) {
                e.printStackTrace();
            }

            //clear Grouped orders
            groupedOrders.clear();
        }
    }

    //getters
    public List<Order> getGroupedOrders() {
        return groupedOrders;
    }

    public List<CarrierDrone> getCarrierDrones() {
        return carrierDrones;
    }

    public List<DistributionCenter> getDistributionCenters() {
        return distributionCenters;
    }

    public boolean schedulerShutdown(){
        return scheduler.isShutdown();
    }


    public boolean distributionCenterPoolShutdown(){
        return DistributionCenterPool.isShutdown();
    }

}
