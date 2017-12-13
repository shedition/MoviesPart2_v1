package com.example.android.moviespart2_v1.data;

import android.content.ContentUris;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by waiyi on 11/29/2017.
 */

public class FavMoviesContract {
    public static final String AUTHORITY = "com.example.android.moviespart2_v1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVMOVIES = "favorites";

    public static final class FMovieEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVMOVIES).build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIEID = "movieID";
        public static final String COLUMN_RELEASEDATE = "releaseDate";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_IMAGE = "image";

    }


    static long getMovieId(Uri uri){
        return ContentUris.parseId(uri);
    }
}
