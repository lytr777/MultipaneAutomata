package com.example.lytr777.multipaneautomata;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Map;

public class StaticAutomata {

    public String link = "http://download.cdn.yandex.net/mobilization-2016/artists.json";
    
    private ListFragment listFragment;
    private WeakReference<MainActivity> main;
    private Map<String, Object> annex;

    private String state;

    public static StaticAutomata getInstance() {
        return ourInstance;
    }

    private StaticAutomata() {
        //Создаем экземпляр класса list fragment один раз в конструкторе статического класса и храним на него ссылку пока существует сам статический класс
        listFragment = ListFragment.newInstance(link);
        state = "null";
        main = null;
        annex = null;
    }

    public void sendEvent(Event event) {
        String oldState = state;
        switch (state) {
            case "null":
                if (event.event.equals("onC")) {
                    String tag = "main";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "scr";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        MainActivity mainActivity = (MainActivity) event.getObject(tag);
                        main = new WeakReference<>(mainActivity);
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "scr":
                if (event.event.equals("onRp")) {
                    if (main.get() == null)
                        Log.e(TAG, "Main link is null in '" + event.event + "'");
                    else {
                        state = "sp";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        main.get().sendEvent(new Event("list", "list", listFragment));
                    }
                } else if (event.event.equals("onRl")) {
                    if (main.get() == null)
                        Log.e(TAG, "Main link is null in '" + event.event + "'");
                    else {
                        state = "sl";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        //передаем ссылку на экземпляр list fragment вместе с событием
                        main.get().sendEvent(new Event("list", "list", listFragment));
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "sp":
                if (event.event.equals("onP")) {
                    state = "sps";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else if (event.event.equals("back")) {
                    state = "null";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    main = null;
                } else if (event.event.equals("ec")) {
                    if (main.get() == null)
                        Log.e(TAG, "Main link is null in '" + event.event + "'");
                    else {
                        state = "dp";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        main.get().sendEvent(new Event("gi", event.getMap()));
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "sl":
                if (event.event.equals("onP")) {
                    state = "sps";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else if (event.event.equals("back")) {
                    state = "null";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    main = null;
                } else if (event.event.equals("ec")) {
                    if (main.get() == null)
                        Log.e(TAG, "Main link is null in '" + event.event + "'");
                    else {
                        state = "dl";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        main.get().sendEvent(new Event("gi", event.getMap()));
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "sps":
                if (event.event.equals("onS")) {
                    state = "sst";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else if (event.event.equals("onRl")) {
                    state = "sl";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else if (event.event.equals("onRp")) {
                    state = "sp";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "sst":
                if (event.event.equals("onD")) {
                    state = "sds";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    main = null;
                } else if (event.event.equals("onRl")) {
                    if (main.get() == null)
                        Log.e(TAG, "Main link is null in '" + event.event + "'");
                    else {
                        state = "sl";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        main.get().sendEvent(new Event("list", "list", listFragment));
                    }
                } else if (event.event.equals("onRp")) {
                    if (main.get() == null)
                        Log.e(TAG, "Main link is null in '" + event.event + "'");
                    else {
                        state = "sp";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        main.get().sendEvent(new Event("list", "list", listFragment));
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "sds":
                if (event.event.equals("onC")) {
                    String tag = "main";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "scr";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        MainActivity mainActivity = (MainActivity) event.getObject(tag);
                        main = new WeakReference<>(mainActivity);
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dp":
                if (event.event.equals("onP")) {
                    state = "dps";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else if (event.event.equals("back")) {
                    if (main.get() == null)
                        Log.e(TAG, "Main link is null in '" + event.event + "'");
                    else {
                        state = "sp";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        main.get().sendEvent(new Event("gl", "list", listFragment));
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dl":
                if (event.event.equals("onP")) {
                    state = "dps";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else if (event.event.equals("ec")) {
                    if (main.get() == null)
                        Log.e(TAG, "Main link is null in '" + event.event + "'");
                    else {
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        main.get().sendEvent(new Event("gi", event.getMap()));
                    }
                } else if (event.event.equals("back")) {
                    state = "null";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    main = null;
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dps":
                if (event.event.equals("onS")) {
                    if (event.getMap() == null)
                        Log.e(TAG, "Data map is null in '" + event.event + "'");
                    else {
                        state = "dst";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        annex = event.getMap();
                    }
                } else if (event.event.equals("onRl")) {
                    state = "dl";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else if (event.event.equals("onRp")) {
                    state = "dp";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dst":
                if (event.event.equals("onD")) {
                    state = "dds";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    main = null;
                } else if (event.event.equals("onRl")) {
                    if (main.get() == null || annex == null)
                        Log.e(TAG, "Main link or data map is null in '" + event.event + "'");
                    else {
                        state = "dl";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        main.get().sendEvent(new Event("info", annex));
                        annex = null;
                    }
                } else if (event.event.equals("onRp")) {
                    if (main.get() == null || annex == null)
                        Log.e(TAG, "Main link or data map is null in '" + event.event + "'");
                    else {
                        state = "dp";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        main.get().sendEvent(new Event("info", annex));
                        annex = null;
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dds":
                if (event.event.equals("onC")) {
                    String tag = "main";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "dcr";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        MainActivity mainActivity = (MainActivity) event.getObject(tag);
                        main = new WeakReference<>(mainActivity);
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dcr":
                if (event.event.equals("onRp")) {
                    if (main.get() == null || annex == null)
                        Log.e(TAG, "Main link or data map is null in '" + event.event + "'");
                    else {
                        state = "dp";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        main.get().sendEvent(new Event("info", annex));
                        annex = null;
                    }
                } else if (event.event.equals("onRl")) {
                    if (main.get() == null || annex == null)
                        Log.e(TAG, "Main link or data map is null in '" + event.event + "'");
                    else {
                        state = "dl";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        annex.put("list", listFragment);
                        main.get().sendEvent(new Event("info", annex));
                        annex = null;
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;
        }
    }

    private static final String TAG = "StaticAutomata";
    private static StaticAutomata ourInstance = new StaticAutomata();
}
