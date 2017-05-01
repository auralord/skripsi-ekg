package com.reach.ekg.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.ir.annotations.Ignore;

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

    public IndividualTestResult(String begin, String end,
                                boolean[] features, List<Double> history,
                                List<ClassificationResult> classificationResults) {
        this.begin = begin;
        this.end = end;
        this.features = features;
        this.history = history;
        this.classificationResults = classificationResults;

        calculateFitness();
    }

    private void calculateFitness() {
        int correct = (int) classificationResults.stream()
                .filter(ClassificationResult::correct)
                .count();
        int total = classificationResults.size();
        this.accuracy = (double) correct / (double) total;

        int used = 0;
        for (int i = 0; i < features.length; i++) {
            if (features[i]) used++;
        }
        this.featuresPercentage = (double) used / (double) features.length;

        this.fitness = 0.85 * accuracy + 0.15 * featuresPercentage;
    }

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