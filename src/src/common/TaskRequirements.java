package src.common;

public abstract class TaskRequirements {

    boolean running = true;

    /**
     * Sets variable used for termination to false
     */
    public void terminate(){
        running = false;
    }

    /**
     * Getter
     * @return whether the Thread is supposed to run or not
     */
    public boolean isRunning() {
        return running;
    }

}
