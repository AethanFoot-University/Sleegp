package uk.ac.bath.csed_group_11.sleegp.logic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Methods associated with input and output on the user console
 */
public abstract class ConsoleUtils {

    /**
     * The constant DEFAULT_BOX_SIZE is the default character width of a peopleOnline box
     */
    public final static int DEFAULT_BOX_SIZE = 78;

    /**
     * Whether or not the listener process should keep accepting input
     */
    private boolean captureConsoleInput = false;

    /**
     * Used for taking scanner input
     */
    private Scanner scanner = null;

    /**
     * The thread on which the console listener is run
     */
    private Thread consoleListenerThread = null;


    /**
     * This method lets the implementing class know that input from the console has been captured.
     *
     * @param userInput the string inputted by the user
     * @param console   the console interface it was inputted on
     */
    public abstract void userInputted(String userInput, ConsoleUtils console);

    /**
     * This runnable takes user input from the console and notifies the implementing class via 'userInputted(..)'
     */
    private Runnable consoleListener = ()->{
        scanner = new Scanner(System.in);
        while(captureConsoleInput && !consoleListenerThread.isInterrupted()) {
            try{
                userInputted(scanner.nextLine(), this);

            } catch(Exception e){
                System.out.println("fail");
            }
        }
    };

    /**
     * Starts the  console listener.
     */
    public void startConsoleListener(){
        if(!captureConsoleInput) {
            captureConsoleInput = true;
            (consoleListenerThread = new Thread(consoleListener)).start();
        }

    }

    /**
     * Stop the console listener.
     */
    public void stopConsoleListener(){
        captureConsoleInput = false;
        scanner.close();
        consoleListenerThread.interrupt();

    }

    /**
     * Displays an error peopleOnline in a nicely formatted box
     *
     * @param title the title of the error
     * @param body  the body of the peopleOnline
     */
    public void displayError(String title, String body) {
        List<String> msgs = new ArrayList<String>();

        msgs.add("[Dammit]"); msgs.add(title);
        msgs.add("-"); msgs.add(body);

        System.out.println(printMessageBox(msgs));

    }

    /**
     * Prints a console in a nicely formatted box
     *
     * @param consoleMessage the text to be displayed
     */
    public void printConsole(String consoleMessage) {
        if(consoleMessage == null) consoleMessage ="";
        List<String> msg = new ArrayList<String>();
        msg.add("[Console]"); msg.add(consoleMessage);
        System.out.println(printMessageBox(msg));
    }

    /**
     * Prints a peopleOnline box with a string inside
     *
     * @param flatString the flat string to be printed
     * @param width      the width of the box
     * @return the string representation of the box
     */
    public static String printMessageBox(String flatString, int width){
        List<String> single = new ArrayList<String>();
        single.add(flatString);
        return printMessageBox(single, width);
    }

    /**
     * Prints a peopleOnline box of fixed width specified by DEFAULT_BOX_SIZE
     *
     * @param flatString the flat string to be printed
     * @return the string representation of the box
     */
    public static String printMessageBox(String flatString){
        return printMessageBox(flatString, DEFAULT_BOX_SIZE);
    }

    /**
     * Prints out an array of strings in a box of fixed width specified by DEFAULT_BOX SIZE
     *
     * @param messages list of lines to be printed
     * @return the string representation of the box
     */
    public static String printMessageBox(List<String> messages){
        return printMessageBox(messages, DEFAULT_BOX_SIZE);
    }

    /**
     * Prints out an array of strings in a box with custom width
     *
     * @param messages list of lines to be printed
     * @param width    the width of the peopleOnline box
     * @return the string representation of the box
     */
    public static String printMessageBox(List<String> messages, int width){
        List<String> formatted = trimCarriageReturns(messages);
        formatted = trimLineLengths(formatted, width);

        width+=2;
        String append = "";

        append+=(fillln(width,'■'));

        for(String s : formatted) append+=(printMiddleln(s , width, ' ', '║'));

        append+=(fill(width,'═', '╚','╝'));

        return append;
    }

    /**
     * Fills a line with a set amount of characters, used for building the top and bottom bars of peopleOnline bars
     *
     * @param amount the character width of the line
     * @param filler Default fill character
     * @param start  the start character
     * @param end    the end character
     * @return the string representation of the newly built line
     */
    private static String fill(int amount, char filler, char start, char end){
        String append = "";
        for(int i =0; i< amount; i++){
            if(i ==0){
                append+=start;
            } else if (i== amount-1){
                append+=end;
            } else{
                append += filler;
            }
        }
        return append;
    }

    /**
     * Alternative implementation to fill() with a carriage return
     *
     * @param amount the character width of the line
     * @param filler Default fill character
     * @param start  the start character
     * @param end    the end character
     * @return the string representation of the newly built line
     */
    private static String fillln(int amount, char filler, char start, char end){
        return fill(amount, filler, start, end) +'\n';
    }
    /**
     * Alternative implementation of fillln() with all characters being the same
     *
     * @param amount the character width of the line
     * @param filler Default fill character
     * @return the string representation of the newly built line
     */
    private static String fillln(int amount, char filler){
        return fillln(amount, filler, filler, filler);
    }

    /**
     * Prints a centered string with (end-start) positions denoting the width parameter
     * @param toPrint String to print
     * @param width Width of the box
     * @param filler Empty characters
     * @param sides Sided characters
     * @return the string representation of the newly built line
     */
    private static String printMiddle(String toPrint, int width, char filler, char sides){
        String append = "";
        if(toPrint ==null) toPrint = "null";
        for(int i =0; i<=width; i++){
            if(i==0 || i == (width)) {
                append+= sides;
            }

            else if(toPrint!=null &&i== (width/2) - (toPrint.length()/2)) {
                for(char c : toPrint.toCharArray()) {
                    append+=c;
                    i++;
                }
            }

            else{
                append+=filler;
            }
        }
        return append;
    }

    /**
     * Alternative implementation, same as printMiddleln +"\n"
     */
    private static String printMiddleln(String toPrint, int width, char filler, char sides){
        return (printMiddle(toPrint, width, filler, sides) + '\n');
    }

    /**d
     * If any carriage returns are detected, this method splits the string up into separate elements
     * in the array
     * @param strings strings to be separated
     * @return formatted array
     */
    private static List<String> trimCarriageReturns(List<String> strings){
        List<String> lineFormatted = new ArrayList<String>();

        for(String st: strings) {
            String append ="";
            for(char c : st.toCharArray()) {
                if(c == '\n') {
                    lineFormatted.add(append);
                    append = "";
                }
                else{
                    append+=c;
                }
            }
            if (append.length()>0) lineFormatted.add(append);
        }
        return lineFormatted;
    }

    /**
     * Each string is trimmed to a max size, any spillages that occur are placed in the proceeding element of the array
     * @param strings to be trimmed
     * @param maxSize max size
     * @return formatted array
     */
    private static List<String> trimLineLengths(List<String> strings, int maxSize){
        List<String> formatted = new ArrayList<String>();

        for(String message: strings){
            if(message!=null) {
                while (message.length() > maxSize) {
                    formatted.add(message.substring(0, maxSize));
                    message = message.substring(maxSize, message.length());
                }
                if (message.length() > 0) formatted.add(message);
            }
        }
        return formatted;
    }

    /**
     * Queries the user for a yes or or response
     *
     * @param question the question to be asked
     * @return User response : True - yes, False - no
     */
    public static boolean getYesNoChoice(String question){
        System.out.println(printMessageBox(question+"\nEnter Yes or No (Y/N): "));
        Scanner sc = new Scanner(System.in);

        while(true) {
            String line = sc.next();
            if(line.equalsIgnoreCase("Yes")||line.equalsIgnoreCase("Y")){
                return true;
            } else if(line.equalsIgnoreCase("No")||line.equalsIgnoreCase("N")){
                return false;
            } else{
                System.out.println(printMessageBox("\""+line+"\" is an invalid choice\nEnter Yes or No (Y/N): "));
            }
        }
    }
}
