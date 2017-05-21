package com.reach.ekg.server;

import spark.ModelAndView;
import spark.TemplateEngine;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.util.HashMap;

public class View {

    private TemplateEngine engine = new ThymeleafTemplateEngine();

    private String template;
    private HashMap<String, Object> map;

    public View() {
        map = new HashMap<>();
    }

    public View template(String template) {
        map = new HashMap<>();
        this.template = template;
        return this;
    }

    public View add(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public String render() {
        return engine.render(new ModelAndView(map, template));
    }
}
