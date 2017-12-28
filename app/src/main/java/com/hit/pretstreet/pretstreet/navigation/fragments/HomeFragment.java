package com.hit.pretstreet.pretstreet.navigation.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.navigation.adapters.HomeGridAdapter;
import com.hit.pretstreet.pretstreet.navigation.adapters.HomePagerAdapter;
import com.hit.pretstreet.pretstreet.navigation.interfaces.HomeTrapeClick;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SHOPBYMOODS;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SHOPBYPRO;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SLIDER;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TERMS_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRAPE;

/**
 * Created by User on 7/19/2017.
 */


public class HomeFragment extends AbstractBaseFragment<HomeActivity>
        implements ViewPager.OnPageChangeListener, View.OnClickListener{

    @BindView(R.id.ll_pager) LinearLayout ll_pager;
    @BindView(R.id.ll_main_cat) LinearLayout ll_main_cat;
    @BindView(R.id.ll_header_cat) LinearLayout ll_header_cat;
    @BindView(R.id.ll_header_moods) LinearLayout ll_header_moods;
    @BindView(R.id.ll_main_cat_bottom) LinearLayout ll_main_cat_bottom;
    @BindView(R.id.rv_category) RecyclerView rv_category;
    @BindView(R.id.rv_moods) RecyclerView rv_moods;
    @BindView(R.id.rl_category) RelativeLayout rl_category;
    @BindView(R.id.rl_all) RelativeLayout rl_all;
    @BindView(R.id.rl_sub) RelativeLayout rl_sub;
    @BindView(R.id.rl_moods) RelativeLayout rl_moods;
    @BindView(R.id.tv_tc) TextViewPret tv_tc;

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
        Utility.setGridLayoutManager(rv_category, getActivity(), 3);
        Utility.setGridLayoutManager(rv_moods, getActivity(), 2);
        String SavedMAinCaTList = PreferenceServices.getInstance().getHomeMainCatList();
        rl_all.setVisibility(View.INVISIBLE);
        loadHomePage(SavedMAinCaTList);

        String udata = tv_tc.getText().toString();
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        tv_tc.setText(content.toString());
        tv_tc.setOnClickListener(this);
        rl_sub.bringToFront();
    }

    @SuppressLint("InflateParams")
    private void loadHomePage(String SavedMAinCaTList) {

        if (SavedMAinCaTList.length() > 1) {
            final ArrayList<HomeCatItems> list = LoginController.getHomeContent(SavedMAinCaTList);
            ll_main_cat.removeAllViews();

            for (int i = 0; i < list.size(); i++) {
                String contentType = list.get(i).getContentTypeId();

                switch (contentType){
                    case TRAPE:
                        try {
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

                            final int finalI = i;
                            if(finalI==list.size()-1){
                                Bitmap mask1 = BitmapFactory.decodeResource(getResources(), R.drawable.mask_malls);
                                Matrix matrix = new Matrix();
                                matrix.preScale(-1.0f, 1.0f);
                                Bitmap mask2 = Bitmap.createBitmap(mask1, 0, 0, mask1.getWidth(), mask1.getHeight(), matrix, true);
                                if (finalI % 2 != 0)
                                    loadImage(homeContentData.getImageSource(), mImageView, mask1, R.drawable.mask_malls);
                                else
                                    loadImage(homeContentData.getImageSource(), mImageView, mask2, R.drawable.mask_malls);
                            }else {
                                Bitmap mask1 = BitmapFactory.decodeResource(getResources(), R.drawable.mask_home);
                                Matrix matrix = new Matrix();
                                matrix.preScale(-1.0f, 1.0f);
                                Bitmap mask2 = Bitmap.createBitmap(mask1, 0, 0, mask1.getWidth(), mask1.getHeight(), matrix, true);
                                if (finalI % 2 == 0)
                                    loadImage(homeContentData.getImageSource(), mImageView, mask1, R.drawable.mask_home);
                                else
                                    loadImage(homeContentData.getImageSource(), mImageView, mask2, R.drawable.brand2);
                            }
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    buttonClickCallback.onTrapeClick(homeContentData,
                                            list.get(finalI).getHomeContentData().getCategoryName());
                                }
                            });
                            ll_main_cat.addView(view);
                            ll_main_cat.setVisibility(View.VISIBLE);
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        } catch (OutOfMemoryError e){
                            e.printStackTrace();
                        }

                        break;
                    case SHOPBYMOODS:
                        ArrayList<HomeCatContentData> homeSubCategoriesArray = list.get(i).getHomeContentData().getHomeCatContentDatas();
                        loadGridMoods(i, homeSubCategoriesArray);
                        break;
                    case SHOPBYPRO:
                        homeSubCategoriesArray = list.get(i).getHomeContentData().getHomeCatContentDatas();
                        loadGrid(i, homeSubCategoriesArray);
                        break;
                    case SLIDER:
                        homeSubCategoriesArray = list.get(i).getHomeContentData().getHomeCatContentDatas();
                        loadPager(i, homeSubCategoriesArray);
                        break;
                    default:
                        break;
                }
            }
            rl_all.setVisibility(View.VISIBLE);
        } else;
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

    private void loadGrid(int position, ArrayList<HomeCatContentData> homeSubCategoriesArray){
        if(homeSubCategoriesArray.size()>0) {
            LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view;
            if (position % 2 == 0) {
                view = infl.inflate(R.layout.header_home_cat1, null);
            } else {
                view = infl.inflate(R.layout.header_home_cat2, null);
            }
            ll_header_cat.addView(view);
            ll_header_cat.setVisibility(View.VISIBLE);
            ll_header_cat.bringToFront();
            ImageView imageView = (ImageView) view.findViewById(R.id.img_cat_image);
            imageView.setVisibility(View.INVISIBLE);
            TextViewPret textViewPret = (TextViewPret) view.findViewById(R.id.txt_cat_name);
            textViewPret.setText("SEARCH BY CATEGORY");
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textViewPret.getLayoutParams();
            lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_large) + 3, -3, 0);
            textViewPret.setLayoutParams(lp);
            lp = new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_large) + 3, -3, 0);
            TextViewPret txt = (TextViewPret) view.findViewById(R.id.txt);
            txt.setLayoutParams(lp);

            HomeGridAdapter homeGridAdapter = new HomeGridAdapter(getActivity(), this, homeSubCategoriesArray, 0);
            rv_category.setAdapter(homeGridAdapter);
            rl_category.setVisibility(View.VISIBLE);
        }
    }

    private void loadGridMoods(int position, ArrayList<HomeCatContentData> homeSubCategoriesArray){
        if(homeSubCategoriesArray.size()>0) {
            LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view;
            if (position % 2 == 0) {
                view = infl.inflate(R.layout.header_home_cat1, null);
            } else {
                view = infl.inflate(R.layout.header_home_cat2, null);
            }
            ll_header_moods.addView(view);
            ll_header_moods.setVisibility(View.VISIBLE);
            ImageView imageView = (ImageView) view.findViewById(R.id.img_cat_image);
            imageView.setVisibility(View.INVISIBLE);
            TextViewPret textViewPret = (TextViewPret) view.findViewById(R.id.txt_cat_name);
            textViewPret.setText("SEARCH BY MOODS");
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textViewPret.getLayoutParams();
            lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_large) + 3, -3, 0);
            textViewPret.setLayoutParams(lp);
            lp = new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            lp.setMargins(-3, (int) getResources().getDimension(R.dimen.padding_large) + 3, -3, 0);
            TextViewPret txt = (TextViewPret) view.findViewById(R.id.txt);
            txt.setLayoutParams(lp);
            txt.setBackgroundColor(getResources().getColor(R.color.moods_bg));

            HomeGridAdapter homeGridAdapter = new HomeGridAdapter(getActivity(),this, homeSubCategoriesArray, 1);
            rv_moods.setAdapter(homeGridAdapter);
            rl_moods.setVisibility(View.VISIBLE);
        }
    }

    private void loadPager(int position1, final ArrayList<HomeCatContentData> homeSubCategoriesArray){
        if(homeSubCategoriesArray.size()>0) {
            LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view;
            if (position1 % 2 == 0) {
                view = infl.inflate(R.layout.content_homepager1, null);
            } else {
                view = infl.inflate(R.layout.content_homepager2, null);
            }

            final ViewPager pager_banner = (ViewPager) view.findViewById(R.id.pager_banners);
            ll_pager.addView(view);
            ll_pager.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), NavigationItemsActivity.class);
        intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
        intent.putExtra("fragment", TERMS_FRAGMENT);
        startActivity(intent);
    }
}