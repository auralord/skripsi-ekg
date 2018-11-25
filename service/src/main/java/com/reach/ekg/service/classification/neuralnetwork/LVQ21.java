package com.reach.ekg.service.classification.neuralnetwork;

import com.reach.ekg.persistence.params.LVQParams;
import com.reach.ekg.service.classification.data.DataSource;
import com.reach.ekg.service.util.DoubleUtils;
import com.reach.ekg.service.util.IndexUtils;
import com.reach.ekg.service.util.RandomUtil;

public class LVQ21 {
    // Params
    private LVQParams params;

    // Architecture
    private int input;
    private int output;
    private double[][] weights;

    public LVQ21(LVQParams params) {
        this.params = params;
    }

    public void train(DataSource data) {
        input = data.row(0).length;
        output = data.numClass();
        weights = new double[output][input];

        double learningRate = params.getLearningRate();
        double learningDecay = params.getLearningDecay();

        for (int i = 0; i < output; i++) {
            int numPerClass = data.count() / data.numClass();
            int index = i * numPerClass + RandomUtil.r().nextInt(numPerClass);

            weights[i] = data.row(index);
        }

        for (int i = 0; i < params.getEpoch(); i++) {
            for (int j = 0; j < data.count(); j++) {
                double[] distances = new double[output];
                double[] x = data.row(j);

                // Count distances
                for (int k = 0; k < output; k++) {
                    distances[k] = DoubleUtils.distance(x, weights[k]);
                }

                // Find closest
                int min = IndexUtils.minOfArray(distances);
                int min2 = 0;
                for (int k = 0; k < output; k++) {
                    if (distances[k] < distances[min2] && k != min) {
                        min2 = k;
                    }
                }

                // Check LVQ2.1 criteria
                final double omega = 0.5;
                double d1 = distances[min] / distances[min2];
                double d2 = distances[min2] / distances[min];
                if (Math.min(d1, d2) < ((1 - omega) / (1 + omega))) {

                    int factor, factor2;
                    if (min == data.target(j)) {
                        factor = 1;
                        factor2 = -1;
                    } else {
                        factor = -1;
                        factor2 = 1;
                    }

                    // Update weights
                    for (int k = 0; k < input; k++) {
                        weights[min][k] += factor * learningRate * (x[k] - weights[min][k]);
                        weights[min2][k] += factor2 * learningRate * (x[k] - weights[min2][k]);
                    }
                }

                // Update learning rate
                learningRate *= learningDecay;
            }
        }
    }

    public int test(double... x) {
        double[] distances = new double[output];
        for (int i = 0; i < output; i++) {
            distances[i] = DoubleUtils.distance(x, weights[i]);
        }

        return IndexUtils.minOfArray(distances);
    }
}
