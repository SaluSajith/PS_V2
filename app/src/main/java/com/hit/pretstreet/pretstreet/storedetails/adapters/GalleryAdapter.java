package com.hit.pretstreet.pretstreet.storedetails.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.storedetails.interfaces.ImageClickCallback;
import com.hit.pretstreet.pretstreet.storedetails.model.ImageModel;

import java.util.ArrayList;

/**
 * Created by User on 02/08/2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryHolder> {
    private Context context;
    ArrayList<String> urls;
    ImageClickCallback imageClickCallback;
    /*private String url[] = {
            "http://farm7.staticflickr.com/6101/6853156632_6374976d38_c.jpg",
            "http://farm8.staticflickr.com/7232/6913504132_a0fce67a0e_c.jpg",
            "http://farm5.staticflickr.com/4133/5096108108_df62764fcc_b.jpg",
            "http://farm5.staticflickr.com/4074/4789681330_2e30dfcacb_b.jpg",
            "http://farm9.staticflickr.com/8208/8219397252_a04e2184b2.jpg",
            "http://farm9.staticflickr.com/8483/8218023445_02037c8fda.jpg",
            "http://farm9.staticflickr.com/8335/8144074340_38a4c622ab.jpg",
            "http://farm9.staticflickr.com/8060/8173387478_a117990661.jpg",
            "http://farm9.staticflickr.com/8056/8144042175_28c3564cd3.jpg",
            "http://farm9.staticflickr.com/8183/8088373701_c9281fc202.jpg",
            "http://farm9.staticflickr.com/8185/8081514424_270630b7a5.jpg",
            "http://farm9.staticflickr.com/8462/8005636463_0cb4ea6be2.jpg",
            "http://farm9.staticflickr.com/8306/7987149886_6535bf7055.jpg",
            "http://farm9.staticflickr.com/8444/7947923460_18ffdce3a5.jpg",
            "http://farm9.staticflickr.com/8182/7941954368_3c88ba4a28.jpg",
            "http://farm9.staticflickr.com/8304/7832284992_244762c43d.jpg",
            "http://farm9.staticflickr.com/8163/7709112696_3c7149a90a.jpg",
            "http://farm8.staticflickr.com/7127/7675112872_e92b1dbe35.jpg",
            "http://farm8.staticflickr.com/7111/7429651528_a23ebb0b8c.jpg",
            "http://farm9.staticflickr.com/8288/7525381378_aa2917fa0e.jpg",
            "http://farm6.staticflickr.com/5336/7384863678_5ef87814fe.jpg",
            "http://farm8.staticflickr.com/7102/7179457127_36e1cbaab7.jpg",
            "http://farm8.staticflickr.com/7086/7238812536_1334d78c05.jpg",
            "http://farm8.staticflickr.com/7243/7193236466_33a37765a4.jpg",
            "http://farm8.staticflickr.com/7251/7059629417_e0e96a4c46.jpg",
            "http://farm8.staticflickr.com/7084/6885444694_6272874cfc.jpg"
    };*/


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
        //return this.urls != null ? urls.size() : 0;
        return urls.size();
    }
    @Override
    public void onBindViewHolder(final GalleryHolder holder, final int position) {

        holder.name.setText("");
        Glide.with(context)
                .load(urls.get(position))
               // .load(urls.get(position))
                .asBitmap()
                .fitCenter()
                .into(holder.image);


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageClickCallback.onClicked(position, urls);
            }
        });

    }
    public class GalleryHolder extends RecyclerView.ViewHolder{
        TextView name;
        ImageView image;

        public GalleryHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_testimonial_name);
            image = (ImageView) itemView.findViewById(R.id.img);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}