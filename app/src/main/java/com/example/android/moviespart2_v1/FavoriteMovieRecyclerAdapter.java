package com.example.android.moviespart2_v1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by waiyi on 12/10/2017.
 */

public class FavoriteMovieRecyclerAdapter extends RecyclerView.Adapter<FavoriteMovieRecyclerAdapter.VHolder> {

    private static final String TAG = "FAdapter";
    private ArrayList<FavoriteMovie> mFavoriteMovies;

    public static class VHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImage;
        private static final String F_MOVIE_KEY = "FMOVIE";
        private FavoriteMovie favoriteMovie;

        public VHolder(View v) {
            super(v);
            mImage = (ImageView) v.findViewById(R.id.item_image);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = itemView.getContext();
            Intent showMovieIntent = new Intent(context, FavoriteMovieActivity.class);
            showMovieIntent.putExtra(F_MOVIE_KEY, favoriteMovie);
            context.startActivity(showMovieIntent);
        }


        public void bindMovie(FavoriteMovie fMovie) {
            favoriteMovie = fMovie;
            Picasso.with(mImage.getContext()).load(fMovie.getmPosterPath()).into(mImage);
        }

    }

    public FavoriteMovieRecyclerAdapter(ArrayList<FavoriteMovie> movies) {
        mFavoriteMovies = movies;

    }

    @Override
    public FavoriteMovieRecyclerAdapter.VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_row, parent, false);
        return new VHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(FavoriteMovieRecyclerAdapter.VHolder holder, int position) {
        FavoriteMovie movie = mFavoriteMovies.get(position);
        holder.bindMovie(movie);
    }

    public void remove(int position) {
        FavoriteMovie amovie = mFavoriteMovies.get(position);
        if (mFavoriteMovies.contains(amovie)) {
            mFavoriteMovies.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return mFavoriteMovies.size();
    }


}

