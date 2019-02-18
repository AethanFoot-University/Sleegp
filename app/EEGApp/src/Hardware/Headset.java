/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hardware;

import Data.Epoch;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mathew Allington
 */
public abstract class Headset {
    public final static String DEFAULT_HOST = "127.0.0.1";
    public final static int DEFAULT_PORT = 13854;
    
    private String host;
    private int port;
    boolean capturing = false;
    int latencyMillis = 20;
    
    private Socket headSocket;
    private BufferedReader JSONStream;
    public Headset(){
       this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public Headset(String host, int port){
        this.host = host;
        this.port = port;
    }
    
    public boolean capture(){
        try {
            headSocket = new Socket(host, port);
            if(!headSocket.isConnected()) return (capturing = false);
            
            JSONStream =  new BufferedReader(new InputStreamReader(headSocket.getInputStream()));
            
            //Run the network thread
            new Thread(networkThread).start();
            capturing = true;
            
        } catch (IOException ex) {
            System.out.println("Have you got another program running? :"+ex.getMessage());
            capturing = false;
        }
        
        return capturing;
    }
    
     Runnable networkThread = new Runnable() {
        @Override
        public void run() {
            while(capturing){
                Epoch ep = new Epoch();
                
                
                update(ep);
            }
        }
    };
    
    public void setArtificialLatency(int latencyMillis){
        this.latencyMillis = latencyMillis;
    }
    
    public void disconnect() throws IOException{
        capturing = false;
        JSONStream.close();
        headSocket.close();
    }
    
    public abstract void update(Epoch data);
}
