package uk.ac.bath.csed_group_11.sleegp.logic.util;

import uk.ac.bath.csed_group_11.sleegp.logic.Classification.Plot;
import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;

import java.util.List;

public class MathUtils {
    public static Transformation<Epoch, Double> movingAverage(String reference, int movingAverageWindow){
        return new Transformation<Epoch, Double>() {
            @Override
            public void transform(List<Epoch> dataSet, int index, List<Double> finalOutput) {
                int start = index -movingAverageWindow;
                start = (start>=0)?start : 0;
                double sum = 0;
                int amount =0;

                for(int a =start; a<=index; a++){
                    sum+= dataSet.get(a).getByReference(reference);
                    amount ++;
                }

                double avg = sum /amount;
                finalOutput.add(avg);
            }
        };
    }

    public static Transformation<Plot, Plot> movingPlotAverage(int movingAverageWindow){
        return new Transformation<Plot, Plot>() {
            @Override
            public void transform(List<Plot> dataSet, int index, List<Plot> finalOutput) {
                int start = index -movingAverageWindow;
                start = (start>=0)?start : 0;
                double sum = 0;
                int amount =0;

                for(int a =start; a<=index; a++){
                    sum+= dataSet.get(a).getLevel();
                    amount ++;
                }

                double avg = sum /amount;

                finalOutput.get(index).setLevel(avg);
            }
        };
    }

    public static Transformation<Epoch, Epoch> movingAverageOnEpochs(String reference, int movingAverageWindow){
        return new Transformation<Epoch, Epoch>() {
            @Override
            public void transform(List<Epoch> dataSet, int index, List<Epoch> finalOutput) {
                int start = index -movingAverageWindow;
                start = (start>=0)?start : 0;
                double sum = 0;
                int amount =0;

                for(int a =start; a<=index; a++){
                    sum+= dataSet.get(a).getByReference(reference);
                    amount ++;
                }

                double avg = sum /amount;

                try{
                finalOutput.get(index).setIntField(reference, (int)avg); }
                catch(Exception e){
                    System.out.println("Mov: parse failed");
                }


            }
        };
    }
}
