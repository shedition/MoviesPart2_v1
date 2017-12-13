package com.example.android.moviespart2_v1;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by waiyi on 12/4/2017.
 */

public class FavoriteMovie implements Serializable {

    private static final String TAG = "FavoriteMovie";
    private static final String TAG_ID = "id";
    private static final String TAG_OVERVIEW = "overview";
    private static final String TAG_POSTER_PATH = "poster_path";
    private static final String TAG_RELEASE_DATE = "release_date";
    private static final String TAG_RUNTIME = "runtime";
    private static final String TAG_TITLE = "title";
    private static final String TAG_VOTE_AVG = "vote_average";
    private static final String VOTE_AVG_APPEND = " out of 10";
    private static final String BASE_URL = "http://image.tmdb.org/t/p/w185//";

    private String mId;
    private String mOverview;
    private String mPosterPath;
    private String mYearOfRelease;
    private String mRuntime;
    private String mTitle;
    private String mVoteAvg;
    private String mURL;

    public FavoriteMovie(JSONObject jsonObject){
        try{
            mId = jsonObject.getString(TAG_ID);
            mOverview = jsonObject.getString(TAG_OVERVIEW);
            mPosterPath = jsonObject.getString(TAG_POSTER_PATH);
            mYearOfRelease = jsonObject.getString(TAG_RELEASE_DATE);
            mRuntime = jsonObject.getString(TAG_RUNTIME);
            mTitle = jsonObject.getString(TAG_TITLE);
            mVoteAvg = jsonObject.getString(TAG_VOTE_AVG);
            mURL = BASE_URL + mPosterPath;
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String getID(){
        return mId;
    }

    public String getmPosterPath(){
        return mURL;
    }

    public String getmYearOfRelease(){
        String[] parseYear = mYearOfRelease.split("-");
        return parseYear[0];
    }

    public String getmOverview(){
        return mOverview;
    }

    public String getmRuntime(){
        return mRuntime;
    }

    public String getmTitle(){
        return mTitle;
    }

    public String getmVoteAvg(){
        Log.d(TAG, "vote_avg =vote_avg = " + mVoteAvg);
        if (mVoteAvg.length() > 3){
            String newStr = mVoteAvg.substring(0, 2);
            mVoteAvg = newStr;
            return (mVoteAvg + VOTE_AVG_APPEND);
        } else {
            Log.d(TAG, "return vote_avg" + mVoteAvg);
            return (mVoteAvg + VOTE_AVG_APPEND);
        }
    }


}
