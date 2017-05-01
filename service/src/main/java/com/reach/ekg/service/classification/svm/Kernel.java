package com.reach.ekg.service.classification.svm;

public interface Kernel {
    public double apply(double[] x, double[] y);
}
