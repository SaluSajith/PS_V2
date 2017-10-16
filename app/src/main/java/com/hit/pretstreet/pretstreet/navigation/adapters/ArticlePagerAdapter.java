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
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;

import java.util.ArrayList;

/**
 * Created by User on 04/08/2017.
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
        glide.load(url).into(imageView);
       /* glide.load(url).asBitmap().into(new SimpleTarget() {
            @Override
            public void onResourceReady(Object resource, GlideAnimation glideAnimation) {
                int width = glideDrawable.getIntrinsicWidth();
                int height = glideDrawable.getIntrinsicHeight();
                viewHolder.image1.setImageDrawable(glideDrawable);
            }
        });*/
    }


    public void setPagerHeight(RequestManager glide, String url, ImageView view){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((HomeInnerActivity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int dwidth = displaymetrics.widthPixels;
        final int dheight = (int) ((displaymetrics.heightPixels) * 0.45);
        glide.load(url).asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(view) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        try {
                            int width = resource.getWidth();
                            int height = resource.getHeight();
                            float scaleWidth = ((float) dwidth) / width;
                            float scaleHeight = ((float) dheight) / height;
                            Matrix matrix = new Matrix();
                            if (width > height)
                                if (scaleHeight > scaleWidth)
                                    matrix.postScale(scaleWidth, scaleWidth);
                                else
                                    matrix.postScale(scaleHeight, scaleHeight);
                            else {
                                if (scaleHeight > scaleWidth)
                                    matrix.postScale(scaleHeight, scaleHeight);
                                else
                                    matrix.postScale(scaleWidth, scaleWidth);
                            }
                            Bitmap resizedBitmap = Bitmap.createBitmap(resource, 0, 0, width, height, matrix, false);

                            view.setImageBitmap(resizedBitmap);
                            view.setScaleType(ImageView.ScaleType.FIT_XY);
                        } catch (Exception e){}
                        /*int width = resource.getWidth();
                        int height = resource.getHeight();

                        float aspectRatio = width/height;
                        if(aspectRatio>=0.25 && aspectRatio <0.75){
                            ViewGroup.LayoutParams params = viewPager.getLayoutParams();
                            params.height = height;
                            viewPager.setLayoutParams(params);
                        }*//* else if(aspectRatio>=0.75 && aspectRatio <1.25){
                            ViewGroup.LayoutParams params = viewPager.getLayoutParams();
                            params.height = (int) context.getResources().getDimension(R.dimen.trending_pager_height);
                            viewPager.setLayoutParams(params);
                        } else if(aspectRatio >= 1.25){

                        } *//*else{
                            ViewGroup.LayoutParams params = viewPager.getLayoutParams();
                            params.height = (int) context.getResources().getDimension(R.dimen.trending_pager_height);
                            viewPager.setLayoutParams(params); */
                    }
                });
    }

}
