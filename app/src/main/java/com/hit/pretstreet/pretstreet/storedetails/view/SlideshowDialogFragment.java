package com.hit.pretstreet.pretstreet.storedetails.view;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.customview.touchImageView.ImageViewTouch;
import com.hit.pretstreet.pretstreet.core.utils.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 02/08/2017.
 */

public class SlideshowDialogFragment extends DialogFragment {

    public SlideshowDialogFragment() {
        // Required empty public constructor
    }
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.lbl_count) TextViewPret lbl_count;
    @BindView(R.id.iv_back) ImageView iv_back;

    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<String> imageModels;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int selectedPosition = 0;

    public static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment fragment = new SlideshowDialogFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery_slideshow, container, false);
        ButterKnife.bind(this, v);
        imageModels = getArguments().getStringArrayList(Constant.PARCEL_KEY);
        selectedPosition = getArguments().getInt(Constant.POSITION_KEY);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        setCurrentItem(selectedPosition);
        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    @OnClick(R.id.iv_back)
    public void onBackpressed() {
        getActivity().onBackPressed();
    }

    private void displayMetaInfo(int position) {
        lbl_count.setText((position + 1) + " of " + imageModels.size());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    public class MyViewPagerAdapter extends PagerAdapter {

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.gallery_fullscreen_image, container, false);
            ImageViewTouch imageViewPreview = (ImageViewTouch) view.findViewById(R.id.image_preview);
            String image = imageModels.get(position);
            Glide.with(getActivity()).load(image)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);
            /*
            imageViewPreview.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Log.d("TEST", "onDoubleTap");
                        return super.onDoubleTap(e);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });*/
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return imageModels.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
