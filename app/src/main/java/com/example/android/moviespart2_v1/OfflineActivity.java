package com.example.android.moviespart2_v1;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by waiyi on 12/17/2017.
 */

public class OfflineActivity extends AppCompatActivity{
    private static final String TAG = "OfflineActivity";

    private ImageView oMovieImageView;
    private TextView oMovieTitle;
    private TextView oUserRating;
    private TextView oYearOfRelease;
    private TextView oRuntime;
    private TextView oOverview;
    private ImageView oFavorite;
    private OfflineFavMovieDetails oFSelectedMovie;
    private RecyclerView mRVTrailer;
    private RecyclerView mRVReview;
    private static final String O_MOVIE_KEY = "OMOVIE";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        oFSelectedMovie = (OfflineFavMovieDetails) getIntent().getExtras().getSerializable(O_MOVIE_KEY);
        oMovieImageView = (ImageView) findViewById(R.id.imageViewPoster);
                mContext = getApplicationContext();
        oMovieTitle = (TextView) findViewById(R.id.movieTitle);
        oYearOfRelease = (TextView) findViewById(R.id.year);
        oRuntime = (TextView) findViewById(R.id.runtime);
        oUserRating = (TextView) findViewById(R.id.rating);
        oFavorite = (ImageView) findViewById(R.id.button);
        oFavorite.setVisibility(View.GONE);
        oOverview = (TextView) findViewById(R.id.synopsis);
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

        oMovieTitle.setText(oFSelectedMovie.getTitle());
        oYearOfRelease.setText(oFSelectedMovie.getReleaseYear());
        oUserRating.setText(oFSelectedMovie.getRating());
        oOverview.setText(oFSelectedMovie.getOverview());
        oMovieImageView.setImageBitmap(oFSelectedMovie.getImage());

    }
}
