package com.reach.ekg.service.classification.ga;

import java.util.Arrays;

public class Chromosome<T> implements Comparable<Chromosome> {

    private T genes;
    private double fitness;

    public Chromosome(T genes) {
        this.genes = genes;
    }

    public T genes() {
        return genes;
    }

    @Override
    public int compareTo(Chromosome that) {
        return Double.compare(this.fitness(), that.fitness());
    }

    @Override
    public String toString() {
        return "Chromosome{" +
                "fitness=" + fitness() +
                '}';
    }

    public double fitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}
