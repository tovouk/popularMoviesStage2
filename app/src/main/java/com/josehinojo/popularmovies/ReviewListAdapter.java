package com.josehinojo.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.MyViewHolder> {

    Context context;
    public List<ParcelableReview> reviewList;
    final private ListItemClickListener itemClickListener;
    ParcelableReview review;

    public interface ListItemClickListener{
        void onListItemClick(ParcelableReview review);
    }

    public void setContext(Context context){
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView reviewAuthor;
        public TextView review;

        public MyViewHolder(View view) {
            super(view);
            reviewAuthor = view.findViewById(R.id.author);
            review = view.findViewById(R.id.review);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            itemClickListener.onListItemClick(reviewList.get(position));
        }
    }

    public ReviewListAdapter(List<ParcelableReview> reviewList, ListItemClickListener listener) {
        this.reviewList = reviewList;
        itemClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ParcelableReview review = reviewList.get(position);
        holder.reviewAuthor.setText("- " + review.getAuthor());
        holder.review.setText(review.getReview());

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}
