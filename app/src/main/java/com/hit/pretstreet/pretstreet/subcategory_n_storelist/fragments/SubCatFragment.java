package com.hit.pretstreet.pretstreet.subcategory_n_storelist.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Handler;
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
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.SubCatActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.SubCatTrapeClick;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;

/**
 * Created by User on 7/25/2017.
 */

public class SubCatFragment extends AbstractBaseFragment<WelcomeActivity> implements SubCatTrapeClick{

    private static final int TERMS_FRAGMENT = 8;
    @BindView(R.id.ll_sub) LinearLayout ll_sub;
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
        String mCatId = "";
        if (getActivity().getIntent() != null) {
            if (getActivity().getIntent().getExtras() != null && getActivity().getIntent().getExtras()
                    .containsKey("mSubCatId")) {
                mCatId = getActivity().getIntent().getStringExtra("mSubCatId");
            }
        }
        ((SubCatActivity)getActivity()).getSubCAtPage(mCatId);
    }

    @SuppressLint("InflateParams")
    private void loadSubCatPage(final ArrayList<HomeCatItems> homeCatItemses) {

        try {
            showProgressDialog(getResources().getString(R.string.loading));
            ll_main_cat.removeAllViews();
            ArrayList<HomeCatItems> homeSubCategories = homeCatItemses;
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
                if(i!=0) {
                    relativeParams.setMargins(0, (int) getActivity().getResources().getDimension(R.dimen.content_overlapmargin_hometrape), 0, 0);
                }
                rl_dd.setLayoutParams(relativeParams);
                rl_dd.requestLayout();

                final HomeCatContentData contentData = homeSubCategories.get(i).getHomeContentData();
                txt_cat_name.setText(contentData.getCategoryName());
                txt_cat_name.getBackground().setFilterBitmap(true);

                String catName = contentData.getCategoryName();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) txt_cat_name.getLayoutParams();
                if(catName.length()>3&&catName.length()<=6)
                    lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_standard) - 2, -3, 0);
                else if(catName.length()>6&&catName.length()<=10)
                    lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_standard) + 0, -3, 0);
                else if(catName.length()>10&&catName.length()<14)
                    lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_subcat_s) + 4, -3, 0);
                else if(catName.length()>=14)
                    lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_subcat) + 7, -3, 0);
                txt_cat_name.setLayoutParams(lp);

                Bitmap mask1 = BitmapFactory.decodeResource(getResources(), R.drawable.mask_home);
                Matrix matrix = new Matrix();
                matrix.preScale(-1.0f, 1.0f);
                Bitmap mask2 = Bitmap.createBitmap(mask1, 0, 0, mask1.getWidth(), mask1.getHeight(), matrix, true);
                Bitmap mask3 = BitmapFactory.decodeResource(getResources(), R.drawable.mask_malls);
                Bitmap mask4= Bitmap.createBitmap(mask3, 0, 0, mask3.getWidth(), mask3.getHeight(), matrix, true);

                final int finalI = i;
                if(finalI == homeSubCategories.size()-1){
                    if (finalI % 2 == 0) loadImage(contentData.getImageSource(), mImageView, mask4, R.drawable.mask_malls);
                    else loadImage(contentData.getImageSource(), mImageView, mask3, R.drawable.mask_malls);
                }
                else {
                    if (finalI % 2 == 0) loadImage(contentData.getImageSource(), mImageView, mask1, R.drawable.mask_home);
                    else loadImage(contentData.getImageSource(), mImageView, mask2, R.drawable.brand2);
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onTrapeClick.onSubTrapeClick(homeCatItemses, contentData.getCategoryName());
                    }
                });
                ll_main_cat.addView(view);
            }
            ll_sub.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ll_main_cat.setVisibility(View.VISIBLE);
                    destroyDialog();
                }
            }, 500);

        } catch (Resources.NotFoundException e) {
            destroyDialog();
            e.printStackTrace();
        } catch (OutOfMemoryError e){
            destroyDialog();
            e.printStackTrace();
        }
    }

    @Override
    public void onSubTrapeClick(ArrayList<HomeCatItems> homeCatItemses, String title) {
        loadSubCatPage(homeCatItemses);
    }

    @OnClick(R.id.tv_tc)
    public void onTCPressed() {
        Intent intent = new Intent(getActivity(), NavigationItemsActivity.class);
        intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
        intent.putExtra("fragment", TERMS_FRAGMENT);
        startActivity(intent);
    }

}