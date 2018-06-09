package com.josehinojo.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.josehinojo.popularmovies.database.FavoriteMovie;
import com.josehinojo.popularmovies.database.MovieDatabase;

public class AddFavoriteMovieViewModel extends ViewModel{
    /*
       Used new lessons in Android Architecture components to model this file

    */
    private LiveData<FavoriteMovie> movie;

    public AddFavoriteMovieViewModel(MovieDatabase db,int id){
        movie = db.favoriteDao().getMovieById(id);
    }

    public LiveData<FavoriteMovie> getMovie() {
        return movie;
    }
}
