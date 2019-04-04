package uk.ac.bath.csed_group_11.sleegp.gui.Utilities;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneUtils {
    public static void setView(Stage stage, String FXML){
        Resource resource = new Resource(FXML);

        try {
            stage.setScene(new Scene(resource.getNode()));
         } catch (IOException e) {
            System.out.println("Failed to change view to: "+FXML);
            e.printStackTrace();
        }
    }
}
