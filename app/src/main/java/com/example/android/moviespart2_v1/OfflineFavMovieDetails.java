package com.example.android.moviespart2_v1;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by waiyi on 12/16/2017.
 */

public class OfflineFavMovieDetails implements Serializable{

    private static final String TAG = "OfflineFavMovieDetails";
    private String aTitle;
    private String aMID;
    private String aYear;
    private String aRating;
    private String aOverview;
    private byte[] aByteArray;
    transient Bitmap anImage;

    public OfflineFavMovieDetails(String aTitle, String aMID, String aYear, String aRating,
                                  String aOverview, Bitmap anImage){

        this.aTitle = aTitle;
        this.aMID = aMID;
        this.aYear = aYear;
        this.aRating = aRating;
        this.aOverview = aOverview;
        this.anImage = anImage;
    }

    public String getTitle(){
        return aTitle;
    }

    public String getMovieID(){
        return aMID;
    }

    public String getReleaseYear(){
        return aYear;
    }

    public String getRating(){
        return aRating;
    }

    public String getOverview(){
        return aOverview;
    }

    public Bitmap getImage() {
        return anImage;
    }



}
