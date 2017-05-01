package com.reach.ekg.service.svm;

import com.reach.ekg.service.data.DataSource;
import com.reach.ekg.service.util.DoubleUtils;
import com.reach.ekg.service.util.IndexUtils;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static com.reach.ekg.service.util.DoubleUtils.sumProduct;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.signum;

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

        double gamma = this.gamma / IntStream.range(0, n)
                .mapToDouble(i -> hessian[i][i])
                .max().getAsDouble();


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

            if (checkDA()) {
                break;
            }
        }

        int kPlus = IndexUtils.max(IntStream.range(0, n)
                .mapToDouble(i -> ds.target(i) == 1 ? a[i] : 0)
                .toArray());

        int kMinus = IndexUtils.max(IntStream.range(0, n)
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
//        com.reach.ekg.backend.util.DoubleUtils.printArray(a);
//        com.reach.ekg.backend.util.DoubleUtils.printArray(e);
//        System.out.println(kPlus + " - " + kMinus);
//        printArray(ayKMinus);
//        printArray(kernel[kMinus]);
//        System.out.printf("%10.6f %10.6f\n", ayKPlus, ayKMinus);
//        System.out.println(b);
//        com.reach.ekg.backend.util.DoubleUtils.printMatrix(kernel);
//        com.reach.ekg.backend.util.DoubleUtils.printMatrix(hessian);
//        printMatrix(data.all());System.out.println("-");
//        IntStream.of(ds.targets()).mapToObj(i->(1 == i) ? "+1" : "-1").forEach(System.out::print);System.out.println();
    }

    private boolean checkDA() {
        double[] daAbs = DoubleStream.of(da).map(Math::abs).toArray();
//        System.out.println("da: " + DoubleUtils.max(daAbs));
        return DoubleUtils.max(daAbs) < epsilon;
    }

    public int test(double... x) {
        double y = IntStream.range(0, ds.count())
                .mapToDouble(i -> a[i] * ds.target(i) * k.apply(ds.row(i), x))
                .sum();
        y += b;

        return (int) signum(y);
    }

}
