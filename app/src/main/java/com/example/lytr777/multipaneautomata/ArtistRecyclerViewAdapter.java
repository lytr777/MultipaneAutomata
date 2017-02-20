package com.example.lytr777.multipaneautomata;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lytr777.multipaneautomata.file.CreateFile;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class ArtistRecyclerViewAdapter extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final List<Data> data;
    private WeakReference<ListFragment> fragment;

    public ArtistRecyclerViewAdapter(Context context, List<Data> data, ListFragment fragment) {
        this.context = context;
        this.data = data;
        this.fragment = new WeakReference<>(fragment);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            // Проверяем что файл с обложкой существует и декодируем его.
            Pair<File, Boolean> exist = CreateFile.createCacheFile(context, Long.toString(data.get(position).id) + "_cover");
            if (exist.second)
                holder.cover.setImageBitmap(BitmapFactory.decodeFile(exist.first.getPath()));
        } catch (IOException e) {
            Log.e(TAG, "Файл не найден : " + e, e);
        }

        holder.item = data.get(position);
        holder.name.setText(data.get(position).name);
        holder.genres.setText(data.get(position).getGenres());
        holder.albumsAndTracks.setText(String.format(context.getResources().getString(R.string.tracks_and_albums),
                data.get(position).albums, data.get(position).tracks));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment.get() == null)
                    Log.e(TAG, "Fragment is null");
                else
                    fragment.get().sendEvent(new Event("lfc", "infoData", holder.item));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final ImageView cover;
        final TextView name;
        final TextView genres;
        final TextView albumsAndTracks;
        Data item;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            cover = (ImageView) view.findViewById(R.id.cover);
            name = (TextView) view.findViewById(R.id.name);
            genres = (TextView) view.findViewById(R.id.genres);
            albumsAndTracks = (TextView) view.findViewById(R.id.albums_and_tracks);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }

    private static final String TAG = "ListFragmentAdapter";
}
