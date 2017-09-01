package com.hit.pretstreet.pretstreet.navigation.fragments;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
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
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.navigation.adapters.ArticlePagerAdapter;
import com.hit.pretstreet.pretstreet.navigation.adapters.HomeGridAdapter;
import com.hit.pretstreet.pretstreet.navigation.adapters.HomePagerAdapter;
import com.hit.pretstreet.pretstreet.navigation.interfaces.HomeTrapeClick;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.sociallogin.TokenService;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.ButtonClickCallbackStoreList;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.MALLS;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SHOPBYMOODS;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SHOPBYPRO;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SLIDER;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRAPE;

/**
 * Created by User on 7/19/2017.
 */

public class HomeFragment extends AbstractBaseFragment<HomeActivity> implements ViewPager.OnPageChangeListener{

    @BindView(R.id.pager_banners) ViewPager pager_banner;
    @BindView(R.id.ll_main_cat) LinearLayout ll_main_cat;
    @BindView(R.id.ll_main_cat_bottom) LinearLayout ll_main_cat_bottom;
    @BindView(R.id.rv_category)RecyclerView rv_category;
    @BindView(R.id.rv_moods)RecyclerView rv_moods;
    @BindView(R.id.rl_category)RelativeLayout rl_category;
    @BindView(R.id.rl_moods)RelativeLayout rl_moods;

    HomeTrapeClick buttonClickCallback;
    private Handler handler = new Handler();
    private Runnable runnable;
    int position = 0;

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
        Utility.setGridLayoutManager(rv_category, getActivity());
        Utility.setGridLayoutManager(rv_moods, getActivity());
        String SavedMAinCaTList = PreferenceServices.getInstance().getHomeMainCatList();
        loadHomePage(SavedMAinCaTList);
    }

    @SuppressLint("InflateParams")
    private void loadHomePage(String SavedMAinCaTList){

        if (SavedMAinCaTList.length() > 1) {
            final ArrayList<HomeCatItems> list = LoginController.getHomeContent(SavedMAinCaTList);

            ll_main_cat.removeAllViews();
            ll_main_cat_bottom.removeAllViews();

            for (int i = 0; i < list.size(); i++) {
                String contentType = list.get(i).getContentTypeId();

                switch (contentType){
                    case TRAPE:
                        LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view;
                        if (i % 2 == 0) {
                            view = infl.inflate(R.layout.row_sub_cat_list1, null);
                        } else {
                            view = infl.inflate(R.layout.row_sub_cat_list2, null);
                        }
                        RelativeLayout rl_dd = (RelativeLayout) view.findViewById(R.id.rl_dd);
                        TextViewPret txt_cat_name = (TextViewPret) view.findViewById(R.id.txt_cat_name);
                        ImageView mImageView = (ImageView) view.findViewById(R.id.img_cat_image);
                        txt_cat_name.setMaxLines(1);

                        LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        if (i != 0) {
                            relativeParams.setMargins(0, (int) getActivity().getResources().getDimension(R.dimen.content_overlapmargin_hometrape), 0, 0);
                        }
                        rl_dd.setLayoutParams(relativeParams);
                        rl_dd.requestLayout();

                        final HomeCatContentData homeContentData = list.get(i).getHomeContentData();
                        txt_cat_name.setText(homeContentData.getCategoryName());
                        txt_cat_name.getBackground().setFilterBitmap(true);
                        if (homeContentData.getCategoryName().length() == 0)
                            txt_cat_name.setVisibility(View.GONE);

                        String catName = homeContentData.getCategoryName();
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) txt_cat_name.getLayoutParams();
                        if (catName.length() > 3 && catName.length() <= 6)
                            lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_standard) - 2, -3, 0);
                        else if (catName.length() > 6 && catName.length() <= 10)
                            lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_standard) + 0, -3, 0);
                        else if (catName.length() > 10 && catName.length() <= 15)
                            lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_standard) + 4, -3, 0);
                        else if (catName.length() >= 15)
                            lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_standard) + 7, -3, 0);
                        txt_cat_name.setLayoutParams(lp);

                        Bitmap mask1 = BitmapFactory.decodeResource(getResources(), R.drawable.mask_home);
                        Matrix matrix = new Matrix();
                        matrix.preScale(-1.0f, 1.0f);
                        Bitmap mask2 = Bitmap.createBitmap(mask1, 0, 0, mask1.getWidth(), mask1.getHeight(), matrix, true);
                        Bitmap mask3 = BitmapFactory.decodeResource(getResources(), R.drawable.mask_malls);
                        Bitmap mask4 = Bitmap.createBitmap(mask3, 0, 0, mask3.getWidth(), mask3.getHeight(), matrix, true);

                        final int finalI = i;
                        if (finalI == list.size() - 1) {
                            if (finalI % 2 == 0)
                                loadImage(homeContentData.getImageSource(), mImageView, mask4);
                            else
                                loadImage(homeContentData.getImageSource(), mImageView, mask3);
                        } else {
                            if (finalI % 2 == 0)
                                loadImage(homeContentData.getImageSource(), mImageView, mask1);
                            else
                                loadImage(homeContentData.getImageSource(), mImageView, mask2);
                        }
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buttonClickCallback.onTrapeClick(homeContentData,
                                        list.get(finalI).getHomeContentData().getCategoryName());
                            }
                        });
                        if (finalI == list.size() - 1) {
                            relativeParams.setMargins(0, 0, 0, 0);
                            rl_dd.setLayoutParams(relativeParams);
                            rl_dd.requestLayout();
                            ll_main_cat_bottom.addView(view);
                        } else
                            ll_main_cat.addView(view);
                        ll_main_cat.setVisibility(View.VISIBLE);
                        ll_main_cat_bottom.setVisibility(View.VISIBLE);

                        break;
                    case MALLS:
                        infl = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        if (i % 2 == 0) {
                            view = infl.inflate(R.layout.row_sub_cat_list1, null);
                        } else {
                            view = infl.inflate(R.layout.row_sub_cat_list2, null);
                        }
                        txt_cat_name = (TextViewPret) view.findViewById(R.id.txt_cat_name);
                        mImageView = (ImageView) view.findViewById(R.id.img_cat_image);
                        txt_cat_name.setMaxLines(1);

                        final HomeCatContentData homeContentData1 = list.get(i).getHomeContentData();
                        txt_cat_name.setText(homeContentData1.getCategoryName());
                        txt_cat_name.getBackground().setFilterBitmap(true);

                        catName = homeContentData1.getCategoryName();
                        lp = (RelativeLayout.LayoutParams) txt_cat_name.getLayoutParams();
                        if (catName.length() > 3 && catName.length() <= 6)
                            lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_standard) - 2, -3, 0);
                        else if (catName.length() > 6 && catName.length() <= 10)
                            lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_standard) + 0, -3, 0);
                        else if (catName.length() > 10 && catName.length() <= 15)
                            lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_standard) + 4, -3, 0);
                        else if (catName.length() >= 15)
                            lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_standard) + 7, -3, 0);
                        txt_cat_name.setLayoutParams(lp);

                        matrix = new Matrix();
                        matrix.preScale(-1.0f, 1.0f);
                        mask3 = BitmapFactory.decodeResource(getResources(), R.drawable.mask_malls);
                        mask4 = Bitmap.createBitmap(mask3, 0, 0, mask3.getWidth(), mask3.getHeight(), matrix, true);

                        final int finali = i;

                        if (finali == list.size() - 1) {
                            if (finali % 2 == 0)
                                loadImage(homeContentData1.getImageSource(), mImageView, mask4);
                            else
                                loadImage(homeContentData1.getImageSource(), mImageView, mask3);
                        }
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buttonClickCallback.onTrapeClick(homeContentData1,
                                        list.get(finali).getHomeContentData().getCategoryName());
                            }
                        });

                        ll_main_cat_bottom.addView(view);

                        ll_main_cat_bottom.setVisibility(View.VISIBLE);
                        break;
                    case SHOPBYMOODS:
                        ArrayList<HomeCatContentData> homeSubCategoriesArray = list.get(i).getHomeContentData().getHomeCatContentDatas();
                        loadGridMoods(homeSubCategoriesArray);
                        break;
                    case SHOPBYPRO:
                        homeSubCategoriesArray = list.get(i).getHomeContentData().getHomeCatContentDatas();
                        loadGrid(homeSubCategoriesArray);
                        break;
                    case SLIDER:
                        homeSubCategoriesArray = list.get(i).getHomeContentData().getHomeCatContentDatas();
                        loadHomeSample(homeSubCategoriesArray);
                        break;
                    default:
                        break;
                }
            }
        } else;
    }

    private void loadImage(String url, final ImageView imageView, final Bitmap mask){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int dwidth = displaymetrics.widthPixels;
        final int dheight = (int) ((displaymetrics.heightPixels) * 0.45);

        Glide.with(getActivity())
                .load(url).asBitmap()
                .placeholder(R.drawable.mask_home)
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
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        /*switch (getResources().getDisplayMetrics().densityDpi) {
                            case DisplayMetrics.DENSITY_MEDIUM:
                                imageView.setScaleType(ImageView.ScaleType.CENTER);
                                break;
                            case DisplayMetrics.DENSITY_HIGH:
                                imageView.setScaleType(ImageView.ScaleType.CENTER);
                                break;
                            default:
                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                break;
                        }*/
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

    private void loadGrid(ArrayList<HomeCatContentData> homeSubCategoriesArray){
        if(homeSubCategoriesArray.size()>0) {
            HomeGridAdapter homeGridAdapter = new HomeGridAdapter(getActivity(), homeSubCategoriesArray);
            rv_category.setAdapter(homeGridAdapter);
            rl_category.setVisibility(View.VISIBLE);
        }
    }

    private void loadGridMoods(ArrayList<HomeCatContentData> homeSubCategoriesArray){
        if(homeSubCategoriesArray.size()>0) {
            HomeGridAdapter homeGridAdapter = new HomeGridAdapter(getActivity(), homeSubCategoriesArray);
            rv_moods.setAdapter(homeGridAdapter);
            rl_moods.setVisibility(View.VISIBLE);
        }
    }

    private void loadHomeSample(final ArrayList<HomeCatContentData> homeSubCategoriesArray){
        if(homeSubCategoriesArray.size()>0) {
            pager_banner.setOnPageChangeListener(this);

            HomePagerAdapter mAdapter = new HomePagerAdapter(getActivity(), homeSubCategoriesArray);
            pager_banner.setAdapter(mAdapter);
            pager_banner.setVisibility(View.VISIBLE);
            ll_main_cat.bringToFront();

            runnable = new Runnable() {
                public void run() {
                    if(pager_banner.getCurrentItem() >= homeSubCategoriesArray.size()-1)
                        position = 0;
                    else
                        position = position + 1;
                    pager_banner.setCurrentItem(position, true);
                    handler.postDelayed(runnable, 3000);
                }
            };
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (handler!= null) {
                handler.removeCallbacks(runnable);
            }
        }catch (Exception e){}
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
        handler.postDelayed(runnable, 3000);
        }catch (Exception e){}
    }
}