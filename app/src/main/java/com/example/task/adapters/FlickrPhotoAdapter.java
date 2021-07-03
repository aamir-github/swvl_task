package com.example.task.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.task.R;

import java.util.ArrayList;
import java.util.List;

import com.example.task.model.flickr.Photo;

public class FlickrPhotoAdapter extends RecyclerView.Adapter<FlickrPhotoAdapter.MyViewHolder> {
    
    private Context context;
    private List<Photo> photoList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView flickrPhoto;

        public MyViewHolder(View view) {
            super(view);
            flickrPhoto = view.findViewById(R.id.flickr_photo);
        }
    }


    public FlickrPhotoAdapter(Context context, List<Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @Override
    public FlickrPhotoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flickr_row_item, parent, false);

        return new FlickrPhotoAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FlickrPhotoAdapter.MyViewHolder holder, final int position) {
        Photo photo = photoList.get(position);

        int farm = (Integer) photo.getFarm();
        String server = photo.getServer();
        String id = photo.getId();
        String secret = photo.getSecret();
        String photoUrl = "http://farm"+farm+".static.flickr.com/"+server+"/"+id+"_"+secret+".jpg";

        Glide.with(context)
                .load(photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.flickrPhoto);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }
}
