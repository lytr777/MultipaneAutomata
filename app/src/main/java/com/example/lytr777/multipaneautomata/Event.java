package com.example.lytr777.multipaneautomata;

import java.util.HashMap;
import java.util.Map;


public class Event {

    public String event;
    private Map<String, Object> annex;

    public Event(String event) {
        this.event = event;
        annex = null;
    }

    public Event(String event, String tag, Object obj) {
        this.event = event;
        annex = new HashMap<>();
        annex.put(tag, obj);
    }

    public Event(String event, Map<String, Object> annex) {
        this.event = event;
        this.annex = annex;
    }

    public Map<String, Object> getMap() {
        return annex;
    }

    public Object getObject(String key) {
        if (annex == null)
            return null;
        return annex.get(key);
    }
}