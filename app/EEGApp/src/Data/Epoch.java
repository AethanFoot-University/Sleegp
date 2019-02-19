/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;






/**
 *
 * @author Mathew Allington
 */
public class Epoch {
    
    //eSense
    private int attention;
    private int meditation;
    
    //eegPower
    private int delta;
    private int theta;
    private int lowAlpha;
    private int highAlpha;
    private int lowBeta;
    private int highBeta;
    private int lowGamma;
    private int highGamma;
    
    //Headset signal
    private int poorSignalLevel;
    
    public Epoch(String JSON) throws JSONException{
       JSONObject obj = new JSONObject(JSON);
       Iterator it = obj.keys();
       
       while(it.hasNext()){
           String key = it.next().toString();
           
           if(key.matches("eSense")) addData(obj.getJSONObject("eSense"));
           if(key.matches("eegPower")) addData(obj.getJSONObject("eegPower"));
           if(key.matches("poorSignalLevel")) addData(obj);
       }
       
    }
    
    private void parseJson(JSONObject obj){
        
    }
    private void addData(JSONObject obj) throws JSONException{
        Iterator it = obj.keys();
        while(it.hasNext()){
            String key = it.next().toString();
     
            try {
                setIntField(key.toString(), obj.getInt(key));
            } catch (Exception e) {
            }
            
            
        }
    }
    
    private void setIntField(String reference, int data)throws IllegalAccessException, NoSuchFieldException {
    Field field = getClass().getDeclaredField(reference);
    field.setInt(this, data);
}
    
    @Override 
    public String toString(){
        String app = "[";
        Field[] fields = getClass().getDeclaredFields();
        
        
        for(Field f : fields) 
            try {
                
            app+=f.getName()+":"+f.getInt(this)+", ";
            
            } catch (Exception ex) {}  
            
        app+="]";
        return app;
    }
    
    
    public int getAttention(){
    return this.attention;
    }
    public int getMeditation(){
    return this.meditation;
    }
    public int getDelta(){
    return this.delta;
    }
    public int getTheta(){
    return this.theta;
    }
    public int getLowAlpha(){
    return this.lowAlpha;
    }
    public int getHighAlpha(){
    return this.highAlpha;
    }
    public int getLowBeta(){
    return this.lowBeta;
    }
    public int getHighBeta(){
    return this.highBeta;
    }
    public int getLowGamma(){
    return this.lowGamma;
    }
    public int getHighGamma(){
    return this.highGamma;
    }
    public int getPoorSignalLevel(){
    return this.poorSignalLevel;
    }
}
