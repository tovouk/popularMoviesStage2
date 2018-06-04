package com.josehinojo.popularmovies.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FavoriteDao {
    /*
    Used new lessons in Android Architecture components to model this file

     */
    @Query("SELECT * FROM movies ORDER BY id")
    List<FavoriteMovie> loadAllMovies();

    @Insert
    void insertMovie(FavoriteMovie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(FavoriteMovie movie);

    @Delete
    void deleteMovie(FavoriteMovie movie);

    @Query("SELECT * FROM movies WHERE id = :id")
    FavoriteMovie getMovieById(int id);
}