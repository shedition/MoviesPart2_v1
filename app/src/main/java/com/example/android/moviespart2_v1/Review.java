package com.example.android.moviespart2_v1;

import java.io.Serializable;

/**
 * Created by waiyi on 11/28/2017.
 */

public class Review implements Serializable{
    private String TAG = "Review";
    private static final String TAG_REVIEW_ID = "id";
    private String mContent;

    public Review(String content){
        mContent = content;
    }

    public String getContent(){
        return mContent;
    }
}
