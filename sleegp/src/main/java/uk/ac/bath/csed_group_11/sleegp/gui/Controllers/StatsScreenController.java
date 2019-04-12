package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import uk.ac.bath.csed_group_11.sleegp.logic.data.User;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class StatsScreenController implements Initializable {
    private User user;

    @FXML
    AnchorPane main;

    @FXML
    Text levelText;

    @FXML
    Text xpText;

    @FXML
    ProgressBar progress;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            user = User.loadDefaultUser();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        addLevel();
        addPoints();
        addProgress();
    }

    public void addLevel() {
        Platform.runLater(() ->{
            levelText.setText(user.returnLvl() + "");
        });
    }

    public void addPoints() {
        Platform.runLater(() ->{
            xpText.setText(user.returnPoints() + "");
        });
    }

    public void addProgress() {
        Platform.runLater(() ->{
            progress.setProgress(user.getPercentageProgress());
        });
    }
}
