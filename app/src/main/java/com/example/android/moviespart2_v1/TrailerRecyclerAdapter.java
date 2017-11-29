package com.example.android.moviespart2_v1;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.attr.start;
import static android.media.CamcorderProfile.get;

/**
 * Created by waiyi on 11/24/2017.
 * **
 * The RecyclerView Adapter uses the TrailerViewHolder class which contains
 * the View components for UI creation and data binding.
 */


public class TrailerRecyclerAdapter extends RecyclerView.Adapter<TrailerRecyclerAdapter.TrailerViewHolder> {

    public ArrayList<Trailer> mTrailers;
    public ArrayList<String> trailerKeys;
    public int count = 1;

    public static class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        private ImageView playButton;
        private TextView trailerNum;
        private Trailer mTrailer;
        private ArrayList<Trailer> videos;

        public TrailerViewHolder(View itemView){
            super(itemView);

            playButton = (ImageView)itemView.findViewById(R.id.trailer_image);
            trailerNum = (TextView)itemView.findViewById(R.id.trailer_num);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            String key;

            key = videos.get(pos).getTrailerID();
            Toast.makeText(v.getContext(), "video key = " + key, Toast.LENGTH_LONG).show();
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + key));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/watch?v=" + key));
                v.getContext().startActivity(i);

            }
        }


        public void bindTrailer(Trailer trailer, int trailerCount, ArrayList<Trailer> trailers){
            mTrailer = trailer;
            videos = new ArrayList<>(trailers);
            playButton.setImageResource(R.drawable.playbuttonred);
            trailerNum.setText("Trailer " + trailerCount);
        }

    }

    public TrailerRecyclerAdapter(ArrayList<Trailer> trailers){
        String key;
        mTrailers = trailers;
        Log.d("TrailerRecyclerAdapter", String.valueOf(mTrailers.size()));
//        for(int i=0; i < trailers.size(); i++){
//            key = trailers.get(i).getTrailerID();
//            Log.d("TrailerRecyclerAdapter", "key=" + key);
//            trailerKeys.add(key);
//        }
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_item_row_trailer, parent, false);
        Log.d("TrailerRecyclerAdapter", " reached");
        return new TrailerViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(TrailerRecyclerAdapter.TrailerViewHolder holder, int position){
        Trailer aTrailer = mTrailers.get(position);
        //holder.bindTrailer(aTrailer, count++);
        holder.bindTrailer(aTrailer, count++, mTrailers);
    }

    @Override
    public int getItemCount(){
        return mTrailers.size();
    }


}
