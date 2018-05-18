package com.josehinojo.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableMovie implements Parcelable {

    private int id;
    private String title;
    private String releaseDate;
    private String posterIMG;
    private String backdropIMG;
    private double voteAverage;
    private String plot;

    public ParcelableMovie(){

    }

    public ParcelableMovie(int id, String title, String releaseDate,String posterIMG,String backdropIMG,
                           double voteAverage,String plot){
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterIMG = posterIMG;
        this.backdropIMG = backdropIMG;
        this.voteAverage = voteAverage;
        this.plot = plot;
    }

    public void setId(int id){
        this.id = id;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setReleaseDate(String releaseDate){
        this.releaseDate = releaseDate;
    }
    public void setPosterIMG(String posterIMG){
        this.posterIMG = posterIMG;
    }
    public void setBackdropIMG(String backdropIMG){
        this.backdropIMG = backdropIMG;
    }
    public void setVoteAverage(double voteAverage){
        this.voteAverage = voteAverage;
    }
    public void setPlot(String plot){
        this.plot = plot;
    }

    public int getId(){
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getReleaseDate(){
        return releaseDate;
    }
    public String getPosterIMG(){
        return posterIMG;
    }
    public String getBackdropIMG(){
        return backdropIMG;
    }
    public double getVoteAverage(){
        return voteAverage;
    }
    public String getPlot(){
        return plot;
    }

    private ParcelableMovie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        releaseDate = in.readString();
        posterIMG = in.readString();
        backdropIMG = in.readString();
        voteAverage = in.readDouble();
        plot = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(posterIMG);
        dest.writeString(backdropIMG);
        dest.writeDouble(voteAverage);
        dest.writeString(plot);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "{" +
                "id:" + id +
                ",title:" + title +
                ",releaseDate:" + releaseDate +
                ",posterIMG:" + posterIMG +
                ",backdropIMG:" + backdropIMG +
                ",average_vote:" + voteAverage +
                ",plot:" + plot +
                "}";
    }

    public static final Parcelable.Creator<ParcelableMovie> CREATOR = new Parcelable.Creator<ParcelableMovie>() {
        @Override
        public ParcelableMovie createFromParcel(Parcel in) {
            return new ParcelableMovie(in);
        }

        @Override
        public ParcelableMovie[] newArray(int size) {
            return new ParcelableMovie[size];
        }
    };
}
