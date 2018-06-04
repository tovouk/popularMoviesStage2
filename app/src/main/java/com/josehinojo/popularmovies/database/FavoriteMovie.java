package com.josehinojo.popularmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "movies")
public class FavoriteMovie implements Parcelable {
    /*
    Used new lessons in Android Architecture components to model this file

     */
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "overview")
    private String overview;
    @ColumnInfo(name = "rating")
    private float rating;

    @Ignore
    public FavoriteMovie(String title,String overview, float rating){
        this.title = title;
        this.overview = overview;
        this.rating = rating;
    }
    public FavoriteMovie(int id, String title,String overview, float rating){
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.rating = rating;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public float getRating() {
        return rating;
    }

    private FavoriteMovie(Parcel in){
        id = in.readInt();
        title = in.readString();
        overview = in.readString();
        rating = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeFloat(rating);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "{" +
                "id:" + id +
                ",title:" + title +
                ",overview: " + overview +
                ",rating: " + rating+
                "}";
    }

    public static final Parcelable.Creator<FavoriteMovie> CREATOR = new Parcelable.Creator<FavoriteMovie>() {
        @Override
        public FavoriteMovie createFromParcel(Parcel in) {
            return new FavoriteMovie(in);
        }

        @Override
        public FavoriteMovie[] newArray(int size) {
            return new FavoriteMovie[size];
        }
    };
}
