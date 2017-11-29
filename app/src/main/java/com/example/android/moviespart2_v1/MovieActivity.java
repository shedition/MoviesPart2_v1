package com.example.android.moviespart2_v1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.android.moviespart2_v1.domain.MovieRuntime;
import com.example.android.moviespart2_v1.util.DetailAPICall;
import com.example.android.moviespart2_v1.util.RuntimeAPICall;
import com.example.android.moviespart2_v1.util.TrailerAPICall;
import com.squareup.picasso.Picasso;

import static android.R.attr.data;
import static java.lang.Runtime.getRuntime;

/**
 * Created by waiyi on 9/10/2017.
 */

public class MovieActivity extends AppCompatActivity {

    private ImageView mMovieImageView;
    private TextView mMovieTitle;
    private TextView mUserRating;
    private TextView mYearOfRelease;
    private TextView mRuntime;
    private TextView mOverview;
    private Button mFavorite;
    private Movie mSelectedMovie;
    private RecyclerView mRVTrailer;
    private RecyclerView mRVReview;
    private static final String MOVIE_KEY = "MOVIE";
    private static final String TAG = "MainActivity";
    private Context mContext;
    private int runtime;
    //private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.example.android.moviespart2_v1.R.layout.activity_movie);

        mSelectedMovie = (Movie) getIntent().getExtras().getSerializable(MOVIE_KEY);


        mMovieImageView = (ImageView) findViewById(R.id.imageViewPoster);
        Picasso.with(this).load(mSelectedMovie.getPosterImagePath()).into(mMovieImageView);

        mContext = getApplicationContext();
        mMovieTitle = (TextView) findViewById(R.id.movieTitle);


        mYearOfRelease = (TextView) findViewById(R.id.year);
        mRuntime = (TextView) findViewById(R.id.runtime);
        mUserRating = (TextView) findViewById(R.id.rating);

        mFavorite = (Button) findViewById(R.id.button);
        mFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "I like this movie", Toast.LENGTH_LONG).show();
            }
        });

        mRVTrailer = (RecyclerView) findViewById(R.id.rvTrailers);
        LinearLayoutManager layoutManagerTrailer = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRVTrailer.setLayoutManager(layoutManagerTrailer);

        mRVReview = (RecyclerView) findViewById(R.id.reviews);
        LinearLayoutManager layoutManagerReview = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRVReview.setLayoutManager(layoutManagerReview);

        mOverview = (TextView) findViewById(R.id.synopsis);


        mMovieTitle.setText(mSelectedMovie.getTitle());
        Log.d(TAG, "Title = " + mSelectedMovie.getTitle());

        mYearOfRelease.setText(mSelectedMovie.getReleaseYear());
        Log.d(TAG, "release = " + mSelectedMovie.getReleaseDate());

        //mRuntime.setText(mSelectedMovie.getRuntime());

        mUserRating.setText(mSelectedMovie.getVoteAvg() + "/10");
        Log.d(TAG, "User Rating = " + mSelectedMovie.getVoteAvg());

        mOverview.setText(mSelectedMovie.getOverview());

        DetailAPICall detailAPICall = new DetailAPICall(mContext, mSelectedMovie.getID(), mRuntime,
                mRVTrailer, mRVReview);




    }


}
