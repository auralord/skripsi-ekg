package com.reach.ekg.service.classification.ga;

import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.service.classification.data.DataSource;
import com.reach.ekg.service.classification.data.DataSources;
import com.reach.ekg.service.classification.data.Dataset;
import com.reach.ekg.service.classification.svm.SVMFactory;
import com.reach.ekg.service.util.RandomUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static com.reach.ekg.service.util.RandomUtil.randomBoolean;
import static java.lang.Math.ceil;

public class GA {

    public interface FitnessFunction {
        double calculate(boolean[] b);
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

        // One cut-point
        this.crossover = ((c1, c2) -> {
            boolean[] newGenes = c2.genes().clone();
            int i = r.nextInt(c1.genes().length);
            System.arraycopy(c1.genes(), 0, newGenes, 0, i);

            return new Chromosome(newGenes);
        });

        // Single mutation
        this.mutation = (c1) -> {
            int i = r.nextInt(c1.genes().length);
            boolean[] newGenes = c1.genes().clone();
            newGenes[i] = !newGenes[i];

            return new Chromosome(newGenes);
        };

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

    public void setFitness(FitnessFunction fitness) {
        this.fitness = fitness;
    }

    public void generatePopulation(int geneLength) {
        population = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            boolean[] b = randomBoolean(geneLength);
            population.add(new Chromosome(b));
        }
    }


    public void run() {
        gBest = population.get(0);
        java.util.Date d1 = new java.util.Date();
        for (int i = 0; i < generation; i++) {
            System.out.println(i);

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
                    .forEach(c -> c.calculateFitness(fitness));

            population.stream()
                    .max(Comparator.naturalOrder())
                    .ifPresent(this::compareGBest);

            // Selection
            population = selection.select(population, popSize);
        }
        java.util.Date d2 = new java.util.Date();
        System.out.println(d2.getTime() - d1.getTime());
    }

    private void compareGBest(Chromosome currentGBest) {
        if (currentGBest.fitness() > gBest.fitness()) {
            gBest = currentGBest;
        }
        history.add(gBest.fitness());
    }

    public Chromosome gBest() {
        return gBest;
    }

    public List<Double> getHistory() {
        return history;
    }

//    public static void main(String[] args) {
//
//        GA ga = new GA(new GAParams()
//                .setCr(0.9)
//                .setMr(0.1)
//                .setGeneration(10)
//                .setPopSize(10));
//
//        ga.setFitness(new ClassificationFitness());
//        ga.generatePopulation(2160);
//
//        DataSource ds = DataSources.fromCSV("data/data-mlii-rev1.csv", ";", 0, 1, 2, 2160);
//        Dataset dataset = new Dataset(ds);
//        dataset.randomize();
//
//        SVMFactory.params = new SVMParams()
//                .setLambda(0.5)
//                .setGamma(0.01)
//                .setC(1)
//                .setEpsilon(0.00001)
//                .setThreshold(0)
//                .setMaxIter(100)
//                .setKernelParam(2);
//
//        SVMFactory.training = dataset.getTraining();
//        SVMFactory.trainingNormalised = dataset.getTrainingNomalised();
//        SVMFactory.testNormalised = dataset.getTestNormalised();
//
//        System.out.println("PREPARE FOR JUSTICE");
//        java.util.Date d1 = new java.util.Date();
//        ga.run();
//        java.util.Date d2 = new java.util.Date();
//
//        ga.history.forEach(System.out::println);
//        System.out.println(d2.getTime() - d1.getTime());
//    }
}
