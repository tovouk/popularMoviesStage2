package com.josehinojo.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MyViewHolder> {

    public List<ParcelableMovie> movieList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView posterIMG;

        public MyViewHolder(View view) {
            super(view);
            posterIMG = view.findViewById(R.id.moviePoster);
        }

    }

    public MovieListAdapter(List<ParcelableMovie> moviesList) {
        this.movieList = moviesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_poster, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ParcelableMovie movie = this.movieList.get(position);
        Picasso.get().load(movie.getPosterIMG()).error(R.drawable.ic_launcher_background).into(holder.posterIMG);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

}
