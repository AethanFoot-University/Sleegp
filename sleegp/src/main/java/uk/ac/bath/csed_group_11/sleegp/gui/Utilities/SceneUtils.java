package uk.ac.bath.csed_group_11.sleegp.gui.Utilities;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SceneUtils<controller> {


    private static List<Resource> resourceCache = new ArrayList<>();

    public static void displayOnPopupFXThread(String message){
        new Thread(()-> Platform.runLater(()->SceneUtils.displayPopUp(message))).start();
    }

    public static void displayPopUp(String message) {
        defaultPopupConfig(message, actionEventsList());
    }

    public static int comboBoxPopup(String message, List objects){
        System.out.println("Displaying");
        final int[] selected = {0};
        AtomicBoolean done = new AtomicBoolean(false);
        Platform.runLater(()->{
        ComboBox comboBox = new ComboBox();
        for(Object b : objects) comboBox.getItems().add(b);
        EventHandler<ActionEvent> event = event1 -> {
            selected[0] =
                comboBox.getSelectionModel().getSelectedIndex();
            done.set(true);
        };

        defaultPopupConfig(message, actionEventsList(event), comboBox);
        });

        while(!done.get()) try{Thread.sleep(100); } catch(Exception e){}

        return selected[0];
    }

    private static List<EventHandler<ActionEvent>> actionEventsList(EventHandler<ActionEvent>... events){
        List<EventHandler<ActionEvent>> eventsList = new ArrayList<EventHandler<ActionEvent>>();

        for(EventHandler<ActionEvent> e : events) eventsList.add(e);
        return eventsList;
    }

    private static void defaultPopupConfig(String message,
                                           List<EventHandler<ActionEvent>> closeEvents,
                                           Node... nodes){
        var popUp = new Stage();
        popUp.initModality(Modality.WINDOW_MODAL);

        var messageLabel = new Label(message);
        messageLabel.getStylesheets().add("style.css");
        messageLabel.getStyleClass().add("text-label");

        var okButton = new Button("Ok");

        EventHandler<ActionEvent> masterEvent = (ActionEvent event) -> {
            for(EventHandler<ActionEvent> e : closeEvents) e.handle(event);
            popUp.close();
        };

        okButton.setOnAction(masterEvent);
        okButton.getStylesheets().add("style.css");
        okButton.getStyleClass().add("basic-button");

        var vBox = new VBox();
        vBox.getChildren().add(messageLabel);

        for(Node n : nodes) vBox.getChildren().add(n);

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

    public static void main(String[] args){

    }

}
