package com.josehinojo.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.josehinojo.popularmovies.ParcelableTrailer;
import com.josehinojo.popularmovies.TrailerListAdapter;

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

public class TrailersAsyncTask extends AsyncTask<URL,Void,String>{
    //Ignore this class for now

    private Context context;
    private TrailerListAdapter tListAdapter;
    private ArrayList<ParcelableTrailer> trailerList = new ArrayList<>();

    public TrailersAsyncTask(Context context){
        this.context = context;
    }

    public void setTrailerParams(TrailerListAdapter tListAdapter,ArrayList<ParcelableTrailer> trailerList) {
        this.tListAdapter = tListAdapter;
        this.trailerList = trailerList;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL trailerURL = urls[0];
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection)trailerURL.openConnection();
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
        if (s.equals("Error!")) {
                Toast.makeText(context,"Error getting Trailers...try again!",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject json = new JSONObject(s);
            JSONArray results = new JSONArray(json.getString("results"));
            for(int i = 0;i<results.length();i++){
                JSONObject trailerObj = results.getJSONObject(i);
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
    }
}
