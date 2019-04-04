package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.Resource;
import uk.ac.bath.csed_group_11.sleegp.logic.Classification.ClassificationUtils;
import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.data.ProcessedDataContainer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * To be written by: Aethan and Xander
 */
public class AnalyseScreenController implements Initializable {
    @FXML
    AnchorPane mainPane;

    @FXML
    private ComboBox<String> rawDataCombo;
    private ObservableList<String> rawDataComboData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rawDataCombo.setItems(rawDataComboData);

        rawDataCombo.setOnAction((event) -> {
            String stringSelected = rawDataCombo.getSelectionModel().getSelectedItem();
            System.out.println("ComboBox Action (selected: " + stringSelected + ")");
        });
    }

    public void back() {
        Resource resource = new Resource("HomeScreen.fxml");

        try {
            mainPane.getChildren().setAll(resource.getNode());
        } catch (IOException e) {
            System.out.println("Failed to change view to: HomeScreen.fxml");
            e.printStackTrace();
        }
    }

    /*public void process() {


        try {
            EpochContainer ec;
            ec = EpochContainer.loadContainerFromFile(new File(ecPath));

            ProcessedDataContainer processedDataContainer = ClassificationUtils.convertData(ec);

            //Saving to file
            processedDataContainer.saveToFile(new File((String) opts.get("--output")));


        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Unable to load container from file: " + e.toString());
            return;
        }
    }*/
}
