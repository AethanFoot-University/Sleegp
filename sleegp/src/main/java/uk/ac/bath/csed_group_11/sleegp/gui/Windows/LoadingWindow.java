package uk.ac.bath.csed_group_11.sleegp.gui.Windows;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uk.ac.bath.csed_group_11.sleegp.gui.Controllers.LoadingScreenController;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.Resource;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.StageLoader;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.StageRunnable;

public class LoadingWindow {

private static StageRunnable<LoadingScreenController> SETUP =
    new StageRunnable<LoadingScreenController>() {
        @Override
        protected Resource<LoadingScreenController> setupStage(Stage stage) {
            Resource<LoadingScreenController> resource = new Resource<LoadingScreenController>("LoadingScreen.fxml");
            stage.initStyle(StageStyle.UNDECORATED);

            return resource;
        }
    };

private LoadingScreenController controller = null;
private String[] args;

    public LoadingWindow(String[] args) {
        this.args = args;
    }

    public void showLoadingScreen(int wait){
        if(controller == null){
            controller = new StageLoader<LoadingScreenController>().open(args, SETUP);
        }
        else{
            controller.showWindow();
        }

        try {
            Thread.sleep(wait);
            while(controller.isExperimentModeActive() &&!controller.isExperimentModeChosen()) Thread.sleep(100);
        } catch (InterruptedException e) {}
    }

    public void hideLoadingScreen(){
        if(controller !=null){
            controller.hideWindow();
        }
    }

    public LoadingScreenController getController() {
        return controller;
    }
}
