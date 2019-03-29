package uk.ac.bath.csed_group_11.sleegp.logic.Classification;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;

import java.util.List;

public class DataSmother {
    private String reference;
    private List<Epoch> epochs;

    public DataSmother(String reference, List<Epoch> epochs) {
        this.reference = reference;
        this.epochs = epochs;
    }

    public List<Plot> smooth(){
        SplineInterpolator spline  = new SplineInterpolator();
        PolynomialSplineFunction poly = spline.interpolate(getTimePeriods() , getDataByReference());

        System.out.println(poly.getPolynomials().length);
        return null;
    }

    private double[] getTimePeriods(){
        double[] timeElapsed = new double[epochs.size()];

        for(int i =0; i< epochs.size() ; i++) timeElapsed[i] = epochs.get(i).timeElapsed();

        return timeElapsed;
    }

    private double[] getDataByReference(){
        double[] data = new double[epochs.size()];
        for(int i =0; i< epochs.size() ; i++) data[i] = epochs.get(i).getByReference(reference);

        return data;
    }


    public static void main(String[] args){
       // DataSmother
    }

}
