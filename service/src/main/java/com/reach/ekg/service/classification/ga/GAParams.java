package com.reach.ekg.service.classification.ga;

public class GAParams {

    private double cr;
    private double mr;
    private int generation;
    private int popSize;

    public double getCr() {
        return cr;
    }

    public GAParams setCr(double cr) {
        this.cr = cr;
        return this;
    }

    public double getMr() {
        return mr;
    }

    public GAParams setMr(double mr) {
        this.mr = mr;
        return this;
    }

    public int getGeneration() {
        return generation;
    }

    public GAParams setGeneration(int generation) {
        this.generation = generation;
        return this;
    }

    public int getPopSize() {
        return popSize;
    }

    public GAParams setPopSize(int popSize) {
        this.popSize = popSize;
        return this;
    }
}
