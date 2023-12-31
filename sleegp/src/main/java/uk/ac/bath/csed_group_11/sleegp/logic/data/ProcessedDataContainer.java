package uk.ac.bath.csed_group_11.sleegp.logic.data;

import uk.ac.bath.csed_group_11.sleegp.logic.Classification.Plot;
import uk.ac.bath.csed_group_11.sleegp.logic.util.FileTools;
import uk.ac.bath.csed_group_11.sleegp.logic.util.MathUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProcessedDataContainer extends ArrayList<Plot>  implements Serializable {

    public static String EXTENSION = ".sd";
    public ProcessedDataContainer(List<Plot> plots) {
        super(plots);
    }

    public void mapLevel(double min, double max){
        double minLevel = minLevel();
        double maxLevel = maxLevel();

        for(Plot plot : this){
            double level = plot.getLevel();
            plot.setLevel((max-min) * (level - minLevel) / (maxLevel-minLevel) + min);
        }
    }

    public void applyMovingAverage(int movingWindow){
        for(int i=0; i< this.size(); i++){
            MathUtils.movingPlotAverage(movingWindow).transform(this, i, this);
        }
    }

    private double maxLevel(){
        double max =0;
        for(Plot p : this){
            if(p.getLevel() > max) max = p.getLevel();
        }
        return max;
    }

    private double minLevel(){
        double min =Double.MAX_VALUE;
        for(Plot p : this){
            if(p.getLevel() < min) min = p.getLevel();
        }
        return min;
    }

    /**
     * Saves the container to a file
     * @param firectory The File Directory (Firectory) to be saved to
     * @return Whether or not the save was successful
     */
    public boolean saveToFile(File firectory) throws FileNotFoundException {
        return FileTools.saveObject(this, firectory, EXTENSION);
    }
}
