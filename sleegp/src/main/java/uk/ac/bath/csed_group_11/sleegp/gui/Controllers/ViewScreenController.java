package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * To be written by: Mathew
 */


public class ViewScreenController implements Initializable {
    @FXML
    LineChart mainChart;

    @FXML
    CategoryAxis xAxis;

    @FXML
    NumberAxis yAxis;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
