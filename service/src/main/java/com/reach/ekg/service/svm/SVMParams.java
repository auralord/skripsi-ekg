package com.reach.ekg.service.svm;

public class SVMParams {

    private double kernelParam;
    private double lambda;
    private double gamma;
    private double c;
    private double epsilon;
    private double threshold;
    private double maxiter;

    public double getKernelParam() {
        return kernelParam;
    }

    public SVMParams setKernelParam(double kernelParam) {
        this.kernelParam = kernelParam;
        return this;
    }

    public double getLambda() {
        return lambda;
    }

    public SVMParams setLambda(double lambda) {
        this.lambda = lambda;
        return this;
    }

    public double getGamma() {
        return gamma;
    }

    public SVMParams setGamma(double gamma) {
        this.gamma = gamma;
        return this;
    }

    public double getC() {
        return c;
    }

    public SVMParams setC(double c) {
        this.c = c;
        return this;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public SVMParams setEpsilon(double epsilon) {
        this.epsilon = epsilon;
        return this;
    }

    public double getThreshold() {
        return threshold;
    }

    public SVMParams setThreshold(double threshold) {
        this.threshold = threshold;
        return this;
    }

    public double getMaxIter() {
        return maxiter;
    }

    public SVMParams setMaxIter(double maxiter) {
        this.maxiter = maxiter;
        return this;
    }
}
