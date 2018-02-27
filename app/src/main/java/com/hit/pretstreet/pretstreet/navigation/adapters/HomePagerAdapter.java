package com.hit.pretstreet.pretstreet.navigation.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.navigation.interfaces.HomeTrapeClick;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;

import java.util.ArrayList;

/**
 * Created by User on 28/08/2017.
 */

public class HomePagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<HomeCatContentData> homeSubCategoriesArray;
    HomeTrapeClick buttonClickCallback;

    public HomePagerAdapter(Context mContext, ArrayList<HomeCatContentData> homeSubCategoriesArray) {
        this.mContext = mContext;
        buttonClickCallback = (HomeActivity)mContext;
        this.homeSubCategoriesArray = homeSubCategoriesArray;
    }

    @Override
    public int getCount() {
        return this.homeSubCategoriesArray != null ? homeSubCategoriesArray.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.image_slider_item, container, false);

        final AppCompatImageView imageView = itemView.findViewById(R.id.img_pager_item);
        if(homeSubCategoriesArray.size()==0){
            imageView.setImageResource(R.drawable.default_banner);
        }else {
            Glide.with(mContext)
                    .load(homeSubCategoriesArray.get(position).getImageSource())
                    .fitCenter()
                    .placeholder(R.drawable.default_banner)
                    .into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickCallback.onTrapeClick(homeSubCategoriesArray.get(position),
                        homeSubCategoriesArray.get(position).getCategoryName());
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
