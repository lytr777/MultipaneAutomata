package com.example.lytr777.multipaneautomata;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private ListFragment listFragment;
    private InfoFragment infoFragment;

    private char orientation;

    private String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoFragment = null;
        listFragment = null;
        orientation = (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) ? 'l' : 'p';

        if (orientation == 'l')
            state = "al";
        else
            state = "ap";

        StaticAutomata.getInstance().sendEvent(new Event("onC", "main", this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        StaticAutomata.getInstance().sendEvent(new Event("onR" + orientation));
    }

    public void sendEvent(Event event) {
        String oldState = state;
        switch (state) {
            case "ap":
                if (event.event.equals("list")) {
                    String tag = "list";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "bl";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        //получаем класс  lisf fragment вместе с событием и добавляем его в контейнер на главном экране
                        listFragment = (ListFragment) event.getObject(tag);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.main_frame, listFragment)
                                .commit();
                    }
                } else if (event.event.equals("info")) {
                    String tag = "infoData";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "bi";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        Data data = (Data) event.getObject(tag);
                        infoFragment = new InfoFragment();
                        infoFragment.sendEvent(new Event("setData", "element", data));
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.main_frame, infoFragment)
                                .commit();
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "al":
                if (event.event.equals("list")) {
                    String tag = "list";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "bls";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        listFragment = (ListFragment) event.getObject(tag);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.main_frame_list, listFragment)
                                .commit();
                    }
                } else if (event.event.equals("info")) {
                    String tag1 = "list", tag2 = "infoData";
                    if (event.getObject(tag1) == null || event.getObject(tag2) == null)
                        Log.e(TAG, "Data from tags : " + tag1 + " or " + tag2 + " not found in '" + event.event + "'");
                    else {
                        state = "bf";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        listFragment = (ListFragment) event.getObject(tag1);
                        Data data = (Data) event.getObject(tag2);
                        infoFragment = new InfoFragment();
                        infoFragment.sendEvent(new Event("setData", "element", data));
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.main_frame_list, listFragment)
                                .add(R.id.main_frame_info, infoFragment)
                                .commit();
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "bl":
                if (event.event.equals("gi")) {
                    String tag = "infoData";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "bi";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        Data data = (Data) event.getObject(tag);
                        infoFragment = new InfoFragment();
                        infoFragment.sendEvent(new Event("setData", "element", data));
                        getSupportFragmentManager().beginTransaction()
                                .remove(listFragment)
                                .add(R.id.main_frame, infoFragment)
                                .commit();
                    }
                } else if (event.event.equals("onSIS")) {
                    state = "ap";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    getSupportFragmentManager().beginTransaction()
                            .remove(listFragment)
                            .commit();
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "bi":
                if (event.event.equals("gl")) {
                    String tag = "list";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "bli";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        listFragment = (ListFragment) event.getObject(tag);
                        getSupportFragmentManager().beginTransaction()
                                .remove(infoFragment)
                                .add(R.id.main_frame, listFragment)
                                .commit();
                    }
                } else if (event.event.equals("onSIS")) {
                    state = "ap";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    getSupportFragmentManager().beginTransaction()
                            .remove(infoFragment)
                            .commit();
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "bli":
                if (event.event.equals("gi")) {
                    String tag = "infoData";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "bi";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        Data data = (Data) event.getObject(tag);
                        infoFragment.sendEvent(new Event("setData", "element", data));
                        getSupportFragmentManager().beginTransaction()
                                .remove(listFragment)
                                .add(R.id.main_frame, infoFragment)
                                .commit();
                    }
                } else if (event.event.equals("onSIS")) {
                    state = "ap";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    getSupportFragmentManager().beginTransaction()
                            .remove(listFragment)
                            .commit();
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "bls":
                if (event.event.equals("gi")) {
                    String tag = "infoData";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "bf";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        Data data = (Data) event.getObject(tag);
                        infoFragment = new InfoFragment();
                        infoFragment.sendEvent(new Event("setData", "element", data));
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.main_frame_info, infoFragment)
                                .commit();
                    }
                } else if (event.event.equals("onSIS")) {
                    state = "al";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    getSupportFragmentManager().beginTransaction()
                            .remove(listFragment)
                            .commit();
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "bf":
                if (event.event.equals("gi")) {
                    String tag = "infoData";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        Data data = (Data) event.getObject(tag);
                        infoFragment.sendEvent(new Event("setData", "element", data));
                    }
                } else if (event.event.equals("onSIS")) {
                    state = "al";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    getSupportFragmentManager().beginTransaction()
                            .remove(infoFragment)
                            .remove(listFragment)
                            .commit();
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (state.equals("bi"))
            StaticAutomata.getInstance().sendEvent(new Event("back"));
        else
            super.onBackPressed();
    }

    @Override
    protected void onPause() {
        StaticAutomata.getInstance().sendEvent(new Event("onP"));
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        sendEvent(new Event("onSIS"));
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onStop() {
        Event event;
        if (infoFragment == null)
            event = new Event("onS");
        else
            event = new Event("onS", "infoData", infoFragment.getData());
        StaticAutomata.getInstance().sendEvent(event);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        StaticAutomata.getInstance().sendEvent(new Event("onD"));
        super.onDestroy();
    }

    private static final String TAG = "MainActivityAutomata";
}
