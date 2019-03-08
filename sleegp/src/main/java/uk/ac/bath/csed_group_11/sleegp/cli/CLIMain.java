package uk.ac.bath.csed_group_11.sleegp.cli;

import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;
import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.hardware.Headset;
import uk.ac.bath.csed_group_11.sleegp.logic.hardware.SimulatedHeadset;
import uk.ac.bath.csed_group_11.sleegp.logic.util.FileTools;
import uk.ac.bath.csed_group_11.sleegp.logic.util.ObjectConverter;

import java.awt.*;
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
public class CLIMain {

    /**
     * Where the big bang happened.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, IOException, AWTException {
        File ecFile = new File("/Users/mathew/Documents/GitHub/project/testData" +
                "/Sleep20012019autoSave.ec");
        File auto = new File("/Users/mathew/Documents/GitHub/project/testData" +
                "/Sleep20012019autoSave.ec");

        boolean simulate = false;
        if (!simulate) {
            EpochContainer ec = new EpochContainer();

            ec.setAutoSave(auto, 10000);

            int epochLost = 0;
            Headset head = new Headset() {
                @Override
                public void update(Epoch data) {
                    if (data.getPoorSignalLevel() < 100) {
                        System.out.println("Recording [Poor Signal Level: "
                                + data.getPoorSignalLevel() + "]");
                        ec.addEpoch(data);
                        System.out.println(data);

                    } else {

                        System.out.println("Headset not on at: " + data.getTimeStamp());
                    }
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

            Thread.sleep(60 * 60 * 10 * 1000);
            ec.saveToFile(ecFile);
            head.disconnect();
        } else {
            System.out.println("Loading file: " + ecFile.toString());

            EpochContainer ec = EpochContainer.loadContainerFromFile(auto);
            String csv = ec.genCSV();
            FileTools.write("/Users/mathew/Desktop/3hoursleep.csv", csv);
            System.out.println(ec.genCSV());

            System.out.println("Serial version ID: " + ObjectConverter.getSerialVersionID(ec));
            System.out.println("Starting simulation");
            SimulatedHeadset sim = new SimulatedHeadset(ec) {
                @Override
                public void update(Epoch data) {
                    System.out.println("Simulated [" + data.getTimeStamp() + "]:" + data);
                }
            };

            sim.setEpochPeriod(200);
            sim.loopData(false);
            sim.capture();
        }
    }
}
