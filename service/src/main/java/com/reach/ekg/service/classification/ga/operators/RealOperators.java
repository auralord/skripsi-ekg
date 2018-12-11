package com.reach.ekg.service.classification.ga.operators;

import com.reach.ekg.service.classification.ga.Chromosome;

import static com.reach.ekg.service.util.RandomUtil.r;

public class RealOperators {

    private final static double R_CROSSOVER_MAX = 1.25;
    private final static double R_CROSSOVER_MIN = -0.25;
    private final static double R_MUTATION_MAX = 0.1;
    private final static double R_MUTATION_MIN = -0.1;

    private double[] max;
    private double[] min;
    private int length;

    public RealOperators(double[] max, double[] min) {
        if (max.length != min.length) {
            throw new IndexOutOfBoundsException("Max and min bounds have different size: " + max.length + " & " + min.length);
        }

        this.max = max;
        this.min = min;
        this.length = max.length;
    }

    private double rC() {
        return R_CROSSOVER_MIN + r().nextDouble() * (R_CROSSOVER_MAX - R_CROSSOVER_MIN);
    }

    private double rM() {
        return R_MUTATION_MIN + r().nextDouble() * (R_MUTATION_MAX - R_MUTATION_MIN);
    }

    public Chromosome<double[]> crossover(Chromosome<double[]> c1, Chromosome<double[]> c2) {
        double[] genes = new double[length];
        for (int i = 0; i < length; i++) {
            double x = c1.genes()[i];
            double y = c2.genes()[i];
            genes[i] = x + rC() * (y - x);
        }
        checkBounds(genes);
        return new Chromosome<>(genes);
    }

    public Chromosome<double[]> mutation(Chromosome<double[]> c1) {
        double[] genes = new double[length];
        for (int i = 0; i < length; i++) {
            double x = c1.genes()[i];
            genes[i] = x + rM() * (max[i] - min[i]);
        }
        checkBounds(genes);
        return new Chromosome<>(genes);
    }

    private void checkBounds(double[] genes) {
        for (int i = 0; i < length; i++) {
            genes[i] = Math.max(genes[i], min[i]);
            genes[i] = Math.min(genes[i], max[i]);
        }
    }
}
