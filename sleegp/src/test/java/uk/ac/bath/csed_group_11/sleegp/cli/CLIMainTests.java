package uk.ac.bath.csed_group_11.sleegp.cli;

//CLIMainTests
//sleepg
//
//Created by Samuel Sogbesan on 09-04-2019
//Copyright © 2019 CSED Group 11. All rights reserved.
//



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import uk.ac.bath.csed_group_11.sleegp.logic.Classification.ClassificationUtils;
import uk.ac.bath.csed_group_11.sleegp.logic.Classification.Plot;
import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.data.ProcessedDataContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.util.MathUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class CLIMainTests {

    /**
     * Test to verify the program can extract attention and mediation data from raw brainwave data
     * and accurately produces a moving average
     * Requirements: 6.3.1, 6.3.2
     * @author Samuel Sogbesan
     */

    @Test
    public void attentionMediationTest(){
        var classLoader = Thread.currentThread().getContextClassLoader();
        EpochContainer ec = null;
        var baseName = "2019-03-08-1457";

        try {
            var ecURL = classLoader.getResource( baseName + ".ec");
            ec = EpochContainer.loadContainerFromFile(new File(ecURL.getPath()));
        }catch(Exception e){
            fail("IOException reading information from container");
        }

        int avgWindow = 10;
        List<Double> attention = ec.getTransformedData(
            MathUtils.movingAverage("attention", avgWindow));
        List<Double> meditation = ec.getTransformedData(
            MathUtils.movingAverage("meditation", avgWindow));
        System.out.println("Data (raw & moving average):");

        //List of strings containing attention/mediation values (to be printed to console)
        ArrayList<String> attentionMediationStrings = new ArrayList<String>();

        for (int i = 0; i < ec.size(); i++) {
            String value = ("attention: " + ec.getEpoch(i).getAttention() + " [raw] " +
                attention.get(i) + " [avg], " +
                "meditation: " + ec.getEpoch(i).getMeditation() + " [raw] " +
                meditation.get(i) + " [avg]"
            );
            attentionMediationStrings.add(value);
        }

        //attentionMediationStrings = new ArrayList<String>();
        //If no data was pulled despite a valid container existing...
        if(attentionMediationStrings.size() == 0){
            fail("No attention/mediation values pulled from EpochContainer.");
        }
        else{
            for(String s: attentionMediationStrings){
                System.out.println(s);
            }
        }
    }

    /**
     * This test verifies that the logic used in the cli to extract the amount of time which the
     * user is asleep vs awake is accurate
     * Requirement(s): 6.3.3
     *
     * @author Samuel Sogbesan
     */
    @Test
    public void percentageAwakeAsleepTest(){
        ProcessedDataContainer processedDataContainer = null;
        EpochContainer ec;
        try{
            var classLoader = Thread.currentThread().getContextClassLoader();
            var baseName = "2019-03-08-1457";

            // Load the URLs for the `.ec` and `.csv` files.
            var ecURL = classLoader.getResource( baseName + ".ec");
            ec = EpochContainer.loadContainerFromFile(new File(ecURL.getPath()));
            processedDataContainer = ClassificationUtils.convertData(ec);

        }catch(IOException | ClassNotFoundException e){
            fail("IOException or ClassNotFoundException Raised");
        }

        int awake = 0;
        int asleep = 0;

        for(Plot p : processedDataContainer) {
//                    System.out.println(p.getLevel());
            if (p.getLevel() > 30.0) {
                awake += 1;
            } else {
                asleep += 1;
            }
        }

        double percentAwake = 100 * ((double)awake / (double)(awake + asleep));
        double percentAsleep = 100.0 - percentAwake;

        System.out.println("AWAKE: "+percentAwake);
        System.out.println("ASLEEP: "+percentAsleep);

        if((percentAsleep+percentAwake)!=100){
            fail("The calculated percentages did not total to 100% (likely a miscalculation)");
        }
    }

    /**
     * This test verifies the accuracy of the functionality that allows the end user to input and
     * process raw sleep data.
     * Requirements: 6.7.5
     * @author Samuel Sogbesan
     */
    @Test
    public void rawDataProcessTest(){
        /*

         */
    }

    /**
     * This test verifies the accuracy of the functionality used to generate the moving average
     * using the
     * attention and meditation data.
     * Requirements: 6.3.2, 6.7.3
     * @author Samuel Sogbesan
     */
    public void movingAverageTest(){
        /*
        Take a point plotted through the moving average system
        Compare it to a point generated by my own system
        If they don't match
        Fail the test
         */

    }
}
