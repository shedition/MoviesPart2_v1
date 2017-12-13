package com.example.android.moviespart2_v1;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviespart2_v1.util.DetailAPICall;
import com.squareup.picasso.Picasso;

/**
 * Created by waiyi on 12/10/2017.
 */

public class FavoriteMovieActivity extends AppCompatActivity {

    private static final String TAG = "FavoriteMovieActivity";
    private ImageView mMovieImageView;
    private TextView mMovieTitle;
    private TextView mUserRating;
    private TextView mYearOfRelease;
    private TextView mRuntime;
    private TextView mOverview;
    private ImageView mFavorite;
    private FavoriteMovie mFSelectedMovie;
    private RecyclerView mRVTrailer;
    private RecyclerView mRVReview;
    private static final String F_MOVIE_KEY = "FMOVIE";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        mFSelectedMovie = (FavoriteMovie) getIntent().getExtras().getSerializable(F_MOVIE_KEY);
        mMovieImageView = (ImageView) findViewById(R.id.imageViewPoster);
        Picasso.with(this).load(mFSelectedMovie.getmPosterPath()).into(mMovieImageView);

        mContext = getApplicationContext();
        mMovieTitle = (TextView) findViewById(R.id.movieTitle);
        mYearOfRelease = (TextView) findViewById(R.id.year);
        mRuntime = (TextView) findViewById(R.id.runtime);
        mUserRating = (TextView) findViewById(R.id.rating);
        mFavorite = (ImageView) findViewById(R.id.button);
        mFavorite.setVisibility(View.GONE);
//        mFavorite.setImageResource(R.drawable.icons8_heart_outline_red);
        mOverview = (TextView) findViewById(R.id.synopsis);
        mRVTrailer = (RecyclerView) findViewById(R.id.rvTrailers);
        LinearLayoutManager layoutManagerTrailer = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRVTrailer.setLayoutManager(layoutManagerTrailer);
        mRVReview = (RecyclerView) findViewById(R.id.reviews);
        LinearLayoutManager layoutManagerReview = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRVReview.setLayoutManager(layoutManagerReview);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext,
                layoutManagerReview.getOrientation());
        dividerItemDecoration.setDrawable(mContext.getResources().getDrawable(R.drawable.line_divider));
        mRVReview.addItemDecoration(dividerItemDecoration);

        mMovieTitle.setText(mFSelectedMovie.getmTitle());
        mYearOfRelease.setText(mFSelectedMovie.getmYearOfRelease());
        mUserRating.setText(mFSelectedMovie.getmVoteAvg());
        mOverview.setText(mFSelectedMovie.getmOverview());


        DetailAPICall detailAPICall = new DetailAPICall(mContext, mFSelectedMovie.getID(), mRuntime,
                mRVTrailer, mRVReview);


    }


}






