package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.ScrollEvent;
import uk.ac.bath.csed_group_11.sleegp.gui.Experiment.ExperimentManager;

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
}
