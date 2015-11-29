package edu.gonzaga.opencvtest;

import java.util.ArrayList;

/**
 * Static functions for getting mean, stdev of a List of pixels
 */
public class PhotoMath {

    public static double[] pixelMean(ArrayList<Double[]> pixels) {
        int size = pixels.get(0).length;
        double[] out = new double[size];
        for (int i = 0; i < size; i++) {
            ArrayList<Double> vals = new ArrayList<>();
            for (Double[] d : pixels) vals.add(d[i]);
            out[i] = mean(vals);
        }
        return out;
    }

    public static double[] pixelStdev(ArrayList<Double[]> pixels) {
        int size = pixels.get(0).length;
        double[] out = new double[size];
        for (int i = 0; i < size; i++) {
            ArrayList<Double> vals = new ArrayList<>();
            for (Double[] d : pixels) vals.add(d[i]);
            out[i] = stdev(vals);
        }
        return out;
    }

    public static double mean(ArrayList<Double> vals) {
        double tot = 0;
        for (Double d : vals) tot += d;
        return tot/vals.size();
    }

    public static double stdev(ArrayList<Double> vals) {
        double avg = mean(vals);
        double sumdiffs = 0.0;
        for (int i = 0; i < vals.size(); i++) {
            sumdiffs += Math.pow((vals.get(i) - avg), 2.0);
        }
        //Divide by (size - 1) for corrected sample standard deviation
        return Math.sqrt(sumdiffs / (vals.size() - 1));
    }

}
