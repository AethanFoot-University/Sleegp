package uk.ac.bath.csed_group_11.sleegp.gui.Utilities;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StageLoader<Controller> extends Application{
    /**
     * Lets any other class know that a GUI is currently being instantiated and it has to wait
     */
    private static boolean initSemaphore =false;

    /**
     * Where the output of a new GUI controller is temporarily held
     */
    private static Object tempUserInterface = null;

    /**
     * Lets the instantiating thread know that it still has to wait, until set false when the UI is ready
     */
    private static boolean  userInterfaceSemaphore = true;

    /**
     * This specifies whether or not the main JavaFX thread has been initialised already
     */
    private static boolean instanceHasStarted = false;

    /**
     * Temporary storage for changes needed to be made to the stage
     */
    private static StageRunnable temporaryStageRunnable =null;




    /**
     * Initialises the GUI
     * @param primaryStage This is what the loaded scene will be added to
     */
    @Override
    public void start(Stage primaryStage) {

        Platform.runLater(()->{
             if(temporaryStageRunnable !=null) {
                 try {
                     //Runs stage setup and returns Resource with contained node
                     Resource r = temporaryStageRunnable.setupStage(primaryStage);
                     if(r!=null) {
                         //Sets the stage with the loaded node
                         primaryStage.setScene(new Scene(r.getNode()));

                         //Sets the temp object, to be returned to parent thread when complete
                         setTempUserInterface(r.getController());

                         primaryStage.show();
                     }
                     //Sets flag to false to notify other thread that process is complete
                     userInterfaceSemaphore = false;
                 } catch (Exception e){
                     System.out.println("Failed to setup stage");
                     e.printStackTrace();
                 }
             }
        });
    }


    /**
     * Gets temp user interface.
     *
     * @return the temp user interface
     */
    private synchronized static Object getTempUserInterface() {
        return StageLoader.tempUserInterface;
    }

    /**
     * @param tempUserInterface the temp user interface to be set.
     */
    private static synchronized void setTempUserInterface(Object tempUserInterface) {
        StageLoader.tempUserInterface = tempUserInterface;
    }

    /**
     * This method employs a series of thread safe techniques to start an instance that is static by nature and retrieve
     * the controller associated with it. Once retrieved, an infinite number of these instances can be recreated
     *
     * @param args commandline arguments
     * @param cGUI Instantiating instance that wishes to create a new UI
     * @return Returns tbe controller instance
     */
    private  static Object loadGUI(String[] args, StageLoader cGUI, StageRunnable runnable){
        //Checks to see if an instance is being made and waits like a good boy (or girl) for his turn
        while(initSemaphore);

        //If reached, this means a new instance can be made, then sets 'initSemaphore' to false so that other threads do
        //not interrupt its operation
        initSemaphore = true;

        //Sets flag to true to state an instance is being made
        userInterfaceSemaphore = true;

        //Sets stage runnable: Set of actions to be run when setting up stage
        temporaryStageRunnable = runnable;

        //Starts the GUI on a separate thread


        if(instanceHasStarted) {
            Platform.runLater(() -> {
                cGUI.start(new Stage());
            });
        } else{
            new Thread(()->launch(args)).start();
            instanceHasStarted = true;
        }

        //Waits for the interface to be made
        while(userInterfaceSemaphore) {
            try {
                Thread.sleep((int)(Math.random()*100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Once this point is reached, the interface has been initialised
        Object clientUI = cGUI.getTempUserInterface();
        initSemaphore = false;
        return clientUI;
    }

    /**
     * Loads and shows a stage with modifications dictated by the StageRunnable
     *
     * @param args     the commandline arguments
     * @param runnable modifications to be made
     * @return the controller associated with the stage
     */
    public Controller open(String[] args, StageRunnable<Controller> runnable){
        Object controller = loadGUI(args, this, runnable);

        try{
            Controller cont = (Controller) controller;
            return cont;
        }
        catch (Exception e){
            System.out.println("Failed to obtain controller");
            return null;
        }
    }
}
