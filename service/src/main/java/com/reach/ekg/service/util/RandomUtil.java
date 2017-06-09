package com.reach.ekg.service.util;

import java.util.Random;

public class RandomUtil {

    private static Random r = new Random();

    public static Random r() {
        return  r;
    }

    public static boolean[] randomBoolean(int length) {
        boolean[] b = new boolean[length];
        for (int i = 0; i < length; i++) {
            b[i] = r.nextBoolean();
        }
        return b;
    }

    public static boolean[] rand(int size) {
        double d = r.nextGaussian();

        boolean[] b = new boolean[size];
        for (int i = 0; i < size; i++) {
            b[i] = (r.nextGaussian() <= d);
        }

        return b;
    }
}
