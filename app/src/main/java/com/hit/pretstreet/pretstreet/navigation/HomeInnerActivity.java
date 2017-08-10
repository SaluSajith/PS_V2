package com.hit.pretstreet.pretstreet.navigation;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.controllers.HomeFragmentController;
import com.hit.pretstreet.pretstreet.navigation.fragments.ExhibitionFragment;
import com.hit.pretstreet.pretstreet.navigation.fragments.TrendingFragment;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingCallback;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingHolderInvoke;
import com.hit.pretstreet.pretstreet.navigation.interfaces.ZoomedViewListener;
import com.hit.pretstreet.pretstreet.navigation.models.ProductImageItem;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.ChangePasswordFragment;
import com.hit.pretstreet.pretstreet.search.MultistoreActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.StoreListingActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.SubCatActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.ButtonClickCallbackStoreList;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.*;

public class HomeInnerActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, TrendingHolderInvoke {

    private int currentFragment = 0;
    private static final int TRENDING_FRAGMENT = 10;
    private static final int EXHIBITION_FRAGMENT = 11;
    private static final int TRENDINGARTICLE_FRAGMENT = 12;

    JsonRequestController jsonRequestController;
    TrendingCallback trendingCallback;
    Toolbar toolbar;

    @BindView(R.id.content) FrameLayout fl_content;
    @BindView(R.id.nsv_header)NestedScrollView nsv_header;
    @BindView(R.id.iv_header)ImageView iv_header;
    @BindView(R.id.tv_cat_name) TextViewPret tv_cat_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_inner);
        init();
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
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
        fl_content.bringToFront();

        Intent intent = getIntent();
        int fragmentId = intent.getIntExtra("fragment", 0);
        setupFragment(fragmentId, false);
    }

    private void setupFragment(int fragmentId, boolean b){
        switch (fragmentId){
            case TRENDING_FRAGMENT:
                tv_cat_name.setText("Trending");
                iv_header.setImageResource(R.drawable.header_yellow);
                TrendingFragment trendingFragment = new TrendingFragment();
                trendingCallback = trendingFragment;
                changeFragment(trendingFragment, b);
                break;
            case EXHIBITION_FRAGMENT:
                tv_cat_name.setText("Exhibition");
                iv_header.setImageResource(R.drawable.header_yellow);
                ExhibitionFragment exhibitionFragment = new ExhibitionFragment();
                trendingCallback = exhibitionFragment;
                changeFragment(exhibitionFragment, b);
                break;
            case TRENDINGARTICLE_FRAGMENT:
                toolbar.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public void getTrendinglist(String offset){
        JSONObject resultJson = HomeFragmentController.getTrendinglistJson(offset);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, TRENDING_URL);
    }

    public void getExhibitionlist(String offset){
        JSONObject resultJson = HomeFragmentController.getExhibitionlistJson(offset);
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
                    ArrayList<TrendingItems> trendingItemses = HomeFragmentController.getTrendingList(response);
                    trendingCallback.bindData(trendingItemses);
                    break;
                case EXHIBITION_URL:
                    ArrayList<TrendingItems> exHItemses = HomeFragmentController.getExhibitionList(response);
                    trendingCallback.bindData(exHItemses);
                    break;
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
        displaySnackBar(error);
    }

    @Override
    public void loadStoreDetails(int position, StoreListModel storeListModel) {
        Intent intent = new Intent(HomeInnerActivity.this, StoreDetailsActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, storeListModel);
        intent.putExtra(Constant.PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
        startActivity(intent);
    }

    @Override
    public void shareUrl(String text) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "PrêtStreet : Your ultimate shopping guide!!!");
        share.putExtra(Intent.EXTRA_TEXT, "Discover the latest talent in Fashion Designers, brands & Jewellers." +
                " Follow us on PrêtStreet, Your ultimate shopping guide.\n\nhttp://www.pretstreet.com/share.php");
        startActivity(Intent.createChooser(share, "Share with.."));
    }

    @Override
    public void likeInvoke(int trendingId) {

    }

    @Override
    public void openTrendingArticle(TrendingItems trendingItems, String prePage) {
        if(trendingItems.getBanner()){
            openNext(trendingItems, prePage);
        }else {
            Intent intent = new Intent(HomeInnerActivity.this, TrendingArticleActivity.class);
            intent.putExtra(Constant.PARCEL_KEY, trendingItems);
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
            /*case Constant.SUBCATPAGE:
                //displaySnackBar(homeCatItems.getHomeContentData().getCategoryName());
                Intent intent = new Intent(this, SubCatActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, prePage);
                intent.putExtra("mHomeCatItems", catContentData);
                intent.putExtra("mTitle", title);
                startActivity(intent);
                break;
            case Constant.STORELISTINGPAGE:
                intent = new Intent(getApplicationContext(), StoreListingActivity.class);
                intent.putExtra("contentData", catContentData);
                intent.putExtra(Constant.PRE_PAGE_KEY, prePage);
                intent.putExtra("mTitle", title);
                startActivity(intent);
                break;
            case Constant.TRENDINGPAGE:
                selectedFragment = TRENDING_FRAGMENT;
                intent = new Intent(HomeInnerActivity.this, HomeInnerActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, prePage);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case Constant.EXHIBITIONPAGE:
                selectedFragment = EXHIBITION_FRAGMENT;
                intent = new Intent(HomeInnerActivity.this, HomeInnerActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, prePage);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;*/
            case Constant.MULTISTOREPAGE:
                intent = new Intent(HomeInnerActivity.this, MultistoreActivity.class);
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

}
