package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoadingScreenController {
    @FXML
    AnchorPane mainPane;

    public void hideWindow(){
        Platform.runLater(()->{getStage().hide(); });
    }

    public void showWindow() {
        Platform.runLater(() -> {
            getStage().show();
        });
    }

    private Stage getStage(){
        return (Stage) mainPane.getScene().getWindow();
    }
}
