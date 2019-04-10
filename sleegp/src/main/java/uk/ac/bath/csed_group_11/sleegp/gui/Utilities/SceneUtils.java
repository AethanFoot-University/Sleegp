package uk.ac.bath.csed_group_11.sleegp.gui.Utilities;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SceneUtils<controller> {


    private static List<Resource> resourceCache = new ArrayList<>();

    public static void displayPopUp(String message) {
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

    public Resource<controller> getResource(String FXML){
        Resource r = lookupCachedResource(FXML);

        if(r ==null) r= new Resource<>(FXML);

        return r;
    }


    public static void clearCache(){
        resourceCache = new ArrayList<Resource>();
    }
    public static void setView(Stage stage, String FXML){
        Resource resource = lookupCachedResource(FXML);

        try {
            if(resource == null) {
                resource = new Resource(FXML);
            }

            if(resource.getNode().getScene() == null){
                stage.setScene(new Scene(resource.getNode()));
            }
            else{
                stage.setScene(resource.getNode().getScene());
            }


         } catch (IOException e) {
            System.out.println("Failed to change view to: "+FXML);
            e.printStackTrace();
        }
    }

    public static void cacheResource(Resource r){
        resourceCache.add(r);
    }

    public static Resource lookupCachedResource(String resource){
        for(Resource r : resourceCache) if(r.getResourceLocation().equals(resource)) return r;
        return null;
    }

}
