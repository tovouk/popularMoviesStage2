package com.josehinojo.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

public class ReviewsAsyncTask extends AsyncTask<URL,Void,String> {

    private Context context;
    private ReviewListAdapter rListAdapter;
    private ArrayList<ParcelableReview> reviewList = new ArrayList<>();

    public ReviewsAsyncTask(Context context){
        this.context = context;
    }

    public void setReviewParams(ReviewListAdapter rListAdapter,ArrayList<ParcelableReview> reviewList){
        this.rListAdapter = rListAdapter;
        this.reviewList = reviewList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL reviewURL = urls[0];
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection)reviewURL.openConnection();
            InputStream in = httpURLConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if(hasInput){
                httpURLConnection.disconnect();
                return scanner.next();

            }else{
                Log.e("Error: ","no data was retrieved");
                return null;
            }
        }catch(FileNotFoundException fileNotFoundException){
            return "Error!";
        } catch (IOException e) {
            Log.e("Error: ","connection cannot be established");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        ParcelableReview errorReview;
        if (s.equals("Error!")) {
                String error ="Either there are no reviews, your api key is invalid, or" +
                                " you are requesting too many movie details too fast.";
                String error2 = "Go back and try again Later";
                errorReview = new ParcelableReview(error2,error);
                reviewList.add(errorReview);
            rListAdapter.notifyItemChanged(0);
            return;
        }
        try {
            JSONObject json = new JSONObject(s);
            JSONArray results = new JSONArray(json.getString("results"));
            if (results.toString().equals("[]")){
                String error = "no reviews";
                String error2 = "Go back and Try again later";
                errorReview = new ParcelableReview(error,error2);
                reviewList.add(errorReview);
                rListAdapter.notifyItemChanged(0);
                return;
            }
            for(int i = 0;i<results.length();i++){
                JSONObject reviewObj = results.getJSONObject(i);
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
