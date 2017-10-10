package com.hit.pretstreet.pretstreet.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.hit.pretstreet.pretstreet.search.interfaces.RecentCallback;
import com.hit.pretstreet.pretstreet.search.interfaces.SearchDataCallback;
import com.hit.pretstreet.pretstreet.search.models.SearchModel;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.storedetails.interfaces.ImageClickCallback;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters.StoreList_RecyclerAdapter;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers.SubCategoryController;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.ButtonClickCallbackStoreList;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.FilterDataModel;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.AUTOSEARCH_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.CLICKTYPE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.FILTERPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.RECENTSEARCH_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SEARCHPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SEARCH_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.UPDATEFOLLOWSTATUS_URL;

public class SearchActivity extends AbstractBaseAppCompatActivity
        implements ApiListenerInterface, ButtonClickCallbackStoreList, ImageClickCallback, RecentCallback {

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

    private JsonRequestController jsonRequestController;
    private SearchController searchController;
    private SubCategoryController subCategoryController;
    private SearchDataCallback searchDataCallback;
    private StoreList_RecyclerAdapter storeList_recyclerAdapter;
    private SearchResultsFragment searchFragment;

    private ArrayList<FilterDataModel> dataModel;
    private JSONArray arrayFilter;

    private String mStrSearch = "", mCatID = "", mCaType;
    private boolean loadmore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
        subCategoryController = new SubCategoryController(this);
        searchController = new SearchController(this);
    }

    private void init(){
        ButterKnife.bind(this);
        dataModel = new ArrayList<>();
        arrayFilter = new JSONArray();
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
                searchFragment = new SearchResultsFragment();
                Bundle bundle = new Bundle();
                bundle.putString(PRE_PAGE_KEY, Constant.SEARCHPAGE);
                bundle.putString(ID_KEY, mCatID);
                bundle.putSerializable(PARCEL_KEY, this.dataModel);
                searchDataCallback = searchFragment;
                searchFragment.setArguments(bundle);
                changeFragment(searchFragment, b);
                break;
            default:
                break;
        }
    }

    private void changeFragment(Fragment fragment, boolean addBackstack) {

        FragmentManager fm = getSupportFragmentManager();//Removing stack
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

    public void getAutoSearch(String mStr, String mCatId, String mCattype){
        JSONObject resultJson = searchController.getAutoSearchListJson(mStr,
                getIntent().getStringExtra(PRE_PAGE_KEY),
                mCatId,
                mCattype);
        jsonRequestController.sendRequest(SearchActivity.this, resultJson, AUTOSEARCH_URL);
    }

    public void getSearchResult(int pageCount, boolean first){
        if(first)
        this.showProgressDialog(getResources().getString(R.string.loading));
        JSONObject resultJson = searchController.getSearchResultJson(mStrSearch,
                getIntent().getStringExtra(PRE_PAGE_KEY), mCatID, pageCount, mCaType, arrayFilter);
        jsonRequestController.sendRequest(SearchActivity.this, resultJson, SEARCH_URL);
    }

    public void openSearchResult(String mStr, String mCatId, String mCattype){
        mStrSearch = mStr;
        mCatID = mCatId;
        mCaType = mCattype;
        setupFragment(SEARCHRESULTS_FRAGMENT, true);
    }

    public void getRecentPage(Fragment fragment, String mCatID){
        searchDataCallback = (AutoSearchFragment) fragment;
        JSONObject resultJson = searchController.getRecentSearchListJson(mCatID, SEARCHPAGE);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, Constant.RECENTSEARCH_URL);
    }

    public void setAdapter(StoreList_RecyclerAdapter storeList_recyclerAdapter){
        this.storeList_recyclerAdapter = storeList_recyclerAdapter;
    }

    private void handleResponse(JSONObject response){
        try {
            String url = response.getString("URL");
            switch (url){
                case AUTOSEARCH_URL:
                    ArrayList<SearchModel> searchModels = searchController.getAutoSearchList(response);
                    searchDataCallback.setAutosearchList(searchModels);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideDialog();
                        }
                    }, 1000);
                    break;
                case SEARCH_URL:
                    ArrayList <StoreListModel> storeListModels = searchController.getSearchList(response);
                    if(storeListModels.size()==0)
                        loadmore = false;
                    searchDataCallback.setSearchList(storeListModels, loadmore);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideDialog();
                        }
                    }, 1000);
                    break;
                case RECENTSEARCH_URL:
                    searchDataCallback.setRecentsearchList(searchController.getRecentViewList(response),
                            searchController.getRecentSearchList(response),
                            searchController.getCategoryList(response));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideDialog();
                        }
                    }, 1000);
                    break;
                case UPDATEFOLLOWSTATUS_URL:
                    JSONObject object = response.getJSONObject("Data");
                    storeList_recyclerAdapter.updateFollowStatus(object.getInt("FollowingStatus"),
                            object.getString("StoreId"));
                    storeList_recyclerAdapter.notifyDataSetChanged();
                    hideDialog();
                    break;
                default: break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResponse(JSONObject response) {
        handleResponse(response);
    }

    @Override
    public void onError(String error) {
        this.hideDialog();
        loadmore = false;
        displaySnackBar(error);
    }

    @Override
    public void buttonClick(StoreListModel storeListModel) {
        switch (storeListModel.getPageTypeId()){
            case Constant.STOREDETAILSPAGE:
                Intent intent = new Intent(SearchActivity.this, StoreDetailsActivity.class);
                intent.putExtra(Constant.PARCEL_KEY, storeListModel);
                intent.putExtra(CLICKTYPE_KEY, "");
                intent.putExtra(Constant.PRE_PAGE_KEY, SEARCHPAGE);
                startActivity(intent);
                break;
            case Constant.TRENDINGPAGE:
                selectedFragment = TRENDING_FRAGMENT;
                intent = new Intent(SearchActivity.this, HomeInnerActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.SEARCHPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case Constant.EXHIBITIONPAGE:
                selectedFragment = EXHIBITION_FRAGMENT;
                intent = new Intent(SearchActivity.this, HomeInnerActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.SEARCHPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case Constant.MULTISTOREPAGE:
                intent = new Intent(SearchActivity.this, MultistoreActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.SEARCHPAGE);
                intent.putExtra(Constant.ID_KEY, storeListModel.getId());
                startActivity(intent);
                break;
            default: break;
        }
    }

    @Override
    public void updateFollowStatus(String id) {
        JSONObject resultJson = SubCategoryController.updateFollowCount(id, SEARCHPAGE,  Constant.FOLLOWLINK);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, UPDATEFOLLOWSTATUS_URL);
    }

    @Override
    public void onClicked(int position, ArrayList<String> imageModels) {
        ArrayList<String> imageModels1 = imageModels;
        Intent intent = new Intent(SearchActivity.this, FullscreenGalleryActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, imageModels1);
        intent.putExtra(Constant.PRE_PAGE_KEY, Integer.parseInt(Constant.SEARCHPAGE));
        intent.putExtra(Constant.POSITION_KEY, position);
        startActivity(intent);
    }

    @Override
    public void viewClick(SearchModel searchModel) {
        StoreListModel storeListModel = new StoreListModel();
        storeListModel.setId(searchModel.getId());
        Intent intent = new Intent(SearchActivity.this, StoreDetailsActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, storeListModel);
        intent.putExtra(Constant.PRE_PAGE_KEY, Constant.SEARCHPAGE);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == Integer.parseInt(FILTERPAGE) && resultCode  == RESULT_OK) {
                Bundle bundle = data.getExtras();
                dataModel = (ArrayList<FilterDataModel>) bundle.getSerializable(PARCEL_KEY);
                arrayFilter = subCategoryController.createFilterModel(dataModel);
                searchFragment.refreshSearchResult();
            }
        } catch (Exception ex) { }
    }
}
