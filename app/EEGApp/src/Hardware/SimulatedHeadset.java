/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hardware;

import Data.Epoch;
import Data.EpochContainer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mathew Allington
 */
public abstract class SimulatedHeadset extends Headset{
    private EpochContainer data;
    private int current = 0;
    private int epochPeriod = 1000;
    private boolean loopData = false;
    /**
     *
     * @param data
     */
    public SimulatedHeadset(EpochContainer data){
        super();
        this.data = data;
    }
     
     Runnable networkSimulaionThread = new Runnable() {
        @Override
        public void run() {
            while(capturing){
                try {
                    if(data.size()>0){
                        update(data.getEpoch(current++));
                        if(!(current<data.size())) 
                                if(loopData)current = 0; 
                                    else capturing = false;
                    }
                    
                    Thread.sleep(epochPeriod);
                    
                } catch (InterruptedException ex) { }
                
            }
        }
    };
     
    /**
     *
     * @param periodMillis
     */
    public void setEpochPeriod(int periodMillis){
        epochPeriod = periodMillis;
    }
     
    /**
     *
     * @return
     */
    @Override public boolean capture(){
        capturing = true;
        new Thread(networkSimulaionThread).start();
        return true;
    }
    
    /**
     *
     */
    @Override public void disconnect(){
        capturing = false;
    }
    
    public void loopData(boolean loop){
        this.loopData = loop;
    }
}
