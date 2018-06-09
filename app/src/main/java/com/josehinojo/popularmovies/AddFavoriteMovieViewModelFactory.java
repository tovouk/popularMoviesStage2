package com.josehinojo.popularmovies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.josehinojo.popularmovies.database.MovieDatabase;

public class AddFavoriteMovieViewModelFactory extends ViewModelProvider.NewInstanceFactory{
    /*
       Used new lessons in Android Architecture components to model this file

    */
    private final MovieDatabase db;
    private final int favoriteMovieId;

    public AddFavoriteMovieViewModelFactory(MovieDatabase db, int favMovieId){
        this.db = db;
        favoriteMovieId = favMovieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AddFavoriteMovieViewModel(db,favoriteMovieId);
    }
}
