package com.example.android.moviespart2_v1;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviespart2_v1.data.FavMoviesContract;
import com.example.android.moviespart2_v1.util.DetailAPICall;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by waiyi on 9/10/2017.
 */

public class MovieActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ImageView mMovieImageView;
    private TextView mMovieTitle;
    private TextView mUserRating;
    private TextView mYearOfRelease;
    private TextView mRuntime;
    private TextView mOverview;
    private ImageView mFavorite;
    private Movie mSelectedMovie;
    private FavoriteMovie mFSelectedMovie;
    private RecyclerView mRVTrailer;
    private RecyclerView mRVReview;
    private static final String MOVIE_KEY = "MOVIE";
    private static final String F_MOVIE_KEY = "FMOVIE";
    private static final String TAG = "MovieActivity";
    private Context mContext;
    public static int buttonState = 0;

    private static final String ON_CREATE = "onCreate";
    private static final String ON_START = "onStart";
    private static final String ON_RESUME = "onResume";
    private static final String ON_PAUSE = "onPause";
    private static final String ON_STOP = "onStop";
    private static final String ON_RESTART = "onRestart";
    private static final String ON_DESTROY = "onDestroy";
    private static final String ON_SAVEINSTANCESTATE = "onSaveInstanceState";

    private static final int LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logAndAppend(ON_CREATE);
        setContentView(com.example.android.moviespart2_v1.R.layout.activity_movie);

        mSelectedMovie = (Movie) getIntent().getExtras().getSerializable(MOVIE_KEY);
        mMovieImageView = (ImageView) findViewById(R.id.imageViewPoster);
        Picasso.with(this).load(mSelectedMovie.getPosterImagePath()).into(mMovieImageView);
        mContext = getApplicationContext();
        mMovieTitle = (TextView) findViewById(R.id.movieTitle);
        mYearOfRelease = (TextView) findViewById(R.id.year);
        mRuntime = (TextView) findViewById(R.id.runtime);
        mUserRating = (TextView) findViewById(R.id.rating);
        mOverview = (TextView) findViewById(R.id.synopsis);
        mOverview.setMovementMethod(new ScrollingMovementMethod());
        mFavorite = (ImageView) findViewById(R.id.button);
        mFavorite.setSaveEnabled(true);
        mRVTrailer = (RecyclerView) findViewById(R.id.rvTrailers);
        LinearLayoutManager layoutManagerTrailer = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRVTrailer.setLayoutManager(layoutManagerTrailer);
        mRVReview = (RecyclerView) findViewById(R.id.reviews);
        LinearLayoutManager layoutManagerReview = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRVReview.setLayoutManager(layoutManagerReview);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext,
                layoutManagerReview.getOrientation());
        dividerItemDecoration.setDrawable(mContext.getResources().getDrawable(R.drawable.line_divider));
        mRVReview.addItemDecoration(dividerItemDecoration);
        mMovieTitle.setText(mSelectedMovie.getTitle());
        mYearOfRelease.setText(mSelectedMovie.getReleaseYear());
        mUserRating.setText(mSelectedMovie.getVoteAvg());
        mOverview.setText(mSelectedMovie.getOverview());

        if (savedInstanceState != null && savedInstanceState.containsKey("mFavorite")) {
            Log.d(TAG, "viewTag = ");

            //int viewTag = Integer.valueOf(savedInstanceState.getInt("mFavorite"));
            int favtag = savedInstanceState.getInt("mFavorite");

            if (favtag == R.drawable.icons8_heart_outline_red) {
                mFavorite.setImageResource(R.drawable.icons8_heart_outline_red);
                mFavorite.setTag(R.drawable.icons8_heart_outline_red);
                buttonState = 1;
            } else {
                mFavorite.setImageResource(R.drawable.icons8_heart_outline_white);
                mFavorite.setTag(R.drawable.icons8_heart_outline_white);
                buttonState = 0;
            }

            Toast.makeText(getBaseContext(), "savedInstanceState called", Toast.LENGTH_LONG).show();

        } else {
            loaderLog("initLoader called in savedInstanceState");
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);

            DetailAPICall detailAPICall = new DetailAPICall(mContext, mSelectedMovie.getID(), mRuntime,
                    mRVTrailer, mRVReview);
        }
    }

    public void onClickAddFavorite(View view) {

        mFavorite = (ImageView) view;
        Cursor cursor;
        Uri mUri = FavMoviesContract.FMovieEntry.CONTENT_URI;
        mUri = mUri.buildUpon().appendPath(mSelectedMovie.getID()).build();

        cursor = getContentResolver().query(mUri,
                null,
                null,
                null,
                null);

        if ((Integer) mFavorite.getTag() == R.drawable.icons8_heart_outline_white) {
            mFavorite.setImageResource(R.drawable.icons8_heart_outline_red);
            mFavorite.setTag(R.drawable.icons8_heart_outline_red);
            buttonState = 1;

            if (cursor.getCount() == 0) {
                Toast.makeText(mContext, "First insert", Toast.LENGTH_LONG).show();
                insertFavMovie();
            } else if (cursor.getCount() == 1) {
                Toast.makeText(mContext, "Movie already in database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "Something's wrong. There is more than one record with the same movie ID",
                        Toast.LENGTH_LONG).show();
            }

        } else {
            mFavorite.setImageResource(R.drawable.icons8_heart_outline_white);
            mFavorite.setTag(R.drawable.icons8_heart_outline_white);
            buttonState = 0;
            if (cursor.getCount() == 0){
                Toast.makeText(mContext, "Movie not in db. Do nothing", Toast.LENGTH_LONG).show();
            } else if (cursor.getCount() == 1){
                //delete from db
                getContentResolver().delete(mUri, null, null);
                Toast.makeText(mContext, "Movie deleted", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(mContext, "Something's wrong", Toast.LENGTH_LONG).show();
            }
        }

        cursor.close();

    }

    public void insertFavMovie() {

        String cvTtitle = mMovieTitle.getText().toString();
        String cvMovieid = mSelectedMovie.getID();
        String cvYearOfRelease = mSelectedMovie.getReleaseYear();
        String cvRating = mSelectedMovie.getVoteAvg();
        String cvSynopsis = mOverview.getText().toString();
        BitmapDrawable drawable = (BitmapDrawable) mMovieImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        byte[] blob = getBitmapAsByteArray(bitmap);

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavMoviesContract.FMovieEntry.COLUMN_TITLE, cvTtitle);
        contentValues.put(FavMoviesContract.FMovieEntry.COLUMN_MOVIEID, cvMovieid);
        contentValues.put(FavMoviesContract.FMovieEntry.COLUMN_RELEASEDATE, cvYearOfRelease);
        contentValues.put(FavMoviesContract.FMovieEntry.COLUMN_RATING, cvRating);
        contentValues.put(FavMoviesContract.FMovieEntry.COLUMN_SYNOPSIS, cvSynopsis);
        contentValues.put(FavMoviesContract.FMovieEntry.COLUMN_IMAGE, blob);


        //Insert fav movie via a ContentResolver
        Uri uri = getContentResolver().insert(FavMoviesContract.FMovieEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            Log.d(TAG, "Heart: uri is not null");
            Toast.makeText(getBaseContext(), "uri.toString = " + uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public Bitmap convertImageToBitmap() {
        mMovieImageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(mMovieImageView.getLayoutParams().width,
                mMovieImageView.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mMovieImageView.layout(0, 0, mMovieImageView.getLayoutParams().width,
                mMovieImageView.getLayoutParams().height);
        mMovieImageView.draw(canvas);
        return bitmap;


    }

    @Override
    protected void onStart() {
        super.onStart();
        logAndAppend(ON_START);

    }

    @Override
    protected void onResume() {
        super.onResume();
        logAndAppend(ON_RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        logAndAppend(ON_PAUSE);
    }


    @Override
    protected void onStop() {
        super.onStop();
        logAndAppend(ON_STOP);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        logAndAppend(ON_RESTART);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logAndAppend(ON_DESTROY);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        logAndAppend(ON_SAVEINSTANCESTATE);

        int favoriteTag = ((Integer) mFavorite.getTag()).intValue();
        outState.putInt("mFavorite", favoriteTag);
        Log.d(TAG, "onSaveInstanceState is called. favoriteTag = " + favoriteTag);


    }

    private void logAndAppend(String lifecycleEvent) {
        Log.d(TAG, "MovieActivity Lifecycle Event: " + lifecycleEvent);

    }

    private void loaderLog(String stage) {
        Log.d(TAG, "Loader method: " + stage);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        loaderLog("onCreateLoader");
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mMovieData = null;

            @Override
            protected void onStartLoading() {
                loaderLog("onStartLoading");
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                loaderLog("loadInBackground");
                try {
                    Uri uri = FavMoviesContract.FMovieEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(mSelectedMovie.getID()).build();
                    Log.d(TAG, "Uri.toString == " + uri.toString());
                    return getContentResolver().query(uri,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                loaderLog("deliverResult");
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loaderLog("onLoadFinished");
        if (data.getCount() == 0) {
            mFavorite.setImageResource(R.drawable.icons8_heart_outline_white);
            mFavorite.setTag(R.drawable.icons8_heart_outline_white);
            buttonState = 0;
            loaderLog("inside onLoadFinished If statement");
        } else if (data.getCount() >= 1) {
            mFavorite.setImageResource(R.drawable.icons8_heart_outline_red);
            mFavorite.setTag(R.drawable.icons8_heart_outline_red);
            buttonState = 1;
            loaderLog("inside onLoadFinished elseif statement");

        } else {
            Log.d(TAG, "cursor getcount = " + data.getCount());
            loaderLog("inside onLoadFinished else statement");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loaderLog("onLoaderReset");
        Log.d(TAG, "onLoaderReset");
        Toast.makeText(mContext, "onLoaderReset", Toast.LENGTH_LONG).show();

    }

    public boolean shareTrailer(){
        DetailAPICall getFirstTrailer = new DetailAPICall();
        ArrayList<Trailer> trailerArrayList = getFirstTrailer.getTrailerArrayList();
        if (trailerArrayList.size() >= 1){
            String pageUrl = "vnd.youtube://" + trailerArrayList.get(0).getTrailerID();
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, pageUrl);

            try {
                startActivity(Intent.createChooser(intent, "Share:"));
            }catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, "Sorry. Unable to share at this time.", Toast.LENGTH_LONG).show();
                return false;
            }

        }
        return true;

    }
}
