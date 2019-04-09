package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uk.ac.bath.csed_group_11.sleegp.gui.Experiment.ExperimentManager;

import java.net.URL;
import java.util.ResourceBundle;

public class LoadingScreenController implements Initializable {
    @FXML
    AnchorPane mainPane;

    @FXML
    Text infoText;

    private boolean experimentModeActive = false;
    private boolean experimentModeChosen = false;
    private int clicked =0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Loading screen...");
        mainPane.requestFocus();
    }


    public void hideWindow(){
        Platform.runLater(()->{getStage().hide(); });
    }

    public void showWindow() {
        Platform.runLater(() -> {
            getStage().show();
        });
    }



    public void clicked(){
        if((clicked> 2) &&!experimentModeActive){
            infoText.setOpacity(1.0);
            experimentModeActive = true;

            new Thread(()->{ExperimentManager.promptUserExperimentChoice();
            experimentModeChosen = true; }).start();
        }
        else{
            clicked ++;
        }
    }

    public boolean isExperimentModeChosen() {
        return experimentModeChosen;
    }

    private Stage getStage(){
        return (Stage) mainPane.getScene().getWindow();
    }

    public boolean isExperimentModeActive() {
        return experimentModeActive;
    }
}
