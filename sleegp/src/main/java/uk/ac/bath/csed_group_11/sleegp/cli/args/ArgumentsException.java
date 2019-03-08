package uk.ac.bath.csed_group_11.sleegp.cli.args;

//
//  ArgumentsException
//  slEEGp
//
//  Created by Søren Mortensen on 2019-03-08.
//  Copyright © 2019 CSED Group 11 Developers. All rights reserved.
//

/**
 * Exception representing an invalid set of command-line arguments.
 */
public class ArgumentsException extends Exception {
    private final String message;

    ArgumentsException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
