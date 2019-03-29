package uk.ac.bath.csed_group_11.sleegp.logic.Classification;

import java.awt.*;

public class Plot{

    private double timeElapsed;
    private double level;

    public Plot(double timeElapsed, double level) {
        this.timeElapsed = timeElapsed;
        this.level = level;
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(double timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    @Override
    public String toString(){
        return timeElapsed+", "+level;
    }
}
