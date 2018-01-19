package com.hit.pretstreet.pretstreet.navigation;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.customview.EmptyFragment;
import com.hit.pretstreet.pretstreet.core.customview.NotificationBadge;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.customview.showCaseView.Showcase;
import com.hit.pretstreet.pretstreet.core.helpers.LocationTracker;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.adapters.NavDrawerExpandableAdapter;
import com.hit.pretstreet.pretstreet.navigation.fragments.HomeFragment;
import com.hit.pretstreet.pretstreet.navigation.interfaces.HomeTrapeClick;
import com.hit.pretstreet.pretstreet.navigation.interfaces.NavigationClick;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.navigationitems.FollowingActivity;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.AboutFragment;
import com.hit.pretstreet.pretstreet.search.SearchActivity;
import com.hit.pretstreet.pretstreet.search.models.BasicModel;
import com.hit.pretstreet.pretstreet.splashnlogin.DefaultLocationActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LocCallbackInterface;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.StoreListingActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.SubCatActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.TwoLevelDataModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.ABOUT_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ACCOUNT_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.CLICKTYPE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.CONTACTUS_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.HOMEPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.HOMEPAGELINK;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.HOMEPAGE_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.NOTIFICATION_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.REFER_EARN_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.REQUEST_INVITE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.STORELISTINGPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SUBCATPAGE;

public class HomeActivity extends AbstractBaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NavigationClick, ApiListenerInterface, HomeTrapeClick, ButtonClickCallback, LocCallbackInterface {

    private int selectedFragment = 0;
    boolean doubleBackToExitPressedOnce = false;

    @BindView(R.id.tv_location) TextViewPret tv_location;
    TextViewPret tv_profile;
    JsonRequestController jsonRequestController;
    LoginController loginController;
    LocationTracker locationTracker;
    private BroadcastReceiver receiver;

    boolean homeopened = false;
    Context context;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (!homeopened) {
                getHomePage();
            }
            int size = PreferenceServices.getInstance().getNotifCOunt();
            tv_profile.setText(PreferenceServices.getInstance().geUsertName());
            updateNotificationFlag(size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        context = getApplicationContext();
        locationTracker = new LocationTracker(HomeActivity.this);
        locationTracker.checkLocationSettings();
        View includedlayout = findViewById(R.id.includedlayout);
        ButterKnife.bind(this, includedlayout);
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openVideo();
            }
        }, 1000);
*/
        tv_location.setText(PreferenceServices.getInstance().getCurrentLocation());
        setupDrawer(includedlayout);
        if (PreferenceServices.getInstance().getShareQueryparam().trim().length()!=0 &&
                PreferenceServices.getInstance().getIdQueryparam().trim().length()!=0) {
            String valueOne = PreferenceServices.getInstance().getShareQueryparam();
            String id = PreferenceServices.getInstance().getIdQueryparam();
            String type = PreferenceServices.getInstance().getTypeQueryparam();
            Log.d("FCM_home", "Received xx" + valueOne + id + type);
            forwardDeepLink(valueOne, id, type);
            PreferenceServices.getInstance().setIdQueryparam("");
            PreferenceServices.getInstance().setShareQueryparam("");
            PreferenceServices.getInstance().setTypeQueryparam("");
            return;
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("RECEIVE_NOTIFICATION");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int size = PreferenceServices.getInstance().getNotifCOunt();
                updateNotificationFlag(size);
            }
        };
        registerReceiver(receiver, filter);

        //displayTuto();
    }

    private void getHomePage(){
        /*String SavedMAinCaTList = PreferenceServices.getInstance().getHomeMainCatList();
        if (SavedMAinCaTList.length() > 1)
            changeFragment(new HomeFragment(), false);
        else*/
        JSONObject resultJson = LoginController.getHomePageJson();
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, HOMEPAGE_URL);
    }

    private void setDrawerAdapter(String SavedMAinCaTList){
        ArrayList<BasicModel> list = LoginController.getNavList(SavedMAinCaTList);

        TwoLevelDataModel[] twoLevelDataModels = new TwoLevelDataModel[]{
                new TwoLevelDataModel("nav_home", "Home", new ArrayList<BasicModel>()),
                new TwoLevelDataModel("nav_categories", "Categories", list),
                new TwoLevelDataModel("nav_following", "Following", new ArrayList<BasicModel>()),
                new TwoLevelDataModel("nav_account", "Account", new ArrayList<BasicModel>()),
                new TwoLevelDataModel("nav_addstore", "Add Store", new ArrayList<BasicModel>()),
                //new NavDrawerItem("nav_invite", "Refer & Earn"),
                new TwoLevelDataModel("nav_about", "About Pretstreet", new ArrayList<BasicModel>()),
                new TwoLevelDataModel("nav_contact", "Contact Us/Support", new ArrayList<BasicModel>())};

        ExpandableListView elv_nav =  findViewById(R.id.elv_nav);
        NavDrawerExpandableAdapter navDrawerExpandableAdapter = new NavDrawerExpandableAdapter
                (elv_nav, HomeActivity.this, twoLevelDataModels, HomeActivity.this);
        elv_nav.setAdapter(navDrawerExpandableAdapter);

    }

    private void setupDrawer(View toolbar){
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String SavedMAinCaTList = PreferenceServices.getInstance().getHomeMainCatList();
        if(SavedMAinCaTList.length()>0)
            setDrawerAdapter(SavedMAinCaTList);

        AppCompatImageView iv_menu =  toolbar.findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isHovered())
                    drawer.openDrawer(Gravity.LEFT);
            }
        });
        AppCompatImageView iv_logo =  toolbar.findViewById(R.id.iv_logo);
        iv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isHovered())
                    drawer.openDrawer(Gravity.LEFT);
            }
        });
        AppCompatImageView iv_search =  toolbar.findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
                intent.putExtra(ID_KEY, "0");
                startActivity(intent);
            }
        });

        tv_profile =  navigationView.findViewById(R.id.tv_profile);
        final AppCompatImageView iv_profile = navigationView.findViewById(R.id.iv_profile);
        TextViewPret tv_rateus = drawer.findViewById(R.id.tv_rateus);
        tv_rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateUs();
            }
        });
        AppCompatImageView iv_notif = drawer.findViewById(R.id.iv_notification);
        iv_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOnClick(NOTIFICATION_FRAGMENT+"");
            }
        });
        AppCompatImageView iv_noti = toolbar.findViewById(R.id.iv_notif);
        iv_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOnClick(NOTIFICATION_FRAGMENT+"");
            }
        });

        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        LoginSession loginSession = sharedPreferencesHelper.getUserDetails();
        if (PreferenceServices.getInstance().geUsertName().equalsIgnoreCase("")) {
            try {
                AboutFragment aboutFragment = new AboutFragment();
                aboutFragment.onLogoutPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            tv_profile.setText(PreferenceServices.getInstance().geUsertName());
            Glide.with(context).load(loginSession.getProfile_pic()).asBitmap()
                    .placeholder(R.drawable.profilepic)
                    .centerCrop().into(new BitmapImageViewTarget(iv_profile) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    iv_profile.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
    }

    private void updateNotificationFlag(int size){
        try {
            View includedlayout = findViewById(R.id.includedlayout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            NotificationBadge badge_home = includedlayout.findViewById(R.id.badge);
            NotificationBadge mBadge = navigationView.findViewById(R.id.badge);
            badge_home.setNumber(size);
            //mBadge.setNumber(size);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rateUs(){
        final String appPackageName = context.getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            displaySnackBar("Click again to exit");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void menuOnClick(String id) {
        String itemId = id;
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
                intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "nav_addstore":
                selectedFragment = Constant.ADDSTORE_FRAGMENT;
                intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "nav_about":
                selectedFragment = ABOUT_FRAGMENT;
                intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "nav_contact":
                selectedFragment = CONTACTUS_FRAGMENT;
                intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case NOTIFICATION_FRAGMENT+"":
                selectedFragment = NOTIFICATION_FRAGMENT;
                intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "nav_following":
                intent = new Intent(HomeActivity.this, FollowingActivity.class);
                intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
                intent.putExtra("mSubTitle", "Following");
                startActivity(intent);
                break;
            case "nav_invite":
                selectedFragment = REFER_EARN_FRAGMENT;
                intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                /*intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                        .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                        .setCallToActionText(getString(R.string.invitation_cta))
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);*/
                break;
            default:
                break;
        }
    }

    private void forceUpdate(String serverVersion){
        //Force Update Option
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int curVersion = info.versionCode;
        int srvrVersion = Integer.parseInt(serverVersion);
        if(curVersion < srvrVersion) {
            showUpdateScreem();
        }
    }

    public void showUpdateScreem() {
        final Dialog popupDialog = new Dialog(HomeActivity.this);
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.popup_update, null);
        ButtonPret btn_send = view.findViewById(R.id.btn_send);

        popupDialog.setCanceledOnTouchOutside(false);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RelativeLayout rl = view.findViewById(R.id.popup_bundle);
        rl.setPadding(0, 0, 0, 0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        rl.setLayoutParams(lp);
        popupDialog.setContentView(view);
        popupDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = popupDialog.getWindow().getAttributes();
        popupDialog.getWindow().setAttributes(params);
        popupDialog.setCancelable(false);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        popupDialog.show();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                            ("market://details?id=com.hit.pretstreet.pretstreet")));
                    popupDialog.dismiss();
                } catch (Exception e){}
            }
        });

    }

    @OnClick(R.id.tv_location)
    public void onTvLocationPressed() {
        //finish();
        Intent intent = new Intent(HomeActivity.this, DefaultLocationActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.iv_location)
    public void onLocationPressed() {
        //finish();
        Intent intent = new Intent(HomeActivity.this, DefaultLocationActivity.class);
        startActivity(intent);
    }

    private void changeFragment(Fragment fragment, boolean addBackstack) {
        try {
            FragmentManager fm = getSupportFragmentManager();       /*Removing stack*/
            for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                fm.popBackStack();
            }
            FrameLayout fl_content = findViewById(R.id.content);
            fl_content.removeAllViews();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); /* Fragment transition*/
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            ft.add(R.id.content, fragment);
            if (addBackstack) {
                ft.addToBackStack(null);
            }
            ft.commit();
            homeopened = true;
        } catch (Exception e){ homeopened = false;}
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
        bundle.putString("retry", "1");
        bundle.putString("pageid", HOMEPAGE);
        emptyFragment.setArguments(bundle);
        changeFragment(emptyFragment, false);
        //displaySnackBar(error);
    }

    private void handleResponse(JSONObject response){
        try {
            String url = response.getString("URL");
            switch (url){
                case HOMEPAGE_URL:
                    String SavedMAinCaTList = PreferenceServices.getInstance().getHomeMainCatList();
                    if(SavedMAinCaTList.length()==0){
                        PreferenceServices.instance().saveHomeMainCatList(response.toString());
                        SavedMAinCaTList = PreferenceServices.getInstance().getHomeMainCatList();
                        setDrawerAdapter(SavedMAinCaTList);
                    }
                    else
                        PreferenceServices.instance().saveHomeMainCatList(response.toString());
                    forceUpdate(response.getString("AndroidVersion"));
                    changeFragment(new HomeFragment(), false);
                    break;
                default: break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrapeClick(HomeCatContentData catContentData, String title) {
        String pageid = catContentData.getPageTypeId();
        switch (pageid){
            case SUBCATPAGE:
                Intent intent = new Intent(this, SubCatActivity.class);
                intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
                intent.putExtra("mSubCatId", catContentData.getCategoryId());
                intent.putExtra("mTitle", title);
                startActivity(intent);
                break;
            case STORELISTINGPAGE:
                ArrayList<HomeCatItems> homeCatItemses = new ArrayList<>();
                intent = new Intent(context, StoreListingActivity.class);
                intent.putExtra("contentData",  homeCatItemses);//TODO
                intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
                intent.putExtra(CLICKTYPE_KEY, HOMEPAGELINK);
                intent.putExtra("mCatId", catContentData.getCategoryId());
                intent.putExtra("mSubTitle", title);
                startActivity(intent);
                break;
            default:
                StoreListModel storeListModel =  new StoreListModel();
                storeListModel.setId(catContentData.getMainCatId());
                storeListModel.setPageTypeId(pageid);
                openNext(storeListModel, HOMEPAGE, HOMEPAGELINK);
                break;
        }
    }

    @Override
    public void buttonClick(int id) {
        Intent intent = new Intent(context, StoreListingActivity.class);
        intent.putExtra("contentData", "");
        intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
        intent.putExtra(CLICKTYPE_KEY, HOMEPAGELINK);
        intent.putExtra("mCatId", id);
        intent.putExtra("mSubTitle", "");
        startActivity(intent);
    }

    public void refreshPage(){
        getHomePage();
    }

    public void checkforLocationChange(final double lat2, final double lng2){

        if (locationTracker.canGetLocation()) {
            if(PreferenceServices.getInstance().isAutoDetect())
                if (isLocationChanged(lat2, lng2)) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                    alertDialog.setTitle("Location Changed!");
                    alertDialog.setMessage("It is detected that your location has been changed!! Do you want to switch?");

                    alertDialog.setPositiveButton("Switch location", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getLocation(lat2, lng2);
                        }
                    });
                    alertDialog.setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
        } else ;
    }

    boolean isLocationChanged(double lat2, double lng2){
        boolean locationChanged = false;
        try {
            if (locationTracker.canGetLocation()) {

                double lat1 = Double.parseDouble(PreferenceServices.instance().getLatitute());
                double lng1 = Double.parseDouble(PreferenceServices.instance().getLongitute());
                if(lat2!=0 && lng2!=0) {
                    locationChanged = LocationTracker.distance(lat1, lng1, lat2, lng2) > 10;
                }
            }
            else ;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return locationChanged;
    }

    public void getLocation(double lat2, double lng2) {

        displaySnackBar("Please wait while fetching your location..");
        double lat1 = lat2;
        double long1 = lng2;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> list;
        try {
            list = geocoder.getFromLocation(lat1, long1, 2);
            if (list.isEmpty()) {
                displaySnackBar("Oops! Something went wrong.");
            } else {
                Address location = list.get(1);
                String currentLocation = location.getSubLocality();
                displaySnackBar( "Location set to " + currentLocation);
                try{
                    String locality = location.getLocality();
                    if(!locality.equalsIgnoreCase("null")) {
                        if (currentLocation.equals(locality))
                            PreferenceServices.instance().saveCurrentLocation(currentLocation);
                        else
                            PreferenceServices.instance().saveCurrentLocation(currentLocation + ", " + locality);
                    }
                }catch (Exception e){
                    PreferenceServices.instance().saveCurrentLocation(currentLocation);
                    e.printStackTrace();
                }
                PreferenceServices.instance().saveLatitute(lat1 + "");
                PreferenceServices.instance().saveLongitute(long1 + "");
                tv_location.setText(PreferenceServices.getInstance().getCurrentLocation());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Constant.TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(Constant.TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // [START_EXCLUDE]
                displaySnackBar("send_failed");
                // [END_EXCLUDE]
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    protected  void openVideo(){
        Intent intent = new Intent(this, VideoActivity.class);
        startActivity(intent);
    }

    protected void displayTuto() {
        Showcase.from(this)
                .setListener(new Showcase.Listener() {
                    @Override
                    public void onDismissed() {
                        //displaynext();
                        //Toast.makeText(getApplicationContext(), "Tutorial dismissed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setContentView(R.layout.tuto_showcase_tuto_sample)
                .setFitsSystemWindows(true)
                .on(R.id.tv_location)
                .addCircle()
                .withBorder()

                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })

                /*.on(R.id.swipable)
                .displaySwipableLeft()
                .delayed(399)
                .animated(true)*/
                .show();
    }

    protected void displaynext() {
        Showcase.from(this)
                .setListener(new Showcase.Listener() {
                    @Override
                    public void onDismissed() {
                        //displaySnackBar("Choose");
                        //Toast.makeText(getApplicationContext(), "Tutorial dismissed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setContentView(R.layout.tuto_showcase_tuto_sample)
                .setFitsSystemWindows(true)
                .on(R.id.tv_location)
                .addCircle()
                .withBorder()

                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })

                /*.on(R.id.swipable)
                .displaySwipableLeft()
                .delayed(399)
                .animated(true)*/
                .show();
    }

    @Override
    public void setLoc(Location location) {
        checkforLocationChange(location.getLatitude(), location.getLongitude());
    }
}