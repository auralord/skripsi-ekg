package com.reach.ekg.persistence.results;

import java.util.Arrays;
import java.util.List;

public class IndividualTestResult {

    // Timestamps
    private String begin;
    private String end;

    // Test result
    private boolean[] features;
    private List<Double> history;
    private List<ClassificationResult> classificationResults;

    // Fitness result
    private double accuracy;
    private double featuresPercentage;
    private double fitness;

    public IndividualTestResult() {}

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean[] getFeatures() {
        return features;
    }

    public void setFeatures(boolean[] features) {
        this.features = features;
    }

    public List<Double> getHistory() {
        return history;
    }

    public void setHistory(List<Double> history) {
        this.history = history;
    }

    public List<ClassificationResult> getClassificationResults() {
        return classificationResults;
    }

    public void setClassificationResults(List<ClassificationResult> classificationResults) {
        this.classificationResults = classificationResults;
    }

    public void addClassificationResult(int predicted, int actual) {
        classificationResults.add(new ClassificationResult(predicted, actual));
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getFeaturesPercentage() {
        return featuresPercentage;
    }

    public void setFeaturesPercentage(double featuresPercentage) {
        this.featuresPercentage = featuresPercentage;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        return "IndividualTestResult{" +
                "begin='" + begin + '\'' +
                ", end='" + end + '\'' +
                ", features=" + Arrays.toString(features) +
                ", history=" + history +
                ", classificationResults=" + classificationResults +
                ", accuracy=" + accuracy +
                ", featuresPercentage=" + featuresPercentage +
                ", fitness=" + fitness +
                '}';
    }
}