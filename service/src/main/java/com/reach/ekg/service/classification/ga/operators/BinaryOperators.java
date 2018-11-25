package com.reach.ekg.service.classification.ga.operators;

import com.reach.ekg.service.classification.ga.Chromosome;

import static com.reach.ekg.service.util.RandomUtil.r;
public class BinaryOperators {

    public static Chromosome<boolean[]> oneCutPoint (Chromosome<boolean[]> c1, Chromosome<boolean[]> c2) {
        boolean[] newGenes = c2.genes().clone();
        int i = r().nextInt(c1.genes().length);
        System.arraycopy(c1.genes(), 0, newGenes, 0, i);

        return new Chromosome<>(newGenes);
    }

    public static Chromosome<boolean[]> singleMutation (Chromosome<boolean[]> c1) {
        int i = r().nextInt(c1.genes().length);
        boolean[] newGenes = c1.genes().clone();
        newGenes[i] = !newGenes[i];

        return new Chromosome<>(newGenes);
    }
}
