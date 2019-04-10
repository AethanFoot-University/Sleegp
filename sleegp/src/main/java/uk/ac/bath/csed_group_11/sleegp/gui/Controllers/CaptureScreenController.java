package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.FilePicker;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.SceneUtils;
import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;
import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.hardware.Headset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * To be written by Tom and Soren
 */
public class CaptureScreenController implements Initializable {
    @FXML
    AnchorPane mainPane;

    @FXML
    private Button backButton;

    @FXML
    private Button connectButton;

    @FXML
    private Button disconnectButton;

    @FXML
    private Button saveLocationButton;

    @FXML
    private Button startRecordingButton;

    @FXML
    private Button stopRecordingButton;

    @FXML
    private TextField saveFilePath;

    private Headset headset;

    private EpochContainer epochContainer;

    private File outputFile;

    private boolean connected = false;

    private boolean saveLocChosen = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.epochContainer = new EpochContainer();

        this.headset = new Headset() {
            @Override
            public void update(Epoch data) {
                epochContainer.addEpoch(data);
            }
        };

        this.disconnectButton.setDisable(true);
        this.startRecordingButton.setDisable(true);
        this.stopRecordingButton.setDisable(true);
    }

    public EpochContainer getEpochContainer() {
        return epochContainer;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.disconnectButton.setDisable(!connected);
        this.connectButton.setDisable(connected);

        if (connected && this.isSaveLocChosen()) {
            this.startRecordingButton.setDisable(false);
        } else {
            this.startRecordingButton.setDisable(true);
        }

        this.connected = connected;
    }

    public boolean isSaveLocChosen() {
        return saveLocChosen;
    }

    public void setSaveLocChosen(boolean saveLocChosen) {
        if (saveLocChosen && this.isConnected()) {
            this.startRecordingButton.setDisable(false);
        } else {
            this.startRecordingButton.setDisable(true);
        }

        this.saveLocChosen = saveLocChosen;
    }

    public void connectHeadset() {
        if (this.headset.connect()) {
            this.setConnected(true);
        } else {
            this.setConnected(false);
            System.err.println("Failed to connect to headset.");
            this.displayPopUp("Failed to connect to headset.");
        }
    }

    private void displayPopUp(String message) {
        var popUp = new Stage();
        popUp.initModality(Modality.WINDOW_MODAL);

        var messageLabel = new Label(message);
        messageLabel.getStylesheets().add("style.css");
        messageLabel.getStyleClass().add("text-label");

        var okButton = new Button("Ok");
        okButton.setOnAction((ActionEvent event) -> popUp.close());
        okButton.getStylesheets().add("style.css");
        okButton.getStyleClass().add("basic-button");

        var vBox = new VBox();
        vBox.getChildren().add(messageLabel);
        vBox.getChildren().add(okButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));

        var popUpScene = new Scene(vBox);
        popUpScene.getStylesheets().add("style.css");

        popUp.setScene(popUpScene);
        popUp.show();
    }

    public void disconnectHeadset() {
        try {
            this.headset.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.setConnected(false);
        }
    }

    public void chooseSaveLocation() {
        var fp = new FilePicker(".ec", "Binary File", "untitled");
        var stage = (Stage)this.mainPane.getScene().getWindow();

        var file = fp.getFile(stage, FilePicker.SAVE);

        if (file != null) {
            this.saveFilePath.clear();
            this.saveFilePath.appendText(file.getAbsolutePath());

            this.outputFile = file;
            var autoSaveFile = new File(file.getAbsolutePath() + ".auto");

            this.epochContainer.reset();
            this.epochContainer.setAutoSave(autoSaveFile, 10000);

            this.setSaveLocChosen(true);
        }
    }

    public void startRecording() {
        // TODO: Check whether the capture was successful
        this.headset.capture();

        this.disconnectButton.setDisable(true);
        this.saveLocationButton.setDisable(true);
        this.startRecordingButton.setDisable(true);
        this.stopRecordingButton.setDisable(false);
    }

    public void stopRecording() {
        try {
            this.headset.stopRecording();
            this.epochContainer.saveToFile(this.outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.disconnectButton.setDisable(false);
            this.saveLocationButton.setDisable(false);
            this.startRecordingButton.setDisable(false);
            this.stopRecordingButton.setDisable(true);
        }
    }

    public void back() {
        SceneUtils.setView((Stage)this.mainPane.getScene().getWindow(), "HomeScreen.fxml");
    }
}
