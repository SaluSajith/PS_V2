package com.hit.pretstreet.pretstreet.core.views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hit.pretstreet.pretstreet.BuildConfig;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.PageState;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.PermissionResult;
import com.hit.pretstreet.pretstreet.navigation.ExhibitionDetailsActivity;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.navigation.TrendingArticleActivity;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.search.MultistoreActivity;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import java.util.ArrayList;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.ADDSTORE_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ARTICLEPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.CLICKTYPE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXARTICLEPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITIONPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITION_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.GIVEAWAYPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.GIVEAWAY_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.HOMEPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.MULTISTOREPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SIGNUPPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.STOREDETAILSPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDINGPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDING_FRAGMENT;

/**
 * Created by User on 7/5/2017.
 * Parent Activity of all the activities
 */

public abstract class AbstractBaseAppCompatActivity extends AppCompatActivity {

    protected PageState pageState;
    private ProgressDialog pDialog;

    private int KEY_PERMISSION = 0;
    private PermissionResult permissionResult;
    private String permissionsAsk[];
    private int selectedFragment = 0;
    private static final int STORE_DETAILS = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpPageState();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        PreferenceServices.init(this);
        setUpController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG){ }
        else {
            /**  Sending Activity class details as events to google - Google Analysis*/
            PretStreet pretStreet = (PretStreet) getApplication();
            Tracker mTracker = pretStreet.tracker();
            mTracker.set("UserTrack", this.getClass().getSimpleName());
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Fragment")
                    .setAction(mTracker.get("UserTrack"))
                    .setLabel(mTracker.get("UserTrack"))
                    .build());
        }
    }
/*
    @Override
    protected void onStop() {
        super.onStop();
        if (BuildConfig.DEBUG) { }
        else {
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        }
    }*/

    private void setUpPageState() {
        this.pageState = new PageState(this);
        this.pageState.setOnPageStateListner(new PageState.PageStateListener() {
            @Override
            public void onErrorIconClicked() {
                onErrorRefresh();
            }
        });
    }

    public void onErrorRefresh() {
    }

    public void displaySnackBar(String message) {
        Utility.hide_keyboard(this);
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public void displaySnackBar(@StringRes int message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public void displayLogMessage(String title, String message) {
        String key;
        if(title.trim().length()==0){
            key = "DEBUG";
        }else{
            key = title;
        }
        Log.d(key, message);
    }

    /** Share button onclick event
     * @param text text to be shared*/
    public void shareUrl(String text) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "PrêtStreet : Your ultimate shopping guide!!!");
        share.putExtra(Intent.EXTRA_TEXT, "\n" +
                text);
        startActivity(Intent.createChooser(share, "Share with.."));
    }

    public void logError(String error) {
        Log.e("ERROR", error);
    }

    public void displayDialog(String title, String message, boolean isCancellable,
                              @StringRes int positiveText, DialogInterface.OnClickListener positiveTextClickListener,
                              @StringRes int cancelText, DialogInterface.OnClickListener cancelTextClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title).setCancelable(isCancellable);

        if (positiveText != 0 || positiveTextClickListener != null) {
            builder.setPositiveButton(positiveText, positiveTextClickListener);
        }

        if (cancelText != 0 || cancelTextClickListener != null) {
            builder.setNegativeButton(cancelText, cancelTextClickListener);
        }

        builder.create().show();
    }

    @SuppressLint("WrongConstant")
    public void startActivityAndFinish(Class<?> mActivity) {
        try {
            Intent intent = new Intent(this, mActivity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void permissionGrantSuccess(int permissionCode) {

    }

    public boolean isPermissionGranted(String permission) {
        int result = ContextCompat.checkSelfPermission(this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) return true;
        return false;
    }

    public void requestPermission(String requestPermission, int permissionCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, requestPermission)) {
            displaySnackBar("Required permission access");
        }
        ActivityCompat.requestPermissions(this, new String[]{requestPermission}, permissionCode);
    }

    public void showProgressDialog(String mContent) {
        if (!pDialog.isShowing()) {
            pDialog.show();
            pDialog.setContentView(R.layout.loading_view);
            //pDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideDialog();
    }

    @Override
    public void onBackPressed() {
        if (pageState.isLoading()) return;
        super.onBackPressed();
    }
    protected abstract void setUpController();



    /**
     * Does some thing in old style.
     *
     * @deprecated use {@link #askCompactPermission(String, PermissionResult)}  instead.
     */
    @Deprecated
    public AbstractBaseAppCompatActivity askPermission(String permission) {
        this.permissionsAsk = new String[]{permission};
        return AbstractBaseAppCompatActivity.this;
    }

    /**
     * Does some thing in old style.
     *
     * @deprecated use {@link #askCompactPermissions(String[], PermissionResult)} instead.
     */
    @Deprecated
    public AbstractBaseAppCompatActivity askPermissions(String permissions[]) {
        this.permissionsAsk = permissions;
        return AbstractBaseAppCompatActivity.this;
    }

    public AbstractBaseAppCompatActivity setPermissionResult(PermissionResult permissionResult) {
        this.permissionResult = permissionResult;
        return AbstractBaseAppCompatActivity.this;
    }

    public AbstractBaseAppCompatActivity requestPermission(int keyPermission) {
        KEY_PERMISSION = keyPermission;
        internalRequestPermission(permissionsAsk);
        return AbstractBaseAppCompatActivity.this;
    }


    public void askCompactPermission(String permission, PermissionResult permissionResult) {
        KEY_PERMISSION = 200;
        permissionsAsk = new String[]{permission};
        this.permissionResult = permissionResult;
        internalRequestPermission(permissionsAsk);
    }

    public void askCompactPermissions(String permissions[], PermissionResult permissionResult) {
        KEY_PERMISSION = 200;
        permissionsAsk = permissions;
        this.permissionResult = permissionResult;
        internalRequestPermission(permissionsAsk);
    }

    public boolean isPermissionGranted(Context context, String permission) {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) ||
                (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }

    private void internalRequestPermission(String[] permissionAsk) {
        String arrayPermissionNotGranted[];
        ArrayList<String> permissionsNotGranted = new ArrayList<>();

        for (int i = 0; i < permissionAsk.length; i++) {
            if (!isPermissionGranted(AbstractBaseAppCompatActivity.this, permissionAsk[i])) {
                permissionsNotGranted.add(permissionAsk[i]);
            }
        }

        if (permissionsNotGranted.isEmpty()) {

            if (permissionResult != null)
                permissionResult.permissionGranted();

        } else {

            arrayPermissionNotGranted = new String[permissionsNotGranted.size()];
            arrayPermissionNotGranted = permissionsNotGranted.toArray(arrayPermissionNotGranted);
            ActivityCompat.requestPermissions(AbstractBaseAppCompatActivity.this, arrayPermissionNotGranted, KEY_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == KEY_PERMISSION) {
            boolean granted = true;

            for (int grantResult : grantResults) {
                if (!(grantResults.length > 0 && grantResult == PackageManager.PERMISSION_GRANTED))
                    granted = false;
            }
            if (permissionResult != null) {
                if (granted) {
                    permissionResult.permissionGranted();
                } else {
                    permissionResult.permissionDenied();
                }
            }
        } else {
            Log.e("ActivityManagePermision", "permissionResult callback was null");
        }
    }

/** Open next activity from data recieved from notifications and referral links
 *  @param valueOne shareparameter which denotes the page to be opened
 *  @param id ID of store/multistore/article/ whatever it is
 *  @param clicktypeId Id to pass to server for logging(can be referral id or notification id*/
    public void forwardDeepLink(String valueOne, String id, String clicktypeId){
        Intent intent;
        switch (valueOne){  //TODO nullpointer excp
            case "store":
                if(id.equals("0")){
                    return;
                }
                StoreListModel storeListModel =  new StoreListModel();
                storeListModel.setId(id);
                intent = new Intent(this, StoreDetailsActivity.class);
                intent.putExtra(PARCEL_KEY, storeListModel);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                startActivity(intent);
                break;
            case "giveaway":
                if(id.equals("0")){
                    intent = new Intent(this, HomeInnerActivity.class);
                    intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
                    intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                    intent.putExtra("fragment", GIVEAWAY_FRAGMENT);
                    startActivity(intent);
                }
                else {
                    TrendingItems trendingItems = new TrendingItems();
                    trendingItems.setId(id);
                    trendingItems.setPagetypeid("");
                    trendingItems.setClicktype("");
                    trendingItems.setPagetype(GIVEAWAYPAGE);
                    intent = new Intent(this, TrendingArticleActivity.class);
                    intent.putExtra(PRE_PAGE_KEY, "");
                    intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                    intent.putExtra(PARCEL_KEY, trendingItems);
                    startActivity(intent);
                }
                break;
            case "trending":
                if(id.equals("0")){
                    intent = new Intent(this, HomeInnerActivity.class);
                    intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
                    intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                    intent.putExtra("fragment", TRENDING_FRAGMENT);
                    startActivity(intent);
                }
                else {
                    TrendingItems trendingItems = new TrendingItems();
                    trendingItems.setId(id);
                    trendingItems.setPagetypeid("");
                    trendingItems.setClicktype("");
                    trendingItems.setPagetype(TRENDINGPAGE);
                    intent = new Intent(this, TrendingArticleActivity.class);
                    intent.putExtra(PRE_PAGE_KEY, "");
                    intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                    intent.putExtra(PARCEL_KEY, trendingItems);
                    startActivity(intent);
                }
                break;
            case "exhibition":
                if(id.equals("0")){
                    intent = new Intent(this, HomeInnerActivity.class);
                    intent.putExtra(PRE_PAGE_KEY, HOMEPAGE);
                    intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                    intent.putExtra("fragment", EXHIBITION_FRAGMENT);
                    startActivity(intent);
                }
                else {
                    TrendingItems trendingItems = new TrendingItems();
                    trendingItems.setId(id);
                    trendingItems.setPagetypeid("");
                    trendingItems.setClicktype("");
                    intent = new Intent(this, ExhibitionDetailsActivity.class);
                    intent.putExtra(PARCEL_KEY, trendingItems);
                    intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                    intent.putExtra(Constant.PRE_PAGE_KEY, EXHIBITIONPAGE);
                    startActivity(intent);
                }
                break;
            case "multistore":
                if(id.equals("0")){
                    return;
                }
                intent = new Intent(this, MultistoreActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                intent.putExtra(ID_KEY, id);
                startActivity(intent);
                break;
            case "addstore":
                intent = new Intent(this, NavigationItemsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, SIGNUPPAGE);
                intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                intent.putExtra("fragment", ADDSTORE_FRAGMENT);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**Open next activity - get dynamic links from listing activities
     * @param storeListModel - Model having id and other essentials
     * @param prepagekey - key which denotes the activity from which the function is called
     * @param clicktypeId - clicktype*/
    public void openNext(StoreListModel storeListModel, String prepagekey, String clicktypeId){
        switch (storeListModel.getPageTypeId()){
            case STOREDETAILSPAGE:
                Intent intent = new Intent(this, StoreDetailsActivity.class);
                intent.putExtra(PARCEL_KEY, storeListModel);
                intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                intent.putExtra(PRE_PAGE_KEY, prepagekey);
                startActivityForResult(intent, STORE_DETAILS);
                break;
            case TRENDINGPAGE:
                selectedFragment = TRENDING_FRAGMENT;
                intent = new Intent(this, HomeInnerActivity.class);
                intent.putExtra(PRE_PAGE_KEY, prepagekey);
                intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case EXHIBITIONPAGE:
                selectedFragment = EXHIBITION_FRAGMENT;
                intent = new Intent(this, HomeInnerActivity.class);
                intent.putExtra(PRE_PAGE_KEY, prepagekey);
                intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case MULTISTOREPAGE:
                intent = new Intent(this, MultistoreActivity.class);
                intent.putExtra(PRE_PAGE_KEY,prepagekey);
                intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                intent.putExtra(ID_KEY, storeListModel.getId());
                startActivity(intent);
                break;
            case ARTICLEPAGE:
                TrendingItems trendingItems = new TrendingItems();
                trendingItems.setId(storeListModel.getId());
                trendingItems.setPagetypeid(storeListModel.getPageTypeId());
                trendingItems.setClicktype(clicktypeId);
                intent = new Intent(this, TrendingArticleActivity.class);
                intent.putExtra(Constant.PARCEL_KEY, trendingItems);
                intent.putExtra(Constant.PRE_PAGE_KEY, prepagekey);
                startActivity(intent);
                break;
            case EXARTICLEPAGE:
                trendingItems = new TrendingItems();
                trendingItems.setId(storeListModel.getId());
                trendingItems.setPagetypeid(storeListModel.getPageTypeId());
                trendingItems.setClicktype(clicktypeId);
                intent = new Intent(this, ExhibitionDetailsActivity.class);
                intent.putExtra(Constant.PARCEL_KEY, trendingItems);
                intent.putExtra(Constant.PRE_PAGE_KEY, prepagekey);
                startActivity(intent);
                break;
            case GIVEAWAYPAGE:
                selectedFragment = GIVEAWAY_FRAGMENT;
                intent = new Intent(this, HomeInnerActivity.class);
                intent.putExtra(PRE_PAGE_KEY, prepagekey);
                intent.putExtra(CLICKTYPE_KEY, clicktypeId);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}

