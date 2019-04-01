package uk.ac.bath.csed_group_11.sleegp.gui;

//
//  GUIMain
//  eegapp
//
//  Created by Søren Mortensen on 2019-02-22.
//  Copyright © 2019 CSED Group 11. All rights reserved.
//

import javafx.stage.Stage;
import uk.ac.bath.csed_group_11.sleegp.gui.Views.HomeScreenController;
import uk.ac.bath.csed_group_11.sleegp.gui.Views.Resource;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.StageLoader;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.StageRunnable;

import java.io.File;

public class GUIMain {

    public static void loadMainUI(String[] args){

        //Sets the particular set of steps needed to load the GUI
        StageRunnable<HomeScreenController> setup = new StageRunnable<HomeScreenController>() {
            @Override
            protected Resource<HomeScreenController> setupStage(Stage stage) {
                System.out.println(getClass().getResource("").getPath());

                System.out.println("File: "+new File("").toPath());

                Resource<HomeScreenController> resource = new Resource<HomeScreenController>(
                    "HomeScreen.fxml");

                stage.setTitle("SlEEG");
                return resource;
            }
        };

        //Class used to load the stage and capture the controller instance
        StageLoader<HomeScreenController> homeStage = new StageLoader<HomeScreenController>();

        //Getting access to the home screen controller
        HomeScreenController homeScreenController = homeStage.open(args, setup);

    }

    public static void main(String[] args){
        loadMainUI(args);
    }


}
