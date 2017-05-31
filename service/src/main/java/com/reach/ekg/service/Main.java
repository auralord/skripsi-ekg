package com.reach.ekg.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

import static com.reach.ekg.service.Config.config;
import static com.reach.ekg.service.Config.defaultConfig;

public class Main {

    public static void main(String[] args) {
        // Read config from file
        ObjectMapper mapper = new ObjectMapper();
        try {
            config = mapper.readValue(new File("config.json"), Config.class);
        } catch (IOException e) {
            System.out.println("Cannot parse file: " + e.getMessage()+ ". Using default config.");
            defaultConfig();
        }
        System.out.println(config);

        if (args.length == 0) {
            StartService.start();
        } else {
            FromFile.start(args[0]);
        }
    }
}