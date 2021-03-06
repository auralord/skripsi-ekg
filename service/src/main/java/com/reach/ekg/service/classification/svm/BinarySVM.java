package com.reach.ekg.service.classification.svm;

import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.service.classification.data.DataSource;
import com.reach.ekg.service.util.DoubleUtils;
import com.reach.ekg.service.util.IndexUtils;

import java.util.OptionalDouble;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static com.reach.ekg.service.util.DoubleUtils.sumProduct;
import static java.lang.Math.*;

public class BinarySVM {

    // Params
    private Kernel k;
    private double lambda;
    private double gamma;
    private double c;
    private double epsilon;
    private double threshold;
    private double maxIter;

    // States
    private DataSource ds;
    private double[][] kernel;
    private double[][] hessian;
    private double[] e;
    private double[] a;
    private double[] da;
    private double b;

    // Error
    private boolean calcError;

    public BinarySVM(SVMParams params, Kernel k) {
        this.k = k;
        this.lambda = params.getLambda();
        this.gamma = params.getGamma();
        this.c = params.getC();
        this.epsilon = params.getEpsilon();
        this.threshold = params.getThreshold();
        this.maxIter = params.getMaxIter();
    }

    public void kernel(double[][] kernel) {
        this.kernel = kernel;
    }

    public void train(DataSource data) {
        int n = data.count();
        this.ds = data;
        this.hessian = new double[n][n];
        this.e = new double[n];
        this.a = new double[n];
        this.da = new double[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                hessian[i][j] = ds.target(i) * ds.target(j) * (kernel[i][j] + lambda * lambda);
            }
        }

        double gamma = this.gamma;
        OptionalDouble maxHessian = IntStream.range(0, n)
                .mapToDouble(i -> hessian[i][i])
                .max();
        if(maxHessian.isPresent()) gamma /= maxHessian.getAsDouble();

        if (calcError) System.out.println(this);

        for (int iteration = 0; (iteration < maxIter); iteration++) {
            for (int i = 0; i < n; i++) {
                e[i] = sumProduct(a, hessian[i]);
            }

            for (int i = 0; i < n; i++) {
                da[i] = min(max(gamma * (1 - e[i]), -a[i]), c - a[i]);
            }

            for (int i = 0; i < n; i++) {
                a[i] += da[i];
            }

            if (calcError) {
                double error = 0;
                for (int i = 0; i < n; i++) {
                    int actual = ds.target(i);
                    int predicted = test(ds.row(i));
                    if (actual != predicted) error += (1.0 / n);
                }
                System.out.println(error);
            }

            if (checkDA()) {
                break;
            }
        }

        int kPlus = IndexUtils.maxOfArray(IntStream.range(0, n)
                .mapToDouble(i -> ds.target(i) == 1 ? a[i] : 0)
                .toArray());

        int kMinus = IndexUtils.maxOfArray(IntStream.range(0, n)
                .mapToDouble(i -> ds.target(i) == -1 ? a[i] : 0)
                .toArray());

        double ayKPlus = IntStream.range(0, n)
                .filter(i -> a[i] > threshold)
                .mapToDouble(i -> a[i] * ds.target(i) * kernel[i][kPlus])
                .sum();

        double ayKMinus = IntStream.range(0, n)
                .filter(i -> a[i] > threshold)
                .mapToDouble(i -> a[i] * ds.target(i) * kernel[i][kMinus])
                .sum();

        b = -0.5 * (ayKMinus + ayKPlus);
//        System.out.println(n);
//        com.reach.ekg.service.util.DoubleUtils.printArray(a);
//        com.reach.ekg.backend.util.DoubleUtils.printArray(e);
//        System.out.println(kPlus + " - " + kMinus);
//        printArray(ayKMinus);
//        printArray(kernel[kMinus]);
//        System.out.printf("%10.6f %10.6f\n", ayKPlus, ayKMinus);
//        System.out.println(b);
//        com.reach.ekg.service.util.DoubleUtils.printMatrix(kernel);
//        com.reach.ekg.backend.util.DoubleUtils.printMatrix(hessian);
//        printMatrix(data.all());System.out.println("-");
//        IntStream.of(ds.targets()).mapToObj(i->(1 == i) ? "+1" : "-1").forEach(System.out::print);System.out.println();
    }

    private boolean checkDA() {
        double max = 0;
        for (double d: da) {
            if (abs(d) > max) max = abs(d);
        }
        return max < epsilon;
    }

    public int test(double... x) {
        double y = IntStream.range(0, ds.count())
                .mapToDouble(i -> a[i] * ds.target(i) * k.apply(ds.row(i), x))
                .sum();
        y += b;

        return (int) signum(y);
    }

    void traceErrorOn() {
        calcError = true;
    }

}
