package com.example.android.moviespart2_v1;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by waiyi on 11/28/2017.
 */

public class ReviewRecyclerAdapter extends RecyclerView.Adapter<ReviewRecyclerAdapter.ReviewViewHolder> {


    public ArrayList<Review> reviewArrayList;

    public static class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView content;

        public ReviewViewHolder(View itemView){
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.tvContent);
        }

        public void bindReview(Review review){
            String aReview = review.getContent();
            content.setText(aReview);

        }

        @Override
        public void onClick(View v){
            Toast.makeText(v.getContext(), "Row clicked.", Toast.LENGTH_LONG).show();
        }
    }

    public ReviewRecyclerAdapter(ArrayList<Review> reviews){
        reviewArrayList = reviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_item_row_review, parent, false);
        return new ReviewViewHolder(inflatedView);

    }

    @Override
    public void onBindViewHolder(ReviewRecyclerAdapter.ReviewViewHolder holder, int position){
        Review review = reviewArrayList.get(position);
        holder.bindReview(review);
    }

    @Override
    public int getItemCount(){
        return reviewArrayList.size();
    }


}
