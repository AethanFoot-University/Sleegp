package uk.ac.bath.csed_group_11.sleegp.logic.data;

import uk.ac.bath.csed_group_11.sleegp.logic.util.Load;
import uk.ac.bath.csed_group_11.sleegp.logic.util.MathUtils;
import uk.ac.bath.csed_group_11.sleegp.logic.util.Save;
import uk.ac.bath.csed_group_11.sleegp.logic.util.Transformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * The purpose if this class is to provide a serializable container
 * @author Mathew Allington
 */
public class EpochContainer implements Serializable {
    /**
     * This is serializable ID, set to static so that there is not an ID mis-match error when saving files
     */
    static final long serialVersionUID = -529434607952910606L;

    /**
     * Default file extension
     */
    public final String EXTENSION = ".ec";

    /**
     * List of all data frames
     */
    private List<Epoch> data;

    /**
     * Directory that file should be temporarily saved to
     */
    private transient File autoSaveLocation = null;

    /**
     * Last system time in millis that file was saved
     */
    private transient long lastSave = 0;

    /**
     * Period at which the class should wait to re-save temporary backup file
     */
    private transient long savePeriod = 5000;

    /**
     * Creates an empty container instance
     */
    public EpochContainer() {
        data = new ArrayList<Epoch>();
    }

    /**
     * Loads EpochContainer instance from specified file location
     * @param file Location of binary container file
     */
    public static EpochContainer loadContainerFromFile(File file) throws IOException, ClassNotFoundException {
        Load loader = new Load(file);
        Object loaded = loader.load();

        return (EpochContainer)loaded;
    }

    public EpochContainer(List<String> CSVRows){
        data = new ArrayList<Epoch>();

        for(String row : CSVRows){
            Epoch e = new Epoch(row);
            data.add(e);
        }

    }

    /**
     * Converts the container into the form of a CSV file
     * @return csv format
     */
    public String genCSV() {
        String app = data.get(0).getCSVHeader() + "\n";
        for (Epoch e : data) {
            app += e.getCSV() + "\n";
        }
        return app;
    }


    /**
     * The amount of epochs in the container
     *
     * @return Amount of epochs
     */
    public int size() {
        return data.size();
    }


    /**
     * Gets epoch object by ID
     *
     * @param id the point in the list
     * @return the epoch
     */
    public Epoch getEpoch(int id) {
        return data.get(id);
    }

    /**
     * Adds an epoch to the container and auto saves the .ec file if needed
     * @param e
     */
    public void addEpoch(Epoch e) {
        data.add(e);
        new Thread(() -> {
            autoSave();
        }).start();
    }

    /**
     * Auto saves the file if autoSaveLocation has been set and save period has been exceeded
     */
    private void autoSave() {

        if (autoSaveLocation != null && (System.currentTimeMillis() - lastSave) >= savePeriod) {
            try {
                saveToFile(this.autoSaveLocation);
                lastSave = System.currentTimeMillis();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("AutoSave Failed");
            }
        }
    }

    /**
     * Sets auto save period and directory
     * @param file file to be saved to
     * @param timePeriod Time in millis to wait before saving again
     */
    public void setAutoSave(File file, long timePeriod) {
        this.autoSaveLocation = file;
        this.lastSave = System.currentTimeMillis();
        this.savePeriod = timePeriod;
    }

    /**
     * Saves the container to a file
     * @param firectory The File Directory (Firectory) to be saved to
     * @return Whether or not the save was successful
     */
    public boolean saveToFile(File firectory) throws FileNotFoundException {
        if (firectory.toString().contains(EXTENSION)) {
            Save saver = new Save(firectory);
            saver.write(this);
        } else {
            System.out.println("Cannot save without proper extenson: " + EXTENSION);
        }
        return false;
    }


    /**
     * Gets a transformed list of data
     *
     * @param transform the transformation to be done on the data set
     * @return the transformed data
     */
    public  List<Double> getTransformedData(Transformation<Epoch, Double> transform){
        List<Double> newList = new ArrayList<Double>();

        for(int i=0; i< data.size(); i++){
            transform.transform(data, i, newList);
        }

        return newList;
    }

    public static void main(String[] args){


        //Example implementation of calculating a moving average for a specific attribute

        EpochContainer ec = new EpochContainer();

        for(int i=0; i< 30; i++){
            Epoch e = new Epoch(null, 0);
            e.setAttention((int)(Math.random()*500));
            e.setMeditation((int)(Math.random()*500));
            ec.addEpoch(e);
        }


        int avgWindow = 10;
        List<Double> attention = ec.getTransformedData(MathUtils.movingAverage("attention", avgWindow));
        List<Double> meditation = ec.getTransformedData(MathUtils.movingAverage("meditation", avgWindow));


    }


}
