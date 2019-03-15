package uk.ac.bath.csed_group_11.sleegp.cli;

import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;
import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.hardware.Headset;
import uk.ac.bath.csed_group_11.sleegp.logic.hardware.SimulatedHeadset;
import uk.ac.bath.csed_group_11.sleegp.logic.util.FileTools;
import uk.ac.bath.csed_group_11.sleegp.logic.util.Load;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class JoTest {
    public static Scanner scann;

    public static void main(String[] main) {
        scann = new Scanner(System.in);

        boolean exit = false;

        System.out.println("This is a pre-alpha release for increment testing (v0.2.1)");

        while (!exit) {
            System.out.println("Please select one of the following tests:" +
                    "\n1. Connect to headset" +
                    "\n2. Read and store brainwave data from headset" +
                    "\n3. Test CSV convert function" +
                    "\n4. Exit");
            System.out.println("Enter your choice:");
            try {
                int choice = scann.nextInt();

                switch (choice) {
                    case 1:
                        testHeadsetConnection();
                        break;

                    case 2:
                        testDataComingBackFromHeadset();
                        break;

                    case 3:
                        testCSVFunction();
                        break;

                    case 4:
                        System.out.println("Exiting");
                        exit = true;
                        break;
                    default:
                        System.out.println("Jo, there is no need to test the CLI, it's not going to be part of the final release XD.");
                        break;

                }
            } catch (Exception e) {
                System.out.println("Very funny, now get back to the real test.\n");
                scann = new Scanner(System.in);
            }
        }
    }


    public static void testHeadsetConnection() {
        Headset head = new Headset() {
            @Override
            public void update(Epoch data) {
            }
        };

        if (head.capture()) {
            System.out.println("Connected");
        } else {
            System.out.println("Connection failed");
        }
    }

    public static void testDataComingBackFromHeadset() {
        int seconds;
        scann = new Scanner(System.in);
        System.out.println("How many seconds of data do you want to record?");

        seconds = scann.nextInt() * 1000;
        EpochContainer ec = new EpochContainer();
        Headset head = new Headset() {
            @Override
            public void update(Epoch data) {
                if (data.getPoorSignalLevel() < 100) {
                    System.out.println("Recording [Poor Signal Level: " + data.getPoorSignalLevel() + "]");
                    ec.addEpoch(data);
                    System.out.println(data);
                } else {
                    System.out.println("Headset not on at: " + data.getTimeStamp());
                }
            }
        };

        head.capture();
        try {
            System.out.println("Waiting...");
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            System.out.println("Error");
        }
        try {
            System.out.println("Finished");
            head.disconnect();
        } catch (IOException e) {
            System.out.println("Error");
        }
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            System.out.println("Error");
        }
        scann = new Scanner(System.in);
        System.out.println("Enter file directory to save (.ec format):");
        String file = scann.nextLine();

        File f = new File(file);
        try {
            ec.saveToFile(f);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.out.println("Stop testing the CLI, this isn't going to be part of the typical release. Typical Jo.");
        }
    }

    public static void testPlayBackFunction() {
        System.out.println("Enter file directory to load and play back (.ec format):");
        scann = new Scanner(System.in);
        String file = scann.nextLine();


        try {
            File f = new File(file);
            Load load = new Load(f);
            EpochContainer ep = (EpochContainer) load.load();

            SimulatedHeadset head = new SimulatedHeadset(ep) {
                @Override
                public void update(Epoch data) {
                    System.out.println(data);
                }
            };

            head.capture();

        } catch (Exception e) {
            System.out.println("File not found");
        }

    }

    public static void testBlinkDetection() {
        scann = new Scanner(System.in);
        Headset head = new Headset() {
            @Override
            public void update(Epoch data) {
                System.out.println("Poor Signal: " + data.getPoorSignalLevel());
            }
        };
        System.out.println("How many seconds do you want to test for?");
        int seconds = scann.nextInt() * 1000;

        head.addBlinkListener(() -> {
            System.out.println("Stop blinking");
        });

        head.capture();
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            System.out.println("Error");
        }
        try {
            head.disconnect();
        } catch (IOException e) {
            System.out.println("Error");
        }

    }


    public static void getUserWait() {
        System.out.println("How many milli-seconds would you like to test for? :");
        while (!scann.hasNextInt()) ;
        int millis = scann.nextInt();

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void testCSVFunction() {

        try {

            scann = new Scanner(System.in);

            System.out.println("Please input your Epoch Container file (*.ec format): ");
            String loadDocument = scann.nextLine();


            System.out.println("Please input your destination (*.csv):");
            String destination = scann.nextLine();
            System.out.println("Loading from " + loadDocument + " -> Saving to : " + destination);
            EpochContainer ec = EpochContainer.loadContainerFromFile(new File(loadDocument));
            FileTools.write(destination, ec.genCSV());
            System.out.println("Written to " + destination);
        } catch (Exception e) {
            System.out.println("You maniac! Stop this at once!\n Please Check your file.\n" + e.getMessage());
        }
    }
}
