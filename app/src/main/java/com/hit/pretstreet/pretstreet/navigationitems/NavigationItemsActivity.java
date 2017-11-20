package com.hit.pretstreet.pretstreet.navigationitems;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.ExhibitionDetailsActivity;
import com.hit.pretstreet.pretstreet.navigation.TrendingArticleActivity;
import com.hit.pretstreet.pretstreet.navigation.controllers.HomeFragmentController;
import com.hit.pretstreet.pretstreet.navigation.fragments.HomeFragment;
import com.hit.pretstreet.pretstreet.navigation.fragments.TrendingFragment;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingCallback;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.navigationitems.controllers.NavItemsController;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.AboutFragment;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.AccountFragment;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.AddStoreFragment;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.ChangePasswordFragment;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.ContactUsFragment;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.HtmlFragment;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.NotificationFragment;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.ReferEarnFragment;
import com.hit.pretstreet.pretstreet.navigationitems.interfaces.ContentBindingInterface;
import com.hit.pretstreet.pretstreet.search.MultistoreActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.LoginFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.SignupFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.*;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.CONTACTUS_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.FEEDBACK_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDING_URL;

public class NavigationItemsActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, ButtonClickCallback, LoginCallbackInterface, TrendingCallback {

    private int currentFragment = 0;
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
    private static final int NOTIFICATION_FRAGMENT = 12;
    private static final int CHANGEPASSWORD_FRAGMENT = 13;
    private static final int REFER_EARN_FRAGMENT = 14;
    private static final int ABOUTDESIGNER_FRAGMENT = 101;
    private static final int PICK_IMAGE_REQUEST = 111;
    private static final int PLACE_PICKER_REQUEST = 112;

    JsonRequestController jsonRequestController;
    NavItemsController navItemsController;
    LoginController loginController;
    TrendingCallback trendingCallback;
    ContentBindingInterface bindingInterface;
    HtmlFragment htmlFragment;

    @BindView(R.id.content) FrameLayout fl_content;
    @BindView(R.id.tv_cat_name) TextViewPret tv_cat_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_items);
        init();
    }

    @Override
    protected void setUpController() {
        navItemsController = new NavItemsController(this);
        loginController = new LoginController(this);
        jsonRequestController = new JsonRequestController(this);
    }

    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);

        View ll_header = findViewById(R.id.ll_header);
        ImageView iv_menu = (ImageView) ll_header.findViewById(R.id.iv_back);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ImageView iv_search = (ImageView) ll_header.findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);
        fl_content.bringToFront();
        ll_header.bringToFront();

        Intent intent = getIntent();
        int fragmentId = intent.getIntExtra("fragment", 0);
        setupFragment(fragmentId, false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch (currentFragment){
            case CONTACTUS_FRAGMENT:
                currentFragment = ABOUT_FRAGMENT;
                tv_cat_name.setText("About Pretstreet");
                break;
            case FEEDBACK_FRAGMENT:
                currentFragment = ABOUT_FRAGMENT;
                tv_cat_name.setText("About Pretstreet");
                break;
            case ABOUTUS_FRAGMENT:
                currentFragment = ABOUT_FRAGMENT;
                tv_cat_name.setText("About Pretstreet");
                break;
            case PRIVACY_FRAGMENT:
                currentFragment = ABOUT_FRAGMENT;
                tv_cat_name.setText("About Pretstreet");
                break;
            case TERMS_FRAGMENT:
                currentFragment = ABOUT_FRAGMENT;
                tv_cat_name.setText("About Pretstreet");
                break;
            case CHANGEPASSWORD_FRAGMENT:
                currentFragment = ACCOUNT_FRAGMENT;
                tv_cat_name.setText("MY PROFILE");
                break;
            default:
                break;
        }
    }

    public void getNotificationlist(){
        ArrayList<TrendingItems> trendingItemses = navItemsController.getNotifList();
        trendingCallback.bindData(trendingItemses);
    }

    private void delayedBackpress(String msg){
        displaySnackBar(msg);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBackPressed();
            }
        }, 3000);
    }

    private void setupFragment(int fragmentId, boolean b){
        switch (fragmentId){
            case ACCOUNT_FRAGMENT:
                currentFragment = ACCOUNT_FRAGMENT;
                AccountFragment accountFragment = new AccountFragment();
                bindingInterface = accountFragment;
                tv_cat_name.setText("MY PROFILE");
                changeFragment(accountFragment, b);
                break;
            case FOLLOWING_FRAGMENT:
                currentFragment = FOLLOWING_FRAGMENT;
                tv_cat_name.setText("Following");
                changeFragment(new ChangePasswordFragment(), b);
                break;
            case ADDSTORE_FRAGMENT:
                currentFragment = ADDSTORE_FRAGMENT;
                tv_cat_name.setText("Add Store");
                changeFragment(new AddStoreFragment(), b);
                break;
            case ABOUT_FRAGMENT:
                currentFragment = ABOUT_FRAGMENT;
                tv_cat_name.setText("About Pretstreet");
                changeFragment(new AboutFragment(), b);
                break;
            case CONTACTUS_FRAGMENT:
                currentFragment = CONTACTUS_FRAGMENT;
                tv_cat_name.setText("Contact Us");
                ContactUsFragment fragment = new ContactUsFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ID_KEY, String.valueOf(CONTACTUS_FRAGMENT));
                fragment.setArguments(bundle);
                changeFragment(fragment, b);
                break;
            case FEEDBACK_FRAGMENT:
                currentFragment = FEEDBACK_FRAGMENT;
                tv_cat_name.setText("Feedback");
                fragment = new ContactUsFragment();
                bundle = new Bundle();
                bundle.putString(ID_KEY, String.valueOf(FEEDBACK_FRAGMENT));
                fragment.setArguments(bundle);
                changeFragment(fragment, b);
                break;
            case ABOUTUS_FRAGMENT:
                currentFragment = ABOUTUS_FRAGMENT;
                tv_cat_name.setText("About Pretstreet");
                htmlFragment = new HtmlFragment();
                bundle = new Bundle();
                bundle.putString(PARCEL_KEY, "");
                bundle.putString(URL_KEY, ABOUT_URL);
                htmlFragment.setArguments(bundle);
                changeFragment(htmlFragment, b);
                break;
            case PRIVACY_FRAGMENT:
                currentFragment = PRIVACY_FRAGMENT;
                tv_cat_name.setText("Privacy Policy");
                htmlFragment = new HtmlFragment();
                bundle = new Bundle();
                bundle.putString(PARCEL_KEY, "");
                bundle.putString(URL_KEY, PRIVACYPOLICY_URL);
                htmlFragment.setArguments(bundle);
                changeFragment(htmlFragment, b);
                break;
            case TERMS_FRAGMENT:
                currentFragment = TERMS_FRAGMENT;
                tv_cat_name.setText("Terms & Conditions");
                htmlFragment = new HtmlFragment();
                bundle = new Bundle();
                bundle.putString(PARCEL_KEY, "");
                bundle.putString(URL_KEY, TC_URL);
                htmlFragment.setArguments(bundle);
                changeFragment(htmlFragment, b);
                break;
            case ABOUTDESIGNER_FRAGMENT:
                currentFragment = ABOUTDESIGNER_FRAGMENT;
                tv_cat_name.setText(getIntent().getStringExtra("NAME"));
                htmlFragment = new HtmlFragment();
                bundle = new Bundle();
                bundle.putString(PARCEL_KEY, getIntent().getStringExtra(PARCEL_KEY));
                htmlFragment.setArguments(bundle);
                changeFragment(htmlFragment, b);
                break;
            case NOTIFICATION_FRAGMENT:
                currentFragment = NOTIFICATION_FRAGMENT;
                tv_cat_name.setText("Notification");
                //vcb
                NotificationFragment notificationFragment = new NotificationFragment();
                trendingCallback = notificationFragment;
                changeFragment(notificationFragment, b);
                break;
            case CHANGEPASSWORD_FRAGMENT:
                currentFragment = CHANGEPASSWORD_FRAGMENT;
                tv_cat_name.setText("Update Password");
                changeFragment(new ChangePasswordFragment(), b);
                break;
            case REFER_EARN_FRAGMENT:
                currentFragment = REFER_EARN_FRAGMENT;
                tv_cat_name.setText("Refer & Earn");
                changeFragment(new ReferEarnFragment(), b);
                break;
            default:
                break;
        }
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
            String msg = response.getString("CustomerMessage");
            switch (url){
                case CONTACTUS_URL:
                    delayedBackpress(msg);
                    break;
                case FEEDBACK_URL:
                    delayedBackpress(msg);
                    break;
                case ADDSTORE_URL:
                    delayedBackpress(msg);
                    break;
                case UPDATE_ACCOUNT_URL:
                    navItemsController.updateSession(response);
                    delayedBackpress(msg);
                    break;
                case UPDATEPASSWORD_ACCOUNT_URL:
                    delayedBackpress(msg);
                    break;
                case ABOUT_URL:
                    String html = navItemsController.getStaticHtmlData(response);
                    htmlFragment.loadHtml(html);
                    break;
                case TC_URL:
                    html = navItemsController.getStaticHtmlData(response);
                    htmlFragment.loadHtml(html);
                    break;
                case PRIVACYPOLICY_URL:
                    html = navItemsController.getStaticHtmlData(response);
                    htmlFragment.loadHtml(html);
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
    public void buttonClick(int id) {
        setupFragment(id, true);
    }

    @Override
    public void validateCallback(EdittextPret editText, String message, int type) {
        if(type == ADDSTORE_FRAGMENT){
            AddStoreFragment addStoreFragment = new AddStoreFragment();
            addStoreFragment.onValidationError(editText, message);
        }
        else if (type == CONTACTUS_FRAGMENT){
            ContactUsFragment contactUsFragment = new ContactUsFragment();
            contactUsFragment.onValidationError(editText, message);
        }
        else if (type == ACCOUNT_FRAGMENT){
            AccountFragment accountFragment = new AccountFragment();
            accountFragment.onValidationError(editText, message);
        }
        else if (type == CHANGEPASSWORD_FRAGMENT){
            ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
            changePasswordFragment.onValidationError(editText, message);
        }
    }

    @Override
    public void validationSuccess(String phonenumber) {
        Utility.hide_keyboard(NavigationItemsActivity.this);
        JSONObject resultJson = navItemsController.getContactUsJson(null);
        this.showProgressDialog(getResources().getString(R.string.loading));
        if(currentFragment==CONTACTUS_FRAGMENT)
            jsonRequestController.sendRequest(this, resultJson, CONTACTUS_URL);
        else if(currentFragment==FEEDBACK_FRAGMENT)
            jsonRequestController.sendRequest(this, resultJson, FEEDBACK_URL);
    }

    @Override
    public void validationSuccess(LoginSession loginSession) {

    }

    @Override
    public void validationSuccess(JSONObject jsonObject, int id) {
        Utility.hide_keyboard(NavigationItemsActivity.this);
        this.showProgressDialog(getResources().getString(R.string.loading));
        if(id==ADDSTORE_FRAGMENT)
            jsonRequestController.sendRequest(this, jsonObject, ADDSTORE_URL);
        else if(id==ACCOUNT_FRAGMENT)
            jsonRequestController.sendRequest(this, jsonObject, UPDATE_ACCOUNT_URL);
        else if(id==CHANGEPASSWORD_FRAGMENT)
            jsonRequestController.sendRequest(this, jsonObject, UPDATEPASSWORD_ACCOUNT_URL);
    }

    public void getHtmlData(String URL){
        JSONObject resultJson = navItemsController.getStaticPageJson();
        showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, URL);
    }
    public void openUpdatePassword(){
        setupFragment(CHANGEPASSWORD_FRAGMENT, true);
    }

    public void chooseProfileImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                    if (resultCode == RESULT_OK) {
                        Place place = PlacePicker.getPlace(data, this);
                        AddStoreFragment addStoreFragment = new AddStoreFragment();
                        addStoreFragment.setLocation(place);
                    }
                break;
            case PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    Uri filePath = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        bindingInterface.updateImage(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default: break;
        }
    }

    @Override
    public void bindData(ArrayList<TrendingItems> trendingItems) {
        forwardDeepLink(trendingItems.get(0).getShareUrl(), trendingItems.get(0).getId(), NOTIFICATIONKEY);
    }
}
