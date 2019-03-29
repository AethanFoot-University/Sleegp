package uk.ac.bath.csed_group_11.sleegp.logic.Classification;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;

import java.util.ArrayList;
import java.util.List;

public class DataSmoother {
    private List<Epoch> epochs;

    public DataSmoother(List<Epoch> epochs) {
        this.epochs = epochs;
    }

    public List<Plot> smooth(String reference){
        SplineInterpolator spline  = new SplineInterpolator();
        PolynomialSplineFunction poly = spline.interpolate(getTimePeriods() , getDataByReference(reference));


        List<Plot> plotList = new ArrayList<Plot>();



        for(double knotPoint : poly.getKnots()){
            plotList.add(new Plot(knotPoint, poly.value(knotPoint)));
        }
        return plotList;
    }

    private double[] getTimePeriods(){
        double[] timeElapsed = new double[epochs.size()];

        for(int i =0; i< epochs.size() ; i++) timeElapsed[i] = epochs.get(i).timeElapsed();

        return timeElapsed;
    }

    private double[] getDataByReference(String reference){
        double[] data = new double[epochs.size()];
        for(int i =0; i< epochs.size() ; i++) data[i] = epochs.get(i).getByReference(reference);

        return data;
    }


    public static void main(String[] args){
       List<Epoch> epochs = new ArrayList<Epoch>();


       for(int i =0; i < 5000; i++){
           Epoch e = new Epoch(i);
           e.setLowAlpha((int)(Math.random()*1000000));
           epochs.add(e);
           System.out.println(e);
       }

       DataSmoother smoother = new DataSmoother(epochs);

       List<Plot> smoothedAlphaData = smoother.smooth("lowAlpha");


        System.out.println("Smoothed Alpha Knots");
       for(Plot plot : smoothedAlphaData){
           System.out.println(plot);
       }

    }

}
