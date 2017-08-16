package com.hit.pretstreet.pretstreet.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.search.controllers.SearchController;
import com.hit.pretstreet.pretstreet.search.fragments.AutoSearchFragment;
import com.hit.pretstreet.pretstreet.search.fragments.SearchResultsFragment;
import com.hit.pretstreet.pretstreet.search.interfaces.SearchDataCallback;
import com.hit.pretstreet.pretstreet.search.models.SearchModel;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.storedetails.interfaces.ImageClickCallback;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.StoreListingActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters.StoreList_RecyclerAdapter;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers.SubCategoryController;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.fragments.SubCatFragment;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.ButtonClickCallbackStoreList;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.AUTOSEARCH_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SEARCH_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.UPDATEFOLLOWSTATUS_URL;

public class SearchActivity extends AbstractBaseAppCompatActivity
        implements ApiListenerInterface, ButtonClickCallbackStoreList, ImageClickCallback {

    @BindView(R.id.fl_content) FrameLayout fl_content;

    private static final int AUTOSEARCH_FRAGMENT = 0;
    private static final int SEARCHRESULTS_FRAGMENT = 1;

    private int selectedFragment = 0;
    private static final int ACCOUNT_FRAGMENT = 0;
    private static final int FOLLOWING_FRAGMENT = 1;
    private static final int ABOUT_FRAGMENT = 2;
    private static final int ADDSTORE_FRAGMENT = 3;
    private static final int CONTACTUS_FRAGMENT = 4;
    private static final int FEEDBACK_FRAGMENT = 5;
    private static final int ABOUTUS_FRAGMENT = 6;
    private static final int PRIVACY_FRAGMENT = 7;
    private static final int TERMS_FRAGMENT = 8;
    private static final int TRENDING_FRAGMENT = 10;
    private static final int EXHIBITION_FRAGMENT = 11;

    JsonRequestController jsonRequestController;
    SearchDataCallback searchDataCallback;
    StoreList_RecyclerAdapter storeList_recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
    }

    private void init(){
        ButterKnife.bind(this);
        setupFragment(AUTOSEARCH_FRAGMENT, false);
    }

    private void setupFragment(int fragmentId, boolean b){
        switch (fragmentId){
            case AUTOSEARCH_FRAGMENT:
                AutoSearchFragment fragment = new AutoSearchFragment();
                searchDataCallback = fragment;
                changeFragment(fragment, b);
                break;
            case SEARCHRESULTS_FRAGMENT:
                SearchResultsFragment searchFragment = new SearchResultsFragment();
                searchDataCallback = searchFragment;
                changeFragment(searchFragment, b);
                break;
            default:
                break;
        }
    }

    private void changeFragment(Fragment fragment, boolean addBackstack) {

        FragmentManager fm = getSupportFragmentManager();/*Removing stack*/
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
        fl_content.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); /* Fragment transition*/
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.fl_content, fragment);
        if (addBackstack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void getAutoSearch(String mStr, String mCatId){
        JSONObject resultJson = SearchController.getAutoSearchListJson(mStr, getIntent().getStringExtra(Constant.PRE_PAGE_KEY), mCatId);
        jsonRequestController.sendRequest(SearchActivity.this, resultJson, AUTOSEARCH_URL);
    }

    public void getSearchResult(String mStr, String mCatId){
        setupFragment(SEARCHRESULTS_FRAGMENT, false);
        this.showProgressDialog(getResources().getString(R.string.loading));
        JSONObject resultJson = SearchController.getSearchResultJson(mStr, getIntent().getStringExtra(Constant.PRE_PAGE_KEY), mCatId, "1");//TODO
        jsonRequestController.sendRequest(SearchActivity.this, resultJson, SEARCH_URL);
    }

    public void getRecentPage(){
        //TODO
        JSONObject resultJson = LoginController.getHomePageJson(Constant.HOMEPAGE);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, Constant.HOMEPAGE_URL);
    }

    public void setAdapter(StoreList_RecyclerAdapter storeList_recyclerAdapter){
        this.storeList_recyclerAdapter = storeList_recyclerAdapter;
    }

    public void getSearchResult(){

    }

    private void handleResponse(JSONObject response){
        try {
            String url = response.getString("URL");
            switch (url){
                case AUTOSEARCH_URL:
                    ArrayList<SearchModel> searchModels = SearchController.getAutoSearchList(response);
                    searchDataCallback.setAutosearchList(searchModels);
                    break;
                case SEARCH_URL:
                    ArrayList <StoreListModel> storeListModels = SearchController.getSearchList(response);
                    searchDataCallback.setSearchList(storeListModels);
                    break;
                case UPDATEFOLLOWSTATUS_URL:
                    JSONObject object = response.getJSONObject("Data");
                    storeList_recyclerAdapter.updateFollowStatus(object.getInt("FollowingStatus"),
                            object.getString("StoreId"));
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
    public void buttonClick(StoreListModel storeListModel) {
        switch (storeListModel.getPageTypeId()){
            case Constant.STOREDETAILSPAGE:
                Intent intent = new Intent(SearchActivity.this, StoreDetailsActivity.class);
                intent.putExtra(Constant.PARCEL_KEY, storeListModel);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
                startActivity(intent);
                break;
            case Constant.TRENDINGPAGE:
                selectedFragment = TRENDING_FRAGMENT;
                intent = new Intent(SearchActivity.this, HomeInnerActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case Constant.EXHIBITIONPAGE:
                selectedFragment = EXHIBITION_FRAGMENT;
                intent = new Intent(SearchActivity.this, HomeInnerActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case Constant.MULTISTOREPAGE:
                intent = new Intent(SearchActivity.this, MultistoreActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
                startActivity(intent);
                break;
            default: break;
        }
    }

    @Override
    public void updateFollowStatus(String id) {
        JSONObject resultJson = SubCategoryController.updateFollowCount(id, Constant.SEARCHPAGE,  Constant.FOLLOWLINK);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, UPDATEFOLLOWSTATUS_URL);
    }

    @Override
    public void onClicked(int position, ArrayList<String> imageModels) {
        ArrayList<String> imageModels1 = imageModels;
        Intent intent = new Intent(SearchActivity.this, FullscreenGalleryActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, imageModels1);
        intent.putExtra(Constant.PRE_PAGE_KEY, Integer.parseInt(Constant.STORELISTINGPAGE));
        intent.putExtra(Constant.POSITION_KEY, position);
        startActivity(intent);
    }
}
