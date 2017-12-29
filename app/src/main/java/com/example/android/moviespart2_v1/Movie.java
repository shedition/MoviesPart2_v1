package com.example.android.moviespart2_v1;

import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by waiyi on 9/10/2017.
 */

public class Movie implements Serializable{

    private static final String TAG_POSTER_PATH = "poster_path";
    private static final String TAG_TITLE = "title";
    private static final String TAG_OVERVIEW = "overview";
    private static final String TAG_VOTE_AVG = "vote_average";
    private static final String TAG_RELEASE_DATE = "release_date";
    private static final String TAG_ID = "id";
    private static final String VOTE_AVG_APPEND = " out of 10";
    private static final String TAG_RUNTIME = "runtime";


    private String base_URL = "http://image.tmdb.org/t/p/w185//";
    private static final String TAG = "Movie";
    private String mTitle;
    private String mPosterImagePath;
    private String mOverview;
    private String mVoteAvg;
    private String mReleaseDate;
    private String mReleaseYear;
    private String mURL;
    private String mId;
    private String mRuntime;
    private ArrayList<HashMap<String, String>> movieList;


    public Movie(JSONObject movieJSON){

        try{

            mTitle = movieJSON.getString(TAG_TITLE);
            mReleaseDate = movieJSON.getString(TAG_RELEASE_DATE);

            mVoteAvg = movieJSON.getString(TAG_VOTE_AVG);
            mPosterImagePath = movieJSON.getString(TAG_POSTER_PATH);
            mOverview = movieJSON.getString(TAG_OVERVIEW);
            mId = movieJSON.getString(TAG_ID);

            mURL = base_URL + mPosterImagePath;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle(){
        return mTitle;
    }

    public int getTitleLength(){
        return mTitle.length();
    }

    public String getPosterImagePath()
    {
        Log.d(TAG, "posterImageURL");
        return mURL;
    }

    public String getVoteAvg(){
        if (mVoteAvg.length() > 3){
            String newStr = mVoteAvg.substring(0, 2);
            Log.d(TAG, "vote_avg substring = " + mVoteAvg);
            mVoteAvg = newStr;
            return (mVoteAvg + VOTE_AVG_APPEND);
        } else {

            return (mVoteAvg + VOTE_AVG_APPEND);
        }
    }

    public String getOverview(){
        return mOverview;
    }



    public String getReleaseDate(){
        return mReleaseDate;
    }

    public String getReleaseYear(){
        String sDate = mReleaseDate;
        String[] parseDate = sDate.split("-");
        return parseDate[0];

    }

    public String getID() {
        return mId;
    }

}