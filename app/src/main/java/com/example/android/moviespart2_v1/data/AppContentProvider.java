package com.example.android.moviespart2_v1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by waiyi on 11/29/2017.
 */

public class AppContentProvider extends ContentProvider {
    private static final String TAG = "AppContentProvider";
    private AppDatabase mDBHelper;
    public static final int FAVORITES = 100;
    public static final int FAVORITE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavMoviesContract.AUTHORITY, FavMoviesContract.PATH_FAVMOVIES, FAVORITES);
        uriMatcher.addURI(FavMoviesContract.AUTHORITY, FavMoviesContract.PATH_FAVMOVIES +
                "/#", FAVORITE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDBHelper = AppDatabase.getInstance(context);
        return true;
    }

    //Self: We can call the insert from "MARK AS FAVORITES" using a content resolver.
    //We'll want to link a call to the insert method to the fav button that is not yellow,
    //so that it's called whenever a user clicks on the button.

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Log.d(TAG, "INSERT match is " + match);
        Uri returnUri;

        switch (match) {
            case FAVORITES:
                //Note to self: If insert wasn't successful, id will be -1.
                long id = db.insert(FavMoviesContract.FMovieEntry.TABLE_NAME, null, values);
                Log.d(TAG, "AppContentProvider insert");

                //Self: So if the ID is valid, then the URI we construct will be our
                //main content URI, which has the authority, path, and id appended to it.
                if (id >= 0) {
                    returnUri = ContentUris.withAppendedId(FavMoviesContract.FMovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //We need to notify the resolver something has changed, so it can update the db and
        //any associated UI accordingly.

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case FAVORITES:
                retCursor = db.query(FavMoviesContract.FMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "movieID=?";
                String[] mSelectionArgs = new String[]{id};

                retCursor = db.query(FavMoviesContract.FMovieEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;


//                queryBuilder.setTables(FavMoviesContract.FMovieEntry.TABLE_NAME);
//                long id = FavMoviesContract.getMovieId(uri);
//                queryBuilder.appendWhere(FavMoviesContract.FMovieEntry.COLUMN_MOVIEID + " = " +
//                        id);
//                retCursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
//                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int movieDeleted;

        switch (match) {
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "movieID=?";
                String[] mSelectionArgs = new String[]{id};
                movieDeleted = db.delete(FavMoviesContract.FMovieEntry.TABLE_NAME, mSelection,
                        mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (movieDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return movieDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update called with uri " + uri);
        throw new UnsupportedOperationException("No update operation for this app");
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                String contentType = "vnd.android.cursor.dir/vnd." + FavMoviesContract.AUTHORITY +
                        "." + FavMoviesContract.FMovieEntry.TABLE_NAME;
                return contentType;

            case FAVORITE_WITH_ID:
                String contentItemType = "vnd.android.cursor.item/vnd." + FavMoviesContract.AUTHORITY +
                        "." + FavMoviesContract.FMovieEntry.TABLE_NAME;
                return contentItemType;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


    }


}
