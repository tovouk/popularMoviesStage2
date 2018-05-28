package com.josehinojo.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class TrailersAndReviewsTask extends AsyncTask<URL,Void,JSONObject[]> {

    private Context context;
    private TrailerListAdapter tListAdapter;
    private ArrayList<ParcelableTrailer> trailerList = new ArrayList<>();
    private ReviewListAdapter rListAdapter;
    private ArrayList<ParcelableReview> reviewList = new ArrayList<>();

    public TrailersAndReviewsTask(Context context){
        this.context = context;
    }

    public void setTrailerParams(TrailerListAdapter tListAdapter,ArrayList<ParcelableTrailer> trailerList,
                                 ReviewListAdapter rListAdapter, ArrayList<ParcelableReview> reviewList) {
        this.tListAdapter = tListAdapter;
        this.trailerList = trailerList;
        this.rListAdapter = rListAdapter;
        this.reviewList = reviewList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject[] doInBackground(URL... urls) {
        JSONObject[] jsonObjects = new JSONObject[2];
        HttpURLConnection urlConnection = null;
        try{
            urlConnection = (HttpURLConnection) urls[0].openConnection();
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            String json = "";
            if(hasInput){
                json = scanner.next();
            }else{
                Log.e("Error: ","no data was retrieved");
                return null;
            }
            jsonObjects[0] = new JSONObject(json);
        }catch(FileNotFoundException fileNotFoundException) {

        }catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            assert urlConnection != null;
            urlConnection.disconnect();
        }
        try{
            urlConnection = (HttpURLConnection) urls[1].openConnection();
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            String json = "";
            if(hasInput){
                json = scanner.next();
            }else{
                Log.e("Error: ","no data was retrieved");
                return null;
            }
            jsonObjects[1] = new JSONObject(json);
        }catch(FileNotFoundException fileNotFoundException) {

        }catch(IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            assert urlConnection != null;
            urlConnection.disconnect();
        }


        return jsonObjects;
    }

    @Override
    protected void onPostExecute(JSONObject[] jsonObjects) {
        super.onPostExecute(jsonObjects);

        try{
            JSONObject trailers = jsonObjects[0];
            JSONArray trailer_results = new JSONArray(trailers.getString("results"));
            Log.i("trailers",trailer_results.toString());
            for(int i = 0;i<trailer_results.length();i++){
                JSONObject trailerObj = trailer_results.getJSONObject(i);
                String id = trailerObj.getString("id");
                String key =  trailerObj.getString("key");
                String name = trailerObj.getString("name");
                String type = trailerObj.getString("type");

                ParcelableTrailer trailer = new ParcelableTrailer(id,key,name,type);
                trailerList.add(trailer);
                tListAdapter.notifyItemChanged(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tListAdapter.notifyDataSetChanged();

        try{
            JSONObject reviews = jsonObjects[1];
            JSONArray review_results = new JSONArray(reviews.getString("results"));
            Log.i("reviews",review_results.toString());
            ParcelableReview errorReview;
            if (review_results.toString().equals("[]")){
                String error = "no reviews";
                String error2 = "Go back and Try again later";
                errorReview = new ParcelableReview(error,error2);
                reviewList.add(errorReview);
                rListAdapter.notifyItemChanged(0);
                return;
            }
            for(int i = 0;i<review_results.length();i++){
                JSONObject reviewObj = review_results.getJSONObject(i);
                String author = reviewObj.getString("author");
                String review =  reviewObj.getString("content");

                ParcelableReview review1 = new ParcelableReview(author,review);
                reviewList.add(review1);
                rListAdapter.notifyItemChanged(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rListAdapter.notifyDataSetChanged();
    }
}
