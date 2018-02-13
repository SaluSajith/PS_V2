package com.hit.pretstreet.pretstreet.storedetails.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.storedetails.interfaces.ImageClickCallback;

import java.util.ArrayList;

/**
 * Created by User on 02/08/2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryHolder> {
    private Context context;
    private ArrayList<String> urls;
    private ImageClickCallback imageClickCallback;

    public GalleryAdapter(Context context,ArrayList<String> urls) {
        this.context = context;
        this.urls = urls;
        imageClickCallback = (ImageClickCallback) context;
    }

    @Override
    public GalleryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mRootView;
        mRootView = LayoutInflater.from(context).inflate(R.layout.row_galleryview, parent, false);
        return new GalleryHolder(mRootView);
    }

    @Override
    public int getItemCount() {
        return this.urls != null ? urls.size() : 0;
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final GalleryHolder holder, final int position) {

        holder.name.setText("");

        try {
            Glide.with(context)
                    .load(urls.get(position))
                    .asBitmap()
                    .placeholder(R.drawable.default_gallery)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.cloud_sad)
                    .fitCenter()
                    .into(holder.image);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageClickCallback.onClicked(position, urls);
            }
        });

    }
    public class GalleryHolder extends RecyclerView.ViewHolder{
        TextView name;
        AppCompatImageView image;

        public GalleryHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_testimonial_name);
            image = itemView.findViewById(R.id.img);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}