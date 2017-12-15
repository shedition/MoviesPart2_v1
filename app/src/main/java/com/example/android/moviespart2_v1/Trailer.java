package com.example.android.moviespart2_v1;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by waiyi on 11/26/2017.
 */

public class Trailer implements Serializable {

    private static final String TAG_TRAILER_ID = "id";
    private String trailerID;

    public Trailer(String trailerKey){

        trailerID = trailerKey;

    }

    public String getTrailerID(){
        return trailerID;
    }


}
