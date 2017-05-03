package com.reach.ekg.server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.Message;
import com.reach.ekg.persistence.State;
import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import spark.Request;
import spark.Response;

import java.util.HashMap;

public class JobManager {

    private static class Job {
        public String label;
        public SVMParams svmParams;
        public GAParams gaParams;
        public int repeat;

        Job(String label, SVMParams svmParams, GAParams gaParams, int repeat) {
            this.label = label;
            this.svmParams = svmParams;
            this.gaParams = gaParams;
            this.repeat = repeat;
        }
    }

    private ObjectMapper mapper;
    private Job job;
    private State state;

    public JobManager(ObjectMapper mapper) {
        this.mapper = mapper;
        this.state = State.IDLE;
    }

    public boolean addJob(String label, SVMParams svmParams, GAParams gaParams, int repeat) {
        if (job == null) {
            job = new Job(label, svmParams, gaParams, repeat);
            state = State.STARTED;
            return true;
        } else {
            return false;
        }
    }

    /*
     * Helper methods
     */
    private String toJson(String k, String v) {
        HashMap<String, String> map = new HashMap<>();
        map.put(k, v);

        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            System.err.println("ERROR: " + e.getMessage());
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    private String stateToJson() {
        return toJson(Message.STATE, state.name());
    }

    private String toJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    /*
     * Methods to handle service requests
     */
    public Object status(Request req, Response res) {
        if (state != State.STARTED) {
            return stateToJson();
        } else {
            return toJson(job);
        }
    }

    public Object start(Request req, Response res) {
        if (job != null && state == State.STARTED) {
            state = State.WORKING;
            return stateToJson();
        } else {
            return toJson(Message.ERROR, "No job available");
        }
    }

    public Object finish(Request req, Response res) {
        if (job != null && state == State.WORKING) {
            state = State.FINISHED;
            return stateToJson();
        } else {
            return toJson(Message.ERROR, "No Job available");
        }
    }

    public Object stop(Request req, Response res) {
        if (job != null && state == State.WORKING) {
            state = State.STOPPED;
            return stateToJson();
        } else {
            return toJson(Message.ERROR, "No Job available");
        }
    }

    public Object reset(Request req, Response res) {
        state = State.IDLE;
        job = null;
        return stateToJson();
    }
}
