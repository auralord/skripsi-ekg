package com.reach.ekg.service;

public class Config {

    public static Config config;

    // Data
    public String pathToCSV;
    public String delimiter;
    public int indexCol;
    public int classCol;
    public int dataColStart;
    public int dataLength;

    // Server
    public String hostname;
    public int port;

    public static void defaultConfig() {
        config = new Config();
        config.pathToCSV = "data/data-mlii-rev2.csv";
        config.delimiter = ";";
        config.indexCol = 0;
        config.classCol = 1;
        config.dataColStart = 2;
        config.dataLength = 2160;

        config.hostname = "localhost";
        config.port = 8080;
    }

    public Config() {
    }

    @Override
    public String toString() {
        return "Config{" +
                "pathToCSV='" + pathToCSV + '\'' +
                ", delimiter='" + delimiter + '\'' +
                ", indexCol=" + indexCol +
                ", classCol=" + classCol +
                ", dataColStart=" + dataColStart +
                ", dataLength=" + dataLength +
                ", hostname='" + hostname + '\'' +
                ", port=" + port +
                "}";
    }
}
