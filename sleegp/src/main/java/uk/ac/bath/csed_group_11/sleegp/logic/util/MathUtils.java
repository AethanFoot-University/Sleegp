package uk.ac.bath.csed_group_11.sleegp.logic.util;

import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;
import uk.ac.bath.csed_group_11.sleegp.logic.data.Transformation;

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
}
