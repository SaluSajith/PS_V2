package com.hit.pretstreet.pretstreet.navigation.fragments;

import android.app.Activity;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.interfaces.HomeTrapeClick;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.navigation.models.HomeContentData;
import com.hit.pretstreet.pretstreet.navigation.models.HomeSubCategory;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 7/19/2017.
 */

public class HomeFragment extends AbstractBaseFragment<WelcomeActivity> implements ViewPager.OnPageChangeListener{

    /*@BindView(R.id.pager_banners) ViewPager pager_banner;
    @BindView(R.id.img1) ImageView img1;
    @BindView(R.id.img2) ImageView img2;
    @BindView(R.id.img3) ImageView img3;*/
    @BindView(R.id.ll_main_cat) LinearLayout ll_main_cat;

    HomeTrapeClick buttonClickCallback;

    //String SavedMAinCaTList, SavedSubCatList;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            buttonClickCallback = (HomeTrapeClick) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onViewSelected");
        }
    }

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        String SavedMAinCaTList = PreferenceServices.getInstance().getHomeMainCatList();
        loadHomePage(SavedMAinCaTList);
        // loadHomeSample();

    }

    private void loadHomePage(String SavedMAinCaTList){

        if (SavedMAinCaTList.length() > 1) {
            ll_main_cat.setVisibility(View.VISIBLE);
            final ArrayList<HomeCatItems> list = new ArrayList<>();
            JSONObject response = null;
            try {

                response = new JSONObject(SavedMAinCaTList);
                JSONArray jsonArray = response.getJSONArray("Data");
                HomeCatItems homeCatItems;
                for (int i = 0; i < jsonArray.length(); i++) {
                    homeCatItems  = new HomeCatItems();
                    homeCatItems.setContentTypeId(jsonArray.getJSONObject(i).getString("ContentTypeId"));
                    homeCatItems.setContentType(jsonArray.getJSONObject(i).getString("ContentType"));
                    HomeContentData homeContentData = null;
                    JSONObject object = jsonArray.getJSONObject(i).getJSONObject("ContentData");
                    for(int j=0;j<object.length();j++){
                        homeContentData = new HomeContentData();
                        homeContentData.setCategoryId(object.getString("MainCategoryId"));
                        homeContentData.setCategoryName(object.getString("CategoryName"));
                        homeContentData.setImageSource(object.getString("ImageSource"));
                        homeContentData.setPageTypeId(object.getString("PageTypeId"));
                        HomeSubCategory homeSubCategory = null;
                        if(object.has("SubCategory")) {
                            JSONArray subcat = object.getJSONArray("SubCategory");
                            for (int k = 0; k < subcat.length(); k++) {
                                homeSubCategory = new HomeSubCategory();
                                homeSubCategory.setContentTypeId(subcat.getJSONObject(k).getString("ContentTypeId"));
                                homeSubCategory.setContentType(subcat.getJSONObject(k).getString("ContentType"));
                                HomeContentData contentData = new HomeContentData();
                                JSONObject content = subcat.getJSONObject(k).getJSONObject("ContentData");
                                contentData.setPageTypeId(content.getString("PageTypeId"));
                                contentData.setImageSource(content.getString("ImageSource"));
                                contentData.setCategoryId(content.getString("CategoryId"));
                                contentData.setCategoryName(content.getString("CategoryName"));
                                homeSubCategory.setContentData(contentData);
                            }
                            homeContentData.setHomeSubCategory(homeSubCategory);
                        }
                        else{
                            homeContentData.setHomeSubCategory(homeSubCategory);
                        }
                    }
                    homeCatItems.setHomeContentData(homeContentData);
                    list.add(homeCatItems);
                }

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            ll_main_cat.removeAllViews();
            for (int i = 0; i < list.size(); i++) {
                LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view;
                if (i % 2 == 0) {
                    view = infl.inflate(R.layout.row_sub_cat_list1, null);
                } else {
                    view = infl.inflate(R.layout.row_sub_cat_list2, null);
                }
                RelativeLayout rl_dd = (RelativeLayout) view.findViewById(R.id.rl_dd);
                TextViewPret txt_cat_name = (TextViewPret) view.findViewById(R.id.txt_cat_name);
                final ImageView mImageView = (ImageView) view.findViewById(R.id.img_cat_image);
                txt_cat_name.setMaxLines(1);

                LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                if(i!=0) {
                    relativeParams.setMargins(0, (int) getActivity().getResources().getDimension(R.dimen.content_overlapmargin), 0, 0);
                }
                rl_dd.setLayoutParams(relativeParams);
                rl_dd.requestLayout();
                HomeContentData homeContentData = list.get(i).getHomeContentData();
                txt_cat_name.setText(homeContentData.getCategoryName());
                txt_cat_name.getBackground().setFilterBitmap(true);

                Bitmap mask1 = BitmapFactory.decodeResource(getResources(), R.drawable.brand1);
                Bitmap mask2 = BitmapFactory.decodeResource(getResources(), R.drawable.brand2);
                final int finalI = i;
                if (finalI % 2 == 0) {
                    loadImage(homeContentData.getImageSource(), mImageView, mask1);
                } else{
                    loadImage(homeContentData.getImageSource(), mImageView, mask2);
                }

                final int finalI1 = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonClickCallback.onTrapeClick(list.get(finalI1));
                    }
                });
                ll_main_cat.addView(view);
            }
        } else {
            ll_main_cat.setVisibility(View.GONE);
        }
    }

    private void loadHomeSample(){
        /*pager_banner.setOnPageChangeListener(this);

        ArrayList imagearray = new ArrayList();
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/Creative-for-article-1-final.jpg");
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/Creative_for_article_2_final.jpg");
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/PAPA-DONT-PREACH-BY-SHUBHIKA.jpg");
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/devnaagri.jpg");
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/abraham_and_thakore.jpg");

        ArticlePagerAdapter mAdapter = new ArticlePagerAdapter(getActivity(), imagearray);

        Bitmap mask1 = BitmapFactory.decodeResource(getResources(), R.drawable.brand1);
        Bitmap mask2 = BitmapFactory.decodeResource(getResources(), R.drawable.brand2);

        loadImage("http://nuuneoi.com/uploads/source/playstore/cover.jpg", img1, mask1);
        pager_banner.setAdapter(mAdapter);
        img1.bringToFront();
        loadImage("http://nuuneoi.com/uploads/source/playstore/cover.jpg", img2, mask1);
        loadImage("http://nuuneoi.com/uploads/source/playstore/cover.jpg", img3, mask2);*/

    }

    private void loadImage(String url, final ImageView imageView, final Bitmap mask){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int dwidth = displaymetrics.widthPixels;
        final int dheight = (int) ((displaymetrics.heightPixels) * 0.45);

        Glide.with(getActivity())
                .load(url).asBitmap()
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

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
                /*Bitmap mask;
                mask = BitmapFactory.decodeResource(getResources(), R.drawable.brand2);
                loadImage(mResources.get(position), imageView, mask);*/
                Glide.with(mContext)
                        .load(mResources.get(position))
                        .fitCenter()
                        .into(imageView);
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