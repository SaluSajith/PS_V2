package com.hit.pretstreet.pretstreet.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;

import java.util.ArrayList;

/**
 * Created by User on 5/9/2017.
 */

public class ViewPagerAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<String> mResources;

        public ViewPagerAdapter(Context mContext, ArrayList<String> mResources) {
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
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.image_slider_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
            //Log.d("mResources", String.valueOf(mResources.get(position).length()));
            if((mResources.get(position)).length()==0){
                imageView.setImageResource(R.mipmap.ic_launcher);
            }else {
                Glide.with(mContext)
                        .load(mResources.get(position))
                        .centerCrop().into(imageView);
                //Log.d("mResources", mResources.get(position));
            }
            //imageView.setImageResource(mResources[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
}
