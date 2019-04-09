package uk.ac.bath.csed_group_11.sleegp.gui.Utilities;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SceneUtils<controller> {


    private static List<Resource> resourceCache = new ArrayList<>();

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
