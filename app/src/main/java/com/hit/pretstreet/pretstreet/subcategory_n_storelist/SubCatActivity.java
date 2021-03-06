package com.hit.pretstreet.pretstreet.subcategory_n_storelist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.EmptyFragment;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.search.SearchActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.DefaultLocationActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.fragments.SubCatFragment;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.SubCatTrapeClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.CLICKTYPE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SUBCATLINK;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SUBCATPAGE;
/**
 * Shows sub category listing page
 * @author SVS
 **/
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        AppCompatImageView iv_menu = toolbar.findViewById(R.id.iv_back);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        AppCompatImageView iv_search = toolbar.findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchPage();
            }
        });
        ll_header.bringToFront();
        hs_categories.setVisibility(View.GONE);

        String title = "";
        if (getIntent() != null) {
            if (getIntent().getExtras() != null && getIntent().getExtras()
                    .containsKey("mTitle")) {
                title = getIntent().getStringExtra("mTitle");
            }
        }
        tv_cat_name.setText(title);
        tv_location.setText(PreferenceServices.getInstance().getCurrentLocation());
        setupFragment(SUBCAT_FRAGMENT, false);
    }

    public void getSubCAtPage(String catId){
        JSONObject resultJson = LoginController.getSubCatPageJson(Constant.HOMEPAGE, catId);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, Constant.SUBCAT_URL);
    }

    private void openSearchPage(){
        if (getIntent() != null) {
            if (getIntent().getExtras() != null && getIntent().getExtras()
                    .containsKey("mSubCatId")) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra(PRE_PAGE_KEY, SUBCATPAGE);
                intent.putExtra(ID_KEY, getIntent().getStringExtra("mSubCatId"));
                startActivity(intent);
            }
        }
    }

    @OnClick(R.id.tv_location)
    public void onTvLocationPressed() {
        Intent intent = new Intent(SubCatActivity.this, DefaultLocationActivity.class);
        startActivity(intent);
    }

    /**Replacing fragments
     * @param addBackstack boolean which denotes whether we have to add the previous to backstack or not
     * @param fragment fragment that should be passed*/
    private void changeFragment(Fragment fragment, boolean addBackstack) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**Handling response corresponding to the URL
     * @param response response corresponding to each URL - here I am appending the URL itself
     *                 to the response so that I will be able to handle each response seperately*/
    private void handleResponse(JSONObject response){
        try {
            String url = response.getString("URL");
            switch (url){
                case Constant.SUBCAT_URL:
                    nsv_header.setBackgroundColor(Color.BLACK);

                    try {
                        //String SavedSubCaTList = PreferenceServices.getInstance().getHomeSubCatList();
                        ArrayList<HomeCatItems> homeCatItemses = LoginController.getSubCatContent(response);
                        subCatTrapeClick.onSubTrapeClick(homeCatItemses, "");
                            /*//Saving data
                            PreferenceServices.instance().saveHomeSubCatList(response+"");*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.hideDialog();
                    break;
                default: break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**To change headername, filter button visibility and all as per the selected fragment
     * @param fragmentId id of the fragment
     * @param b shows addtobackstack boolean*/
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
        //this.hideDialog();
        handleResponse(response);
    }

    @Override
    public void onError(String error) {
        this.hideDialog();
            EmptyFragment emptyFragment = new EmptyFragment();
            Bundle bundle = new Bundle();
            bundle.putString("error", error);
            bundle.putString("retry", "1");
            bundle.putString("pageid", SUBCATPAGE);
            emptyFragment.setArguments(bundle);
            changeFragment(emptyFragment, false);
    }

    @Override
    public void buttonClick(int id) {
        setupFragment(SUBCAT_FRAGMENT, true);
    }

    @Override
    public void onSubTrapeClick(ArrayList<HomeCatItems> homeCatItemses, String title) {
        if (getIntent() != null) {
            if (getIntent().getExtras() != null && getIntent().getExtras()
                    .containsKey("mSubCatId")) {
                Intent intent = new Intent(getApplicationContext(), StoreListingActivity.class);
                intent.putExtra("contentData", homeCatItemses);
                intent.putExtra(PRE_PAGE_KEY, SUBCATPAGE);
                intent.putExtra(CLICKTYPE_KEY, SUBCATLINK);
                intent.putExtra(ID_KEY, getIntent().getStringExtra("mSubCatId"));
                intent.putExtra("mTitle", title);
                intent.putExtra("mSubTitle", tv_cat_name.getText().toString());
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    public void refreshPage(){
        setupFragment(SUBCAT_FRAGMENT, false);
    }
}
