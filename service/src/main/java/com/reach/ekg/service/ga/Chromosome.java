package com.reach.ekg.service.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.reach.ekg.service.util.RandomUtil.randomBoolean;

public class Chromosome implements Comparable<Chromosome> {

    public static List<Chromosome> generatePopulation(int popSize, int geneLength) {
        List<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            boolean[] b = randomBoolean(geneLength);
            population.add(new Chromosome(b));
        }
        return population;
    }

    private boolean[] genes;
    private double fitness;
    private boolean fitnessCalculated;

    public Chromosome(boolean[] genes) {
        this.genes = genes;
    }

    public boolean[] genes() {
        return genes;
    }

    public boolean gene(int i) {
        return genes[i];
    }

    public void genes(boolean[] genes) {
        this.genes = genes;
    }

    public void gene(int i, boolean gene) {
        this.genes[i] = gene;
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
                "genes=" + Arrays.toString(genes) + " " +
                "calculate=" + fitness() +
                '}';
    }

    public double fitness() {
        return fitness;
    }

    public void calculateFitness(GA.FitnessFunction f) {
        fitness = f.calculate(genes);
    }

    //    public static void main(String[] args) {
//        Chromosome c = new Chromosome(new boolean[]{false, true, false}) {
//            @Override
//            public double fitnessFunction() {
//                System.out.println("lel");
//                return java.util.Arrays.hashCode(genes());
//            }
//        };
//
//        for (int i = 0; i < 100; i++) {
//            System.out.println(c.calculate() + " " + c.fitnessCalculated);
//        }
//    }
}
