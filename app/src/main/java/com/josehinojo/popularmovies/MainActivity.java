package com.josehinojo.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.ListItemClickListener {

    private static final String MOVIELIST_KEY = "movieList";

    static int pageNumber = 1;

    final static String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    final static String POPULAR = "popular";
    final static String TOPRATED = "top_rated";
    final static String API_KEY = "[YOUR_API_KEY_HERE]";
    final static String PAGE_NUMBER = "&language=en-US&page=" + pageNumber;
    final static String IMAGE_URL = "http://image.tmdb.org/t/p/original";

    private static ArrayList<ParcelableMovie> movieList = new ArrayList<>();
    private static RecyclerView movie_recycler_view;
    private static MovieListAdapter mListAdapter;



    static TextView messageTextView;
    static ProgressBar loadingIndicator;

    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
//        messageTextView = (TextView)findViewById(R.id.messageTextView);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        movie_recycler_view = findViewById(R.id.movie_recycler_view);
        showLoader();
        mListAdapter = new MovieListAdapter(movieList,this);
        mListAdapter.setContext(context);
        /*
        I used the following to help understand gridlayoutmanager better
        https://www.journaldev.com/13792/android-gridlayoutmanager-example
         */
        int spancount =2;
        /*
        if orientation changes adjust grid columns
        https://developer.android.com/reference/android/content/res/Configuration#orientation
         */
        if(getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            spancount = 2;
        }else if(getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            spancount = 3;
        }
        RecyclerView.LayoutManager  layoutManager = new GridLayoutManager(getApplicationContext(),spancount);
        movie_recycler_view.setLayoutManager(layoutManager);
        movie_recycler_view.setAdapter(mListAdapter);

        if(savedInstanceState == null || !savedInstanceState.containsKey(MOVIELIST_KEY)) {

            try {
                URL url = new URL(MOVIEDB_BASE_URL + POPULAR + API_KEY + PAGE_NUMBER);
                new MovieAsyncTask().execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else{
            showMovies();
            movieList = savedInstanceState.getParcelableArrayList(MOVIELIST_KEY);
        }

        mListAdapter.update();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIELIST_KEY,movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(ParcelableMovie movie) {
        Log.i("movie backdrop",movie.getBackdropIMG());
        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        intent.putExtra("sentMovie", movie);
        startActivity(intent);
    }

    public static class MovieAsyncTask extends AsyncTask<URL,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL movieURL = urls[0];
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection)movieURL.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");
                boolean hasInput = scanner.hasNext();
                if(hasInput){
                    return scanner.next();
                }else{
                    Log.e("Error: ","no data was retrieved");
                    return null;
                }
            } catch (IOException e) {
                Log.e("Error: ","connection cannot be established");
                e.printStackTrace();
            }finally {
                assert httpURLConnection != null;
                httpURLConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showMovies();
                /*
                Refreshed knowledge on JSONArray and JSONObject
                https://developer.android.com/reference/org/json/JSONArray
                https://developer.android.com/reference/org/json/JSONObject
                 */
                try {
                    JSONObject json = new JSONObject(s);
                    JSONArray results = new JSONArray(json.getString("results"));
                    for(int i = 0;i<results.length();i++){
                        JSONObject movieObj = results.getJSONObject(i);
                        int id = movieObj.getInt("id");
                        String title = movieObj.getString("title");
                        String releaseDate = movieObj.getString("release_date");
                        String poster = IMAGE_URL + movieObj.getString("poster_path");
                        String backdrop = IMAGE_URL + movieObj.getString("backdrop_path");
                        double vote = movieObj.getDouble("vote_average");
                        String overview = movieObj.getString("overview");
                        ParcelableMovie movie = new ParcelableMovie(id,title,releaseDate,poster,
                                backdrop,vote,overview);
                        movieList.add(movie);
                        mListAdapter.notifyItemChanged(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }

    public static void showLoader(){
        movie_recycler_view.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    public static void showMovies(){
        loadingIndicator.setVisibility(View.GONE);
        movie_recycler_view.setVisibility(View.VISIBLE);
        Log.i("Movies shown",": true");
    }

}
