package com.hit.pretstreet.pretstreet.navigation.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 7/19/2017.
 */

public class HomeFragment extends AbstractBaseFragment<WelcomeActivity> implements ViewPager.OnPageChangeListener{

    @BindView(R.id.pager_banners) ViewPager pager_banner;

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        pager_banner.setOnPageChangeListener(this);

        ArrayList imagearray = new ArrayList();
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/Creative-for-article-1-final.jpg");
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/Creative_for_article_2_final.jpg");
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/PAPA-DONT-PREACH-BY-SHUBHIKA.jpg");
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/devnaagri.jpg");
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/abraham_and_thakore.jpg");

        ArticlePagerAdapter mAdapter = new ArticlePagerAdapter(getActivity(), imagearray);
        pager_banner.setAdapter(mAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /*@OnClick(R.id.btn_submit)
    public void onSubmitPressed() {
        getActivity().finish();
    }*/




    private class ArticlePagerAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<String> mResources;
        private ArrayList<ProductImageItem > mImagearray;

        public ArticlePagerAdapter(Context mContext, ArrayList<String> mResources) {
            this.mContext = mContext;
            this.mResources = mResources;
            mImagearray = new ArrayList<>();

            ProductImageItem productImageItem;
            for(int i = 0;i<mResources.size();i++) {
                productImageItem = new ProductImageItem();
                productImageItem.setImage(mResources.get(i));
                mImagearray.add(productImageItem);
            }
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
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                final int dwidth = displaymetrics.widthPixels;
                final int dheight = (int) ((displaymetrics.heightPixels) * 0.45);

                Glide.with(getActivity())
                        //.load("http://nuuneoi.com/uploads/source/playstore/cover.jpg").asBitmap()
                        .load((mResources.get(position))).asBitmap()
                        .centerCrop()
                        .into(new BitmapImageViewTarget(imageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
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
                                Bitmap mask;
                                mask = BitmapFactory.decodeResource(getResources(), R.drawable.brand1);

                                Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
                                Canvas mCanvas = new Canvas(result);
                                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                                //mCanvas.drawBitmap(resource, 0, 0, null);
                                mCanvas.drawBitmap(resizedBitmap, 0, 0, null);
                                mCanvas.drawBitmap(mask, 0, 0, paint);
                                paint.setXfermode(null);
                                imageView.setImageBitmap(result);
                                switch (getResources().getDisplayMetrics().densityDpi) {
                                    case DisplayMetrics.DENSITY_MEDIUM:
                                        imageView.setScaleType(ImageView.ScaleType.CENTER);
                                        break;
                                    case DisplayMetrics.DENSITY_HIGH:
                                        imageView.setScaleType(ImageView.ScaleType.CENTER);
                                        break;
                                    default:
                                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                        break;
                                }

                            }
                        });

            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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


    public static class ProductImageItem implements Parcelable {
        String image;

        public ProductImageItem() {
        }
        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        protected ProductImageItem(Parcel in) {
            image = in.readString();
        }

        public final Creator<ProductImageItem> CREATOR = new Creator<ProductImageItem>() {
            @Override
            public ProductImageItem createFromParcel(Parcel in) {
                return new ProductImageItem(in);
            }

            @Override
            public ProductImageItem[] newArray(int size) {
                return new ProductImageItem[size];
            }
        };


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(image);
        }


    }
}