package uk.ac.bath.csed_group_11.sleegp.logic.hardware;

import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;

/**
 * @author Mathew Allington
 */
public abstract class SimulatedHeadset extends Headset {
    private EpochContainer data;
    private int current = 0;
    private int epochPeriod = 200;
    private boolean loopData = false;
    Runnable networkSimulaionThread = new Runnable() {
        @Override
        public void run() {
            while (isCapturing() && data != null) {
                try {
                    if (data.size() > 0) {

                        if (data.getEpoch(current).timeElapsed() <= (System.currentTimeMillis() - systemStartTime)) {
                            update(data.getEpoch(current++));
                        }


                        if (!(current < data.size()))
                            if (loopData) {
                                current = 0;
                                systemStartTime = System.currentTimeMillis();
                            } else setCapturing(false);
                    }

                    Thread.sleep(epochPeriod);

                } catch (InterruptedException ex) {
                    System.out.println("File is invalid");
                }

            }

        }
    };

    /**
     * @param data
     */
    public SimulatedHeadset(EpochContainer data) {
        super();
        this.data = data;
    }

    /**
     * @param periodMillis
     */
    public void setEpochPeriod(int periodMillis) {
        epochPeriod = periodMillis;
    }

    /**
     * @return
     */
    @Override
    public boolean capture() {
        setCapturing(true);
        systemStartTime = System.currentTimeMillis();
        new Thread(networkSimulaionThread).start();
        return true;
    }


    @Override
    public void disconnect() {
        setCapturing(false);
    }

    public void loopData(boolean loop) {
        this.loopData = loop;
    }
}
