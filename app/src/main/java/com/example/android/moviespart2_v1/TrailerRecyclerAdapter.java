package com.example.android.moviespart2_v1;

import android.content.ActivityNotFoundException;
import android.content.Intent;
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
    private static final String TAG = "TrailerRecyclerAdapter";

    public static class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private ImageView playButton;
        private TextView trailerNum;
        private ImageView shareVideo;
        private Trailer mTrailer;
        private ArrayList<Trailer> videos;
        public int vHPosition;

        public TrailerViewHolder(View itemView) {
            super(itemView);

            playButton = (ImageView) itemView.findViewById(R.id.trailer_image);
            trailerNum = (TextView) itemView.findViewById(R.id.trailer_num);
            shareVideo = (ImageView) itemView.findViewById(R.id.shareImageView);
            Log.d(TAG, "itemview = " + itemView.getId());
            playButton.setOnClickListener(this);
            shareVideo.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            int id = v.getId();
            String key = videos.get(pos).getTrailerID();

            if (v == playButton) {
                Log.d(TAG, "You clicked trailer");
                key = videos.get(pos).getTrailerID();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + key));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/watch?v=" + key));
                    v.getContext().startActivity(i);

                }
            } else if (v == shareVideo) {
                Log.d(TAG, "You clicked share");
                String pageUrl = "https://youtube.com/watch?v=" + key;
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, pageUrl);

                try {
                    v.getContext().startActivity(Intent.createChooser(intent, "Share:"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(v.getContext(), "Sorry. Unable to share at this time.", Toast.LENGTH_LONG).show();
                }

            } else {
                Log.d(TAG, "no match");
            }
    }




    public void bindTrailer(Trailer trailer, int trailerCount, ArrayList<Trailer> trailers, int pos) {
        mTrailer = trailer;
        videos = new ArrayList<>(trailers);
        vHPosition = pos;
        playButton.setImageResource(R.drawable.playbuttonred);
        trailerNum.setText("Trailer " + trailerCount);
        if (trailerCount == 1) {
            shareVideo.setVisibility(View.VISIBLE);
        }
    }

}

    public TrailerRecyclerAdapter(ArrayList<Trailer> trailers) {
        String key;
        mTrailers = trailers;
        Log.d("TrailerRecyclerAdapter", String.valueOf(mTrailers.size()));
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_item_row_trailer, parent, false);
        return new TrailerViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(TrailerRecyclerAdapter.TrailerViewHolder holder, int position) {
        Trailer aTrailer = mTrailers.get(position);
        holder.bindTrailer(aTrailer, count++, mTrailers, position);
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.recyclerview_item_row_trailer;
    }


}
