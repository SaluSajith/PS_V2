package com.hit.pretstreet.pretstreet.search;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.SimpleDividerItemDecoration;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.search.controllers.SearchController;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.storedetails.interfaces.ImageClickCallback;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters.StoreList_RecyclerAdapter;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers.SubCategoryController;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.ButtonClickCallbackStoreList;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.FilterDataModel;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.CLICKTYPE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.FILTERPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.MULTILINK;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.MULTISTORE_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.UPDATEFOLLOWSTATUS_URL;

public class MultistoreActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, ImageClickCallback, ButtonClickCallbackStoreList {

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
    private static final int STORE_DETAILS = 14;

    JsonRequestController jsonRequestController;
    SubCategoryController subCategoryController;
    SearchController searchController;

    @BindView(R.id.tv_name)TextViewPret tv_name;
    @BindView(R.id.tv_storecount)TextViewPret tv_storecount;
    @BindView(R.id.rv_storelist)RecyclerView rv_storelist;

    String shareText;
    Context context;
    StoreList_RecyclerAdapter storeList_recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multistore);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUi();
    }

    private void initUi(){
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        context = getApplicationContext();

        Utility.setListLayoutManager_(rv_storelist, MultistoreActivity.this);
        rv_storelist.addItemDecoration(new SimpleDividerItemDecoration(context));
        getShoplist();
    }

    private void getShoplist(){
        String pageid = getIntent().getStringExtra(Constant.PRE_PAGE_KEY);
        String mCatid = getIntent().getStringExtra(Constant.ID_KEY);
        JSONObject resultJson = searchController.getMultiStoreListJson(mCatid, pageid, "1");//caat, prepage, clicktype, id
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, MULTISTORE_URL);
    }

    private void setAdapter(ArrayList<StoreListModel> storeListModels){
        storeList_recyclerAdapter = new StoreList_RecyclerAdapter(Glide.with(this), rv_storelist, MultistoreActivity.this, storeListModels);
        rv_storelist.setAdapter(storeList_recyclerAdapter);
        storeList_recyclerAdapter.setLoaded();
    }

    private void setupCollapsingHeader(String title, String imageUrl){
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(title);
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(context, R.color.transparent)); // transperent color = #00000000
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(context, R.color.white));
        loadBackdrop(imageUrl);
    }

    private void loadBackdrop(String imageUrl) {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(context)
                .load(imageUrl)
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate()
                .placeholder(R.drawable.default_banner)
                .into(imageView);

    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
        searchController = new SearchController(this);
    }

    private void handleResponse(JSONObject response){
        try {
            String url = response.getString("URL");
            //displaySnackBar(response.getString("CustomerMessage"));
            switch (url){
                case Constant.MULTISTORE_URL:
                    JSONObject jsonObject = response.getJSONObject("Data");
                    tv_name.setText(jsonObject.getString("MultistoreTitle"));
                    tv_storecount.setText(jsonObject.getString("MultistoreCount")+" Stores");
                    setupCollapsingHeader(jsonObject.getString("MultistoreTitle"), jsonObject.getString("MultistoreImageSource"));
                    shareText = jsonObject.getString("Share");
                    ArrayList<StoreListModel> storeListModels = searchController.getList(response);
                    setAdapter(storeListModels);
                    break;
                case UPDATEFOLLOWSTATUS_URL:
                    JSONObject object = response.getJSONObject("Data");
                    storeList_recyclerAdapter.updateFollowStatus(object.getInt("FollowingStatus"),
                            object.getString("StoreId"));
                    storeList_recyclerAdapter.notifyDataSetChanged();
                break;
                default: break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_share:
                shareUrl(shareText);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResponse(JSONObject response) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideDialog();
            }
        }, 1000);
        handleResponse(response);
    }

    @Override
    public void onError(String error) {
        this.hideDialog();
        displaySnackBar( error);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    @Override
    public void onClicked(int position, ArrayList<String> imageModels) {
        ArrayList<String> imageModels1 = imageModels;
        Intent intent = new Intent(MultistoreActivity.this, FullscreenGalleryActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, imageModels1);
        intent.putExtra(Constant.PRE_PAGE_KEY, Integer.parseInt(Constant.STORELISTINGPAGE));
        intent.putExtra(Constant.POSITION_KEY, position);
        startActivity(intent);

    }

    @Override
    public void buttonClick(StoreListModel storeListModel) {
        switch (storeListModel.getPageTypeId()){
            case Constant.STOREDETAILSPAGE:
                Intent intent = new Intent(MultistoreActivity.this, StoreDetailsActivity.class);
                intent.putExtra(Constant.PARCEL_KEY, storeListModel);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
                intent.putExtra(CLICKTYPE_KEY, MULTILINK);
                startActivityForResult(intent, STORE_DETAILS);
                break;
            case Constant.TRENDINGPAGE:
                selectedFragment = TRENDING_FRAGMENT;
                intent = new Intent(MultistoreActivity.this, HomeInnerActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case Constant.EXHIBITIONPAGE:
                selectedFragment = EXHIBITION_FRAGMENT;
                intent = new Intent(MultistoreActivity.this, HomeInnerActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case Constant.MULTISTOREPAGE:
                intent = new Intent(MultistoreActivity.this, MultistoreActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
                startActivity(intent);
                break;
            default: break;
        }
    }

    @Override
    public void updateFollowStatus(String id) {
        JSONObject resultJson = subCategoryController.updateFollowCount(id, Constant.MULTISTOREPAGE,  Constant.FOLLOWLINK);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, UPDATEFOLLOWSTATUS_URL);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode  == RESULT_OK) {
                if (requestCode == STORE_DETAILS){
                    int status = Integer.parseInt(data.getStringExtra(PARCEL_KEY));
                    String storeId = data.getStringExtra(ID_KEY);
                    storeList_recyclerAdapter.updateFollowStatus_fromDetails(status, storeId, data.getStringExtra("followcount"));
                    storeList_recyclerAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception ex) { }
    }
}
