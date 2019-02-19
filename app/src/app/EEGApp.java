/*
 *
 */
package app;

import data.Epoch;
import data.EpochContainer;
import hardware.Headset;
import hardware.SimulatedHeadset;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
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
    public static void main(String[] args) throws InterruptedException, IOException, AWTException {

        File ecFile = new File("/Users/mathew/Documents/GitHub/project/testData/FirstRecording.ec");

        boolean simulate = true;
    if(!simulate) {
        EpochContainer ec = new EpochContainer();
        Headset head = new Headset() {
            @Override
            public void update(Epoch data) {
                ec.addEpoch(data);
            }
        };
        head.addBlinkListener(() -> {
            System.out.println("Stop blinking");
        });

        head.addRemovedHeadsetListener(() -> {
            System.out.println("Headset removed");
        });

        head.addPutOnHeadsetListener(() -> {
            System.out.println("Headset put on");
        });

        if (head.capture()) {
            System.out.println("Connected");
        } else {
            System.out.println("Connection failed");
        }

        Thread.sleep(300000);
        ec.saveToFile(new File("/Users/mathew/Documents/GitHub/project/testData/FirstRecording.ec"));
        head.disconnect();

    }
        else {
        System.out.println("Loading file: "+ecFile.toString());

        EpochContainer ec = EpochContainer.loadContainerFromFile(ecFile);

        System.out.println("Starting simulation");
        SimulatedHeadset sim = new SimulatedHeadset(ec) {
            @Override
            public void update(Epoch data) {
                System.out.println("Simulated:" + data);
            }
        };

        sim.setEpochPeriod(200);
        sim.loopData(true);
        sim.capture();

    }


    }
}
