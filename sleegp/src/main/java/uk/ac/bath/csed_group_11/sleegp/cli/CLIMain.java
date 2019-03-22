package uk.ac.bath.csed_group_11.sleegp.cli;

//
//  CLIMain
//  slEEGp
//
//  Created by Mathew Allington on 2019-02-18.
//  Copyright © 2019 CSED Group 11 Developers. All rights reserved.
//

import org.docopt.Docopt;
import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;
import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.hardware.Headset;
import uk.ac.bath.csed_group_11.sleegp.logic.hardware.SimulatedHeadset;
import uk.ac.bath.csed_group_11.sleegp.logic.util.ObjectConverter;

import java.io.*;
import java.util.Map;

/**
 * Contains the main entry point for command-line execution of slEEGp.
 */
public class CLIMain {
    /**
     * Usage message for the CLI; also defines the format for acceptable arguments using Docopt.
     *
     * @see org.docopt.Docopt
     * @since 0.3.0
     */
    public static final String doc =
        "Usage:\n" +
        "  sleegp record [-o <output-file>]\n" +
//        "  sleegp simulate <data-file>\n" +
        "  sleegp convert [-o <output-file>] <input-file>\n" +
        "  sleegp -h | --help | --version\n" +
        "\n" +
        "Arguments:\n" +
//        "  <data-file>   recorded data file to simulate from\n" +
        "  <input-file>  file to convert, in .ec or .csv format\n" +
        "\n" +
        "Options:\n" +
        "  -o, --output <output-file>  output file location\n" +
        "  -h, --help                  show this help message and exit\n" +
        "  --version                   show version and exit\n";

    /**
     * Main command-line entry point for slEEGp.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        Map<String, Object> opts = new Docopt(doc).withVersion("sleegp 0.3.0").parse(args);

        if (opts.get("record").equals(true)) {
            EpochContainer ec = new EpochContainer();

            var maybeOutputFile = (String)opts.get("--output");
            if (maybeOutputFile != null)
                ec.setAutoSave(new File(maybeOutputFile + ".auto"), 10000);

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

                if (maybeOutputFile != null)
                    ec.saveToFile(new File(maybeOutputFile));

                head.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.toString());
                Thread.currentThread().interrupt();
            }
//        } else if (opts.get("simulate").equals(true)) {
//            var fileName = (String)opts.get("<data-file>");
//
//            try {
//                CLIMain.simulateHeadset(fileName);
//            } catch (NoSuchFileException e) {
//                System.err.println("Unable to simulate headset from file \"" + fileName +
//                        "\": File does not exist.");
//                System.exit(1);
//            } catch (IOException e) {
//                System.err.println("Unable to simulate headset from file \"" + fileName +
//                        "\": " + e.toString());
//                System.exit(1);
//            } catch (ClassNotFoundException e) {
//                System.err.println("File \"" + fileName + "\" tries to load class that was not " +
//                        "found: " + e.toString());
//                System.exit(1);
//            }
        } else if (opts.get("convert").equals(true)) {
            var ecPath = (String)opts.get("<input-file>");
            EpochContainer ec;

            try {
                ec = EpochContainer.loadContainerFromFile(new File(ecPath));
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Unable to load container from file: " + e.toString());
                return;
            }

            String converted = ec.genCSV();

            var maybeOutputFile = (String)opts.get("--output");
            if (maybeOutputFile != null) {
                var file = new File(maybeOutputFile);

                try (var fop = new FileOutputStream(file)) {
                    if (!file.exists()) file.createNewFile();

                    byte[] content = converted.getBytes();

                    fop.write(content);
                    fop.flush();
                } catch (FileNotFoundException e) {
                    System.err.println("Output file not found: " + e.toString());
                } catch (IOException e) {
                    System.err.println("Could not write converted data to file: " + e.toString());
                }
            } else {
                System.out.println(converted);
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
