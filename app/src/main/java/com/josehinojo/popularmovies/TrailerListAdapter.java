package com.josehinojo.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.josehinojo.popularmovies.DetailActivity.THUMB_URL_END;
import static com.josehinojo.popularmovies.DetailActivity.THUMB_URL_START;

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.MyViewHolder>{

    Context context;
    public List<ParcelableTrailer> trailerList;
    final private ListItemClickListener itemClickListener;
    ParcelableTrailer trailer;

    public interface ListItemClickListener{
        void onListItemClick(ParcelableTrailer trailer);
    }

    public void setContext(Context context){
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView trailerThumbnail;
        public TextView trailerTitle;
        public TextView trailerType;

        public MyViewHolder(View view) {
            super(view);
            trailerThumbnail = (ImageView) view.findViewById(R.id.trailerThumbnail);
            trailerTitle = view.findViewById(R.id.trailerTitle);
            trailerType = view.findViewById(R.id.trailerType);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            itemClickListener.onListItemClick(trailerList.get(position));
        }
    }

    public TrailerListAdapter(List<ParcelableTrailer> trailerList, ListItemClickListener listener) {
        this.trailerList = trailerList;
        itemClickListener = listener;
    }


    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {
        ParcelableTrailer trailer = trailerList.get(position);

        Picasso.get().load(THUMB_URL_START + trailer.getKey() + THUMB_URL_END).fit().placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background).into(holder.trailerThumbnail);
        holder.trailerTitle.setText(trailer.getName());
        holder.trailerType.setText(trailer.getType());
    }

    @Override
    public int getItemCount() {

        return trailerList.size();
    }

    public void update(){
        notifyDataSetChanged();
    }
}
