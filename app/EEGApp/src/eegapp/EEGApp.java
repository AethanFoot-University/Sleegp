/*
 * 
 */
package eegapp;

import Data.Epoch;
import Data.EpochContainer;
import Hardware.Headset;
import Hardware.SimulatedHeadset;

/** Credits:
 *  @author Allington, Mathew
 *  @author Draper, Tom
 *  @author Foot, Aethan
 *  @author Ito-Low, Alexander
 *  @author Millischer, Christophe
 *  @author Mortensen, Soren
 *  @author Mortimer, Lloyd
 *  @author Sogbesan, Samuel
 *  @author Songthammakul, Ravit 
 *  
 *  Created: 18-02-2019
 *  Copyright © 2019 CSED Group 11. All rights reserved.
 */
public class EEGApp {

    /**Where the big bang happened.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EpochContainer ec = new EpochContainer();
        Headset head;
        boolean simulation = false;
        
        if(simulation){
         //Creates simulated headset
         head = new SimulatedHeadset(new EpochContainer()) {
            @Override
            public void update(Epoch data) {
                System.out.println("Simulated");
            }
        };
         
        }
        else{
            //Creates an actual connection to the headset
            head = new Headset() {
            @Override
            public void update(Epoch data) {
                System.out.println("Low Gamma level: "+data.getLowGamma());
            }
        };
            
          
        }
       
         head.addBlinkListener(new Runnable() {
            @Override
            public void run() {
               System.out.println("Stop blinking");
            }
        });
        
         head.capture();
        
         
        
    }
    
}
