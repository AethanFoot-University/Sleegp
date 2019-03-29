package uk.ac.bath.csed_group_11.sleegp.logic.Classification;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;
import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.data.ProcessedDataContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.util.MathUtils;

import java.io.File;
import java.io.IOException;
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


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        EpochContainer ec = EpochContainer.loadContainerFromFile(new File("/Users/mathew/Documents/GitHub/project/testData/3 Hour (Fixed).ec"));



       ec.transformCurrentData(MathUtils.movingAverageOnEpochs("lowAlpha", 10));
       List<Epoch> epochs = ec.getEpochList();




       DataSmoother smoother = new DataSmoother(epochs);

       List<Plot> smoothedAlphaData = smoother.smooth("lowAlpha");

        ProcessedDataContainer proc = new ProcessedDataContainer(smoothedAlphaData);
        proc.applyMovingAverage(10);
        proc.mapLevel(0, 100);

        for(Plot p : proc){
            System.out.println("Mapped Value: "+p);
        }


        System.out.println("Smoothed Alpha Knots");
       for(Plot plot : smoothedAlphaData){
           System.out.println(plot);
       }

    }

}
