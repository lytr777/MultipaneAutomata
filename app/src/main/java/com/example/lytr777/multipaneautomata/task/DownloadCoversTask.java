package com.example.lytr777.multipaneautomata.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.example.lytr777.multipaneautomata.Event;
import com.example.lytr777.multipaneautomata.ListFragment;
import com.example.lytr777.multipaneautomata.file.CreateFile;
import com.example.lytr777.multipaneautomata.file.DownloadFile;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

public class DownloadCoversTask extends AsyncTask<Void, Integer, Void> {

    private WeakReference<ListFragment> fragment;
    private List<Pair<Long, URL>> urls;
    private Context context;

    private String state;
    private int dataSize;
    private int currentProgress;

    public DownloadCoversTask(WeakReference<ListFragment> fragment, List<Pair<Long, URL>> urls, Context context) {
        this.fragment = fragment;
        this.urls = urls;
        this.context = context;

        state = "dcc";
        dataSize = urls.size();
        currentProgress = 0;
    }

    private DownloadCoversTask() {} // заблокируем пустой конструктор.

    protected void onProgressUpdate(Integer... values) {
        sendEvent(new Event("pubProg", "progress", currentProgress));
    }

    private void sendEvent(Event event) {
        String oldState = state;
        switch (state) {
            case "dcc":
                if (event.event.equals("exec")) {
                    state = "dce";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dce":
                if (event.event.equals("pubProg")) {
                    if (fragment.get() == null)
                        Log.e(TAG, "Fragment link is null on event '" + event.event + "'");
                    else {
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        fragment.get().sendEvent(new Event("pp", event.getMap()));
                    }
                } else if (event.event.equals("er")) {
                    state = "dcer";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else if (event.event.equals("ok")) {
                    state = "dcf";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dcer":
                if (event.event.equals("post")) {
                    if (fragment.get() == null)
                        Log.e(TAG, "Fragment link is null on event '" + event.event + "'");
                    else {
                        state = "dcp";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        fragment.get().sendEvent(new Event("er"));
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dcf":
                if (event.event.equals("post")) {
                    if (fragment.get() == null)
                        Log.e(TAG, "Fragment link is null on event '" + event.event + "'");
                    else {
                        state = "dcp";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        fragment.get().sendEvent(new Event("cOk"));
                    }
                } else if (event.event.equals("pubProg")) {
                    Log.d(TAG, "ignore event on state " + state + " in " + event.event);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;
        }

    }

    @Override
    protected Void doInBackground(Void... params) {
        sendEvent(new Event("exec"));
        try {
            for (int i = 0; i < dataSize; i++) {
                downloadFile(context, urls.get(i).first, urls.get(i).second);
                if (currentProgress < (i + 1) * 100 / dataSize) {
                    currentProgress = (i + 1) * 100 / dataSize;
                    publishProgress((i + 1) * 100 / dataSize);
                }
            }
            sendEvent(new Event("ok"));
        } catch (IOException e) {
            sendEvent(new Event("er"));
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        sendEvent(new Event("post"));
    }

    private void downloadFile(Context context, long id, URL url) throws IOException {
        Pair<File, Boolean> exist = CreateFile.createCacheFile(context, Long.toString(id) + "_cover");

        if (!exist.second)
            DownloadFile.downloadFile(url, exist.first);
    }

    private static final String TAG = "DownloadCoversAutomata";
}
