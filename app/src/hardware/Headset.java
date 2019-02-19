/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hardware;

import data.Epoch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Mathew Allington
 */
public abstract class Headset {

    private static final String TROUBLE_SHOOTING_MSG = "\nTrouble Shooting:"
            + "\n1. Make sure ThinkGearConnector app is running."
            + "\n2. Make sure this is the only EEGApp instance running."
            + "\n3. Make sure the headset is connected and paired via Bluetooth"
            + "\n4. Contact mma82@bath.ac.uk or try Slack for more help.";

    /**
     *
     */
    public final static String DEFAULT_HOST = "127.0.0.1";

    /**
     *
     */
    public final static int DEFAULT_PORT = 13854;

    private String host;
    private int port;
    boolean capturing = false;

    private Socket headSocket;
    private BufferedReader JSONStream;
    private OutputStream outputStream = null;

    private Runnable blinkRunnable = null;

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

    /**
     *
     */
    public void removeBlinkListener() {
        blinkRunnable = null;
    }

    /**
     * @return
     */
    public boolean capture() {
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

    Runnable networkThread = () -> {
        String input;
        try {
            while ((input = JSONStream.readLine()) != null) {

                try {
                    if (input.contains("eSense")) {
                        update(new Epoch(input));
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
    };

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
}
