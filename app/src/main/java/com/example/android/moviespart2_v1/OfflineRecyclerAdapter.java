package com.example.android.moviespart2_v1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by waiyi on 12/17/2017.
 */

public class OfflineRecyclerAdapter extends RecyclerView.Adapter<OfflineRecyclerAdapter.Holder>{

    private static final String TAG = "ORecyclerAdapter";
    private ArrayList<OfflineFavMovieDetails> olDataList;

    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mImage;
        private OfflineFavMovieDetails movieData;
        private static final String O_MOVIE_KEY = "OMOVIE";

        public Holder(View v){
            super(v);
            mImage = (ImageView) v.findViewById(R.id.item_image);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            Context context = itemView.getContext();
            Intent showMovieIntent = new Intent(context, OfflineActivity.class);
            showMovieIntent.putExtra(O_MOVIE_KEY, movieData);
//            showMovieIntent.putExtra(O_MOVIE_KEY, movieData);
            context.startActivity(showMovieIntent);
        }

        public void bindMovie(OfflineFavMovieDetails olMovie){
            movieData = olMovie;
        //    Picasso.with(mImage.getContext()).load(olMovie.getImage()).into(mImage);
            mImage.setImageBitmap(movieData.getImage());
        }

    }

    public OfflineRecyclerAdapter(ArrayList<OfflineFavMovieDetails> movieList){
        olDataList = movieList;
    }

    @Override
    public OfflineRecyclerAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType){
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_row, parent, false);
        return new Holder(inflatedView);
    }

    @Override
    public void onBindViewHolder(OfflineRecyclerAdapter.Holder holder, int position){
        OfflineFavMovieDetails aMovie = olDataList.get(position);
        holder.bindMovie(aMovie);
    }

    public void remove(int position){
        OfflineFavMovieDetails aMovie = olDataList.get(position);
        if(olDataList.contains(aMovie)){
            olDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount(){
        return olDataList.size();
    }

}
