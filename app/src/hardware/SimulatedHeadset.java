/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hardware;

import data.EpochContainer;

/**
 * @author Mathew Allington
 */
public abstract class SimulatedHeadset extends Headset {
    private EpochContainer data;
    private int current = 0;
    private int epochPeriod = 200;
    private boolean loopData = false;

    /**
     * @param data
     */
    public SimulatedHeadset(EpochContainer data) {
        super();
        this.data = data;
    }

    Runnable networkSimulaionThread = new Runnable() {
        @Override
        public void run() {
            while (capturing) {
                try {
                    if (data.size() > 0) {

                        if(data.getEpoch(current).timeElapsed() <= (System.currentTimeMillis()-systemStartTime)){
                            update(data.getEpoch(current++));
                        }



                        if (!(current < data.size()))
                            if (loopData) {
                            current = 0;
                                systemStartTime = System.currentTimeMillis();
                            }
                            else capturing = false;
                    }

                    Thread.sleep(epochPeriod);

                } catch (InterruptedException ex) {
                }

            }
        }
    };

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
        capturing = true;
        systemStartTime = System.currentTimeMillis();
        new Thread(networkSimulaionThread).start();
        return true;
    }

    /**
     *
     */
    @Override
    public void disconnect() {
        capturing = false;
    }

    public void loopData(boolean loop) {
        this.loopData = loop;
    }
}
