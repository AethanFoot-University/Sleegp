package uk.ac.bath.csed_group_11.sleegp.logic.Classification;


import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import uk.ac.bath.csed_group_11.sleegp.logic.util.FileUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;



/**
 * Used for determining the frequency given a set of points
 */
public class FourierTransform {


    public static  double log2(double val){
        return Math.log(val)/Math.log(2);
    }

    public static int nearestPow2(double value){
       int power = (int) Math.floor(Math.log(value) / Math.log(2));
       int convertion = (int) Math.pow(2, power);
       return convertion;
    }
    private static List<Double> transform(List<Double> values)
    {

        int size = nearestPow2(values.size());
        double[] input = new double[size];

        for(int i =0; i< size; i++) {
            input[i] = values.get(i);
        }

        double[] tempConversion = new double[input.length];

        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);

        try {
          //  System.out.println("Converting values: ");
           // for(double d : input ) System.out.println(d);

            Complex[] complx = transformer.transform(input, TransformType.FORWARD);

            for (int i = 0; i < complx.length; i++) {
                double rr = (complx[i].getReal());
                double ri = (complx[i].getImaginary());

                tempConversion[i] = Math.sqrt((rr * rr) + (ri * ri));
            }

        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
        List<Double> out = new ArrayList<>();
        for(double d : tempConversion) out.add(d);

        return out;
    }


    public static void main(String[] args) throws FileNotFoundException {


        List<Double> values = FileUtils.convertFromFile("/Users/mathew/Downloads/delta.txt");



        System.out.println("Final output");
        //transform(values);
        for(Double d : transform(values)) System.out.println(d);


    }
}
