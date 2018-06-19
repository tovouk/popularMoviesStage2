package com.josehinojo.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.josehinojo.popularmovies.database.FavoritesContract;
import com.josehinojo.popularmovies.database.FavoritesDbHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.josehinojo.popularmovies.MainActivity.API_KEY;

public class DetailActivity extends AppCompatActivity implements TrailerListAdapter.ListItemClickListener,
ReviewListAdapter.ListItemClickListener{

    public static final String URL_START = "https://api.themoviedb.org/3/movie/";
    //insert movie id between the two
    public static final String TRAILER_URL_MID = "/videos?api_key=";
    //or
    public static final String REVIEW_URL_MID = "/reviews?api_key=";
    //insert API_KEY between these
    public static final String URL_END = "&language=en-US";

    static String ASYNC_URL;

    public static final String THUMB_URL_START = "https://img.youtube.com/vi/";
    //insert video key in between the two
    public static final String THUMB_URL_END = "/default.jpg";

    @BindView(R.id.backdrop) ImageView backdrop;
    @BindView(R.id.detail_poster) ImageView poster;
    ParcelableMovie movie;
    @BindView(R.id.movie_title) TextView title;
    @BindView(R.id.ratingBar) RatingBar average;
    @BindView(R.id.averageVote) TextView averageVote;
    @BindView(R.id.overview) TextView overview;
    @BindView(R.id.releaseDate) TextView releaseDate;
    @BindView(R.id.favBtn) ImageButton favBtn;
    @BindView(R.id.favLabel) TextView favLabel;
    boolean clicked;

    ArrayList<ParcelableTrailer> trailerList = new ArrayList<>();
    RecyclerView trailer_recycler_view;
    TrailerListAdapter tListAdapter;
    ArrayList<ParcelableReview> reviewList = new ArrayList<>();
    RecyclerView review_recycler_view;
    ReviewListAdapter rListAdapter;
    Context context;
    public String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        trailer_recycler_view = findViewById(R.id.Trailers);
        review_recycler_view = findViewById(R.id.Reviews);
        ButterKnife.bind(this);
        if(savedInstanceState != null){
            clicked = savedInstanceState.getBoolean("clicked");
            if(clicked){
                favBtn.setImageResource(R.drawable.heart);
                favLabel.setText(R.string.favorite);
            }else{
                favBtn.setImageResource(R.drawable.pinkheart);
                favLabel.setText(R.string.unfavorite);
            }
        }else{
            clicked = false;
        }
        context = getApplicationContext();

        tListAdapter = new TrailerListAdapter(trailerList,this);
        tListAdapter.setContext(context);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        trailer_recycler_view.setLayoutManager(layoutManager);
        trailer_recycler_view.setAdapter(tListAdapter);

        rListAdapter = new ReviewListAdapter(reviewList,this);
        rListAdapter.setContext(context);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        review_recycler_view.setLayoutManager(layoutManager1);
        review_recycler_view.setAdapter(rListAdapter);


        movie = (ParcelableMovie) getIntent().getExtras().getParcelable("sentMovie");

        id = Integer.toString(movie.getId());
        getTrailersAndReviews();

        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        /*
        change backdrop height on orientation change
        https://developer.android.com/reference/android/content/res/Configuration#orientation
         */
        if(getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            size.y /= 3.33;
        }else if(getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            size.y /= 2;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size.x, size.y);
        backdrop.setLayoutParams(layoutParams);

        title.setText(movie.getTitle());

        average.setRating((float)movie.getVoteAverage()/2);

        averageVote.setText(Double.toString(movie.getVoteAverage()));

        overview.setText(movie.getPlot());

        releaseDate.setText("Release Date:\n" +
                movie.getReleaseDate());

        Picasso.get().load(movie.getBackdropIMG()).into(backdrop);
        Picasso.get().load(movie.getPosterIMG()).into(poster);
        //condition will be if a movie with that id is not found
        String[] columns = {FavoritesContract.FavoritesEntry.COLUMN_ID};
        Uri uri = FavoritesContract.FavoritesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(Integer.toString(movie.getId())).build();
            if(getContentResolver().query(uri,null,null,null,null).getCount()>0){
            clicked = true;
            favBtn.setImageResource(R.drawable.pinkheart);
            favLabel.setText(R.string.unfavorite);
        }else{
            clicked = false;
            favBtn.setImageResource(R.drawable.heart);
            favLabel.setText(R.string.favorite);
        }

    }


    public void getTrailersAndReviews(){

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnected()) {


            ASYNC_URL = URL_START + id + TRAILER_URL_MID + API_KEY + URL_END;
            String ReviewURL = URL_START + id + REVIEW_URL_MID + API_KEY + URL_END;
            try {
                // KNOWN ISSUE
                // WHEN too many requests are made, either task, sometimes both, will not return the
                // reviews or trailers due to a connection leak
                URL url = new URL(ASYNC_URL);
                TrailersAsyncTask trailersAsyncTask = new TrailersAsyncTask(getApplicationContext());
                trailersAsyncTask.setTrailerParams(tListAdapter, trailerList);
                trailersAsyncTask.execute(url);
                url = new URL(ReviewURL);
                ReviewsAsyncTask reviewsAsyncTask = new ReviewsAsyncTask(getApplicationContext());
                reviewsAsyncTask.setReviewParams(rListAdapter,reviewList);
                reviewsAsyncTask.execute(url);
                /*
                Ignore this, tried to have a single asynctask but other problems arose
                Namely null object references when getting JSONArrays
                TrailersAndReviewsTask trailersAndReviewsTask = new TrailersAndReviewsTask(getApplicationContext());
                trailersAndReviewsTask.setTrailerParams(tListAdapter,trailerList,rListAdapter,reviewList);
                trailersAndReviewsTask.execute(new URL(ASYNC_URL),new URL(ReviewURL));
                */

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }else{
            Toast.makeText(context, "No Internet? Wuuuuuut", Toast.LENGTH_SHORT).show();
        }
        tListAdapter.update();
        
    }

    @Override
    public void onListItemClick(ParcelableTrailer trailer) {
        /*
        Learned to start youtube/web browser from
        https://stackoverflow.com/questions/3004515/sending-an-intent-to-browser-to-open-specific-url
         */
        String youtubeURL = "https://www.youtube.com/watch?v=" + trailer.getKey() +"/";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL)));
    }

    @Override
    public void onListItemClick(ParcelableReview review) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("clicked",clicked);
    }

    public void toggleFavorite(View view){

        if(clicked){
            removeFromDb(movie.getId());
            //update ui
            changeImage();
        }else{
            insertToDb();
            changeImage();

        }
    }

    public void insertToDb(){
        ContentValues cv = new ContentValues();
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_ID,movie.getId());
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_TITLE,movie.getTitle());
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_RELEASE,movie.getReleaseDate());
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_RATING,movie.getVoteAverage());
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_PLOT,movie.getPlot());
        Uri uri = getContentResolver().insert(FavoritesContract.FavoritesEntry.CONTENT_URI,cv);
    }

    public void removeFromDb(int id){
        Uri uri = FavoritesContract.FavoritesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(Integer.toString(id)).build();
        getContentResolver().delete(uri, FavoritesContract.FavoritesEntry.COLUMN_ID + "=" + id
                ,null);

    }

    public void changeImage(){
        if (clicked){
            clicked = false;
            favBtn.setImageResource(R.drawable.heart);
            favLabel.setText(R.string.favorite);
        }else{
            clicked = true;
            favBtn.setImageResource(R.drawable.pinkheart);
            favLabel.setText(R.string.unfavorite);
        }
    }
}
