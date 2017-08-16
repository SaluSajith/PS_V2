package com.hit.pretstreet.pretstreet.navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.DividerDecoration;
import com.hit.pretstreet.pretstreet.core.customview.NotificationBadge;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.adapters.NavDrawerAdapter;
import com.hit.pretstreet.pretstreet.navigation.fragments.HomeFragment;
import com.hit.pretstreet.pretstreet.navigation.interfaces.HomeTrapeClick;
import com.hit.pretstreet.pretstreet.navigation.interfaces.NavigationClick;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.navigation.models.NavDrawerItem;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.search.MultistoreActivity;
import com.hit.pretstreet.pretstreet.search.SearchActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.DefaultLocationActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.StoreListingActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.SubCatActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;

public class HomeActivity extends AbstractBaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NavigationClick, ApiListenerInterface, HomeTrapeClick {

    //TODO put same in NavigationItemsActivity as well
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

    @BindView(R.id.tv_location) TextViewPret tv_location;
    NavDrawerAdapter navDrawerAdapter;
    JsonRequestController jsonRequestController;
    LoginController loginController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
        loginController = new LoginController(null, this);
    }

    private void init() {
        PreferenceServices.init(this);

        View includedlayout =  findViewById(R.id.includedlayout);
        //includedlayout.bringToFront();
        ButterKnife.bind(this, includedlayout);

        tv_location.setText(PreferenceServices.getInstance().getCurrentLocation());
        setupDrawer(includedlayout);
        String SavedMAinCaTList = PreferenceServices.getInstance().getHomeMainCatList();
        /*if (SavedMAinCaTList.length() > 1)
            changeFragment(new HomeFragment(), false);
        else*/
            getHomePage();
    }

    private void getHomePage(){
        JSONObject resultJson = LoginController.getHomePageJson(Constant.HOMEPAGE);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, Constant.HOMEPAGE_URL);
    }

    private void setupDrawer(View toolbar){
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        NotificationBadge mBadge = (NotificationBadge) navigationView.findViewById(R.id.badge);
        mBadge.setNumber(10);
        mBadge.bringToFront();

        NavDrawerItem[] navArray = new NavDrawerItem[]{
                new NavDrawerItem("nav_home", "Home"),
                new NavDrawerItem("nav_account", "Account"),
                new NavDrawerItem("nav_following", "Following"),
                new NavDrawerItem("nav_addstore", "Add Store"),
                new NavDrawerItem("nav_about", "About Pretstreet"),
                new NavDrawerItem("nav_contact", "Contact Us/Support"),
                new NavDrawerItem("storedetails", "Store details")};

        navDrawerAdapter = new NavDrawerAdapter(HomeActivity.this, navArray, HomeActivity.this);
        RecyclerView rv_nav = (RecyclerView) findViewById(R.id.rv_nav);
        Utility.setListLayoutManager(rv_nav, HomeActivity.this);
        rv_nav.addItemDecoration(new DividerDecoration(getApplicationContext(),
                ContextCompat.getColor(getApplicationContext(), R.color.trending_grey), 0.5f));
        rv_nav.setNestedScrollingEnabled(false);
        rv_nav.getItemAnimator().setChangeDuration(0);
        rv_nav.setAdapter(navDrawerAdapter);

        ImageView iv_menu = (ImageView) toolbar.findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isHovered())
                    drawer.openDrawer(Gravity.LEFT);
            }
        });
        ImageView iv_logo = (ImageView) toolbar.findViewById(R.id.iv_logo);
        iv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isHovered())
                    drawer.openDrawer(Gravity.LEFT);
            }
        });
        ImageView iv_search = (ImageView) toolbar.findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                startActivity(intent);
            }
        });

        NotificationBadge badge_home = (NotificationBadge)toolbar.findViewById(R.id.badge);
        badge_home.setNumber(10);
        badge_home.bringToFront();

        TextViewPret tv_profile = (TextViewPret) navigationView.findViewById(R.id.tv_profile);
        TextViewPret tv_rateus = (TextViewPret) drawer.findViewById(R.id.tv_rateus);
        tv_rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateUs();
            }
        });

        if (PreferenceServices.getInstance().geUsertName().equalsIgnoreCase("")) {
            //TODO : get Username Api
            tv_profile.setText("");
        }
        else{
            tv_profile.setText(PreferenceServices.getInstance().geUsertName());
        }
    }

    private void rateUs(){
        final String appPackageName = getApplicationContext().getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void menuOnClick(String id) {
        String itemId = id;
        Toast.makeText(HomeActivity.this, id, Toast.LENGTH_SHORT).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        switch (itemId) {
            case "nav_home":
                String SavedMAinCaTList = PreferenceServices.getInstance().getHomeMainCatList();
                if (SavedMAinCaTList.length() > 1)
                    changeFragment(new HomeFragment(), false);
                else getHomePage();
                break;
            case "nav_account":
                selectedFragment = ACCOUNT_FRAGMENT;
                Intent intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "nav_following":
                selectedFragment = FOLLOWING_FRAGMENT;
                intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "nav_addstore":
                selectedFragment = ADDSTORE_FRAGMENT;
                intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "nav_about":
                selectedFragment = ABOUT_FRAGMENT;
                intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "nav_contact":
                selectedFragment = CONTACTUS_FRAGMENT;
                intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "storedetails":
                intent = new Intent(HomeActivity.this, MultistoreActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                startActivity(intent);
                break;
            /*case "storedetails":
                selectedFragment = TRENDING_FRAGMENT;
                intent = new Intent(HomeActivity.this, HomeInnerActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;*/
            default:
                break;
        }
    }

    @OnClick(R.id.tv_location)
    public void onTvLocationPressed() {
        Intent intent = new Intent(HomeActivity.this, DefaultLocationActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.iv_location)
    public void onLocationPressed() {
        Intent intent = new Intent(HomeActivity.this, DefaultLocationActivity.class);
        startActivity(intent);
    }

    private void changeFragment(Fragment fragment, boolean addBackstack) {

        FragmentManager fm = getSupportFragmentManager();       /*Removing stack*/
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
        FrameLayout fl_content = (FrameLayout) findViewById(R.id.content);
        fl_content.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); /* Fragment transition*/
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.add(R.id.content, fragment);
        if (addBackstack) {
            ft.addToBackStack(null);
        }
        ft.commit();
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


    private void handleResponse(JSONObject response){
        String strsuccess = null;
        try {
            String url = response.getString("URL");
            strsuccess = response.getString("Status");
            if (strsuccess.equals("1")) {
                //displaySnackBar(response.getString("CustomerMessage"));
                switch (url){
                    case Constant.HOMEPAGE_URL:
                        PreferenceServices.instance().saveHomeMainCatList(response.toString());
                        changeFragment(new HomeFragment(), false);
                        break;
                    case Constant.SUBCAT_URL:

                        break;
                    default: break;
                }
            } else {
                displaySnackBar(response.getString("Message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrapeClick(HomeCatContentData catContentData, String title) {
        String pageid = catContentData.getPageTypeId();
        switch (pageid){
            case Constant.SUBCATPAGE:
                //displaySnackBar(homeCatItems.getHomeContentData().getCategoryName());
                Intent intent = new Intent(this, SubCatActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra("mSubCatId", catContentData.getCategoryId());
                intent.putExtra("mTitle", title);
                startActivity(intent);
                break;
            case Constant.STORELISTINGPAGE:
                intent = new Intent(getApplicationContext(), StoreListingActivity.class);
                intent.putExtra("contentData", catContentData.getHomeSubCategoryArrayList());
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra("mCatId", catContentData.getCategoryId());
                intent.putExtra("mSubTitle", title);
                startActivity(intent);
                break;
            case Constant.TRENDINGPAGE:
                selectedFragment = TRENDING_FRAGMENT;
                intent = new Intent(HomeActivity.this, HomeInnerActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case Constant.EXHIBITIONPAGE:
                selectedFragment = EXHIBITION_FRAGMENT;
                intent = new Intent(HomeActivity.this, HomeInnerActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case Constant.MULTISTOREPAGE:
                intent = new Intent(HomeActivity.this, MultistoreActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                startActivity(intent);
                break;
            case Constant.STOREDETAILSPAGE:
                StoreListModel storeListModel =  new StoreListModel();
                storeListModel.setId(catContentData.getMainCatId());
                intent = new Intent(HomeActivity.this, StoreDetailsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                startActivity(intent);
                break;
            default: break;
        }

    }
}