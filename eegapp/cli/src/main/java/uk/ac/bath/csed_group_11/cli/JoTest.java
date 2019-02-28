package uk.ac.bath.csed_group_11.cli;

import uk.ac.bath.csed_group_11.logic.data.Epoch;
import uk.ac.bath.csed_group_11.logic.hardware.Headset;
import uk.ac.bath.csed_group_11.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.logic.hardware.Headset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                    "\n1.Test headset connection" +
                    "\n2.Get data from headset" +
                    "\n3.Test play back function" +
                    "\n4.Test data save function" +
                    "\n5.Test blink function+" +
                    "\n6. Exit");
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
                        testDataSave();
                        break;

                    case 5:
                        testBlinkDetection();
                        break;

                    case 6:
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


    public void testHeadsetConnection(){
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

    public void testDataComingBackFromHeadset(){
        String input = "";
        int seconds;
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader r = new BufferedReader(isr);

        System.out.println("How many seconds of data do you want to record?");
        try {
            input = r.readLine();
        } catch (IOException e) {
            System.out.println("Error");
        }
        seconds = Integer.valueOf(input) * 1000;

        Headset head = new Headset() {
            EpochContainer ec = new EpochContainer();

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

        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            System.out.println("Error");
        }

    }

    public static void testPlayBackFunction(){

    }

    public static void testDataSave(){

    }
    public static void testBlinkDetection(){
        Headset headset = new Headset() {
            @Override
            public void update(Epoch data) {

            }
        };


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
