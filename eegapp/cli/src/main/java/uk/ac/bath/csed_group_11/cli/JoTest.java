package uk.ac.bath.csed_group_11.cli;

import uk.ac.bath.csed_group_11.logic.data.Epoch;
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

    public void testPlayBackFunction(){

    }
    public void testBlinkDetection(){

    }
}
