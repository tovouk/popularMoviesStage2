package com.josehinojo.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableReview implements Parcelable{

    String author;
    String review;

    public ParcelableReview(){

    }

    public ParcelableReview(String author,String review){
        this.author = author;
        this.review = review;
    }

    //setters
    public void setAuthor(String author){ this.author = author; }
    public void setReview(String review){ this.review = review; }

    //getters
    public String getAuthor(){return author;}
    public String getReview() { return review; }

    private ParcelableReview(Parcel in){
        author = in.readString();
        review = in.readString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(review);
    }

    @Override
    public String toString() {
        return "Review contents:\n" +
                "Author: " + author +
                "\nReview: " + review;
    }

    public static final Parcelable.Creator<ParcelableReview> CREATOR = new Parcelable.Creator<ParcelableReview>(){
        @Override
        public ParcelableReview createFromParcel(Parcel in) {
            return new ParcelableReview(in);
        }

        @Override
        public ParcelableReview[] newArray(int size) { return new ParcelableReview[size]; }
    };

}
