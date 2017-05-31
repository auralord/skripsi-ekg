package com.reach.ekg.service.classification.ga;

import java.util.*;

import static com.reach.ekg.service.util.RandomUtil.randomBoolean;

public class Chromosome implements Comparable<Chromosome> {

    private boolean[] genes;
    private double fitness;
    private boolean calculated;
    private GA.FitnessFunction function;

    public Chromosome(boolean[] genes) {
        this.genes = genes;
    }

    public Chromosome(boolean[] genes, GA.FitnessFunction fitnessFunction) {
        this.genes = genes;
        this.calculated = false;
        this.function = fitnessFunction;
    }

    public boolean[] genes() {
        return genes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chromosome that = (Chromosome) o;

        return Arrays.equals(genes, that.genes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genes);
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
        if (!calculated) {
            fitness = function.calculate(genes);
            calculated = true;
        }

        return fitness;
    }

    public void calculateFitness(GA.FitnessFunction func) {
        if(calculated) return;

        fitness = func.calculate(genes);
        calculated = true;
    }

    public GA.FitnessFunction getFunction() {
        return function;
    }
}
