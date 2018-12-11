package com.reach.ekg.service.classification.ga;

import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.service.classification.ga.operators.BinaryOperators;
import com.reach.ekg.service.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.ceil;

public class GA {

    private static final double CONVERGENCE_THRESHOLD = 0.015;

    public interface FitnessFunction {
        double calculate(Chromosome chromosome);
    }

    public interface MutationOperator {
        Chromosome mutate(Chromosome c);
    }

    public interface SelectionOperator {
        List<Chromosome> select(List<Chromosome> c, int size);
    }

    public interface CrossoverOperator {
        Chromosome cross(Chromosome c1, Chromosome c2);
    }

    public interface InitializationOperator {
        Chromosome init(int length);
    }

    private double cr;
    private double mr;
    private int popSize;
    private int generation;

    private CrossoverOperator crossover;
    private MutationOperator mutation;
    private SelectionOperator selection;
    private FitnessFunction fitness;

    private List<Chromosome> population;
    private List<Double> history = new ArrayList<>();
    private Chromosome gBest;

    private Random r = RandomUtil.r();

    public GA(GAParams params) {
        this.cr = params.getCr();
        this.mr = params.getMr();
        this.popSize = params.getPopSize();
        this.generation = params.getGeneration();

        // Crossover + Mutation
        this.crossover = BinaryOperators::oneCutPoint;
        this.mutation = BinaryOperators::singleMutation;

        // Binary tournament
        this.selection = (p, n) -> {
            List<Chromosome> pop = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                int a = r.nextInt(p.size());
                int b = r.nextInt(p.size());

                if (p.get(a).fitness() >= p.get(b).fitness()) {
                    pop.add(p.get(a));
                } else {
                    pop.add(p.get(b));
                }
            }
            return pop;
        };
    }

    public void setCrossover(CrossoverOperator crossover) {
        this.crossover = crossover;
    }

    public void setMutation(MutationOperator mutation) {
        this.mutation = mutation;
    }

    public void setFitness(FitnessFunction fitness) {
        this.fitness = fitness;
    }

    public void generatePopulation(int geneLength, InitializationOperator method) {
        population = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            population.add(method.init(geneLength));
        }
    }


    public void run() {
        gBest = population.get(0);
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < generation; i++) {
            System.out.println("Generation " + i);

            // Crossover
            for (int j = 0; j < ceil(cr * popSize); j++) {
                Chromosome parent1 = population.get(r.nextInt(popSize));
                Chromosome parent2 = population.get(r.nextInt(popSize));
                population.add(crossover.cross(parent1, parent2));
            }

            // Mutation
            for (int j = 0; j < ceil(mr * popSize); j++) {
                Chromosome parent1 = population.get(r.nextInt(popSize));
                population.add(mutation.mutate(parent1));
            }

            // Evaluation
            population.parallelStream()
                    .forEach(c -> c.setFitness(fitness.calculate(c)));

            population.stream()
                    .max(Chromosome::compareTo)
                    .ifPresent(this::compareGBest);

            if(hasConverged()) break;

            // Selection
            population = selection.select(population, popSize);
        }
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    private void compareGBest(Object o) {
        Chromosome currentGBest = (Chromosome) o;

        if (currentGBest.fitness() > gBest.fitness()) {
            gBest = currentGBest;
        }
        history.add(gBest.fitness());
        System.out.println(gBest.fitness());
    }

    private boolean hasConverged() {
        int i = history.size() - 1;
        return i >= generation;
    }

    public Chromosome gBest() {
        return gBest;
    }

    public List<Double> getHistory() {
        return history;
    }

}
