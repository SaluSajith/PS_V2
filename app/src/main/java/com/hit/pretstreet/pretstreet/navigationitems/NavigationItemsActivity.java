package com.hit.pretstreet.pretstreet.navigationitems;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.AccountFragment;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.AddStoreFragment;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.ChangePasswordFragment;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.ContactUsFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.LoginFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.SignupFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.WelcomeFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NavigationItemsActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, ButtonClickCallback, LoginCallbackInterface {

    private int currentFragment = 0;
    private static final int ACCOUNT_FRAGMENT = 0;
    private static final int FOLLOWING_FRAGMENT = 1;
    private static final int ABOUT_FRAGMENT = 2;
    private static final int ADDSTORE_FRAGMENT = 3;
    private static final int CONTACTUS_FRAGMENT = 4;

    JsonRequestController jsonRequestController;

    @BindView(R.id.content) FrameLayout fl_content;
    @BindView(R.id.nsv_header)NestedScrollView nsv_header;
    @BindView(R.id.tv_cat_name) TextViewPret tv_cat_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_items);
        init();
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
    }

    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView iv_menu = (ImageView) toolbar.findViewById(R.id.iv_back);
        fl_content.bringToFront();
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setupFragment();
    }

    private void setupFragment(){
        Intent intent = getIntent();
        int fragmentId = intent.getIntExtra("fragment", 0);
        switch (fragmentId){
            case ACCOUNT_FRAGMENT:
                tv_cat_name.setRotation(getResources().getInteger(R.integer.rotation_catname));
                tv_cat_name.setText("MY PROFILE");
                changeFragment(new AccountFragment(), false);
                break;
            case FOLLOWING_FRAGMENT:
                tv_cat_name.setRotation(getResources().getInteger(R.integer.rotation_catname));
                tv_cat_name.setText("Following");
                changeFragment(new ChangePasswordFragment(), false);
                break;
            case ADDSTORE_FRAGMENT:
                tv_cat_name.setRotation(getResources().getInteger(R.integer.rotation_catname));
                tv_cat_name.setText("Add Store");
                changeFragment(new AddStoreFragment(), false);
                break;
            case ABOUT_FRAGMENT:
                tv_cat_name.setRotation(getResources().getInteger(R.integer.rotation_catname_long));
                tv_cat_name.setText("About Pretstreet");
                break;
            case CONTACTUS_FRAGMENT:
                tv_cat_name.setRotation(getResources().getInteger(R.integer.rotation_catname));
                tv_cat_name.setText("Contact Us");
                changeFragment(new ContactUsFragment(), false);
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

    @Override
    public void onResponse(JSONObject response) {
        Log.e("Volley", response.toString());

    }

    @Override
    public void onError(String error) {
        this.hideDialog();
        displaySnackBar(error);
    }

    @Override
    public void buttonClick(int id) {

    }

    @Override
    public void validateCallback(EdittextPret editText, String message, int type) {

    }

    @Override
    public void validationSuccess(String phonenumber) {
        JSONObject resultJson = LoginController.getNormalLoginDetails(null);
        //TODO :  api call
        this.showProgressDialog(getResources().getString(R.string.loading));
        //jsonRequestController.test();
    }

    @Override
    public void validationSuccess(LoginSession loginSession) {

    }
}
