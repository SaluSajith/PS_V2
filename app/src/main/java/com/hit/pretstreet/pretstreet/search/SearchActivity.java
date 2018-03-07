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
import com.hit.pretstreet.pretstreet.search.controllers.SearchController;
import com.hit.pretstreet.pretstreet.search.fragments.AutoSearchFragment;
import com.hit.pretstreet.pretstreet.search.fragments.SearchResultsFragment;
import com.hit.pretstreet.pretstreet.search.interfaces.RecentCallback;
import com.hit.pretstreet.pretstreet.search.interfaces.SearchDataCallback;
import com.hit.pretstreet.pretstreet.search.models.BasicModel;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.storedetails.interfaces.ImageClickCallback;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters.StoreList_RecyclerAdapter;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers.SubCategoryController;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.ButtonClickCallbackStoreList;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.TwoLevelDataModel;
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
import static com.hit.pretstreet.pretstreet.core.utils.Constant.LIMIT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.RECENTSEARCH_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SEARCHLISTLINK;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SEARCHPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SEARCH_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.UPDATEFOLLOWSTATUS_URL;
/**
 * Search parent activity which shows fragments like
 *          Search main fragment which shows recent search and popular search
 *          AutoSearch Results fragment
 *          Search Results fragment
 **/
public class SearchActivity extends AbstractBaseAppCompatActivity
        implements ApiListenerInterface, ButtonClickCallbackStoreList, ImageClickCallback, RecentCallback {

    @BindView(R.id.fl_content) FrameLayout fl_content;

    private static final int AUTOSEARCH_FRAGMENT = 0;
    private static final int SEARCHRESULTS_FRAGMENT = 1;

    private static final int STORE_DETAILS = 14;

    private JsonRequestController jsonRequestController;
    private SearchController searchController;
    private SubCategoryController subCategoryController;
    private SearchDataCallback searchDataCallback;
    private StoreList_RecyclerAdapter storeList_recyclerAdapter;
    private SearchResultsFragment searchFragment;

    private ArrayList<TwoLevelDataModel> dataModel;
    private JSONArray arrayFilter;

    private String mStrSearch = "", mCatID = "", mCaType;
    private boolean loadmore = true;
    private String prepagekey;

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
        /**Default fragment which lists recent searches and popular searches*/
        setupFragment(AUTOSEARCH_FRAGMENT, false);

        if (getIntent() != null) {
            if (getIntent().getExtras() != null && getIntent().getExtras()
                    .containsKey(PRE_PAGE_KEY)) {
                prepagekey = getIntent().getStringExtra(PRE_PAGE_KEY);
            }
        }
    }

    /**To change headername, filter button visibility and all as per the selected fragment
     * @param fragmentId id of the fragment
     * @param b shows addtobackstack boolean*/
    private void setupFragment(int fragmentId, boolean b){
        switch (fragmentId){
            case AUTOSEARCH_FRAGMENT: //Default fragment which lists recent searches and popular searches
                AutoSearchFragment fragment = new AutoSearchFragment();
                searchDataCallback = fragment;
                changeFragment(fragment, b);
                break;
            case SEARCHRESULTS_FRAGMENT://search results list
                searchFragment = new SearchResultsFragment();
                Bundle bundle = new Bundle();
                bundle.putString(PRE_PAGE_KEY, SEARCHPAGE);
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

    /**Replacing fragments
     * @param addBackstack boolean which denotes whether we have to add the previous to backstack or not
     * @param fragment fragment that should be passed*/
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

    /** Default fragment which lists recent searches and popular searches
     * */
    public void getAutoSearch(String mStr, String mCatId, String mCattype){
        JSONObject resultJson = searchController.getAutoSearchListJson(mStr,
                prepagekey,
                mCatId,
                mCattype);
        jsonRequestController.sendRequest(SearchActivity.this, resultJson, AUTOSEARCH_URL);
    }

    /** To handle search button press after typing all
     * @param first to handle pagination
     * @param pageCount to handle pagination*/
    public void getSearchResult(int pageCount, boolean first){
        if(first)
            this.showProgressDialog(getResources().getString(R.string.loading));
        JSONObject resultJson = searchController.getSearchResultJson(mStrSearch,
                prepagekey, mCatID, pageCount, mCaType, arrayFilter);
        jsonRequestController.cancelAllPendingRequests();
        jsonRequestController.sendRequest(SearchActivity.this, resultJson, SEARCH_URL);
    }

    /**To handle the SEARCH button press
     * @param mCatId 0-All, 1-Designers, 2-Brands, 3-Jewellery, 4-Retails - Coming from server
     * @param mCattype 0-stores, 1-Products
     * @param mStr char/word that is typed*/
    public void openSearchResult(String mStr, String mCatId, String mCattype){
        mStrSearch = mStr;
        mCatID = mCatId;
        mCaType = mCattype;
        setupFragment(SEARCHRESULTS_FRAGMENT, true);
    }

    /**Get recent searchlist from server
     * @param mCatID 0-All, 1-Designers, 2-Brands, 3-Jewellery, 4-Retails - Coming from server*/
    public void getRecentPage(Fragment fragment, String mCatID){
        searchDataCallback = (AutoSearchFragment) fragment;
        JSONObject resultJson = searchController.getRecentSearchListJson(mCatID, SEARCHPAGE);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, Constant.RECENTSEARCH_URL);
    }

    public void setAdapter(StoreList_RecyclerAdapter storeList_recyclerAdapter){
        this.storeList_recyclerAdapter = storeList_recyclerAdapter;
    }

    /**Handling response corresponding to the URL
     * @param response response corresponding to each URL - here I am appending the URL itself
     *                 to the response so that I will be able to handle each response seperately*/
    private void handleResponse(JSONObject response){
        try {
            String url = response.getString("URL");
            switch (url){
                case AUTOSEARCH_URL:
                    ArrayList<BasicModel> searchModels = searchController.getAutoSearchList(response);
                    searchDataCallback.setAutosearchList(searchModels); //loading result data to list
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideDialog();
                        }
                    }, 200);
                    break;
                case SEARCH_URL:
                    ArrayList <StoreListModel> storeListModels = searchController.getSearchList(response);
                    if(storeListModels.size()<Integer.parseInt(LIMIT))
                        loadmore = false;
                    searchDataCallback.setSearchList(storeListModels, loadmore);  //loading result data to list
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideDialog();
                        }
                    }, 200);
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
                    }, 100);
                    break;
                case UPDATEFOLLOWSTATUS_URL:
                    try {
                        JSONObject object = response.getJSONObject("Data");
                        storeList_recyclerAdapter.updateFollowStatus(object.getInt("FollowingStatus"),
                                object.getString("StoreId"));
                        storeList_recyclerAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        openNext(storeListModel, SEARCHPAGE, SEARCHLISTLINK);
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
    public void viewClick(BasicModel searchModel) {
        StoreListModel storeListModel = new StoreListModel();
        storeListModel.setId(searchModel.getId());
        Intent intent = new Intent(SearchActivity.this, StoreDetailsActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, storeListModel);
        intent.putExtra(CLICKTYPE_KEY, SEARCHLISTLINK);
        intent.putExtra(Constant.PRE_PAGE_KEY, Constant.SEARCHPAGE);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode  == RESULT_OK) {
                /**Updating filterpage array */
                if (requestCode == Integer.parseInt(FILTERPAGE)) {
                    Bundle bundle = data.getExtras();
                    dataModel = (ArrayList<TwoLevelDataModel>) bundle.getSerializable(PARCEL_KEY);
                    arrayFilter = subCategoryController.createFilterModel(dataModel);
                    searchFragment.refreshSearchResult();
                }
                else if (requestCode == STORE_DETAILS){
                    /**To update follow status and count in search result page*/
                    int status = Integer.parseInt(data.getStringExtra(PARCEL_KEY));
                    String storeId = data.getStringExtra(ID_KEY);
                    storeList_recyclerAdapter.updateFollowStatus_fromDetails(status, storeId,
                            data.getStringExtra("followcount"));
                    storeList_recyclerAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception ex) { }
    }
}
