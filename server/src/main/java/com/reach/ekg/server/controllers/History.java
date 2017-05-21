package com.reach.ekg.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.persistence.results.AggregateTestResult;
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
import java.util.stream.Collectors;

public class History {

    private static class HistoryEntry {
        public String label;
        public String time;
        public double accuracy;
        public String progressClass;

        public HistoryEntry() {
        }

        public HistoryEntry(String label, String time, double accuracy) {
            this.label = label;
            this.time = time;
            this.accuracy = accuracy;

            this.progressClass = "bg-danger";
            if (accuracy >= 50) this.progressClass = "bg-warning";
            if (accuracy >= 70) this.progressClass = "bg-info";
            if (accuracy >= 90) this.progressClass = "bg-success";
        }

        @Override
        public String toString() {
            return "HistoryEntry{" +
                    "label='" + label + '\'' +
                    ", time='" + time + '\'' +
                    ", accuracy=" + accuracy +
                    '}';
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final View view = new View();

    private JobManager manager;

    public History(JobManager manager) {
        this.manager = manager;
    }

    private HistoryEntry pathToEntry(String path){
        try {
            AggregateTestResult result = mapper.readValue(
                    new File(path), AggregateTestResult.class);

            String label = result.getLabel();
            String time = result.getTime();
            double accuracy = result.getAverageAccuracy() * 100;

            return new HistoryEntry(label, time, accuracy);
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

        // CurrentJob
        JobManager.Job job;
        if (manager.jobAvailable()) {
            job = manager.getJob();
        } else {
            job = null;
        }

        return view.template("_ekg-history")
                .add("job", job)
                .add("history", history)
                .render();
    }
}
