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
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.Resource;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.StageLoader;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.StageRunnable;
import uk.ac.bath.csed_group_11.sleegp.gui.Windows.LoadingWindow;

public class GUIMain {

    public static void loadMainUI(String[] args){

        //Sets the particular set of steps needed to load the GUI
        StageRunnable<HomeScreenController> setup = new StageRunnable<HomeScreenController>() {
            @Override
            protected Resource<HomeScreenController> setupStage(Stage stage) {
        Resource<HomeScreenController> resource = new Resource<HomeScreenController>("HomeScreen.fxml");

                stage.setTitle("SlEEG");
                stage.setMinWidth(300);
                stage.setMinHeight(300);

                return resource;
            }
        };



        //Class used to load the stage and capture the controller instance
        StageLoader<HomeScreenController> homeStage = new StageLoader<HomeScreenController>();

        //Loads loading screen while setup is taking place
        LoadingWindow loadingWindow = new LoadingWindow(args);

        loadingWindow.showLoadingScreen();

        //This is where setup should happen:

        try{ Thread.sleep(3000); } catch(Exception e) {}

        loadingWindow.hideLoadingScreen();

        //Getting access to the home screen controller
        HomeScreenController homeScreenController = homeStage.open(args, setup);



    }

    public static void main(String[] args){
        loadMainUI(args);
    }


}
