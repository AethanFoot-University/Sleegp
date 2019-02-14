/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eegtesting;

import com.ericblue.mindstream.client.ThinkGearSocketClient;
import com.ericblue.mindstream.preferences.PreferenceManager;
import java.io.IOException;

/**
 *
 * @author mathew
 */
public class EEGTesting {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
       String host = PreferenceManager.loadPreferences().get("thinkgearHost", "");
        int port = PreferenceManager.loadPreferences().getInt("thinkgearPort", 0);

        final ThinkGearSocketClient client = new ThinkGearSocketClient(host, port);
        try{
            
        while(true){
        client.connect();
        if(client.isConnected()){
            System.out.println("Connected");
            break;
        }
        else{
            System.out.println("Not Connected");
        }
        Thread.sleep(1000);
        
        }
        
        
        } catch (Exception e){
            client.close();
            System.out.println("Connection failed");
        
        }
        
    }
    
}
