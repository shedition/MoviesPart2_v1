package com.example.android.moviespart2_v1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by waiyi on 11/29/2017.
 */

class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";
    private static final String DATABASE_NAME = "FavMoviesDB.db";
    private static final int VERSION = 1;
    //private static volatile AppDatabase instance = null;
    private static AppDatabase instance = null;

    private AppDatabase(Context context){
        super(context, DATABASE_NAME, null, VERSION);
        Log.d(TAG, "AppDatabase: constructor");
    }

    static AppDatabase getInstance(Context context){
        if(instance == null){
            Log.d(TAG, "getInstance: creating new instance");
            instance = new AppDatabase(context);
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        final String CREATE_TABLE = "CREATE TABLE " + FavMoviesContract.FMovieEntry.TABLE_NAME + " (" +
                FavMoviesContract.FMovieEntry._ID + " INTEGER PRIMARY KEY, " +
                FavMoviesContract.FMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavMoviesContract.FMovieEntry.COLUMN_MOVIEID + " TEXT NOT NULL, " +
                FavMoviesContract.FMovieEntry.COLUMN_RELEASEDATE + " TEXT, " +
                FavMoviesContract.FMovieEntry.COLUMN_RATING + " TEXT, " +
                FavMoviesContract.FMovieEntry.COLUMN_SYNOPSIS + " TEXT, " +
                FavMoviesContract.FMovieEntry.COLUMN_IMAGE + " BLOB);";

        Log.d(TAG, CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + FavMoviesContract.FMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
