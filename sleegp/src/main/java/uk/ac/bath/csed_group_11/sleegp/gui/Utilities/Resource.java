package uk.ac.bath.csed_group_11.sleegp.gui.Utilities;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * This class provides a way of loading a FXML page and getting its attached
 * controller so that the instigating class has a way of interacting with the
 * GUI it has just loaded
 *
 * Note: This class is taken from a previous project of mine
 * @author mathew allington
 */
public class Resource<ControllerType> {

    private FXMLLoader LOADER;
    private String RESOURCE;
    private Parent node = null;
    private ControllerType controller = null;

    /**
     * Upon initialisation it stores the resource and creates a new FXML loader
     *
     * @param res FXML page to be loaded
     */
    public Resource(String res) {
        System.out.println("Starting in: "+ getClass().getResource("/").getPath());
        LOADER = new FXMLLoader();
        RESOURCE = res;

        try {
            getNode();
        } catch (IOException e) {
            System.out.println("Failed to fetch node");
        }
    }

    /**
     * Loads the resource file an outputs as a node to be loaded into a Stage/
     * Parent node
     *
     * @return Node to be loaded
     * @throws IOException
     */
    public Parent getNode() throws IOException {

        var classLoader = Thread.currentThread().getContextClassLoader();
        return (node ==null) ?
            (node = LOADER.load(classLoader.getResource(RESOURCE).openStream())) : node;
    }

    /**
     * Returns the controller instance attached to the loaded node
     *
     * @return Controller instance
     * @throws IOException
     */
    public ControllerType getController() throws IOException {

        return (controller ==null) ? (controller = LOADER.getController()) : controller;
    }

    /**
     * Sets the root component of the loaded FXMl document
     * @param root to be set
     */
    public void setRoot(Object root){
        LOADER.setRoot(root);
    }

}