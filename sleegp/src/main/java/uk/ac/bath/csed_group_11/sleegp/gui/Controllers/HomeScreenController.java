package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
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

    private void setView(String FXML){
        Resource resource = new Resource(FXML);

        try {
            mainPane.getChildren().setAll(resource.getNode());
        } catch (IOException e) {
            System.out.println("Failed to change view to: "+FXML);
            e.printStackTrace();
        }
    }
}
