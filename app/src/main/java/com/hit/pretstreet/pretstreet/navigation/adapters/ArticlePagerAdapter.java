package com.hit.pretstreet.pretstreet.navigation.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;

import java.util.ArrayList;

/**
 * Created by User on 04/08/2017.
 * Image Sliders - ViewPagers
 * Used inside Trending listing n Giveaway listing
 */

public class ArticlePagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String> mResources;
    private final RequestManager glide;

    public ArticlePagerAdapter(final RequestManager glide, Context mContext, ArrayList<String> mResources) {
        this.mContext = mContext;
        this.mResources = mResources;
        this.glide = glide;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.image_slider_item, container, false);

        final ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
        //double checking for image array
        if((mResources.get(position)).length()==0){
            imageView.setImageResource(R.drawable.default_banner);
        } else {
            loadImage(glide, mResources.get(position), imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeInnerActivity)mContext).onClicked(position, mResources);
            }
        });
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    void loadImage(RequestManager glide, String url, final ImageView imageView) {
        glide.load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
    }

}
