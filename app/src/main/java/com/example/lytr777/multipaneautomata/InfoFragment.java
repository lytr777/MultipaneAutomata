package com.example.lytr777.multipaneautomata;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.lytr777.multipaneautomata.file.CreateFile;
import com.example.lytr777.multipaneautomata.file.DownloadFile;
import com.example.lytr777.multipaneautomata.task.DownloadCoverTask;
import com.example.lytr777.multipaneautomata.task.DownloadCoversTask;
import com.example.lytr777.multipaneautomata.task.DownloadJsonTask;


public class InfoFragment extends Fragment {

    private ProgressBar progress;
    private ImageView bigCover;
    private Button reload;
    private TextView genres;
    private TextView albumsAndTracks;
    private TextView description;

    private Data element;
    private Bitmap bitmap;

    private String state;
    private Context context;
    private DownloadCoverTask downloadCoverTask;

    public InfoFragment() {
        state = "null";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        if (state.equals("ifds"))
            sendEvent(new Event("onFCr"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);

        progress = (ProgressBar) v.findViewById(R.id.big_cover_progress);
        bigCover = (ImageView) v.findViewById(R.id.artist_cover);
        reload = (Button) v.findViewById(R.id.info_reload_button);
        genres = (TextView) v.findViewById(R.id.artist_genres);
        albumsAndTracks = (TextView) v.findViewById(R.id.artist_tracks_and_albums);
        description = (TextView) v.findViewById(R.id.description);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        sendEvent(new Event("onCrV"));
        super.onActivityCreated(savedInstanceState);
    }

    public void sendEvent(Event event) {
        String oldState = state;
        switch (state) {
            case "null":
                if (event.event.equals("setData")) {
                    String tag = "element";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "ifds";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        element = (Data) event.getObject(tag);
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "ifds":
                if (event.event.equals("onFCr")) {
                    state = "ifcd";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    downloadCoverTask = new DownloadCoverTask(new WeakReference<>(this), element.bigCover, context);
                    downloadCoverTask.execute();
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "ifcd":
                if (event.event.equals("er")) {
                    state = "ifce";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    downloadCoverTask = null;
                } else if (event.event.equals("cOk")) {
                    String tag = "bitmap";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "if";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        bitmap = (Bitmap) event.getObject(tag);
                    }
                } else if (event.event.equals("setData")) {
                    String tag = "element";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        downloadCoverTask.cancel(true);
                        downloadCoverTask.sendEvent(new Event("obs"));
                        element = (Data) event.getObject(tag);
                        downloadCoverTask = new DownloadCoverTask(new WeakReference<>(this), element.bigCover, context);
                        downloadCoverTask.execute();
                    }
                } else if (event.event.equals("onCrV")) {
                    state = "ifcdv";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    reload.setText(R.string.connection_error_button);
                    reload.setVisibility(View.INVISIBLE);
                    progress.setVisibility(View.VISIBLE);

                    genres.setText(element.getGenres());
                    albumsAndTracks.setText(String.format(getResources().getString(R.string.tracks_and_albums),
                            element.albums, element.tracks));
                    description.setText(element.name + " - " + element.description);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "ifce":
                if (event.event.equals("setData")) {
                    String tag = "element";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "ifcd";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        element = (Data) event.getObject(tag);
                        downloadCoverTask = new DownloadCoverTask(new WeakReference<>(this), element.bigCover, context);
                        downloadCoverTask.execute();
                    }
                } else if (event.event.equals("onCrV")) {
                    state = "ifcev";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    genres.setText(element.getGenres());
                    albumsAndTracks.setText(String.format(getResources().getString(R.string.tracks_and_albums),
                            element.albums, element.tracks));
                    description.setText(element.name + " - " + element.description);
                    reload.setText(R.string.connection_error_button);
                    reload.setVisibility(View.VISIBLE);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "if":
                if (event.event.equals("setData")) {
                    String tag = "element";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "ifcd";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        element = (Data) event.getObject(tag);
                        downloadCoverTask = new DownloadCoverTask(new WeakReference<>(this), element.bigCover, context);
                        downloadCoverTask.execute();
                    }
                } else if (event.event.equals("onCrV")) {
                    state = "ifv";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    progress.setVisibility(View.INVISIBLE);
                    reload.setVisibility(View.INVISIBLE);
                    genres.setText(element.getGenres());
                    albumsAndTracks.setText(String.format(getResources().getString(R.string.tracks_and_albums),
                            element.albums, element.tracks));
                    description.setText(element.name + " - " + element.description);
                    bigCover.setImageBitmap(bitmap);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "ifcdv":
                if (event.event.equals("er")) {
                    state = "ifcev";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    downloadCoverTask = null;
                    progress.setVisibility(View.INVISIBLE);
                    reload.setText(R.string.connection_error_button);
                    reload.setVisibility(View.VISIBLE);
                } else if (event.event.equals("setData")) {
                    String tag = "element";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "ifcdv";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        downloadCoverTask.cancel(true);
                        downloadCoverTask.sendEvent(new Event("obs"));
                        element = (Data) event.getObject(tag);
                        genres.setText(element.getGenres());
                        albumsAndTracks.setText(String.format(getResources().getString(R.string.tracks_and_albums),
                                element.albums, element.tracks));
                        description.setText(element.name + " - " + element.description);

                        downloadCoverTask = new DownloadCoverTask(new WeakReference<>(this), element.bigCover, context);
                        downloadCoverTask.execute();
                    }
                } else if (event.event.equals("cOk")) {
                    String tag = "bitmap";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "ifv";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        bitmap = (Bitmap) event.getObject(tag);
                        progress.setVisibility(View.INVISIBLE);
                        bigCover.setImageBitmap(bitmap);
                    }
                } else if (event.event.equals("onDsV")) {
                    state = "ifcd";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    progress = null;
                    bigCover = null;
                    reload = null;
                    genres = null;
                    albumsAndTracks = null;
                    description = null;
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "ifcev":
                if (event.event.equals("re")) {
                    state = "ifcdv";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    reload.setVisibility(View.INVISIBLE);
                    progress.setProgress(View.VISIBLE);

                    downloadCoverTask = new DownloadCoverTask(new WeakReference<>(this), element.bigCover, context);
                    downloadCoverTask.execute();
                } else if (event.event.equals("setData")) {
                    String tag = "element";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "ifcdv";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);

                        element = (Data) event.getObject(tag);
                        reload.setVisibility(View.INVISIBLE);
                        progress.setProgress(View.VISIBLE);
                        genres.setText(element.getGenres());
                        albumsAndTracks.setText(String.format(getResources().getString(R.string.tracks_and_albums),
                                element.albums, element.tracks));
                        description.setText(element.name + " - " + element.description);

                        downloadCoverTask = new DownloadCoverTask(new WeakReference<>(this), element.bigCover, context);
                        downloadCoverTask.execute();
                    }
                } else if (event.event.equals("onDsV")) {
                    state = "ifce";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    progress = null;
                    bigCover = null;
                    reload = null;
                    genres = null;
                    albumsAndTracks = null;
                    description = null;
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "ifv":
                if (event.event.equals("setData")) {
                    String tag = "element";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "ifcdv";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);

                        element = (Data) event.getObject(tag);
                        progress.setVisibility(View.VISIBLE);
                        bigCover.setImageResource(0);
                        genres.setText(element.getGenres());
                        albumsAndTracks.setText(String.format(getResources().getString(R.string.tracks_and_albums),
                                element.albums, element.tracks));
                        description.setText(element.name + " - " + element.description);

                        downloadCoverTask = new DownloadCoverTask(new WeakReference<>(this), element.bigCover, context);
                        downloadCoverTask.execute();
                    }
                } else if (event.event.equals("onDsV")) {
                    state = "if";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    progress = null;
                    bigCover = null;
                    reload = null;
                    genres = null;
                    albumsAndTracks = null;
                    description = null;
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;
        }
    }

    public Data getData() {
        return element;
    }

    @Override
    public void onDestroyView() {
        sendEvent(new Event("onDsV"));
        super.onDestroyView();
    }

    private static final String TAG = "InfoFragmentAutomata";
}
