package com.hit.pretstreet.pretstreet.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
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
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.storedetails.controllers.StoreDetailsController;
import com.hit.pretstreet.pretstreet.storedetails.interfaces.ImageClickCallback;
import com.hit.pretstreet.pretstreet.storedetails.model.StoreDetailsModel;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.StoreListingActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters.StoreList_RecyclerAdapter;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers.SubCategoryController;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.ButtonClickCallbackStoreList;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.MULTISTORE_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.STORELISTING_URL;
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

    JsonRequestController jsonRequestController;
    SearchController searchController;

    @BindView(R.id.tv_name)TextViewPret tv_name;
    @BindView(R.id.tv_storecount)TextViewPret tv_storecount;
    @BindView(R.id.rv_storelist)RecyclerView rv_storelist;

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

        Utility.setListLayoutManager(rv_storelist, MultistoreActivity.this);
        rv_storelist.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        getShoplist("1");
    }

    private void getShoplist(String mCatid){
        String pageid = getIntent().getStringExtra(Constant.PRE_PAGE_KEY);
        JSONObject resultJson = SearchController.getShopListJson(mCatid, pageid, "1", "1");//caat, prepage, clicktype, id
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, MULTISTORE_URL);
    }

    private void setAdapter(ArrayList<StoreListModel> storeListModels){
        StoreList_RecyclerAdapter storeList_recyclerAdapter = new StoreList_RecyclerAdapter(MultistoreActivity.this, storeListModels);
        rv_storelist.setAdapter(storeList_recyclerAdapter);
    }

    private void setupCollapsingHeader(String title, String imageUrl){
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(title);
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent)); // transperent color = #00000000
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        loadBackdrop(imageUrl);
    }

    private void loadBackdrop(String imageUrl) {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(getApplicationContext())
                .load(imageUrl)
                .asBitmap()
                .centerCrop()
                .into(imageView);

    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
        searchController = new SearchController(this);
    }

    private void shareUrl(String text) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "PrêtStreet : Your ultimate shopping guide!!!");
        share.putExtra(Intent.EXTRA_TEXT, "Discover the latest talent in Fashion Designers, brands & Jewellers." +
                " Follow us on PrêtStreet, Your ultimate shopping guide.\n\nhttp://www.pretstreet.com/share.php");
        startActivity(Intent.createChooser(share, "Share with.."));
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
                    ArrayList<StoreListModel> storeListModels = SearchController.getList(response);
                    setAdapter(storeListModels);
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
                shareUrl("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResponse(JSONObject response) {
        this.hideDialog();
        handleResponse(response);
    }

    @Override
    public void onError(String error) {
        this.hideDialog();
        displaySnackBar( error);
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
                startActivity(intent);
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
        JSONObject resultJson = SubCategoryController.updateFollowCount(id, Constant.MULTISTOREPAGE,  Constant.FOLLOWLINK);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, UPDATEFOLLOWSTATUS_URL);
    }
}
