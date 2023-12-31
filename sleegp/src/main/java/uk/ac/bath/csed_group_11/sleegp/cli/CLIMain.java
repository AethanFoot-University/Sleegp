package uk.ac.bath.csed_group_11.sleegp.cli;

//
//  CLIMain
//  slEEGp
//
//  Created by Mathew Allington on 2019-02-18.
//  Copyright © 2019 CSED Group 11 Developers. All rights reserved.
//

import org.docopt.Docopt;
import uk.ac.bath.csed_group_11.sleegp.logic.Classification.ClassificationUtils;
import uk.ac.bath.csed_group_11.sleegp.logic.Classification.Plot;
import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;
import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.data.ProcessedDataContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.hardware.Headset;
import uk.ac.bath.csed_group_11.sleegp.logic.hardware.SimulatedHeadset;
import uk.ac.bath.csed_group_11.sleegp.logic.util.ConsoleUtils;
import uk.ac.bath.csed_group_11.sleegp.logic.util.Load;
import uk.ac.bath.csed_group_11.sleegp.logic.util.ObjectConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.text.DecimalFormat;
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
    public static final String doc = "Usage:\n" +
        "  sleegp record [-s <seconds> | -m <minutes>] [-o <output-file>]\n" +
//        "  sleegp simulate <data-file>\n" +
        "  sleegp convert [-o <output-file>] <data-file>\n" +
        "  sleegp process [-o <output-file>] <data-file>\n" +
        "  sleegp display <sleep-data-file>\n" +
        "  sleegp -h | --help | --version\n" +
        "\n" +
        "Arguments:\n" +
        "  <data-file>        recorded data file, in .ec format\n" +
        "  <sleep-data-file>  processed sleep data file\n" +
        "\n" +
        "Options:\n" +
        "  -o, --output <output-file>  output file location\n" +
        "  -s, --seconds <seconds>     limit recording time to <seconds> seconds\n" +
        "  -m, --minutes <minutes>     limit recording time to <minutes> minutes\n" +
        "  -h, --help                  show this help message and exit\n" +
        "  --version                   show version and exit\n";

    /**
     * Main command-line entry point for slEEGp.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        Map<String, Object> opts = new Docopt(doc).withVersion("sleegp 0.3.0").parse(args);

        Object subcommand;

        if (((subcommand = opts.get("record")) != null) && subcommand.equals(true)) {
            EpochContainer ec = new EpochContainer();

            var maybeOutputFile = (String) opts.get("--output");
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
                Object time;

                if ((time = opts.get("--seconds")) != null) {
                    Integer seconds = Integer.valueOf((String)time);
                    Thread.sleep(1000 * seconds);
                } else if ((time = opts.get("--minutes")) != null) {
                    Integer minutes = Integer.valueOf((String)time);
                    Thread.sleep( 60 * 1000 * minutes);
                } else {
                    Thread.sleep(60 * 60 * 10 * 1000);
                }

                if (maybeOutputFile != null)
                    ec.saveToFile(new File(maybeOutputFile));

                head.disconnect();
            } catch (NumberFormatException e) {
                System.err.println("Invalid time value entered: " + e.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.toString());
                Thread.currentThread().interrupt();
            }
        } else if (((subcommand = opts.get("simulate")) != null) && subcommand.equals(true)) {
            var fileName = (String) opts.get("<data-file>");

            try {
                CLIMain.simulateHeadset(fileName);
            } catch (NoSuchFileException e) {
                System.err.println("Unable to simulate headset from file \"" + fileName +
                    "\": File does not exist.");
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Unable to simulate headset from file \"" + fileName +
                    "\": " + e.toString());
                System.exit(1);
            } catch (ClassNotFoundException e) {
                System.err.println("File \"" + fileName + "\" tries to load class that was not " +
                    "found: " + e.toString());
                System.exit(1);
            }
        } else if (((subcommand = opts.get("convert")) != null) && subcommand.equals(true)) {
            var ecPath = (String) opts.get("<data-file>");
            EpochContainer ec;

            try {
                ec = EpochContainer.loadContainerFromFile(new File(ecPath));
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Unable to load container from file: " + e.toString());
                return;
            }

            String converted = ec.genCSV();

            var maybeOutputFile = (String) opts.get("--output");
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
        } else if (((subcommand = opts.get("process")) != null) && subcommand.equals(true)) {
            var ecPath = (String) opts.get("<data-file>");


            try {
                EpochContainer ec;
                ec = EpochContainer.loadContainerFromFile(new File(ecPath));

                ProcessedDataContainer processedDataContainer = ClassificationUtils.convertData(ec);

                //Saving to file
                processedDataContainer.saveToFile(new File((String) opts.get("--output")));


            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Unable to load container from file: " + e.toString());
                return;
            }



            /*int avgWindow = 10;
            List<Double> attention = ec.getTransformedData(
                MathUtils.movingAverage("attention", avgWindow));
            List<Double> meditation = ec.getTransformedData(
                MathUtils.movingAverage("meditation", avgWindow));

            System.out.println("Data (raw & moving average):");
            for (int i = 0; i < ec.size(); i++) {
                System.out.println("attention: " + ec.getEpoch(i).getAttention() + " [raw] " +
                    attention.get(i) + " [avg], " +
                    "meditation: " + ec.getEpoch(i).getMeditation() + " [raw] " +
                    meditation.get(i) + " [avg]"
                );
            }  */



        } else if (((subcommand = opts.get("display")) != null) && subcommand.equals(true)) {
            String dataToDisplay = (String) opts.get("<sleep-data-file>");

            String toFormat = "";

            try {
                ProcessedDataContainer container =
                    (ProcessedDataContainer)(new Load(new File(dataToDisplay)).load());

                int awake = 0;
                int asleep = 0;

                for(Plot p : container) {
//                    System.out.println(p.getLevel());

                    if (p.getLevel() > 30.0) {
                        awake += 1;
                    } else {
                        asleep += 1;
                    }
                }

                double percentAwake = 100 * ((double)awake / (double)(awake + asleep));
                double percentAsleep = 100.0 - percentAwake;

                var percentFormat = new DecimalFormat("###.00");

                toFormat += "Awake | " + percentFormat.format(percentAwake) + "%\n";
                toFormat += "Asleep | " + percentFormat.format(percentAsleep) + "%\n";

                String outputFormat = ConsoleUtils.printMessageBox(toFormat,
                    35);

                System.out.println(outputFormat);
            } catch (IOException e) {
                System.err.println("Please check file");
            } catch (ClassNotFoundException e) {
                System.err.println("Invalid Data");
            }




            //System.err.println("Subcommand `display` not yet implemented.");
        }
    }

    private static void simulateHeadset(String dataFilePath) throws IOException,
        ClassNotFoundException {
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
