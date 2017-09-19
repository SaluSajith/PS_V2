package com.hit.pretstreet.pretstreet.navigation.adapters;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.navigation.models.ProductImageItem;

import java.util.ArrayList;

/**
 * Created by User on 04/08/2017.
 */

public class ArticlePagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String> mResources;
    //private ArrayList<ProductImageItem> mImagearray;

    public ArticlePagerAdapter(Context mContext, ArrayList<String> mResources) {
        this.mContext = mContext;
        this.mResources = mResources;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.image_slider_item, container, false);

        final ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
        if((mResources.get(position)).length()==0){
            imageView.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext)
                    .load(mResources.get(position))
                    .placeholder(R.drawable.default_banner)
                    .fitCenter()
                    .into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> mImagearray = new ArrayList<>();
                mImagearray.add(mResources.get(position));
                ((HomeInnerActivity)mContext).onClicked(0, mImagearray);
            }
        });
        container.addView(itemView);

        return itemView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
