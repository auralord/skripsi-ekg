package com.reach.ekg.service.classification;

import com.reach.ekg.service.util.RandomUtil;

import java.util.Arrays;
import java.util.Random;

public class View {
    public static void main(String[] args) {
        boolean[] b1 = RandomUtil.randomBoolean(2160);
        boolean[] b2 = RandomUtil.randomBoolean(2160);
        Random r = new Random();

        long l1 = System.currentTimeMillis();
        int hash = 0;
        for (int i = 0; i < 20000; i++) {
            boolean[] newGenes = b2.clone();
            int x = r.nextInt(b1.length);
            System.arraycopy(b1, 0, newGenes, 0, x);
        }
        long l2 = System.currentTimeMillis();

        System.out.println(hash);
        System.out.println(l2 - l1);

    }
}
