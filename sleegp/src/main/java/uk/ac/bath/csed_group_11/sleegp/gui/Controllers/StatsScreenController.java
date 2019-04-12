package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.*;

public class StatsScreenController implements Initializable {

    public static List<String> names = Arrays.asList("Soren", "Mathew", "Tom", "Sam", "Christophe", "Aethan", "Xander", "Gen");


    List<Slider> sliders = new ArrayList<Slider>();

    @FXML
    AnchorPane main;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

  /*  public void genSliders(int id, String name){
        HBox h= new HBox();
        Slider s = new Slider();
        s.setMax(100);
        s.setMin(0);

        s.valueProperty().addListener(new ChangeListener<ObservableValue>() {

            @Override
            public void changed(ObservableValue arg0, ObservableValue old,
                                ObservableValue newValue) {
               for(int i =0; i< sliders.size(); i++){
                   if(i!=id){
                       Slider s = sliders.get(i);
                       s.setValue(s.getValue() - (newValue.));
                   }
               }

            }
        });
    } */


}
