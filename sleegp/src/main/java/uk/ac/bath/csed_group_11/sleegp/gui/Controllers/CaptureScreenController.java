package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import uk.ac.bath.csed_group_11.sleegp.cli.SleegpConstants;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.FilePicker;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.SceneUtils;
import uk.ac.bath.csed_group_11.sleegp.logic.data.DataCouple;
import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;
import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.data.User;
import uk.ac.bath.csed_group_11.sleegp.logic.hardware.Headset;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * To be written by Tom and Soren
 */
public class CaptureScreenController implements Initializable {
    @FXML
    AnchorPane mainPane;

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

    @FXML
    private StackPane chartPane;

    private AreaChart<String, Number> chart;

    private Headset headset;

    private EpochContainer epochContainer;

    private File outputFile;

    private boolean connected = false;

    private boolean saveLocChosen = false;
    private boolean dataActuallyRecieved = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.epochContainer = new EpochContainer();

        var xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(Arrays.asList(
            "a", "m", "Δ", "Θ", "lα", "hα", "lβ", "hβ", "lγ", "hγ", "s"
        )));
        xAxis.setLabel("Value");
        xAxis.setAutoRanging(true);
        xAxis.setAnimated(false);

        var yAxis = new NumberAxis();
        yAxis.setLowerBound(100.0);
        yAxis.setUpperBound(1000.0);
        yAxis.setLabel("Level");
        yAxis.setAutoRanging(true);
        yAxis.setAnimated(false);

        this.chart = new AreaChart<>(xAxis, yAxis);
        this.chart.setTitle("Live Data");
        this.chart.setAnimated(false);

        var series = new XYChart.Series<String, Number>();
        series.setName("Latest");

        series.getData().add(new XYChart.Data<>("a", 0));
        series.getData().add(new XYChart.Data<>("m", 0));
        series.getData().add(new XYChart.Data<>("Δ", 0));
        series.getData().add(new XYChart.Data<>("Θ", 0));
        series.getData().add(new XYChart.Data<>("lα", 0));
        series.getData().add(new XYChart.Data<>("hα", 0));
        series.getData().add(new XYChart.Data<>("lβ", 0));
        series.getData().add(new XYChart.Data<>("hβ", 0));
        series.getData().add(new XYChart.Data<>("lγ", 0));
        series.getData().add(new XYChart.Data<>("hγ", 0));
        series.getData().add(new XYChart.Data<>("s", 0));

        Platform.runLater(() -> this.chart.getData().add(series));
        this.chartPane.getChildren().add(chart);

        var controller = this;

        this.headset = new Headset() {
            @Override
            public void update(Epoch data) {
                if(!dataActuallyRecieved) dataActuallyRecieved = true;
                epochContainer.addEpoch(data);
                controller.updateChart(data);
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
        this.startRecordingButton.setDisable(!connected);

        this.connected = connected;
    }

    public boolean isSaveLocChosen() {
        return saveLocChosen;
    }

    public void setSaveLocChosen(boolean saveLocChosen) {
        this.saveLocChosen = saveLocChosen;
    }

    public void connectHeadset() {
        if (this.headset.connect()) {
            this.setConnected(true);
        } else {
            this.setConnected(false);
            System.err.println("Failed to connect to headset.");
            SceneUtils.displayPopUp("Failed to connect to headset.");
        }
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
        dataActuallyRecieved = false;
        this.headset.capture();

        this.disconnectButton.setDisable(true);
        this.saveLocationButton.setDisable(true);
        this.startRecordingButton.setDisable(true);
        this.stopRecordingButton.setDisable(false);
    }

    public void stopRecording() {
        try {
            this.headset.stopRecording();

            if(dataActuallyRecieved) {
                if (this.isSaveLocChosen()) {
                    this.epochContainer.saveToFile(this.outputFile);
                }

                try {
                    var user = User.loadDefaultUser();
                    user.add(new DataCouple(this.epochContainer));
                    user.saveToFile(new File(SleegpConstants.RELATIVE_USER_FILE));

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else{
                SceneUtils.displayOnPopupFXThread("No data was captured.");
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.disconnectButton.setDisable(false);
            this.saveLocationButton.setDisable(false);
            this.startRecordingButton.setDisable(false);
            this.stopRecordingButton.setDisable(true);
        }


    }

    private void updateChart(Epoch data) {
        var series = this.chart.getData().get(0);

        Platform.runLater(() -> {
            series.getData().get(0).setYValue(data.getAttention());
            series.getData().get(1).setYValue(data.getMeditation());
            series.getData().get(2).setYValue(data.getDelta() / 10000);
            series.getData().get(3).setYValue(data.getTheta() / 1000);
            series.getData().get(4).setYValue(data.getLowAlpha() / 100);
            series.getData().get(5).setYValue(data.getHighAlpha() / 100);
            series.getData().get(6).setYValue(data.getLowBeta() / 100);
            series.getData().get(7).setYValue(data.getHighBeta() / 100);
            series.getData().get(8).setYValue(data.getLowGamma() / 100);
            series.getData().get(9).setYValue(data.getHighGamma() / 100);
            series.getData().get(10).setYValue(data.getPoorSignalLevel());
        });
    }

    public void back() {
        SceneUtils.setView((Stage)this.mainPane.getScene().getWindow(), "HomeScreen.fxml");
    }
}
