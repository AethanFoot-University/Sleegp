/*
 *
 */
package app;

import data.Epoch;
import data.EpochContainer;
import hardware.Headset;
import hardware.SimulatedHeadset;

import java.io.IOException;

/**
 * Credits:
 *
 * @author Allington, Mathew
 * @author Draper, Tom
 * @author Foot, Aethan
 * @author Ito-Low, Alexander
 * @author Millischer, Christophe
 * @author Mortensen, Soren
 * @author Mortimer, Lloyd
 * @author Sogbesan, Samuel
 * @author Songthammakul, Ravit
 * <p>
 * Created: 18-02-2019
 * Copyright © 2019 CSED Group 11. All rights reserved.
 */
public class EEGApp {

    /**
     * Where the big bang happened.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        EpochContainer ec = new EpochContainer();

        //Creates an actual connection to the headset
        Headset head = new Headset() {
            @Override
            public void update(Epoch data) {
                System.out.println("Low Gamma level: " + data.getLowGamma());
                ec.addEpoch(data);
            }
        };

        head.addBlinkListener(new Runnable() {
            @Override
            public void run() {
                System.out.println("Stop blinking");
            }
        });

        head.capture();

        Thread.sleep(15000);

        head.disconnect();

        System.out.println("-------Playing back-------");

        SimulatedHeadset simHead = new SimulatedHeadset(ec) {
            @Override
            public void update(Epoch data) {
                System.out.println(data.toString());
            }
        };

        simHead.setEpochPeriod(1000);

        simHead.capture();
    }
}
