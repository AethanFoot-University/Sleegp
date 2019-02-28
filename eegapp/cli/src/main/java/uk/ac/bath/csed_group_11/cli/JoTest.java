package uk.ac.bath.csed_group_11.cli;

import uk.ac.bath.csed_group_11.logic.data.Epoch;
import uk.ac.bath.csed_group_11.logic.hardware.Headset;
import uk.ac.bath.csed_group_11.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.logic.hardware.Headset;
import uk.ac.bath.csed_group_11.logic.hardware.SimulatedHeadset;
import uk.ac.bath.csed_group_11.logic.util.Load;

import java.io.*;
import java.nio.Buffer;
import java.util.Scanner;

public class JoTest {

    public static Scanner in;
    public static void main(String[] main){
        in = new Scanner(System.in);

        boolean exit = false;

        System.out.println("This is a pre-alpha release for increment testing (v0.1)");

        while(!exit) {
            System.out.println("Please select one of the following tests:" +
                    "\n1. Test headset connection" +
                    "\n2. Get data from headset" +
                    "\n3. Test play back function" +
                    "\n4. Test blink function" +
                    "\n5. Exit");
            System.out.println("Enter your choice:");
            try {
                int choice = in.nextInt();

                switch (choice) {
                    case 1:
                        testHeadsetConnection();
                        break;

                    case 2:
                        testDataComingBackFromHeadset();
                        break;

                    case 3:
                        testPlayBackFunction();
                        break;

                    case 4:
                        testBlinkDetection();
                        break;

                    case 5:
                        System.out.println("Exiting");
                        exit = true;
                        break;
                    default:
                        System.out.println("Jo, there is no need to test the CLI, it's not going to be part of the final release XD.");
                        break;

                }
            }
            catch(Exception e){
                System.out.println("Very funny, now get back to the real test.\n");
                in = new Scanner(System.in);
            }
        }
    }


    public static void testHeadsetConnection(){
        Headset head = new Headset() {
            @Override
            public void update(Epoch data) {}
        };
        if(head.capture()) {
            System.out.println("Connected");
        } else {
            System.out.println("Connection failed");
        }
    }

    public static void testDataComingBackFromHeadset(){
        int seconds;

        System.out.println("How many seconds of data do you want to record?");

        seconds = in.nextInt() * 1000;
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

        System.out.println("Enter file directory to save (.ec format):");
        Scanner scan = new Scanner(System.in);
        String file = scan.nextLine();

        File f = new File(file);
        try {
            ec.saveToFile(f);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.out.println("Stop testing the CLI, this isn't going to be part of the typical release. Typical Jo.");
        }
    }

    public static void testPlayBackFunction(){
        System.out.println("Enter file directory to load and play back (.ec format):");
        Scanner scan = new Scanner(System.in);
        String file = scan.nextLine();

        File f = new File(file);

        try {
            Load load = new Load(f);
            EpochContainer ep = (EpochContainer)load.load();

            SimulatedHeadset head = new SimulatedHeadset(ep) {
                @Override
                public void update(Epoch data) {
                    System.out.println(data);
                }
            };

            head.capture();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

    }

    public static void testBlinkDetection(){
        Headset head = new Headset() {
            @Override
            public void update(Epoch data) {
                System.out.println("Signal: "+data.getPoorSignalLevel());
            }
        };
        System.out.println("How many seconds do you want to test for?");
        int seconds = in.nextInt() * 1000;

        head.addBlinkListener(()->{
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


    public static void getUserWait(){
        System.out.println("How many milli-seconds would you like to test for? :");
        while(!in.hasNextInt());
        int millis = in.nextInt();

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
