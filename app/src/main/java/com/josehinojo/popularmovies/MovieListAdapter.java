package com.josehinojo.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.josehinojo.popularmovies.database.FavoritesContract;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MyViewHolder> {

    /*
    I used the following link to help model my adapter and Movie item
    https://www.androidhive.info/2016/01/android-working-with-recycler-view/amp/

     */


    Context context;
    final private ListItemClickListener itemClickListener;
    private boolean favsSelected;
    private Cursor cursor;

    public List<ParcelableMovie> movieList;

    public interface ListItemClickListener{
        void onListItemClick(ParcelableMovie movie);
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void setCursor(Cursor cursor) {

        this.cursor = cursor;
        movieList.clear();
        favsSelected = true;
        for (int i = 0; i < cursor.getCount(); i++) {
            if(cursor.moveToPosition(i)){
                int id = cursor.getInt(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_TITLE));
                String release = cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RELEASE));
                double rating = cursor.getDouble(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RATING));
                String plot = cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_PLOT));
                ParcelableMovie movie = new ParcelableMovie();
                movie.setId(id);
                movie.setTitle(title);
                movie.setReleaseDate(release);
                movie.setVoteAverage(rating);
                movie.setPlot(plot);
                movieList.add(movie);
            }else{return;}

        }
        notifyDataSetChanged();
    }

    public void notFavs(){
        cursor = null;
        favsSelected =false;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView posterIMG;
        public TextView movTitle;

        public MyViewHolder(View view) {
            super(view);
            posterIMG = view.findViewById(R.id.moviePoster);
            movTitle = view.findViewById(R.id.movTitle);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            itemClickListener.onListItemClick(movieList.get(position));
        }
    }

    public MovieListAdapter(List<ParcelableMovie> moviesList, ListItemClickListener listener) {
        this.movieList = moviesList;
        itemClickListener = listener;

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

        /*
        The below code was from the following sources
        https://stackoverflow.com/questions/1016896/get-screen-dimensions-in-pixels
        https://stackoverflow.com/questions/13219291/change-the-size-of-an-imageview-in-android

        !!!!I originally used a constraintlayout and constraintguidelines to limit the imageview to
        50% on the width and height but had issues with displays on the pixel 2 so I decided
        to look for an all around solution and this works the way I wanted it to.
         */

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x /2;
        int height = (int)(size.y /2.5);

        /*
        Pseudo responsive layout
        https://developer.android.com/reference/android/content/res/Configuration#orientation
         */
        if(context.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            width = (int)(size.x/3);
            height = (int)(size.y/1.5);
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width,height);


        holder.posterIMG.setLayoutParams(layoutParams);

        Picasso.get().load(movie.getPosterIMG()).fit().placeholder(R.mipmap.movie_foreground)
                .error(R.drawable.ic_launcher_background).into(holder.posterIMG);
        holder.movTitle.setText(movie.getTitle());
    }
    public void update(){
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }



}
