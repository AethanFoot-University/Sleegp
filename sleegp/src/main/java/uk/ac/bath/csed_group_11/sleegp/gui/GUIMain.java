package uk.ac.bath.csed_group_11.sleegp.gui;

//
//  GUIMain
//  eegapp
//
//  Created by Søren Mortensen on 2019-02-22.
//  Copyright © 2019 CSED Group 11. All rights reserved.
//

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uk.ac.bath.csed_group_11.sleegp.gui.Controllers.HomeScreenController;
import uk.ac.bath.csed_group_11.sleegp.gui.Controllers.LoadingScreenController;
import uk.ac.bath.csed_group_11.sleegp.gui.Experiment.ExperimentManager;
import uk.ac.bath.csed_group_11.sleegp.gui.Experiment.Flag;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.Resource;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.StageLoader;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.StageRunnable;
import uk.ac.bath.csed_group_11.sleegp.gui.Windows.LoadingWindow;

public class GUIMain {

    public static void loadMainUI(String[] args){

        //Class used to load the stage and capture the controller instance
        StageLoader<HomeScreenController> homeStage = new StageLoader<HomeScreenController>();

        //Loads loading screen while setup is taking place
        LoadingWindow loadingWindow = new LoadingWindow(args);
        loadingWindow.showLoadingScreen();
        //This is where setup should happen:

        try{ Thread.sleep(3000); } catch(Exception e) {}

        loadingWindow.hideLoadingScreen();
        if(loadingWindow.getController().isExperimentModeActive()) setupExperiment();


        //Sets the particular set of steps needed to load the GUI
        StageRunnable<HomeScreenController> setup = new StageRunnable<HomeScreenController>() {
            @Override
            protected Resource<HomeScreenController> setupStage(Stage stage) {
                Resource<HomeScreenController> resource = new Resource<HomeScreenController>("HomeScreen.fxml");

                if(loadingWindow.getController().isExperimentModeActive()) {
                    stage.setTitle("SlEEG - Experiment Mode");
                }
                else stage.setTitle("SlEEG");

                stage.setMinWidth(300);
                stage.setMinHeight(300);

                return resource;
            }
        };

        //Getting access to the home screen controller
        HomeScreenController homeScreenController = homeStage.open(args, setup);



    }

    public static void setupExperiment(){
        ExperimentManager.setExperimentMode(true);
        ExperimentManager.setOnExperimentEnd(()->{
            System.out.println("Experiment Completed...");
            System.out.println("Experiment Duration: "+(ExperimentManager.getEndTime()-ExperimentManager.getStartTime()));

            for(Flag f : ExperimentManager.getFlagList()) System.out.println(f);
        });
    }

    public static void main(String[] args){
        loadMainUI(args);
    }


}
