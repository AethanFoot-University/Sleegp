package uk.ac.bath.csed_group_11.cli;

import uk.ac.bath.csed_group_11.logic.data.Epoch;
import uk.ac.bath.csed_group_11.logic.hardware.Headset;

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

    public static  void testHeadsetConnection(){

    }

    public static void testDataComingBackFromHeadset(){

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
