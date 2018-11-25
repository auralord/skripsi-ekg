package com.reach.ekg.service.classification.ga;

import com.reach.ekg.service.classification.data.DataSource;
import static com.reach.ekg.service.util.DoubleUtils.sum;

public class Variance {

    private DataSource data;

    public Variance(DataSource data) {
        this.data = data;
    }

    public double get() {
        int n = data.count();
        int c = data.numClass();
        int f = data.row(0).length;

        double[] meanI = new double[c];
        double[] probI = new double[c];
        int[] numI = new int[c];

        // Find total mean
        double[] sumRow = new double[n];
        for (int i = 0; i < n; i++) {
            double[] x = data.row(i);
            sumRow[i] = sum(x) / x.length;
        }

        double mean = sum(sumRow) / n;

        // Find mean and probability of each class
        for (int i = 0; i < n; i++) {
            int index = data.target(i);
            numI[index]++;
            meanI[index] += sumRow[i];
        }

        for (int i = 0; i < c; i++) {
            meanI[i] /= numI[i];
        }

        for (int i = 0; i < c; i++) {
            meanI[i] /= numI[i];
            probI[i] = ((double) numI[i] )/ n;
        }

        // Count variance
        double variance = 0;
        for (int i = 0; i < c; i++) {
            variance += probI[i] * Math.pow((mean - meanI[i]), 2);
        }

        return variance;
    }
}
