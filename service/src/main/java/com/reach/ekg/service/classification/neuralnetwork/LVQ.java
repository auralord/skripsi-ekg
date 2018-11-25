package com.reach.ekg.service.classification.neuralnetwork;

import com.reach.ekg.persistence.params.LVQParams;
import com.reach.ekg.service.classification.data.DataSource;
import com.reach.ekg.service.util.DoubleUtils;
import com.reach.ekg.service.util.IndexUtils;
import com.reach.ekg.service.util.RandomUtil;

public class LVQ {
    // Params
    private LVQParams params;

    // Architecture
    private int input;
    private int output;
    private double[][] weights;

    public LVQ(LVQParams params) {
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

                // Update weights
                int factor = (min == data.target(j)) ? 1 : -1;
                for (int k = 0; k < input; k++) {
                    weights[min][k] += factor * learningRate * (x[k] - weights[min][k]);
                }
            }

            // Update learning rate
            learningRate *= learningDecay;
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
