package uk.ac.bath.csed_group_11.sleegp.logic.hardware;

import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Mathew Allington
 */
public abstract class Headset {

    /**
     *
     */
    public final static String DEFAULT_HOST = "127.0.0.1";
    /**
     *
     */
    public final static int DEFAULT_PORT = 13854;
    private static final String TROUBLE_SHOOTING_MSG = "\nTrouble Shooting:"
            + "\n1. Make sure ThinkGearConnector app is running."
            + "\n2. Make sure this is the only EEGApp instance running."
            + "\n3. Make sure the headset is connected and paired via Bluetooth"
            + "\n4. Contact mma82@bath.ac.uk or try Slack for more help.";
    long systemStartTime;
    boolean capturing = false;
    boolean headsetOn = true;
    private String host;
    private int port;
    private Socket headSocket;
    private BufferedReader JSONStream;
    private OutputStream outputStream = null;

    private Runnable blinkRunnable = null;
    private Runnable removedHeadsetRunnable = null;
    private Runnable putOnHeadsetRunnable = null;
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
     *
     */
    public Headset() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    /**
     * @param host
     * @param port
     */
    public Headset(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * @param data
     */
    public abstract void update(Epoch data);

    /**
     * @param run
     */
    public void addBlinkListener(Runnable run) {
        blinkRunnable = run;
    }

    public void removeBlinkListener() {
        blinkRunnable = null;
    }

    public void addRemovedHeadsetListener(Runnable run) {
        removedHeadsetRunnable = run;
    }

    public void removeRemovedHeadsetListener(Runnable run) {
        removedHeadsetRunnable = null;
    }

    public void addPutOnHeadsetListener(Runnable run) {
        putOnHeadsetRunnable = run;
    }

    public void removePutOnHeadsetListener(Runnable run) {
        putOnHeadsetRunnable = null;
    }

    /**
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

    public long getTimeElapsed() {
        return System.currentTimeMillis() - this.systemStartTime;
    }

    /**
     * @throws IOException
     */
    public void disconnect() throws IOException {
        capturing = false;
        JSONStream.close();
        headSocket.close();
    }

    private void writeMessage(String message) {
        PrintWriter out = new PrintWriter(outputStream, true);
        System.out.println("Writing: " + message);
        out.println(message);
    }

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

    private void runListener(Runnable r) {
        if (r != null) new Thread(r).start();
    }
}
