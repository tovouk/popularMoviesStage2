package com.josehinojo.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.josehinojo.popularmovies.database.FavoriteMovie;
import com.josehinojo.popularmovies.database.MovieDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel{
    /*
       Used new lessons in Android Architecture components to model this file

    */
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<FavoriteMovie>> favMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        MovieDatabase db = MovieDatabase.getDatabaseInstance(this.getApplication());
        Log.d(TAG,"Retrieving movies from database");
        favMovies = db.favoriteDao().loadAllMovies();
    }

    public LiveData<List<FavoriteMovie>> getFavMovies() {
        return favMovies;
    }
}
