package com.reach.ekg.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.results.AggregateTestResult;
import com.reach.ekg.persistence.results.IndividualTestResult;
import com.reach.ekg.server.View;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class History {

    private static class HistoryEntry {
        public String label;
        public String time;
        public double fitness;
        public String progressClass;

        public HistoryEntry() {
        }

        public HistoryEntry(String label, String time, double fitness) {
            this.label = label;
            this.time = time;
            this.fitness = fitness;

            this.progressClass = "bg-danger";
            if (this.fitness >= 0.50) this.progressClass = "bg-warning";
            if (this.fitness >= 0.65) this.progressClass = "bg-info";
            if (this.fitness >= 0.80) this.progressClass = "bg-success";
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final View view = new View();

    private HistoryEntry pathToEntry(String path){
        try {
            AggregateTestResult result = mapper.readValue(
                    new File(path), AggregateTestResult.class);

            String label = path.substring(path.lastIndexOf('\\') + 1).split("\\.")[0];
            String time = result.getTime();

            OptionalDouble opt = result.getResults().stream()
                    .mapToDouble(IndividualTestResult::getFitness)
                    .average();

            double fitness = opt.isPresent() ? opt.getAsDouble() : 0;

            return new HistoryEntry(label, time, fitness);
        } catch (IOException e) {
            return null;
        }
    }

    public Object viewHistory(Request req, Response res) throws IOException {
        // List JSONs
        List<String> files = new ArrayList<>();
        Files.newDirectoryStream(Paths.get("results"),
                path -> path.toString().endsWith(".json"))
                .forEach(path -> files.add(path.toString()));

        // Build history
        List<HistoryEntry> history = files.stream()
                .map(this::pathToEntry)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return view.template("_ekg-history")
                .add("history", history)
                .render();
    }
}
