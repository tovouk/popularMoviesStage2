package com.josehinojo.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.josehinojo.popularmovies.database.FavoriteMovie;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.MyViewHolder>{

    Context context;
    final private ListItemClickListener itemClickListener;


    public interface ListItemClickListener{
        void onListItemClick(FavoriteMovie movie);
    }

    public void setContext(Context context){
        this.context = context;
    }

    public List<FavoriteMovie> movieList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView favTitle;
        public TextView favRating;

        public MyViewHolder(View view) {
            super(view);
            favTitle = view.findViewById(R.id.favTitle);
            favRating = view.findViewById(R.id.favRating);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            itemClickListener.onListItemClick(movieList.get(position));
        }
    }

    public FavoritesAdapter(List<FavoriteMovie> moviesList, ListItemClickListener listener) {
        this.movieList = moviesList;
        itemClickListener = listener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_movie, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FavoriteMovie movie = this.movieList.get(position);


        holder.favTitle.setText(movie.getTitle());
        holder.favRating.setText(Float.toString(movie.getRating()));
    }
    public void update(){
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void setMovieList(List<FavoriteMovie> movieList){
        this.movieList = movieList;
        update();
    }

    public List<FavoriteMovie> getMovieList(){
        return movieList;
    }
}
