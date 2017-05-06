package com.reach.ekg.server.controllers;

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

    private Job job;
    private String state;
    private int completed;


    public JobManager() {
        this.state = State.IDLE;
    }

    public boolean addJob(String label, SVMParams svmParams, GAParams gaParams, int repeat) {
        if (job == null) {
            job = new Job(label, svmParams, gaParams, repeat);
            completed = 0;
            state = State.STARTED;
            return true;
        } else {
            return false;
        }
    }

    /*
     * Helper methods
     */
    private HashMap<String, Object> map(String k, Object v) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(k, v);
        return map;
    }

    private HashMap<String, Object> stateMap() {
        return map(Message.STATE, state);
    }

    private Object error(Response res) {
        res.status(500);
        return map(Message.ERROR, "No job available");
    }

    /*
     * Methods to handle service requests
     */
    public Object status(Request req, Response res) {
        if (state.equals(State.STARTED) && job != null) {
            HashMap<String, Object> message = new HashMap<>();
            message.put(Message.STATE, state);
            message.put(Message.JOB, job);
            return message;
        } else {
            return stateMap();
        }
    }

    public Object start(Request req, Response res) {
        if (state.equals(State.STARTED) && job != null) {
            state = State.WORKING;
            return stateMap();
        } else {
            return error(res);
        }
    }

    public Object update(Request req, Response res) {
        if (state.equals(State.WORKING) && job != null) {
            completed++;
            return stateMap();
        } else {
            return error(res);
        }
    }

    public Object finish(Request req, Response res) {
        if (state.equals(State.WORKING) && job != null) {
            state = State.FINISHED;
            return stateMap();
        } else {
            return error(res);
        }
    }

    public Object reset(Request req, Response res) {
        state = State.IDLE;
        job = null;
        return stateMap();
    }

    /*
     * Methods to handle client requests
     */

    public Object jobStatus(Request req, Response res) {
        return map(Message.COMPLETED, completed);
    }
}
