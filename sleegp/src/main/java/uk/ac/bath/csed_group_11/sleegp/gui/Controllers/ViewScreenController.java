package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.dialog.Dialogs;
import uk.ac.bath.csed_group_11.sleegp.gui.Experiment.ExperimentManager;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.FilePicker;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.SceneUtils;
import uk.ac.bath.csed_group_11.sleegp.logic.data.DataCouple;
import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.data.User;
import uk.ac.bath.csed_group_11.sleegp.logic.util.FileTools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * To be written by: Mathew
 */


public class ViewScreenController implements Initializable {
    @FXML
    LineChart<?,?> mainChart;

    @FXML
    CategoryAxis xAxis;

    @FXML
    NumberAxis yAxis;

    @FXML
    Button playButton;

    @FXML
    Slider scrollBar;

    @FXML
    Text speedText;

    @FXML
    Text totalTime;

    @FXML
    Text currentTime;

    @FXML
    ComboBox<String> attributeBox;

    @FXML
    ColorPicker colourPicker;


    private GraphPlayer graphPlayer = null;
    private EpochContainer epochContainer = null;

    private boolean clickToPlay = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        attributeBox.getItems().addAll("attention","meditation","delta",
            "highAlpha","lowBeta",	 "highBeta","lowGamma","highGamma"," poorSignalLevel");

        attributeBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String old, String newS) {
                if(graphPlayer != null){
                    graphPlayer.setAttribute(newS);
                }
            }
        });
        /*colourPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                if(graphPlayer !=null && colourPicker !=null){



                    Color color = colourPicker.getValue();

                    graphPlayer.setColour(colourPicker.getValue());
                }
                else{
                    System.out.println("Graph Player is null: "+(graphPlayer ==null) +"\n Colour " +
                        "Picker is null: "+(colourPicker ==null));
                }
            }
        });*/

    }

    public void play(){
        if(clickToPlay) {
            if(ExperimentManager.isExperimentMode()) ExperimentManager.notify("Action:Play");
            playButton.setText("■");
            clickToPlay = false;

            playBackData();
        }
        else{
            if(ExperimentManager.isExperimentMode()) ExperimentManager.notify("Action:Stop");
            if(ExperimentManager.isExperimentMode()) ExperimentManager.endExperiment();
            playButton.setText("▶️");
            clickToPlay = true;

            stopPlayBack();
        }
    }

    public void back(){
        if(!clickToPlay) play();
        SceneUtils.setView(getStage(), "HomeScreen.fxml");
    }

    public void openECFile() {

            FilePicker filePicker = new FilePicker(".ec", "Epoch Container", "");
            File epLocation = filePicker.getFile(getStage(), FilePicker.OPEN);

            if(epLocation !=null) {
                new Thread(() -> {
                    Platform.runLater(() -> {

                        try {

                            loadEpochContainer(EpochContainer.loadContainerFromFile(epLocation));

                        } catch (IOException e) {
                            System.out.println("Epoch Container Load Failed");

                        } catch (ClassNotFoundException e) {
                            System.out.println("Mis-labeled file!");
                        }

                    });
                }).start();
            }
    }
    private void loadEpochContainer(EpochContainer epoch){
        epochContainer = epoch;
        prepareECView();
        setupPlayBack();
    }

    public void openUserFile(){

        System.out.println("Loading file from user");
        new Thread(()->{

            try {
                User u = User.loadDefaultUser();

                List<String> options = new ArrayList<String>();

                if(u!=null &&u.size()>0) for(DataCouple coup :
                    u) options.add(coup.getRawData().toString());
                int selected = SceneUtils.comboBoxPopup("Please select a container to playback",
                    options);

                Platform.runLater(()->{if(selected>=0)loadEpochContainer(u.get(selected).getRawData());});

                System.out.println(selected);
            } catch (IOException e) {} catch (ClassNotFoundException e) {}


        }).start();
    }

    public void exportToCSV(){

                if (epochContainer != null) {

                    FilePicker filePicker = new FilePicker(".csv", "Comma-Separated Values", "Export.csv");
                    File exportLocation = filePicker.getFile(getStage(), FilePicker.SAVE);

                    if(exportLocation !=null) {
                        new Thread(() -> {
                            String csv = epochContainer.genCSV();
                            try {
                                FileTools.write(exportLocation.getPath(), csv);
                            } catch (IOException e) {
                                System.out.println("CSV write failed!");
                            }
                        }).start();
                    }
                } else {
                    SceneUtils.displayPopUp("Please load some data first into the view to export");
                }

    }

    public void seek(){
        System.out.println("Seeking");
        if(graphPlayer !=null){
            graphPlayer.setTimeProgressed((long)(graphPlayer.getEndTime() *(scrollBar.getValue()/scrollBar.getMax())));
        }
    }

    //Playback

    public void setWindow(int window){
        if(graphPlayer!=null) graphPlayer.setWindow(window);
    }

    private void playBackData(){
        if(graphPlayer!= null) graphPlayer.setPlay(true);
    }

    private void stopPlayBack(){
        if(graphPlayer!= null) graphPlayer.setPlay(false);
    }

    private void prepareECView(){
        if(epochContainer !=null && epochContainer.size() >0){
            getStage().setTitle("SlEEG - "+epochContainer.getEpoch(0).getTimeStamp());
            setTimeFromMillis(totalTime, epochContainer.getEpoch(epochContainer.size()-1).timeElapsed());
        }
    }

    private void setupPlayBack(){
        if(epochContainer !=null && epochContainer.size() >0){

            if(graphPlayer !=null) graphPlayer.dispose();

            graphPlayer = new GraphPlayer(mainChart, epochContainer, "lowAlpha") {
                @Override
                public void sendUpdate(long timeProgressed) {

                    Platform.runLater(()->{
                        scrollBar.setValue(scrollBar.getMax() * ((double)timeProgressed/(double)graphPlayer.getEndTime()));
                        setTimeFromMillis(currentTime, timeProgressed);
                    });

                }

                @Override
                public void notifyEndState() {
                    currentTime.setText("Finished");
                    clickToPlay = false;
                    Platform.runLater(()->play());
                }
            };
            graphPlayer.setPlay(!clickToPlay);
        }
    }

    public void setSpeed(double speed){
        speedText.setText("("+speed+"x)");
        if(graphPlayer !=null) graphPlayer.setPlayBackSpeed(speed);
    }

    //Setting speeds
    //Yes, I know. Absolutely horrible

    public void setSpeedHalf(){
        setSpeed(0.5);
    }

    public void setSpeed1(){
        setSpeed(1);
    }

    public void setSpeed2(){
        setSpeed(2);
    }

    public void setSpeed4(){
        setSpeed(4);
    }

    public void setSpeed16(){
        setSpeed(16);
    }

    public void setSpeed64(){
        setSpeed(64);
    }

    public void setSpeed256(){
        setSpeed(256);
    }

    public void setSpeed1024(){
        setSpeed(1024);
    }

    //Windows
    public void setWindow16(){
        setWindow(16);
    }

    public void setWindow32(){
        setWindow(32);
    }

    public void setWindow64(){
        setWindow(64);
    }

    public void setWindow128(){
        setWindow(128);
    }

    public void setWindow256(){
        setWindow(256);
    }

    public void setWindow512(){
        setWindow(512);
    }

    public void setWindow1024(){
        setWindow(1024);
    }
    
    //Util Methods
    private void setTimeFromMillis(Text toSet, long millis){
        long hours = millis/(1000*60*60);
        millis = millis%(1000*60*60);

        long minutes = millis/(1000*60);
        millis = millis%(1000*60);

        long seconds = millis/(1000);
        millis = millis%(1000);

        if(toSet != null)toSet.setText(hours+":"+minutes+":"+seconds+"."+millis);
        else System.out.println("Failed to set time (null Text object)");
    }

    public Stage getStage(){
        return (Stage)playButton.getScene().getWindow();
    }
}
