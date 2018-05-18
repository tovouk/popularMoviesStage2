package com.josehinojo.popularmovies;

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
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    static int pageNumber = 1;

    final static String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    final static String POPULAR = "popular";
    final static String TOPRATED = "top_rated";
    final static String API_KEY = "[YOUR API KEY HERE]";
    final static String PAGE_NUMBER = "&language=en-US&page=" + pageNumber;

    private static List<ParcelableMovie> movieList = new ArrayList<>();
    private static RecyclerView movie_recycler_view;
    private static MovieListAdapter mListAdapter;



    static TextView messageTextView;
    static ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        messageTextView = (TextView)findViewById(R.id.messageTextView);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        movie_recycler_view = findViewById(R.id.movie_recycler_view);
        Log.i("Components initialized","Success");
        mListAdapter = new MovieListAdapter(movieList);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        movie_recycler_view.setLayoutManager(layoutManager);
        movie_recycler_view.setAdapter(mListAdapter);
        Log.i("recyclerview init ","Success");
        try {
            URL url = new URL(MOVIEDB_BASE_URL+POPULAR+API_KEY + PAGE_NUMBER);
            new MovieAsyncTask().execute(url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public static class MovieAsyncTask extends AsyncTask<URL,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader();
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
                Log.i("Has Input: ","true...continuing");
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
            if(s != null &&  !s.equals("")){
                try {
                    JSONObject json = new JSONObject(s);
                    JSONArray results = new JSONArray(json.getString("results"));
                    for(int i = 0;i<results.length();i++){
                        JSONObject movieObj = results.getJSONObject(i);
                        String poster = movieObj.getString("poster_path");
                        ParcelableMovie movie = new ParcelableMovie();
                        movie.setPosterIMG("http://image.tmdb.org/t/p/original" + poster);
                        Log.i("poster path",movie.getPosterIMG());
                        movieList.add(movie);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mListAdapter.notifyDataSetChanged();

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
