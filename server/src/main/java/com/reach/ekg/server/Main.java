package com.reach.ekg.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.util.HashMap;

public class Main {

    public enum Status {
        IDLE,
        WORKING,
        STOPPED,
        FINISHED
    }

    private static Status status = Status.IDLE;

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();

        Spark.port(8080);
        Spark.get("/hello/:user", (req, res) -> {
            ThymeleafTemplateEngine engine = new ThymeleafTemplateEngine();
            HashMap<String, String> model = new HashMap<>();
            model.put("title", "Hello");
            model.put("body", req.params("user"));

            return engine.render(new ModelAndView(model, "test"));
        });

        Spark.get("/status", "application/json", (req, res) -> {
            return newMap("topkek", status.name());
        }, mapper::writeValueAsString);


        Spark.get("/start", (req, res) -> {
            status = Status.WORKING;
            return "status has been set to " + status.name();
        });

        Spark.get("/stop", (req, res) -> {
            status = Status.STOPPED;
            return "status has been set to " + status.name();
        });

        Spark.get("/finish", (req, res) -> {
            status = Status.FINISHED;
            return "status has been set to " + status.name();
        });

    }

    public static HashMap<String, String> newMap(String key, String value) {
        HashMap<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
