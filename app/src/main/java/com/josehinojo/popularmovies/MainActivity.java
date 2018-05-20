package com.josehinojo.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

    final static String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    static String sortOption = "popular";
    final static String API_KEY = "[YOUR_API_KEY_HERE]";
    static int pageNumber = 1;
    static String PAGE_NUMBER = "&language=en-US&page=" + pageNumber;
    final static String IMAGE_URL = "http://image.tmdb.org/t/p/original";

    static String ASYNC_URL =  MOVIEDB_BASE_URL + sortOption + API_KEY + PAGE_NUMBER;

    private static ArrayList<ParcelableMovie> movieList = new ArrayList<>();
    private static RecyclerView movie_recycler_view;
    private static MovieListAdapter mListAdapter;

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
            pageNumber = 1;
            getJson();
        }else{
            showMovies();
            movieList = savedInstanceState.getParcelableArrayList(MOVIELIST_KEY);
        }

        mListAdapter.update();

    }

    /*
    Refreshed Memory on menus via older Lesson
    https://github.com/udacity/ud851-Exercises/blob/student/Lesson02-GitHub-Repo-Search/T02.02-Solution-AddMenu/app/src/main/java/com/example/android/datafrominternet/MainActivity.java
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.menuSortPopular) {
            pageNumber = 1;
            movieList.clear();
            mListAdapter.notifyDataSetChanged();
            sortOption = "popular";
            ASYNC_URL =  MOVIEDB_BASE_URL + sortOption + API_KEY + PAGE_NUMBER;
            getJson();
            return true;

        }else if(itemId == R.id.menuSortRating){
            pageNumber = 1;
            movieList.clear();
            mListAdapter.notifyDataSetChanged();
            sortOption = "top_rated";
            ASYNC_URL =  MOVIEDB_BASE_URL + sortOption + API_KEY + PAGE_NUMBER;
            getJson();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    //Went back to my Growing With Google Challenge Scholarship course to review onSaveInstanceState
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIELIST_KEY,movieList);
        super.onSaveInstanceState(outState);
    }


    /*
    Used the following link to help me with passing parcelables
    https://stackoverflow.com/questions/10107442/android-how-to-pass-parcelable-object-to-intent-and-use-getparcelable-method-of
     */
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

    //written this way for page prep for stage 2
    public static int determineMoviePosition(){
        int startPosition = 0;
        if(pageNumber ==1){
            startPosition = 0;
        }else if (pageNumber > 1){
            startPosition = pageNumber * 20 - 20;
        }else{
            Toast.makeText(context,"Could not determine position...let the developer know",
                    Toast.LENGTH_SHORT).show();
        }
        return startPosition;
    }

    /*
    preps ASYNC_URL for async task
    Written this way to prepare for stage 2
    */
    public void getJson(){
        PAGE_NUMBER = "&language=en-US&page=" + pageNumber;
        ASYNC_URL =  MOVIEDB_BASE_URL + sortOption + API_KEY + PAGE_NUMBER;
        try {
            URL url = new URL(ASYNC_URL);
            new MovieAsyncTask().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
