package uk.ac.bath.csed_group_11.sleegp.logic.hardware;

import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * The purpose of this class is to interface with the Think Gear Socket API, reading values from the stream
 * and supplying the data via abstract means.
 * Also provides action listeners such as removal of headset, blink detection etc.
 * @author Mathew Allington
 */
public abstract class Headset {

    /**
     * The default host name for the API
     */
    public final static String DEFAULT_HOST = "127.0.0.1";
    /**
     * Default port number for the API
     */
    public final static int DEFAULT_PORT = 13854;

    /**
     * Set of steps that can be taken to resolve any problems
     */
    private static final String TROUBLE_SHOOTING_MSG = "\nTrouble Shooting:"
            + "\n1. Make sure ThinkGearConnector app is running."
            + "\n2. Make sure this is the only EEGApp instance running."
            + "\n3. Make sure the headset is connected and paired via Bluetooth"
            + "\n4. Contact mma82@bath.ac.uk or try Slack for more help.";

    /**
     * System time that the recording was initiated
     */
    long systemStartTime;
    /**
     * Whether or not a recording should take place
     */
    private boolean capturing = false;
    /**
     * According to most recent data, is the headset on the users head?
     */
    private boolean headsetOn = true;
    /**
     * Host name/ Address of the API
     */
    private String host;
    /**
     * Port of the API
     */
    private int port;
    /**
     * Open socket to the ThinkGearSocket API
     */
    private Socket headSocket;
    /**
     * Stream used for reading JSON packets
     */
    private BufferedReader JSONStream;
    /**
     * Used for sending the API commands such as initiating the API
     */
    private OutputStream outputStream = null;

    /**
     * The Runnable that is run when a blink is detected
     */
    private Runnable blinkRunnable = null;
    /**
     * The Runnable that is run when the headset is removed from users head
     */
    private Runnable removedHeadsetRunnable = null;
    /**
     * The Runnable that is run when the headset is put on user head
     */
    private Runnable putOnHeadsetRunnable = null;

    /**
     * Main thread that the updater API runs on
     */
    Runnable networkThread = new Runnable() {
        @Override
        public void run() {
            String input;
            try {
                while ((input = JSONStream.readLine()) != null) {

                    try {
                        if (input.contains("eSense")) {
                            Epoch e = new Epoch(input, System.currentTimeMillis() - systemStartTime);
                            checkHeadset(e.getPoorSignalLevel());
                            update(e);
                        } else if (input.contains("blink")) {
                            new Thread(blinkRunnable).start();
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to serialise object.\n" + e.getMessage());
                    }

                }
            } catch (SocketException e) {
            } catch (IOException ex) {
            }

        }
    };

    /**
     * Instantiates a new Headset instance
     */
    public Headset() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }


    /**
     * Alternative: Instantiates a new Headset with a particular address and port
     *
     * @param host the host to connect to
     * @param port the port number
     */
    public Headset(String host, int port) {
        this.host = host;
        this.port = port;
    }


    /**
     * Abstract method is called when a new piece of converted data is available for processing
     *
     * @param data the data to be processed
     */
    public abstract void update(Epoch data);

    /**
     * Add blink listener.
     *
     * @param run the run
     */
    public void addBlinkListener(Runnable run) {
        blinkRunnable = run;
    }

    /**
     * Remove blink listener.
     */
    public void removeBlinkListener() {
        blinkRunnable = null;
    }

    /**
     * Add removed headset listener.
     *
     * @param run the run
     */
    public void addRemovedHeadsetListener(Runnable run) {
        removedHeadsetRunnable = run;
    }

    /**
     * Remove removed headset listener.
     *
     * @param run the run
     */
    public void removeRemovedHeadsetListener(Runnable run) {
        removedHeadsetRunnable = null;
    }

    /**
     * Add put on headset listener.
     *
     * @param run the run
     */
    public void addPutOnHeadsetListener(Runnable run) {
        putOnHeadsetRunnable = run;
    }

    /**
     * Remove put on headset listener.
     *
     * @param run the run
     */
    public void removePutOnHeadsetListener(Runnable run) {
        putOnHeadsetRunnable = null;
    }

    /**
     * Connects to the API and starts streaming data
     * @return
     */
    public boolean capture() {
        systemStartTime = System.currentTimeMillis();

        try {
            headSocket = new Socket(host, port);

            if (!headSocket.isConnected()) {
                return (capturing = false);
            }

            JSONStream = new BufferedReader(new InputStreamReader(headSocket.getInputStream()));
            outputStream = headSocket.getOutputStream();

            if (!initStream()) {
                return false;
            }

            //Run the network thread
            capturing = headSocket.isConnected();
            new Thread(networkThread).start();

        } catch (IOException ex) {
            System.out.println(ex.getMessage() + TROUBLE_SHOOTING_MSG);
            capturing = false;
        }

        return capturing;
    }

    /**
     * Gets time elapsed before recording first begun in millis
     * @return time elapsed
     */
    public long getTimeElapsed() {
        return System.currentTimeMillis() - this.systemStartTime;
    }

    /**
     * Disconnects from the API and stops main updater thread
     * @throws IOException
     */
    public void disconnect() throws IOException {
        //Stops main thread
        capturing = false;

        //Closes streams
        JSONStream.close();
        headSocket.close();
    }

    /**
     * Writes a command to the API
     * @param message to send
     */
    private void writeMessage(String message) {
        PrintWriter out = new PrintWriter(outputStream, true);
        System.out.println("Writing: " + message);
        out.println(message);
    }

    /**
     * Attempts to initiate stream by sending a command to the API
     * @return whether or not the stream was successfully initiated
     */
    private boolean initStream() {
        if (outputStream != null) {
            try {

                writeMessage("{\"enableRawOutput\":false,\"format\":\"Json\"}");

                return true;
            } catch (Exception e) {
                System.out.println(e.getMessage() + TROUBLE_SHOOTING_MSG);
            }

        }

        return false;
    }

    /**
     * Checks for significant change in poor signal value and then only runs the listeners is they
     * haven't yet been run in that particular state
     * @param poorSignalLevel integer value (0-200), 0 = Good, 200 = Poor
     */
    private void checkHeadset(int poorSignalLevel) {
        if (poorSignalLevel > 50) {
            if (headsetOn) {
                runListener(this.removedHeadsetRunnable);
                headsetOn = false;
            }
        } else if (!headsetOn) {
            runListener(this.putOnHeadsetRunnable);
            headsetOn = true;
        }

    }

    /**
     * Checks to see if a listener is null and runs it accordingly on a new thread
     * @param r runnable to be run
     */
    private void runListener(Runnable r) {
        if (r != null) new Thread(r).start();
    }

    /**
     * Is capturing boolean.
     *
     * @return the boolean
     */
    public boolean isCapturing() {
        return capturing;
    }

    /**
     * Sets capturing.
     *
     * @param capturing whether or not to capture
     */
    public void setCapturing(boolean capturing) {
        this.capturing = capturing;
    }

    /**
     * Is headset on boolean.
     *
     * @return the boolean
     */
    public boolean isHeadsetOn() {
        return headsetOn;
    }

}
