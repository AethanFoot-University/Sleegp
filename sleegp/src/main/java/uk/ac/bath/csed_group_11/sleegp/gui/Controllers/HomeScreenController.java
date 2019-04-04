package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import uk.ac.bath.csed_group_11.sleegp.gui.Experiment.ExperimentManager;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.SceneUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Author: Mathew Allington
 */
public class HomeScreenController implements Initializable {

    @FXML
    AnchorPane mainPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Setting up main home screen controller");
        if(ExperimentManager.isExperimentMode())ExperimentManager.startExperiment();

    }

    public void capture(){
        SceneUtils.setView(getStage(), "CaptureScreen.fxml");
        if(ExperimentManager.isExperimentMode()) ExperimentManager.notify("Start:Capture");
    }

    public void view(){
        if(ExperimentManager.isExperimentMode()) ExperimentManager.notify("Start:View");
        SceneUtils.setView(getStage(), "ViewScreen.fxml");
    }

    public void analyse(){
        if(ExperimentManager.isExperimentMode()) ExperimentManager.notify("Start:Analyse");
        SceneUtils.setView(getStage(), "AnalyseScreen.fxml");
    }

    public void switchUser(){

    }

    public void settings(){

    }
    public Stage getStage(){
        return ((Stage)mainPane.getScene().getWindow());
    }

}
