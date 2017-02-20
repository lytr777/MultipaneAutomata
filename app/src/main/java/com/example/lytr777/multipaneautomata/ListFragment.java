package com.example.lytr777.multipaneautomata;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.lytr777.multipaneautomata.task.DownloadCoversTask;
import com.example.lytr777.multipaneautomata.task.DownloadJsonTask;

public class ListFragment extends Fragment {

    private static final String JSON_LINK = "jsonLink";

    private ProgressBar progress;
    private TextView wait;
    private Button reload;

    private String link;
    private List<Data> data;

    private DownloadJsonTask downloadJsonTask;
    private DownloadCoversTask downloadCoversTask;

    private Context context;

    private String state;

    public ListFragment() {
        state = "null";
    }

    public static ListFragment newInstance(String link) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(JSON_LINK, link);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        context = getActivity().getApplicationContext();
        if (getArguments() != null && link == null) {
            link = getArguments().getString(JSON_LINK);
            sendEvent(new Event("onFCr"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_list, container, false);
        progress = (ProgressBar) v.findViewById(R.id.cover_progress);
        wait = (TextView) v.findViewById(R.id.wait);
        reload = (Button) v.findViewById(R.id.list_reload_button);
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
                if (event.event.equals("onFCr")) {
                    state = "lfjd";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    try {
                        URL url = new URL(link);
                        downloadJsonTask = new DownloadJsonTask(new WeakReference<>(this), url);
                        downloadJsonTask.execute();
                    } catch (MalformedURLException e) {
                        Log.e(TAG, "Cannot create url from : " + link + " in '" + event.event + "'");
                    }
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "lfjd":
                if (event.event.equals("er")) {
                    state = "lfje";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    downloadJsonTask = null;
                } else if (event.event.equals("jOk")) {
                    String tag = "data";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "lfcd";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        downloadJsonTask = null;
                        data = (List<Data>) event.getObject(tag);
                        List<Pair<Long, URL>> urls = new ArrayList<>();
                        for (int i = 0; i < data.size(); i++)
                            urls.add(data.get(i).getIdAndSmallUrlPair());
                        downloadCoversTask = new DownloadCoversTask(new WeakReference<>(this), urls, context);
                        downloadCoversTask.execute();
                    }
                } else if (event.event.equals("onCrV")) {
                    state = "lfjdv";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    reload.setVisibility(View.INVISIBLE);
                    wait.setText(R.string.wait);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "lfje":
                if (event.event.equals("onCrV")) {
                    state = "lfjev";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    reload.setVisibility(View.VISIBLE);
                    wait.setText(R.string.connection_error);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "lfcd":
                if (event.event.equals("er")) {
                    state = "lfce";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    downloadCoversTask = null;
                } else if (event.event.equals("cOk")) {
                    state = "lf";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    downloadCoversTask = null;
                } else if (event.event.equals("onCrV")) {
                    state = "lfcdv";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    reload.setVisibility(View.INVISIBLE);
                    wait.setText(R.string.wait);
                } else if (event.event.equals("pp")) {
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "lfce":
                if (event.event.equals("onCrV")) {
                    state = "lfcev";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    reload.setVisibility(View.VISIBLE);
                    wait.setText(R.string.connection_error);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "lfjdv":
                if (event.event.equals("er")) {
                    state = "lfjev";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    downloadJsonTask = null;
                    reload.setVisibility(View.VISIBLE);
                    wait.setText(R.string.connection_error);
                } else if (event.event.equals("jOk")) {
                    String tag = "data";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        state = "lfcdv";
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        downloadJsonTask = null;
                        data = (List<Data>) event.getObject(tag);
                        List<Pair<Long, URL>> urls = new ArrayList<>();
                        for (int i = 0; i < data.size(); i++)
                            urls.add(data.get(i).getIdAndSmallUrlPair());
                        downloadCoversTask = new DownloadCoversTask(new WeakReference<>(this), urls, context);
                        downloadCoversTask.execute();
                    }
                } else if (event.event.equals("onDsV")) {
                    state = "lfjd";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    progress = null;
                    wait = null;
                    reload = null;
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "lfjev":
                if (event.event.equals("re")) {
                    state = "lfjdv";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    reload.setVisibility(View.INVISIBLE);
                    wait.setText(R.string.wait);
                    try {
                        URL url = new URL(link);
                        downloadJsonTask = new DownloadJsonTask(new WeakReference<>(this), url);
                        downloadJsonTask.execute();
                    } catch (MalformedURLException e) {
                        Log.e(TAG, "Cannot create url from : " + link + " in '" + event.event + "'");
                    }
                } else if (event.event.equals("onDsV")) {
                    state = "lfje";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    progress = null;
                    wait = null;
                    reload = null;
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;


            case "lfcdv":
                if (event.event.equals("er")) {
                    state = "lfcev";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    downloadCoversTask = null;
                    reload.setVisibility(View.VISIBLE);
                    wait.setText(R.string.connection_error);
                } else if (event.event.equals("cOk")) {
                    state = "lfv";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    downloadCoversTask = null;
                    progress.setVisibility(View.INVISIBLE);
                    wait.setVisibility(View.INVISIBLE);
                    RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.list);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new ArtistRecyclerViewAdapter(context, data, this));
                } else if (event.event.equals("onDsV")) {
                    state = "lfcd";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    progress = null;
                    wait = null;
                    reload = null;
                } else if (event.event.equals("pp")) {
                    String tag = "progress";
                    if (event.getObject(tag) == null)
                        Log.e(TAG, "Data from tag : " + tag + " not found in '" + event.event + "'");
                    else {
                        Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                        int currentProgress = (int) event.getObject(tag);
                        progress.setProgress(currentProgress);
                    }
                }  else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "lfcev":
                if (event.event.equals("re")) {
                    state = "lfcdv";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    reload.setVisibility(View.INVISIBLE);
                    progress.setProgress(0);
                    wait.setText(R.string.wait);
                    List<Pair<Long, URL>> urls = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++)
                        urls.add(data.get(i).getIdAndSmallUrlPair());
                    downloadCoversTask = new DownloadCoversTask(new WeakReference<>(this), urls, context);
                    downloadCoversTask.execute();
                } else if (event.event.equals("onDsV")) {
                    state = "lfce";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    progress = null;
                    wait = null;
                    reload = null;
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "lf":
                if (event.event.equals("onCrV")) {
                    state = "lfv";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.list);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new ArtistRecyclerViewAdapter(context, data, this));
                    progress.setVisibility(View.INVISIBLE);
                    wait.setVisibility(View.INVISIBLE);
                    reload.setVisibility(View.INVISIBLE);
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;

            case "lfv":
                if (event.event.equals("onDsV")) {
                    state = "lf";
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    progress = null;
                    wait = null;
                    reload = null;
                } else if (event.event.equals("lfc")) {
                    Log.d(TAG, "state " + oldState + " ---(" + event.event + ")---> " + state);
                    StaticAutomata.getInstance().sendEvent(new Event("ec", event.getMap()));
                } else Log.e(TAG, "Unknown event '" + event.event + "'");
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //never been called: sad true(((
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        sendEvent(new Event("onDsV"));
        super.onDestroyView();
    }

    private static final String TAG = "ListFragmentAutomata";
}
