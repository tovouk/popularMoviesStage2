package com.josehinojo.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {FavoriteMovie.class},version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase{
    /*
    Used new lessons in Android Architecture components to model this file

     */
    private static final String LOG_TAG = MovieDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "favorites";
    private static MovieDatabase databaseInstance;

    public static MovieDatabase getDatabaseInstance(Context context){
        if(databaseInstance == null){
            synchronized (LOCK){
                Log.d(LOG_TAG,"Creating new database instance");
                databaseInstance = Room.databaseBuilder(context.getApplicationContext(),MovieDatabase.class,
                        MovieDatabase.DATABASE_NAME).allowMainThreadQueries().build();
            }
        }
        Log.d(LOG_TAG,"Getting Database Instance");
        return databaseInstance;
    }

    public abstract FavoriteDao favoriteDao();

}
