package com.hit.pretstreet.pretstreet.subcategory_n_storelist.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.StoreListingActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.SubCatTrapeClick;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 7/25/2017.
 */

public class SubCatFragment extends AbstractBaseFragment<WelcomeActivity> {

    @BindView(R.id.ll_main_cat) LinearLayout ll_main_cat;

    SubCatTrapeClick onTrapeClick;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onTrapeClick = (SubCatTrapeClick) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onViewSelected");
        }
    }

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subcat, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {

        HomeCatContentData catContentData = (HomeCatContentData) getActivity().getIntent()
                .getExtras().getSerializable("mHomeCatItems");
        loadSubCatPage(catContentData);

    }

    private void loadSubCatPage(final HomeCatContentData catContentData) {

        ll_main_cat.setVisibility(View.VISIBLE);
        ll_main_cat.removeAllViews();
        ArrayList<HomeCatItems> homeSubCategories = catContentData.getHomeSubCategoryArrayList();
        for (int i = 0; i < homeSubCategories.size(); i++) {

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
            if (i != 0) {
                relativeParams.setMargins(0, (int) getActivity().getResources().getDimension(R.dimen.content_overlapmargin), 0, 0);
            }
            rl_dd.setLayoutParams(relativeParams);
            rl_dd.requestLayout();

            final HomeCatContentData contentData = homeSubCategories.get(i).getHomeContentData();
            txt_cat_name.setText(contentData.getCategoryName());
            txt_cat_name.getBackground().setFilterBitmap(true);

            Bitmap mask1 = BitmapFactory.decodeResource(getResources(), R.drawable.brand1);
            Matrix matrix = new Matrix();
            matrix.preScale(-1.0f, 1.0f);
            Bitmap mask2 = Bitmap.createBitmap(mask1, 0, 0, mask1.getWidth(), mask1.getHeight(), matrix, true);

            final int finalI = i;
            if (finalI % 2 == 0) {
                loadImage(contentData.getImageSource(), mImageView, mask1);
            } else {
                loadImage(contentData.getImageSource(), mImageView, mask2);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTrapeClick.onSubTrapeClick(catContentData, contentData.getCategoryName());
                }
            });
            ll_main_cat.addView(view);
        }

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

}