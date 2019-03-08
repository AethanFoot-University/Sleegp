package uk.ac.bath.csed_group_11.sleegp.logic.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * @author Mathew Allington
 */
public class Epoch implements Serializable {
    static final long serialVersionUID = -8619870403385249300L;
    //Time
    private long timeElapsed;
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
    private int poorSignalLevel;

    /**
     * @param JSON
     * @throws JSONException
     */
    public Epoch(String JSON, long timeElapsedMillis) throws JSONException {
        this.timeElapsed = timeElapsedMillis;
        timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());

        JSONObject obj = new JSONObject(JSON);
        Iterator it = obj.keys();

        while (it.hasNext()) {
            String key = it.next().toString();

            if (key.matches("eSense")) addData(obj.getJSONObject("eSense"));
            if (key.matches("eegPower")) addData(obj.getJSONObject("eegPower"));
            if (key.matches("poorSignalLevel")) addData(obj);
        }

    }

    private void addData(JSONObject obj) throws JSONException {
        Iterator it = obj.keys();
        while (it.hasNext()) {
            String key = it.next().toString();
            try {
                setIntField(key.toString(), obj.getInt(key));
            } catch (Exception e) {
            }


        }
    }

    private void setIntField(String reference, int data) throws IllegalAccessException, NoSuchFieldException {
        Field field = getClass().getDeclaredField(reference);
        field.setInt(this, data);
    }

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
     * @return
     */
    public int getAttention() {
        return this.attention;
    }

    /**
     * @return
     */
    public int getMeditation() {
        return this.meditation;
    }

    /**
     * @return
     */
    public int getDelta() {
        return this.delta;
    }

    /**
     * @return
     */
    public int getTheta() {
        return this.theta;
    }

    /**
     * @return
     */
    public int getLowAlpha() {
        return this.lowAlpha;
    }

    /**
     * @return
     */
    public int getHighAlpha() {
        return this.highAlpha;
    }

    /**
     * @return
     */
    public int getLowBeta() {
        return this.lowBeta;
    }

    /**
     * @return
     */
    public int getHighBeta() {
        return this.highBeta;
    }

    /**
     * @return
     */
    public int getLowGamma() {
        return this.lowGamma;
    }

    /**
     * @return
     */
    public int getHighGamma() {
        return this.highGamma;
    }

    /**
     * @return
     */
    public int getPoorSignalLevel() {
        return this.poorSignalLevel;
    }

    /**
     * @return
     */
    public long timeElapsed() {
        return this.timeElapsed;
    }

    public String getTimeStamp() {
        return this.timeStamp;
    }
}
