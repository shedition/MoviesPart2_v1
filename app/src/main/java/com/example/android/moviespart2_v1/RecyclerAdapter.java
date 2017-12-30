package com.example.android.moviespart2_v1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by waiyi on 9/11/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MovieHolder> {

    private ArrayList<Movie> mMovies;

    public static class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mItemImage;
        private static final String MOVIE_KEY = "MOVIE";
        private static final String SORTTYPE = "SORT_TYPE";
        private SharedPreferences pref;
        private SharedPreferences.Editor editor;
        private Context context;
        private String sortType;
        private int itemPos;
        private Movie mMovie;


        public MovieHolder (View v){
            super(v);

            mItemImage = (ImageView) v.findViewById(com.example.android.moviespart2_v1.R.id.item_image);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            context = itemView.getContext();
            findSortType();
            storeItemPos();
            Intent showMovieIntent = new Intent(context, MovieActivity.class);
            showMovieIntent.putExtra(MOVIE_KEY, mMovie);
            showMovieIntent.putExtra(SORTTYPE, sortType);
            context.startActivity(showMovieIntent);
        }

        public void bindMovie (Movie movie, int pos){
            mMovie = movie;
            itemPos = pos;
            Picasso.with(mItemImage.getContext()).load(movie.getPosterImagePath()).into(mItemImage);
            Log.v("bindMovie", "after Picasso");
        }

        public void findSortType(){
            pref = context.getApplicationContext().getSharedPreferences("MenuOptions", Context.MODE_APPEND);
            sortType = pref.getString("menu", "");
        }

        public void storeItemPos(){
            editor = pref.edit();
            editor.putInt("itemPosition", itemPos);
            editor.commit();
        }

    }

    public RecyclerAdapter(ArrayList<Movie> movies){
        mMovies = movies;

    }

    @Override
    public RecyclerAdapter.MovieHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(com.example.android.moviespart2_v1.R.layout.recyclerview_item_row, parent, false);
        return new MovieHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.MovieHolder holder, int position){

        Movie itemMovie = mMovies.get(position);
        holder.bindMovie(itemMovie, position);
    }

    @Override
    public int getItemCount(){
        return mMovies.size();
    }


}
