package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.Resource;

import java.io.IOException;
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
    }

    public void capture(){
        setView("CaptureScreen.fxml");
    }

    public void view(){
        setView("ViewScreen.fxml");
    }

    public void analyse(){
        setView("AnalyseScreen.fxml");
    }

    public void switchUser(){

    }

    public void settings(){

    }

    private void setView(String FXML){
        Resource resource = new Resource(FXML);

        try {
            ((Stage)mainPane.getScene().getWindow()).setScene(new Scene(resource.getNode()));
         } catch (IOException e) {
            System.out.println("Failed to change view to: "+FXML);
            e.printStackTrace();
        }
    }
}
