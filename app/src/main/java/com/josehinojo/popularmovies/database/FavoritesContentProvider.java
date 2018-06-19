package com.josehinojo.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.josehinojo.popularmovies.database.FavoritesContract.FavoritesEntry.TABLE_NAME;

public class FavoritesContentProvider extends ContentProvider {
    //From Udacity Build Your Own content provider lesson
    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoritesContract.AUTHORITY,FavoritesContract.PATH_FAVORITES,FAVORITES);
        uriMatcher.addURI(FavoritesContract.AUTHORITY,FavoritesContract.PATH_FAVORITES + "/#",FAVORITES_WITH_ID);
        return uriMatcher;
    }

    private FavoritesDbHelper favoritesDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        favoritesDbHelper = new FavoritesDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = favoritesDbHelper.getReadableDatabase();
        int matcher = uriMatcher.match(uri);
        Cursor cursor;
        switch (matcher){
            case FAVORITES:
                cursor = db.query(TABLE_NAME,
                        projection,selection,selectionArgs,null,null,
                        sortOrder);
                break;
            case FAVORITES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String selection1 = FavoritesContract.FavoritesEntry.COLUMN_ID + "=?";
                String[] argument = new String[]{id};
                cursor = db.query(TABLE_NAME,
                        projection,selection1,argument,null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = uriMatcher.match(uri);

        switch (match) {
            case FAVORITES:
                // directory
                return "vnd.android.cursor.dir" + "/" + FavoritesContract.AUTHORITY + "/" + FavoritesContract.PATH_FAVORITES;
            case FAVORITES_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + FavoritesContract.AUTHORITY + "/" + FavoritesContract.PATH_FAVORITES;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();
        int matcher = uriMatcher.match(uri);

        Uri finalUri;
        switch (matcher){
            case FAVORITES:
                long id = db.insert(TABLE_NAME,null,values);
                if (id>0){
                    finalUri = ContentUris.withAppendedId(FavoritesContract.FavoritesEntry.CONTENT_URI,id);
                }else{
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri "+ uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return finalUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();

        int matcher = uriMatcher.match(uri);
        int tasksDeleted;

        switch (matcher) {

            case FAVORITES_WITH_ID:

                String id = uri.getPathSegments().get(1);
                Log.i("Got the following Id",id);
                tasksDeleted = db.delete(TABLE_NAME,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        //Keep track of if an update occurs
        int favoritesUpdated;

        // match code
        int matcher = uriMatcher.match(uri);

        switch (matcher) {
            case FAVORITES_WITH_ID:
                //update a single task by getting the id
                String id = uri.getPathSegments().get(1);
                //using selections
                favoritesUpdated = favoritesDbHelper.getWritableDatabase().update(TABLE_NAME, values, "id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (favoritesUpdated != 0) {
            //set notifications if a task was updated
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return number of tasks updated
        return favoritesUpdated;
    }
}
