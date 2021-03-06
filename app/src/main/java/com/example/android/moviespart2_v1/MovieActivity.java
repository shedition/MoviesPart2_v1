package com.example.android.moviespart2_v1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviespart2_v1.data.FavMoviesContract;
import com.example.android.moviespart2_v1.util.DetailAPICall;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

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
    private static final String SORTTYPE = "SORT_TYPE";
    private static final String TAG = "MovieActivity";
    private Context mContext;
    public static int buttonState = 0;

    private static final int LOADER_ID = 0;
    private static final String SAVED_STATE = "state";
    private boolean state = false;
    private String mSortSelection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.moviespart2_v1.R.layout.activity_movie);
        mSelectedMovie = (Movie) getIntent().getExtras().getSerializable(MOVIE_KEY);
        mSortSelection = getIntent().getExtras().getString(SORTTYPE);
        if (mSortSelection.equals("topRated")){
            getSupportActionBar().setTitle(R.string.top_rated_action_bar);
        }
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
        if (mSelectedMovie.getTitleLength() > 18){
            mMovieTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
        }
        mMovieTitle.setText(mSelectedMovie.getTitle());
        mYearOfRelease.setText(mSelectedMovie.getReleaseYear());
        mUserRating.setText(mSelectedMovie.getVoteAvg());
        mOverview.setText(mSelectedMovie.getOverview());
        state = true;

        if (savedInstanceState != null && savedInstanceState.getBoolean(SAVED_STATE) == true) {
            Toast.makeText(mContext, "State Saved", Toast.LENGTH_SHORT).show();
            DetailAPICall detailAPICall = new DetailAPICall(mContext, mSelectedMovie.getID(), mRuntime,
                    mRVTrailer, mRVReview);
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        DetailAPICall detailAPICall = new DetailAPICall(mContext, mSelectedMovie.getID(), mRuntime,
                mRVTrailer, mRVReview);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {

                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                insertFavMovie();
            }

        } else {
            mFavorite.setImageResource(R.drawable.icons8_heart_outline_white);
            mFavorite.setTag(R.drawable.icons8_heart_outline_white);
            buttonState = 0;
            if (cursor.getCount() == 0) {
                Toast.makeText(mContext, "Movie not in db. Do nothing", Toast.LENGTH_LONG).show();
            } else if (cursor.getCount() == 1) {
                //delete from db
                getContentResolver().delete(mUri, null, null);

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
        }
    }

    public byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_STATE, false);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        state = savedInstanceState.getBoolean(SAVED_STATE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mMovieData = null;

            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    Uri uri = FavMoviesContract.FMovieEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(mSelectedMovie.getID()).build();
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
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            mFavorite.setImageResource(R.drawable.icons8_heart_outline_white);
            mFavorite.setTag(R.drawable.icons8_heart_outline_white);
            buttonState = 0;
        } else if (data.getCount() >= 1) {
            mFavorite.setImageResource(R.drawable.icons8_heart_outline_red);
            mFavorite.setTag(R.drawable.icons8_heart_outline_red);
            buttonState = 1;

        } else {
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
