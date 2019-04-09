package uk.ac.bath.csed_group_11.sleegp.logic.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * This class is a serializable container for a data frame at a specific point in time
 *
 * @author Mathew Allington
 */
public class Epoch implements Serializable, Comparable<Epoch>{
    /**
     * This is serializable ID, set to static so that there is not an ID mis-match error when saving files
     */
    static final long serialVersionUID = -8619870403385249300L;
    //Time Variables
    /**
     * Time since data recording first begun
     */
    private long timeElapsed;
    /**
     * Specific date and time of epoch: yyyy.MM.dd.HH.mm.ss.SSS
     */
    private String timeStamp;
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
    /**
     * Range 0-200, 200 = highest poor signal, 0 = Great signal
     */
    private int poorSignalLevel;

    /**
     * Instantiates a new Epoch.
     *
     * @param JSON              the json
     * @param timeElapsedMillis the time elapsed millis
     * @throws JSONException the json exception
     */
    public Epoch(String JSON, long timeElapsedMillis) throws JSONException {
        this.timeElapsed = timeElapsedMillis;
        timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());

        try {
            JSONObject obj = new JSONObject(JSON);
            Iterator it = obj.keys();

            while (it.hasNext()) {
                String key = it.next().toString();

                if (key.matches("eSense")) addData(obj.getJSONObject("eSense"));
                if (key.matches("eegPower")) addData(obj.getJSONObject("eegPower"));
                if (key.matches("poorSignalLevel")) addData(obj);
            }
        }
        catch (Exception e){
            System.out.println("JSON Parser Failed");
        }

    }

    public Epoch(int timeElapsed){
        this.timeElapsed = timeElapsed;
    }

    public Epoch(String csvRow){
        String[] attributes = csvRow.split(",");
        Field[] fields = getClass().getDeclaredFields();


        //for(String att : attributes) System.out.println(att);
        int d =0;
        for (int i =3; i<fields.length ;i++)
            try {


                setIntField(fields[i].getName(), Integer.parseInt(attributes[i-3]));
               // System.out.println("i: "+(i-d)+", "+attributes[i]);

            } catch (Exception ex) {
               // System.out.println(ex);
                System.out.println("fail");
            }

        timeElapsed = Long.parseLong(attributes[attributes.length - 2]);
        timeStamp = attributes[attributes.length - 1];
    }

    /**
     * Processes JSON frame and extracts its values
     * @param obj Object to be processed
     * @throws JSONException
     */
    private void addData(JSONObject obj) throws JSONException {
        Iterator it = obj.keys();
        while (it.hasNext()) {
            String key = it.next().toString();
            try {
                setIntField(key, obj.getInt(key));
            } catch (Exception e) {
            }


        }
    }

    /**
     * Sets a specific data field by its variable name
     * @param reference variable name
     * @param data Integer value to be set
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public void setIntField(String reference, int data) throws IllegalAccessException, NoSuchFieldException {
        Field field = getClass().getDeclaredField(reference);
        field.setInt(this, data);
    }

    /**
     * Converts the object to string form
     * @return String form of epoch
     */
    @Override
    public String toString() {
        String app = "[";
        Field[] fields = getClass().getDeclaredFields();

        for (Field f : fields)
            try {

                app += f.getName() + ":" + f.getInt(this) + ", ";

            } catch (Exception ex) {
            }
        app += "timeElapse: " + timeElapsed;
        app += "]";
        return app;
    }

    /**
     * Gets epoch data in the form of a CSV
     *
     * @return the csv
     */
    public String getCSV() {
        String app = "";
        Field[] fields = getClass().getDeclaredFields();
        for (Field f : fields)
            try {
                app += f.getInt(this) + ", ";

            } catch (Exception ex) {
            }
        app += timeElapsed + "," + timeStamp;

        return app;
    }

    /**
     * Gets csv header to be written to the top of a CSV file
     *
     * @return the csv header
     */
    public String getCSVHeader() {
        String app = "";
        Field[] fields = getClass().getDeclaredFields();

        for (Field f : fields)
            try {
                f.getInt(this);
                app += f.getName() + ", ";

            } catch (Exception ex) {
            }
        app += "timeElapsed,timeStamp";

        return app;
    }

    /**
     * Gets attention.
     *
     * @return the attention
     */
    public int getAttention() {
        return this.attention;
    }

    /**
     * Gets meditation.
     *
     * @return the meditation
     */
    public int getMeditation() {
        return this.meditation;
    }

    /**
     * Gets delta.
     *
     * @return the delta
     */
    public int getDelta() {
        return this.delta;
    }

    /**
     * Gets theta.
     *
     * @return the theta
     */
    public int getTheta() {
        return this.theta;
    }

    /**
     * Gets low alpha.
     *
     * @return the low alpha
     */
    public int getLowAlpha() {
        return this.lowAlpha;
    }

    /**
     * Gets high alpha.
     *
     * @return the high alpha
     */
    public int getHighAlpha() {
        return this.highAlpha;
    }

    /**
     * Gets low beta.
     *
     * @return the low beta
     */
    public int getLowBeta() {
        return this.lowBeta;
    }

    /**
     * Gets high beta.
     *
     * @return the high beta
     */
    public int getHighBeta() {
        return this.highBeta;
    }

    /**
     * Gets low gamma.
     *
     * @return the low gamma
     */
    public int getLowGamma() {
        return this.lowGamma;
    }

    /**
     * Gets high gamma.
     *
     * @return the high gamma
     */
    public int getHighGamma() {
        return this.highGamma;
    }

    /**
     * Gets poor signal level.
     *
     * @return the poor signal level
     */
    public int getPoorSignalLevel() {
        return this.poorSignalLevel;
    }

    /**
     * Time elapsed long.
     *
     * @return the long
     */
    public long timeElapsed() {
        return this.timeElapsed;
    }

    /**
     * Gets time stamp.
     *
     * @return the time stamp
     */
    public String getTimeStamp() {
        return this.timeStamp;
    }
    public int getByReference(String reference){
        try {
            Field field = getClass().getDeclaredField(reference);
            return field.getInt(this);
        }
        catch(Exception e){
            return -1;
        }
    }



    //Complete bodge


    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setAttention(int attention) {
        this.attention = attention;
    }

    public void setMeditation(int meditation) {
        this.meditation = meditation;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public void setTheta(int theta) {
        this.theta = theta;
    }

    public void setLowAlpha(int lowAlpha) {
        this.lowAlpha = lowAlpha;
    }

    public void setHighAlpha(int highAlpha) {
        this.highAlpha = highAlpha;
    }

    public void setLowBeta(int lowBeta) {
        this.lowBeta = lowBeta;
    }

    public void setHighBeta(int highBeta) {
        this.highBeta = highBeta;
    }

    public void setLowGamma(int lowGamma) {
        this.lowGamma = lowGamma;
    }

    public void setHighGamma(int highGamma) {
        this.highGamma = highGamma;
    }

    public void setPoorSignalLevel(int poorSignalLevel) {
        this.poorSignalLevel = poorSignalLevel;
    }

    @Override
    public int compareTo(Epoch o) {
        return 0;
    }
}
