package uk.ac.bath.csed_group_11.sleegp.cli.args;

//
//  Options
//  slEEGp
//
//  Created by Søren Mortensen on 2019-03-08.
//  Copyright © 2019 CSED Group 11 Developers. All rights reserved.
//

import java.util.Optional;

/**
 * Represents options specified by the user through the command-line arguments interface.
 *
 * @see Arguments
 * @since 0.3.0
 */
public class Options {
    /**
     * Indicates whether the user specified that the program should simulate a headset by reading
     * a data file.
     *
     * @see Options#shouldSimulate()
     */
    private boolean simulate;

    /**
     * If {@code simulate} is {@code true}, contains the string provided by the user as the path
     * of the data file to simulate from.
     *
     * @see Options#shouldSimulate()
     * @see Options#getSimulationFile()
     */
    private String simulationFile;

    /**
     * Creates an instance of {@code Options} with the specified settings.
     *
     * @param simulate       Whether the program should simulate a headset by reading from a data file.
     * @param simulationFile The provided value for the path to the file to simulate from.
     * @since 0.3.0
     */
    Options(boolean simulate, String simulationFile) {
        this.simulate = simulate;
        this.simulationFile = simulationFile;
    }

    /**
     * Creates an instance of {@code Options} with default values.
     *
     * @return A default-valued instance of {@code Options}.
     * @since 0.3.0
     */
    public static Options defaultOptions() {
        return new Options(false, null);
    }

    /**
     * Indicates whether the user specified that the program should simulate a headset by reading
     * a data file, rather than reading data directly from a headset.
     *
     * @return {@code true} if the program should simulate a headset; {@code false} otherwise.
     * @since 0.3.0
     */
    public boolean shouldSimulate() {
        return simulate;
    }

    /**
     * Sets the value of {@code simulate}, indicating whether the program should simulate a
     * headset by reading a data file.
     *
     * @param simulate The new value for {@code simulate}.
     * @see Options#shouldSimulate()
     * @since 0.3.0
     */
    void setSimulate(boolean simulate) {
        this.simulate = simulate;
    }

    /**
     * Gets the value of {@code simulationFile}, indicating the path to the data file to simulate
     * from.
     *
     * @return If {@code this.simulate == true} and a value exists for {@code simulationFile},
     * returns an {@code Optional} containing the value for {@code simulationFile}. Otherwise,
     * returns {@code Optional.empty()}.
     * @since 0.3.0
     */
    public Optional<String> getSimulationFile() {
        if (this.simulate && this.simulationFile != null) {
            return Optional.of(this.simulationFile);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Sets the value of {@code simulationFile}.
     *
     * @param simulationFile The new value for {@code simulationFile}.
     * @see Options#getSimulationFile()
     * @since 0.3.0
     */
    void setSimulationFile(String simulationFile) {
        this.simulationFile = simulationFile;
    }
}
