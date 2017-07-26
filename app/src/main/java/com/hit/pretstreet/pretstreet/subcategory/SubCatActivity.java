package com.hit.pretstreet.pretstreet.subcategory;

import android.content.Intent;
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
import android.widget.LinearLayout;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.splashnlogin.DefaultLocationActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.subcategory.fragments.SubCatFragment;
import com.hit.pretstreet.pretstreet.subcategory.interfaces.SubCatTrapeClick;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubCatActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, ButtonClickCallback, LoginCallbackInterface, SubCatTrapeClick {

    private static final int SUBCAT_FRAGMENT = 0;

    @BindView(R.id.content) FrameLayout fl_content;
    @BindView(R.id.nsv_header)NestedScrollView nsv_header;
    @BindView(R.id.tv_cat_name) TextViewPret tv_cat_name;
    @BindView(R.id.tv_location) TextViewPret tv_location;
    @BindView(R.id.ll_header) LinearLayout ll_header;

    JsonRequestController jsonRequestController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_cat);
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
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ll_header.bringToFront();

        String title = getIntent().getStringExtra("mTitle");
        tv_cat_name.setText(title);
        tv_location.setText(PreferenceServices.getInstance().getCurrentLocation());
        setupFragment(SUBCAT_FRAGMENT, false);
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


    private void setupFragment(int fragmentId, boolean b){
        switch (fragmentId){
            case SUBCAT_FRAGMENT:
                changeFragment(new SubCatFragment(), b);
                break;
            default:
                break;
        }
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
        setupFragment(SUBCAT_FRAGMENT, true);
    }

    @Override
    public void validateCallback(EdittextPret editText, String message, int type) {

    }

    @Override
    public void validationSuccess(String phonenumber) {
        JSONObject resultJson = LoginController.getNormalLoginDetails(null);
        //TODO :  api call
        this.showProgressDialog(getResources().getString(R.string.loading));
    }

    @Override
    public void validationSuccess(LoginSession loginSession) {

    }

    @Override
    public void onTrapeClick(HomeCatItems homeSubCategory) {

    }
}
