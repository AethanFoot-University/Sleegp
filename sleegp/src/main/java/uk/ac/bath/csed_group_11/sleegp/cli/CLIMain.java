package uk.ac.bath.csed_group_11.sleegp.cli;

//
//  CLIMain
//  slEEGp
//
//  Created by Mathew Allington on 2019-02-18.
//  Copyright © 2019 CSED Group 11 Developers. All rights reserved.
//

import uk.ac.bath.csed_group_11.sleegp.cli.args.Arguments;
import uk.ac.bath.csed_group_11.sleegp.cli.args.ArgumentsException;
import uk.ac.bath.csed_group_11.sleegp.cli.args.Options;
import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;
import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.hardware.Headset;
import uk.ac.bath.csed_group_11.sleegp.logic.hardware.SimulatedHeadset;
import uk.ac.bath.csed_group_11.sleegp.logic.util.ObjectConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

/**
 * Contains the main entry point for command-line execution of slEEGp.
 */
public class CLIMain {

    /**
     * Main command-line entry point for slEEGp.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        // Parse options from the provided command line arguments.
        Options options = Options.defaultOptions();
        try {
            options = new Arguments(args).parse();
        } catch (ArgumentsException e) {
            System.err.println(e.toString());
            System.exit(1);
        }

        if (options.shouldSimulate()) {
            var maybeSimFile = options.getSimulationFile();

            if (maybeSimFile.isEmpty()) {
                System.err.println("Unexpected error: value not found for simulation file path.");
                System.exit(2);
            }

            try {
                CLIMain.simulateHeadset(maybeSimFile.get());
            } catch (NoSuchFileException e) {
                System.err.println("Unable to simulate headset from file \"" + maybeSimFile.get() +
                        "\": File does not exist.");
            } catch (IOException e) {
                System.err.println("Unable to simulate headset from file \"" + maybeSimFile.get() +
                        "\": " + e.toString());
                System.exit(1);
            } catch (ClassNotFoundException e) {
                System.err.println("File \"" + maybeSimFile.get() + "\" tries to load class that " +
                        "was not found: " + e.toString());
                System.exit(1);
            }
        } else {
            EpochContainer ec = new EpochContainer();

//            ec.setAutoSave(auto, 10000);

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

            head.addBlinkListener(() -> System.out.println("Stop blinking"));
            head.addRemovedHeadsetListener(() -> System.out.println("Headset removed"));
            head.addPutOnHeadsetListener(() -> System.out.println("Headset put on"));

            if (head.capture()) {
                System.out.println("Connected");
            } else {
                System.out.println("Connection failed");
            }

            try {
                Thread.sleep(60 * 60 * 10 * 1000);
//                ec.saveToFile(ecFile);
                head.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.toString());
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void simulateHeadset(String dataFilePath) throws IOException, ClassNotFoundException {
        var ecFile = new File(dataFilePath);

        System.out.println("Loading file: " + ecFile.toString());

        EpochContainer ec = EpochContainer.loadContainerFromFile(ecFile);

//        String csv = ec.genCSV();
//        FileTools.write("/Users/mathew/Desktop/3hoursleep.csv", csv);
//        System.out.println(ec.genCSV());

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
