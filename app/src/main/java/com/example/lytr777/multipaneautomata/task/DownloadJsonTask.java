package com.example.lytr777.multipaneautomata.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.lytr777.multipaneautomata.Data;
import com.example.lytr777.multipaneautomata.Event;
import com.example.lytr777.multipaneautomata.ListFragment;
import com.example.lytr777.multipaneautomata.Reader;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;


public class DownloadJsonTask extends AsyncTask<Void, Void, Void> {

    private URL url;
    private WeakReference<ListFragment> fragment;
    private List<Data> data;

    private String state;

    public DownloadJsonTask(WeakReference<ListFragment> fragment, URL url) {
        this.fragment = fragment;
        this.url = url;
        state = "djc";
    }

    private DownloadJsonTask() {
    } // заблокируем пустой конструктор.

    private void sendEvent(Event event) {
        String oldState = state;
        switch (state) {
            case "djc":
                if (event.event.equals("exec")) {
                    state = "dje";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dje":
                if (event.event.equals("er")) {
                    state = "djer";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else if (event.event.equals("ok")) {
                    state = "djf";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "djer":
                if (event.event.equals("post")) {
                    state = "djp";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    if (fragment.get() != null)
                        fragment.get().sendEvent(new Event("er"));
                    else Log.e(TAG, "Fragment link is null on event '" + event.event + "'");
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "djf":
                if (event.event.equals("post")) {
                    state = "djp";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    if (fragment.get() != null)
                        fragment.get().sendEvent(new Event("jOk", "data", data));
                    else Log.e(TAG, "Fragment link is null on event '" + event.event + "'");
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        sendEvent(new Event("exec"));
        try {
            data = Reader.downloadJson(url);
            sendEvent(new Event("ok"));
        } catch (IOException | NullPointerException e) {
            sendEvent(new Event("er"));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        sendEvent(new Event("post"));
    }

    private static final String TAG = "DownloadJsonAutomata";
}
