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
import android.support.v4.view.ViewPager;
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
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.adapters.ArticlePagerAdapter;
import com.hit.pretstreet.pretstreet.navigation.interfaces.HomeTrapeClick;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRAPE_CONTENT_TYPE;

/**
 * Created by User on 7/19/2017.
 */

public class HomeFragment extends AbstractBaseFragment<WelcomeActivity> implements ViewPager.OnPageChangeListener{

    @BindView(R.id.pager_banners) ViewPager pager_banner;
    @BindView(R.id.ll_main_cat) LinearLayout ll_main_cat;
    @BindView(R.id.ll_main_cat_bottom) LinearLayout ll_main_cat_bottom;

    HomeTrapeClick buttonClickCallback;

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
    }

    private void loadHomePage(String SavedMAinCaTList){

        if (SavedMAinCaTList.length() > 1) {
            final ArrayList<HomeCatItems> list = LoginController.getHomeContent(SavedMAinCaTList);

            ll_main_cat.removeAllViews();
            ll_main_cat_bottom.removeAllViews();
            for (int i = 0; i < list.size(); i++) {
                String contentType = list.get(i).getContentTypeId();
                switch (contentType){
                case TRAPE_CONTENT_TYPE:
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
                default:
                    break;
                }
            }
        } else;
        //loadHomeSample();
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

    private void loadHomeSample(){

        LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = infl.inflate(R.layout.row_home_pager, null);

        pager_banner.setOnPageChangeListener(this);

        ArrayList imagearray = new ArrayList();
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/Creative-for-article-1-final.jpg");
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/Creative_for_article_2_final.jpg");
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/PAPA-DONT-PREACH-BY-SHUBHIKA.jpg");
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/devnaagri.jpg");
        imagearray.add("http://52.77.174.143/admin/media/trendingpage/trendingpageimages/abraham_and_thakore.jpg");

        ArticlePagerAdapter mAdapter = new ArticlePagerAdapter(getActivity(), imagearray);
        pager_banner.setAdapter(mAdapter);
        ll_main_cat.addView(view);
    }

}