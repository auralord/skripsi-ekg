package com.reach.ekg.persistence.params;

public class LVQParams {
    private double learningRate;
    private double learningDecay;
    private double minError;
    private int epoch;

    public LVQParams(double learningRate, double learningDecay, double minError, int epoch) {
        this.learningRate = learningRate;
        this.learningDecay = learningDecay;
        this.minError = minError;
        this.epoch = epoch;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public double getLearningDecay() {
        return learningDecay;
    }

    public void setLearningDecay(double learningDecay) {
        this.learningDecay = learningDecay;
    }

    public double getMinError() {
        return minError;
    }

    public void setMinError(double minError) {
        this.minError = minError;
    }

    public int getEpoch() {
        return epoch;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    @Override
    public String toString() {
        return "LVQParams{" +
                "learningRate=" + learningRate +
                ", learningDecay=" + learningDecay +
                ", minError=" + minError +
                ", epoch=" + epoch +
                '}';
    }
}
