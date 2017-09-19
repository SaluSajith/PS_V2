package com.hit.pretstreet.pretstreet.subcategory_n_storelist;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.fragments.HomeFragment;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.search.SearchActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.DefaultLocationActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.fragments.SubCatFragment;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.SubCatTrapeClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SUBCATPAGE;

public class SubCatActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, ButtonClickCallback, SubCatTrapeClick {

    private static final int SUBCAT_FRAGMENT = 0;

    @BindView(R.id.content) FrameLayout fl_content;
    @BindView(R.id.nsv_header)NestedScrollView nsv_header;
    @BindView(R.id.tv_cat_name) TextViewPret tv_cat_name;
    @BindView(R.id.tv_location) TextViewPret tv_location;
    @BindView(R.id.ll_header) LinearLayout ll_header;
    @BindView(R.id.hs_categories) HorizontalScrollView hs_categories;

    JsonRequestController jsonRequestController;
    LoginController loginController;
    SubCatTrapeClick subCatTrapeClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_cat);
        init();
    }

    @Override
    protected void setUpController() {
        loginController = new LoginController(this);
        jsonRequestController = new JsonRequestController(this);
    }

    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView iv_menu = (ImageView) toolbar.findViewById(R.id.iv_back);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ImageView iv_search = (ImageView) toolbar.findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchPage();
            }
        });
        ll_header.bringToFront();
        hs_categories.setVisibility(View.GONE);

        String title = getIntent().getStringExtra("mTitle");
        tv_cat_name.setText(title);
        tv_location.setText(PreferenceServices.getInstance().getCurrentLocation());
        setupFragment(SUBCAT_FRAGMENT, false);
    }

    public void getSubCAtPage(String catId){
        JSONObject resultJson = loginController.getSubCatPageJson(Constant.HOMEPAGE, catId);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, Constant.SUBCAT_URL);
    }

    private void openSearchPage(){
        finish();
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        intent.putExtra(PRE_PAGE_KEY, SUBCATPAGE);
        intent.putExtra(ID_KEY, getIntent().getStringExtra("mSubCatId"));
        startActivity(intent);
    }

    @OnClick(R.id.tv_location)
    public void onTvLocationPressed() {
        Intent intent = new Intent(SubCatActivity.this, DefaultLocationActivity.class);
        startActivity(intent);
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
                switch (url){
                    case Constant.SUBCAT_URL:
                        nsv_header.setBackgroundColor(Color.BLACK);
                        ArrayList<HomeCatItems> homeCatItemses = loginController.getSubCatContent(response);
                        subCatTrapeClick.onSubTrapeClick(homeCatItemses, "");
                        break;
                    default: break;
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupFragment(int fragmentId, boolean b){
        switch (fragmentId){
            case SUBCAT_FRAGMENT:
                SubCatFragment fragment = new SubCatFragment();
                subCatTrapeClick = fragment;
                changeFragment(fragment, b);
                break;
            default:
                break;
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
    public void buttonClick(int id) {
        setupFragment(SUBCAT_FRAGMENT, true);
    }

    @Override
    public void onSubTrapeClick(ArrayList<HomeCatItems> homeCatItemses, String title) {
        Intent intent = new Intent(getApplicationContext(), StoreListingActivity.class);
        intent.putExtra("contentData", homeCatItemses);
        intent.putExtra(PRE_PAGE_KEY, SUBCATPAGE);
        intent.putExtra(ID_KEY, getIntent().getStringExtra("mSubCatId"));
        intent.putExtra("mTitle", title);
        intent.putExtra("mSubTitle", tv_cat_name.getText().toString());
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
