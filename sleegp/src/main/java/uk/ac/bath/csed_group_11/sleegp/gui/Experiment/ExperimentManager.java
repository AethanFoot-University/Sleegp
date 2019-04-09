package uk.ac.bath.csed_group_11.sleegp.gui.Experiment;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ExperimentManager {

    public static String[] FXML = {"AnalyseScreen.fxml", "AnalyseScreenVersion2.fxml" };

    private static String VIEW = null;

    private static long startTime = 0;
    private static List<Flag> FLAG_LIST = new ArrayList<>();
    private static boolean experimentEnded = false;
    private static boolean experimentMode = false;
    private static Runnable experimentEnd = null;
    private static long endTime = 0;

    public static void promptUserExperimentChoice(){
        VIEW = (String) JOptionPane.showInputDialog(null, "Choose a view to run: ",
            "Experiment Chooser", JOptionPane.QUESTION_MESSAGE, null,FXML,FXML[1]);
    }

    public static String getVIEW() {
        return VIEW;
    }

    public static void startExperiment(){
        startTime = System.currentTimeMillis();
        System.out.println("Start: "+startTime);
        notify("START");
    }

    public static void notify(String flag){
        FLAG_LIST.add(new Flag(flag, System.currentTimeMillis()-startTime));
    }

    public static void endExperiment(){
        endTime = System.currentTimeMillis();
        System.out.println("End: "+endTime);
        notify("END");
        experimentEnded = true;
        experimentMode = false;
        if(experimentEnd !=null) new Thread(experimentEnd).start();
    }

    public boolean isExperimentStarted() {
        return experimentEnded;
    }

    public static boolean isExperimentMode() {
        return experimentMode;
    }

    public static void setExperimentMode(boolean experimentMode) {
        ExperimentManager.experimentMode = experimentMode;
    }

    public static void setOnExperimentEnd(Runnable experimentEnd) {
        ExperimentManager.experimentEnd = experimentEnd;
    }

    public static long getStartTime() {
        return startTime;
    }

    public static long getEndTime() {
        return endTime;
    }

    public static List<Flag> getFlagList() {
        return FLAG_LIST;
    }

    public static void main(String[] args){
    }
}
