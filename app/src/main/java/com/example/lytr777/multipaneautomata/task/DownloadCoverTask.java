package com.example.lytr777.multipaneautomata.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lytr777.multipaneautomata.Event;
import com.example.lytr777.multipaneautomata.InfoFragment;
import com.example.lytr777.multipaneautomata.file.CreateFile;
import com.example.lytr777.multipaneautomata.file.DownloadFile;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

public class DownloadCoverTask extends AsyncTask<Void, Void, Void> {

    private URL url;
    private WeakReference<InfoFragment> fragment;
    private Context context;

    private Bitmap bitmap;
    private String state;

    public DownloadCoverTask(WeakReference<InfoFragment> fragment, URL url, Context context) {
        this.fragment = fragment;
        this.url = url;
        this.context = context;
        state = "dbcc";
    }

    private DownloadCoverTask() {
    }

    public void sendEvent(Event event) {
        String oldState = state;
        switch (state) {
            case "dbcc":
                if (event.event.equals("exec")) {
                    state = "dbce";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dbce":
                if (event.event.equals("er")) {
                    state = "dbcer";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else if (event.event.equals("ok")) {
                    state = "dbcf";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else if (event.event.equals("obs")) {
                    state = "dbco";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dbco":
                if (event.event.equals("er")) {
                    state = "dbcoer";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dbcoer":
                if (event.event.equals("onCanc")) {
                    state = "dbcp";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dbcer":
                if (event.event.equals("post")) {
                    state = "dbcp";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    if (fragment.get() != null)
                        fragment.get().sendEvent(new Event("er"));
                    else Log.e(TAG, "Fragment link is null on event '" + event.event + "'");
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "dbcf":
                if (event.event.equals("post")) {
                    state = "dbcp";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    if (fragment.get() != null)
                        fragment.get().sendEvent(new Event("cOk", "bitmap", bitmap));
                    else Log.e(TAG, "Fragment link is null on event '" + event.event + "'");
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        sendEvent(new Event("exec"));
        try {
            bitmap = downloadFile(context, url);
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

    @Override
    protected void onCancelled() {
        super.onCancelled();
        sendEvent(new Event("onCanc"));
    }

    private Bitmap downloadFile(Context context, URL url) throws IOException {
        File file = CreateFile.createCacheFile(context, "big_cover").first;
        DownloadFile.downloadFile(url, file);
        return BitmapFactory.decodeFile(file.getPath());
    }

    private static final String TAG = "DownloadCoverAutomata";
}
