package com.josehinojo.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static com.josehinojo.popularmovies.MainActivity.IMAGE_URL;
import static com.josehinojo.popularmovies.MainActivity.determineMoviePosition;
import static com.josehinojo.popularmovies.MainActivity.showMovies;

public class MovieAsyncTask extends AsyncTask<URL,Void,String> {

    private MovieListAdapter mListAdapter;
    private ArrayList<ParcelableMovie> movieList = new ArrayList<>();
    private int pageNumber;


    public void setMovieParams(MovieListAdapter movieListAdapter,ArrayList<ParcelableMovie> movieList){
        this.mListAdapter = movieListAdapter;
        this.movieList = movieList;
    }
    public void setPageNumber(int num){
        this.pageNumber = num;
    }


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
            for(int i = determineMoviePosition();i<results.length();i++){
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
                pageNumber++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
