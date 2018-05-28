package com.josehinojo.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableTrailer implements Parcelable{

    String id;
    String key;
    String name;
    String type;

    public ParcelableTrailer(){

    }

    public ParcelableTrailer(String id,String key,String name,String type){
        this.id = id;
        this.key = key;
        this.name = name;
        this.type = type;
    }

    //setters
    public void setId(String id){ this.id = id; }
    public void setKey(String key){ this.key = key; }
    public void setName(String name){ this.name = name; }
    public void setType(String type){ this.type = type; }

    //getters
    public String getId(){ return id; }
    public String getKey(){ return key; }
    public String getName() { return name; }
    public String getType() { return type; }

    private ParcelableTrailer(Parcel in){
        id = in.readString();
        key = in.readString();
        name = in.readString();
        type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(type);
    }

    public String toString(){
        return  "This Trailer's info" +
                "\nid: " + id +
                "\nkey: " + key +
                "\nname: " + name +
                "\ntype: " + type;
    }

    public static final Parcelable.Creator<ParcelableTrailer> CREATOR = new Parcelable.Creator<ParcelableTrailer>(){
        @Override
        public ParcelableTrailer createFromParcel(Parcel in) {
            return new ParcelableTrailer(in);
        }

        @Override
        public ParcelableTrailer[] newArray(int size) {
            return new ParcelableTrailer[size];
        }
    };

}
