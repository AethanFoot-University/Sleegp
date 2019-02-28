package uk.ac.bath.csed_group_11.logic.data;

import uk.ac.bath.csed_group_11.logic.util.Load;
import uk.ac.bath.csed_group_11.logic.util.Save;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mathew Allington
 */
public class EpochContainer implements Serializable {
    static final long serialVersionUID = -529434607952910606L;
    public final String EXTENSION = ".ec";
    private List<Epoch> data;
    private transient File autoSaveLocation = null;
    private transient long lastSave = 0;
    private transient long savePeriod = 0;

    /**
     * Creates an empty container instance
     */
    public EpochContainer() {
        data = new ArrayList<Epoch>();
    }

    /**
     * @param file
     */
    public static EpochContainer loadContainerFromFile(File file) throws IOException {
        Load loader = new Load(file);
        Object loaded = loader.load();

        if (loaded.getClass().equals(EpochContainer.class)) {
            return (EpochContainer) loaded;
        } else {
            return null;
        }

    }

    public String genCSV() {
        String app = data.get(0).getCSVHeader() + "\n";
        for (Epoch e : data) {
            app += e.getCSV() + "\n";
        }
        return app;
    }

    /**
     * @return
     */
    public int size() {
        return data.size();
    }

    /**
     * @param id
     * @return
     */
    public Epoch getEpoch(int id) {
        return data.get(id);
    }

    /**
     * @param e
     */
    public void addEpoch(Epoch e) {
        data.add(e);
        new Thread(() -> {
            autoSave();
        }).start();
    }

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

    public void setAutoSave(File file, long timePeriod) {
        this.autoSaveLocation = file;
        this.lastSave = System.currentTimeMillis();
        this.savePeriod = timePeriod;
    }

    /**
     * @param firectory
     * @return
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
}
