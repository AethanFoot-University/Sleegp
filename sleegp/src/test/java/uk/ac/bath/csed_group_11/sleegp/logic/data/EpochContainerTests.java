package uk.ac.bath.csed_group_11.sleegp.logic.data;

//
//  EpochContainerTests
//  sleegp
//
//  Created by Søren Mortensen on 2019-03-08.
//  Copyright © 2019 CSED Group 11. All rights reserved.
//

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

public class EpochContainerTests {
    /**
     * Test that a {@code .ec} file can be deserialised into an {@code EpochContainer} instance,
     * and then serialised correctly as a {@code .csv} file.
     *
     * @author Mortensen, Soren
     */
    @Test
    public void convertECToCSV() {
        // Create a ClassLoader so we can get test resource files.
        var classLoader = Thread.currentThread().getContextClassLoader();
        var baseName = "2019-03-08-1457";

        // Load the URLs for the `.ec` and `.csv` files.
        var ecURL = classLoader.getResource( baseName + ".ec");
        var csvURL = classLoader.getResource(baseName + ".csv");

        if (ecURL == null || ecURL.getPath() == null) {
            fail("Unable to find resource \"" + baseName + ".ec\"");
            return;
        }

        if (csvURL == null) {
            fail("Unable to find resource \"" + baseName + ".csv\"");
            return;
        }

        // Load the contents of the `.ec` file.
        EpochContainer ec;
        try {
            System.out.println(ecURL.getPath());
            ec = EpochContainer.loadContainerFromFile(new File(ecURL.getPath()));

            if (ec == null)
                fail("Epoch container failed to load from file \"" + baseName + ".csv\".");
        } catch (IOException e) {
            fail("IOException while loading container from file \"" + baseName + ".csv\": " +
                    e.toString());
            return;
        } catch (ClassNotFoundException e) {
            fail("ClassNotFoundException while loading container from file \"" + baseName + ".csv" +
                    "\": " + e.toString());
            return;
        }

        // Load the contents of the `.csv` file.
        String expectedCSV;
        try {
            byte[] csvEncoded = Files.readAllBytes(Paths.get(csvURL.getPath()));
            expectedCSV = new String(csvEncoded, Charset.defaultCharset()).trim();
        } catch (IOException e) {
            fail("IOException while loading CSV data from file \"" + baseName + ".csv\": " +
                    e.toString());
            return;
        }

        // Generate CSV data from the epoch container.
        var actualCSV = ec.genCSV().trim();

        // Make sure that data is the same as the expected CSV data.
        Assertions.assertEquals(expectedCSV, actualCSV,
                "EpochContainer generated incorrect CSV data");
    }
}
