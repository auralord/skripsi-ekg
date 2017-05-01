package com.reach.ekg.service.svm;

/**
 * Created by Reach on 23/03/2017.
 */
public interface Kernel {
    public double apply(double[] x, double[] y);
}
