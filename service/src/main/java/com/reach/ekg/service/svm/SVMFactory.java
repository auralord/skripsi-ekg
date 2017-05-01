package com.reach.ekg.service.svm;

import com.reach.ekg.service.data.DataSource;

public class SVMFactory {

    public static DataSource training;
    public static DataSource trainingNormalised;
    public static DataSource testNormalised;
    public static SVMParams params;

    public static BDTSVM newInstance() {
        return new BDTSVM(params);
    }
}
