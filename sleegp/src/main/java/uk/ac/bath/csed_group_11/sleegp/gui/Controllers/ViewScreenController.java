package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uk.ac.bath.csed_group_11.sleegp.gui.Experiment.ExperimentManager;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.SceneUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * To be written by: Mathew
 */


public class ViewScreenController implements Initializable {
    @FXML
    LineChart mainChart;

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

    private boolean buttonPlay = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void play(){
        if(buttonPlay) {
            if(ExperimentManager.isExperimentMode()) ExperimentManager.notify("Action:Play");
            playButton.setText("■");
            buttonPlay = false;
        }
        else{
            if(ExperimentManager.isExperimentMode()) ExperimentManager.notify("Action:Stop");
            if(ExperimentManager.isExperimentMode()) ExperimentManager.endExperiment();
            playButton.setText("▶️");
            buttonPlay = true;
        }
    }

    public void setSpeed(double speed){
        speedText.setText("("+speed+"x)");
    }

    public void back(){
        SceneUtils.setView((Stage)playButton.getScene().getWindow(), "HomeScreen.fxml");
    }

    public void exportToCSV(){

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
        setSpeed(64);
    }

    public void setSpeed1024(){
        setSpeed(1024);
    }
}
