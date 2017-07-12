package com.hit.pretstreet.pretstreet.navigationitems;

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
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigationitems.fragments.AddStoreFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.LoginFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.SignupFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.WelcomeFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NavigationItemsActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, ButtonClickCallback, LoginCallbackInterface {

    private int currentFragment = 0;
    private static final int ADDSTORE_FRAGMENT = 0;
    private static final int OFFERS_FRAGMENT = 1;
    private static final int FOLLOWING_FRAGMENT = 2;
    private static final int ABOUT_FRAGMENT = 3;
    private static final int CONTACTUS_FRAGMENT = 4;
    private static final int TERMS_FRAGMENT = 5;
    private static final int PRIVACYPOLICY_FRAGMENT = 6;
    private static final int FEEDBACK_FRAGMENT = 7;

    JsonRequestController jsonRequestController;

    @BindView(R.id.content)
    FrameLayout fl_content;
    @BindView(R.id.nsv_header)NestedScrollView nsv_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_items);
        init();
        changeFragment(new AddStoreFragment(), false);
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
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
        this.destroyDialog();
        Snackbar.make( getWindow().getDecorView().getRootView(), error, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void buttonClick(int id) {

    }

    @Override
    public void validateCallback(EdittextPret editText, String message) {

    }

    @Override
    public void validationSuccess(String phonenumber) {
        JSONObject resultJson = LoginController.getNormalLoginDetails(phonenumber);
        //TODO :  api call
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.test();
    }
}
