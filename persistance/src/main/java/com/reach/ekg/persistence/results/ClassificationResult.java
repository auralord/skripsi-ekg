package com.reach.ekg.persistence.results;

public class ClassificationResult {
    private int predicted;
    private int actual;

    public ClassificationResult() {}

    public ClassificationResult(int predicted, int actual) {
        this.predicted = predicted;
        this.actual = actual;
    }

    public boolean correct() {
        return predicted == actual;
    }

    public int getPredicted() {
        return predicted;
    }

    public void setPredicted(int predicted) {
        this.predicted = predicted;
    }

    public int getActual() {
        return actual;
    }

    public void setActual(int actual) {
        this.actual = actual;
    }

    @Override
    public String toString() {
        return "ClassificationResult{" +
                "predicted=" + predicted +
                ", actual=" + actual +
                '}';
    }
}
