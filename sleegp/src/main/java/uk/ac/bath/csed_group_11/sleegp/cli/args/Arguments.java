package uk.ac.bath.csed_group_11.sleegp.cli.args;

//
//  Arguments
//  slEEGp
//
//  Created by Søren Mortensen on 2019-03-08.
//  Copyright © 2019 CSED Group 11 Developers. All rights reserved.
//

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Helper class for parsing command-line arguments.
 */
public class Arguments {
    /**
     * The original list of arguments given to the program.
     */
    private final ArrayList<String> args;

    /**
     * The next element to be processed in {@code args}.
     * <p>
     * Used within {@code parse()} for such things as determining whether there are enough
     * elements remaining in {@code args} for a certain argument to be valid.
     *
     * @see Arguments#args
     * @see Arguments#parse()
     */
    private int cursor;

    /**
     * Creates a new instance of {@code Arguments} from the arguments provided to the main method.
     *
     * @param args The original command-line arguments, as provided to the main method.
     * @since 0.3.0
     */
    public Arguments(String[] args) {
        this.args = new ArrayList<>();
        this.args.addAll(Arrays.asList(args));

        this.cursor = 0;
    }

    /**
     * Attempts to parse the arguments into an {@code Options} instance.
     * <p>
     * Valid arguments:
     *
     * <ul>
     * <li>
     * {@code "--simulate-from"}/{@code "-s"}: Specifies that the program should simulate a
     * headset by reading from a data file rather than by connecting to the headset. This
     * argument must be followed by a value specifying the path to the file to read from.
     * </li>
     * </ul>
     *
     * @return The parsed {@code Options}.
     * @throws ArgumentsException if the arguments provided were invalid.
     * @see Options
     * @since 0.3.0
     */
    public Options parse() throws ArgumentsException {
        var options = Options.defaultOptions();

        var argsLen = this.args.size();
        while (this.cursor < argsLen) {
            var arg = this.args.get(this.cursor);

            switch (arg) {
                case "--simulate-from":
                case "-s":
                    options.setSimulate(true);

                    this.cursor += 1;
                    if (this.cursor >= argsLen)
                        throw new ArgumentsException("Missing value for argument \"" + arg + "\".");

                    options.setSimulationFile(this.args.get(this.cursor));

                    break;
                default:
                    throw new ArgumentsException("Unrecognised argument: " + arg);
            }

            this.cursor += 1;
        }

        return options;
    }
}
