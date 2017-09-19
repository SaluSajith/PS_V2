package com.hit.pretstreet.pretstreet.navigation;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.EmptyFragment;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.controllers.HomeFragmentController;
import com.hit.pretstreet.pretstreet.navigation.fragments.ExhibitionFragment;
import com.hit.pretstreet.pretstreet.navigation.fragments.TrendingFragment;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingCallback;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingHolderInvoke;
import com.hit.pretstreet.pretstreet.navigation.interfaces.ZoomedViewListener;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.search.MultistoreActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.*;

public class HomeInnerActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, TrendingHolderInvoke, ZoomedViewListener {

    private int currentFragment = 0;
    private static final int TRENDING_FRAGMENT = 10;
    private static final int EXHIBITION_FRAGMENT = 11;
    private static final int TRENDINGARTICLE_FRAGMENT = 12;

    JsonRequestController jsonRequestController;
    HomeFragmentController homeFragmentController;
    TrendingCallback trendingCallback;
    TrendingFragment trendingFragment;
    ExhibitionFragment exhibitionFragment;
    Toolbar toolbar;

    @BindView(R.id.content) FrameLayout fl_content;
    @BindView(R.id.iv_header)ImageView iv_header;
    @BindView(R.id.iv_filter)ImageView iv_filter;
    @BindView(R.id.tv_cat_name) TextViewPret tv_cat_name;
    @BindView(R.id.nsv_header)NestedScrollView nsv_header;

    int pageCount = 1;
    boolean requestCalled = false;
    boolean loadmore = true, first = true;
    private static String catTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_inner);
        init();
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
        homeFragmentController = new HomeFragmentController(this);
    }

    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView iv_menu = (ImageView) toolbar.findViewById(R.id.iv_back);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ImageView iv_search = (ImageView) toolbar.findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);
        fl_content.bringToFront();
        refreshListviewOnScrolling();

        Intent intent = getIntent();
        int fragmentId = intent.getIntExtra("fragment", 0);
        setupFragment(fragmentId, false);
    }

    @OnClick(R.id.iv_filter)
    public void filterResult(){
        showSortScreem();
    }

    private void refreshListviewOnScrolling(){
        nsv_header.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if (scrollY > oldScrollY) {
                        if (scrollY == ((v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()))) {
                            if(!requestCalled){
                                requestCalled = true;
                                first = false;
                                if(loadmore) {
                                    pageCount++;
                                    if(currentFragment == TRENDING_FRAGMENT) {
                                        getTrendinglist(pageCount);
                                        trendingFragment.update_loadmore_adapter(true);
                                    } else {
                                        exhibitionFragment.update_loadmore_adapter(true);
                                        getExhibitionlist(pageCount);
                                    }
                                }
                            }
                            if(!loadmore)
                                displaySnackBar("No more data available!");
                        }
                    }
                }
            }
        });
    }

    private void setupFragment(int fragmentId, boolean b){
        switch (fragmentId){
            case TRENDING_FRAGMENT:
                pageCount = 1;
                currentFragment = TRENDING_FRAGMENT;
                tv_cat_name.setText("Trending");
                iv_filter.setVisibility(View.GONE);
                iv_header.setImageResource(R.drawable.header_yellow);
                trendingFragment = new TrendingFragment();
                trendingCallback = trendingFragment;
                changeFragment(trendingFragment, b);
                break;
            case EXHIBITION_FRAGMENT:
                pageCount = 1;
                currentFragment = EXHIBITION_FRAGMENT;
                tv_cat_name.setText("Exhibition");
                iv_filter.setVisibility(View.GONE);
                iv_header.setImageResource(R.drawable.header_yellow);
                exhibitionFragment = new ExhibitionFragment();
                trendingCallback = exhibitionFragment;
                changeFragment(exhibitionFragment, b);
                break;
            case TRENDINGARTICLE_FRAGMENT:
                pageCount = 0;
                currentFragment = TRENDINGARTICLE_FRAGMENT;
                iv_filter.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public void getTrendinglist(int offset){
        JSONObject resultJson = homeFragmentController.getTrendinglistJson(offset, getIntent().getStringExtra(Constant.PRE_PAGE_KEY));
        if(first)
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, TRENDING_URL);
    }

    public void getExhibitionlist(int offset){
        JSONObject resultJson = homeFragmentController.getExhibitionlistJson(offset, getIntent().getStringExtra(Constant.PRE_PAGE_KEY));
        if(first)
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, EXHIBITION_URL);
    }

    private void changeFragment(Fragment fragment, boolean addBackstack) {
        FragmentManager fm = getSupportFragmentManager();       /*Removing stack*/
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
        fl_content.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); /* Fragment transition*/
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.content, fragment);
        if (addBackstack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    private void handleResponse(JSONObject response){
        try {
            String url = response.getString("URL");
            //displaySnackBar(response.getString("CustomerMessage"));
            switch (url){
                case TRENDING_URL:
                    first = true;
                    requestCalled = false;
                    trendingFragment.update_loadmore_adapter(false);
                    ArrayList<TrendingItems> trendingItemses = homeFragmentController.getTrendingList(response);
                    trendingCallback.bindData(trendingItemses);
                    break;
                case EXHIBITION_URL:
                    first = true;
                    requestCalled = false;
                    exhibitionFragment.update_loadmore_adapter(false);
                    ArrayList<TrendingItems> exHItemses = homeFragmentController.getExhibitionList(response);
                    trendingCallback.bindData(exHItemses);
                    break;
                case TRENDINGLIKE_URL:
                    JSONObject object = response.getJSONObject("Data");
                    trendingFragment.updateLikeStatus(object.getInt("LikeStatus"),
                            object.getString("Id"));
                    break;
                case EXHIBITIONLIKE_URL:
                    object = response.getJSONObject("Data");
                    exhibitionFragment.updateLikeStatus(object.getInt("LikeStatus"),
                            object.getString("Id"));
                    break;
                case EXHIBITIONREGISTER_URL:
                    object = response.getJSONObject("Data");
                    displaySnackBar(response.getString("CustomerMessage"));
                    exhibitionFragment.updateLikeStatus(object.getInt("LikeStatus"),
                            object.getString("Id"));
                default: break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        this.hideDialog();
        handleResponse(response);
    }

    @Override
    public void onError(String error) {
        this.hideDialog();
        EmptyFragment emptyFragment = new EmptyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("error", error);
        emptyFragment.setArguments(bundle);
        changeFragment(emptyFragment, false);
    }

    @Override
    public void loadStoreDetails(int position, StoreListModel storeListModel) {
        Intent intent = new Intent(HomeInnerActivity.this, StoreDetailsActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, storeListModel);
        intent.putExtra(Constant.PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
        startActivity(intent);
    }

    @Override
    public void shareurl(String text) {
        this.shareUrl(text);
    }

    @Override
    public void likeInvoke(int trendingId, int pageId) {
        JSONObject resultJson;
        this.showProgressDialog(getResources().getString(R.string.loading));
        if(pageId == TRENDING_FRAGMENT) {
            resultJson = homeFragmentController.getTrendinglikeJson(trendingId + "", getIntent().getStringExtra(PRE_PAGE_KEY));
            jsonRequestController.sendRequest(this, resultJson, TRENDINGLIKE_URL);
        } else {
            resultJson = homeFragmentController.getExhibitionlikeJson(EXUNLIKELINK, trendingId + "", getIntent().getStringExtra(PRE_PAGE_KEY));
            jsonRequestController.sendRequest(this, resultJson, EXHIBITIONLIKE_URL);
        }
    }

    @Override
    public void registerInvoke(int Id) {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(getApplicationContext());
        LoginSession loginSession = sharedPreferencesHelper.getUserDetails();
        String phone = loginSession.getMobile();
        if(phone.trim().length()==0||phone.equalsIgnoreCase("null")){
            showRegisterPopup(Id);
        }
        else {
            HomeInnerActivity.this.showProgressDialog(getResources().getString(R.string.loading));
            JSONObject resultJson = homeFragmentController.getExhibitionRegisterJson(EXNOTGOINGLINK,
                    Id + "", getIntent().getStringExtra(PRE_PAGE_KEY), phone);
            jsonRequestController.sendRequest(this, resultJson, EXHIBITIONREGISTER_URL);
        }
    }

    public void showRegisterPopup(final int id) {

        final Dialog popupDialog = new Dialog(HomeInnerActivity.this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.popup_register, null);

        popupDialog.setCanceledOnTouchOutside(true);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        popupDialog.setContentView(view);
        popupDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) popupDialog.getWindow().getAttributes();
        popupDialog.getWindow().setAttributes(params);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();

        final EdittextPret edt_phone = (EdittextPret) view.findViewById(R.id.edt_phone);
        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });

        ButtonPret btn_send = (ButtonPret) view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regexStr = "^[789]\\d{9}$";
                String number = edt_phone.getText().toString();
                if(number.length()<10 || number.length()>13 || number.matches(regexStr)==false  ) {
                    edt_phone.setError("Invalid phone number!");
                }
                else{
                    popupDialog.dismiss();
                    HomeInnerActivity.this.showProgressDialog(getResources().getString(R.string.loading));
                    JSONObject resultJson = homeFragmentController.getExhibitionRegisterJson(EXNOTGOINGLINK,
                            id + "", getIntent().getStringExtra(PRE_PAGE_KEY), number);
                    jsonRequestController.sendRequest(HomeInnerActivity.this, resultJson, EXHIBITIONREGISTER_URL);
                }
            }
        });
    }

    @Override
    public void interestInvoke(int trendingId, int pageId) {
    }

    @Override
    public void openTrendingArticle(TrendingItems trendingItems, String prePage) {
        if(trendingItems.getBanner()){
            openNext(trendingItems, prePage);
        }else {
            Intent intent = new Intent(HomeInnerActivity.this, TrendingArticleActivity.class);
            intent.putExtra(Constant.PARCEL_KEY, trendingItems);
            intent.putExtra(Constant.PRE_PAGE_KEY, Constant.TRENDINGPAGE);
            startActivity(intent);
        }
    }

    public void openExhibitionDetails(TrendingItems trendingItems){
        Intent intent = new Intent(HomeInnerActivity.this, ExhibitionDetailsActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, trendingItems);
        intent.putExtra(Constant.PRE_PAGE_KEY, Constant.EXHIBITIONPAGE);
        startActivity(intent);
    }

    private void openNext(TrendingItems trendingItems, String prePage){
        String pageid = trendingItems.getPagetypeid();
        Intent intent;
        switch (pageid){
            case ARTICLEPAGE:
                intent = new Intent(HomeInnerActivity.this, TrendingArticleActivity.class);
                intent.putExtra(Constant.PARCEL_KEY, trendingItems);
                startActivity(intent);
                break;
            case EXARTICLEPAGE:
                intent = new Intent(HomeInnerActivity.this, ExhibitionDetailsActivity.class);
                intent.putExtra(Constant.PARCEL_KEY, trendingItems);
                intent.putExtra(Constant.PRE_PAGE_KEY, STORELISTINGPAGE);
                startActivity(intent);
                break;
            case Constant.MULTISTOREPAGE:
                intent = new Intent(HomeInnerActivity.this, MultistoreActivity.class);
                intent.putExtra(Constant.ID_KEY, trendingItems.getId());//TODO : check id
                intent.putExtra(Constant.PRE_PAGE_KEY, prePage);
                startActivity(intent);
                break;
            case Constant.STOREDETAILSPAGE:
                StoreListModel storeListModel =  new StoreListModel();
                storeListModel.setId(trendingItems.getId());
                intent = new Intent(HomeInnerActivity.this, StoreDetailsActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra(Constant.PARCEL_KEY, storeListModel);
                startActivity(intent);
                break;
            default: break;
        }
    }

    public void showSortScreem() {

        final Dialog popupDialog = new Dialog(HomeInnerActivity.this);
        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.popup_ex_sort, null);
        ButtonPret btn_apply = (ButtonPret) view.findViewById(R.id.btn_apply);
        final RadioGroup radioGroup_sortby = (RadioGroup) view.findViewById(R.id.radioGroup);
        final RadioGroup radioGroup_orderby = (RadioGroup) view.findViewById(R.id.radioGroup1);

        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popupDialog.setContentView(view);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        popupDialog.show();

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(radioGroup_sortby.getCheckedRadioButtonId());
                System.out.println(radioGroup_orderby.getCheckedRadioButtonId());
                popupDialog.dismiss();
            }
        });
    }

    @Override
    public void onClicked(int position, ArrayList<String> mImagearray) {

        ArrayList<String> imageModels1 = mImagearray;
        Intent intent = new Intent(getApplicationContext(), FullscreenGalleryActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, imageModels1);
        intent.putExtra(Constant.PRE_PAGE_KEY, Integer.parseInt(Constant.HOMEPAGE));
        intent.putExtra(Constant.POSITION_KEY, position);
        startActivity(intent);
    }
}
