package com.hit.pretstreet.pretstreet.navigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.EmptyFragment;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.helpers.IncomingSms;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.PermissionResult;
import com.hit.pretstreet.pretstreet.navigation.controllers.HomeFragmentController;
import com.hit.pretstreet.pretstreet.navigation.fragments.ExhibitionFragment;
import com.hit.pretstreet.pretstreet.navigation.fragments.GiveawayFragment;
import com.hit.pretstreet.pretstreet.navigation.fragments.TrendingFragment;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingCallback;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingHolderInvoke;
import com.hit.pretstreet.pretstreet.navigation.interfaces.ZoomedViewListener;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.search.MultistoreActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.SmsListener;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.*;

public class HomeInnerActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, TrendingHolderInvoke, ZoomedViewListener {

    private int currentFragment = 0;

    JsonRequestController jsonRequestController;
    HomeFragmentController homeFragmentController;
    TrendingCallback trendingCallback;
    TrendingFragment trendingFragment;
    GiveawayFragment giveawayFragment;
    ExhibitionFragment exhibitionFragment;
    Toolbar toolbar;

    @BindView(R.id.content) FrameLayout fl_content;
    @BindView(R.id.iv_header)ImageView iv_header;
    @BindView(R.id.iv_filter)ImageView iv_filter;
    @BindView(R.id.tv_cat_name) TextViewPret tv_cat_name;
    @BindView(R.id.nsv_header)AppBarLayout nsv_header;

    private String number;
    private JSONObject registerJson;
    private EdittextPret edittextPret;

    Dialog popupDialog;
    boolean requestCalled = false;
    boolean first = true;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_inner);
        init();
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
        homeFragmentController = new HomeFragmentController(this);
    }

    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        popupDialog = new Dialog(this);
        context = getApplicationContext();
        //checkDevice();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView iv_menu = (ImageView) toolbar.findViewById(R.id.iv_back);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ImageView iv_search = (ImageView) toolbar.findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);
        nsv_header.bringToFront();

        Intent intent = getIntent();
        int fragmentId = intent.getIntExtra("fragment", 0);
        setupFragment(fragmentId, false);
    }

    @OnClick(R.id.iv_filter)
    public void filterResult(){
        showSortScreem();
    }
    /*
    private void checkDevice(){
        String manufacturer = android.os.Build.MANUFACTURER;
        if(manufacturer.equalsIgnoreCase("samsung")){
            nsv_header.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }*/

    private void setupFragment(int fragmentId, boolean b){
        switch (fragmentId){
            case TRENDING_FRAGMENT:
                first = true;
                currentFragment = TRENDING_FRAGMENT;
                tv_cat_name.setText("Trending");
                iv_filter.setVisibility(View.GONE);
                iv_header.setImageResource(R.drawable.header_yellow);
                trendingFragment = new TrendingFragment();
                trendingCallback = trendingFragment;
                changeFragment(trendingFragment, b);
                break;
            case GIVEAWAY_FRAGMENT:
                first = true;
                currentFragment = GIVEAWAY_FRAGMENT;
                tv_cat_name.setText("Giveaway");
                iv_filter.setVisibility(View.GONE);
                iv_header.setImageResource(R.drawable.header_yellow);
                giveawayFragment = new GiveawayFragment();
                trendingCallback = giveawayFragment;
                changeFragment(giveawayFragment, b);
                break;
            case EXHIBITION_FRAGMENT:
                first = true;
                currentFragment = EXHIBITION_FRAGMENT;
                tv_cat_name.setText("Exhibition");
                iv_filter.setVisibility(View.GONE);
                iv_header.setImageResource(R.drawable.header_yellow);
                exhibitionFragment = new ExhibitionFragment();
                trendingCallback = exhibitionFragment;
                changeFragment(exhibitionFragment, b);
                break;
            case TRENDINGARTICLE_FRAGMENT:
                first = true;
                currentFragment = TRENDINGARTICLE_FRAGMENT;
                iv_filter.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public void getTrendinglist(int offset){
        JSONObject resultJson = homeFragmentController.getTrendinglistJson(offset,
                getIntent().getStringExtra(Constant.PRE_PAGE_KEY),
                getIntent().getStringExtra(Constant.CLICKTYPE_KEY));
        if(first)
            this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, TRENDING_URL);
    }

    public void getGiveawaylist(int offset){
        JSONObject resultJson = homeFragmentController.getGiveawaylistJson(offset,
                getIntent().getStringExtra(Constant.PRE_PAGE_KEY),
                getIntent().getStringExtra(Constant.CLICKTYPE_KEY));
        if(first)
            this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, GIVEAWAY_URL);
    }

    public void getExhibitionlist(int offset){
        JSONObject resultJson = homeFragmentController.getExhibitionlistJson(offset,
                getIntent().getStringExtra(Constant.PRE_PAGE_KEY),
                getIntent().getStringExtra(Constant.CLICKTYPE_KEY));
        if(first)
            this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, EXHIBITION_URL);
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
            //displaySnackBar(response.getString("CustomerMessage"));
            switch (url){
                case TRENDING_URL:
                    first = false;
                    requestCalled = false;
                    //trendingFragment.update_loadmore_adapter(false);
                    ArrayList<TrendingItems> trendingItemses = homeFragmentController.getTrendingList(response);
                    trendingCallback.bindData(trendingItemses);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideDialog();
                        }
                    }, 1000);
                    break;
                case GIVEAWAY_URL:
                    first = false;
                    requestCalled = false;
                    tv_cat_name.setText(homeFragmentController.getHeading(response));
                    ArrayList<TrendingItems> giveawayItemses = homeFragmentController.getGiveawayList(response);
                    trendingCallback.bindData(giveawayItemses);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideDialog();
                        }
                    }, 1000);
                    break;
                case EXHIBITION_URL:
                    first = false;
                    requestCalled = false;
                    ArrayList<TrendingItems> exHItemses = homeFragmentController.getExhibitionList(response);
                    trendingCallback.bindData(exHItemses);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideDialog();
                        }
                    }, 1000);
                    break;
                case TRENDINGLIKE_URL:
                    JSONObject object = response.getJSONObject("Data");
                    trendingFragment.updateLikeStatus(object.getInt("LikeStatus"),
                            object.getString("Id"));
                    hideDialog();
                    break;
                case EXHIBITIONLIKE_URL:
                    object = response.getJSONObject("Data");
                    exhibitionFragment.updateLikeStatus(object.getInt("LikeStatus"),
                            object.getString("Id"));
                    hideDialog();
                    break;
                case EXHIBITIONREGISTER_URL:
                    object = response.getJSONObject("Data");
                    displaySnackBar(response.getString("CustomerMessage"));
                    exhibitionFragment.updateLikeStatus(object.getInt("LikeStatus"),
                            object.getString("Id"));
                    hideDialog();
                    break;
                case EXHIBITIONREGISTEROTP_URL:
                    String otpValue = response.getJSONObject("Data").getString("OTP");
                    showOTPScreem(otpValue);
                    hideDialog();
                    break;
                default: break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        handleResponse(response);
    }

    @Override
    public void onError(String error) {
        this.hideDialog();
        EmptyFragment emptyFragment = new EmptyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("error", error);
        bundle.putString("retry", "0");
        bundle.putString("pageid", TRENDINGPAGE);
        emptyFragment.setArguments(bundle);
        changeFragment(emptyFragment, false);
    }

    @Override
    public void loadStoreDetails(int position, StoreListModel storeListModel) {
        String pagetypeid = storeListModel.getPageType();
        switch (pagetypeid){
            case MULTISTOREPAGE:
                Intent intent = new Intent(HomeInnerActivity.this, MultistoreActivity.class);
                intent.putExtra(Constant.ID_KEY, storeListModel.getId());
                intent.putExtra(CLICKTYPE_KEY, storeListModel.getClickType());
                intent.putExtra(Constant.PRE_PAGE_KEY, storeListModel.getPageTypeId());
                startActivity(intent);
                break;
            case STOREDETAILSPAGE:
                intent = new Intent(HomeInnerActivity.this, StoreDetailsActivity.class);
                intent.putExtra(Constant.PARCEL_KEY, storeListModel);
                intent.putExtra(CLICKTYPE_KEY, storeListModel.getClickType());
                intent.putExtra(Constant.PRE_PAGE_KEY, storeListModel.getPageTypeId());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void shareurl(String text) {
        this.shareUrl(text);
    }

    @Override
    public void likeInvoke(int trendingId, int pageId) {
        JSONObject resultJson;
        this.showProgressDialog(getResources().getString(R.string.loading));
        if(pageId == TRENDING_FRAGMENT) {
            resultJson = homeFragmentController.getTrendinglikeJson(trendingId + "", getIntent().getStringExtra(PRE_PAGE_KEY));
            jsonRequestController.sendRequest(this, resultJson, TRENDINGLIKE_URL);
        } else {
            resultJson = homeFragmentController.getExhibitionlikeJson(EXUNLIKELINK, trendingId + "", getIntent().getStringExtra(PRE_PAGE_KEY));
            jsonRequestController.sendRequest(this, resultJson, EXHIBITIONLIKE_URL);
        }
    }

    @Override
    public void registerInvoke(int Id) {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        LoginSession loginSession = sharedPreferencesHelper.getUserDetails();
        String phone = loginSession.getMobile();
        registerJson = homeFragmentController.getExhibitionRegisterJson(EXARTICLEREGISTER,
                Id + "", getIntent().getStringExtra(PRE_PAGE_KEY), phone);
        if(phone.trim().length()==0||phone.equalsIgnoreCase("null")){
            showRegisterPopup();
        }
        else {
            HomeInnerActivity.this.showProgressDialog(getResources().getString(R.string.loading));
            jsonRequestController.sendRequest(this, registerJson, EXHIBITIONREGISTER_URL);
        }
    }


    private void showOTPScreem(final String otpValue) {

        if(!popupDialog.isShowing()) {
            popupDialog.setCanceledOnTouchOutside(false);
            popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = li.inflate(R.layout.popup_otp_screen, null);
            ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
            final EdittextPret edt_otp = (EdittextPret) view.findViewById(R.id.edt_otp);
            edittextPret = edt_otp;
            ButtonPret btn_send = (ButtonPret) view.findViewById(R.id.btn_send);

            RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.popup_bundle);
            rl.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 0);
            rl.setLayoutParams(lp);
            popupDialog.setContentView(view);

            popupDialog.getWindow().setGravity(Gravity.CENTER);
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) popupDialog.getWindow().getAttributes();
            popupDialog.getWindow().setAttributes(params);
            popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            popupDialog.show();

            img_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupDialog.dismiss();
                }
            });

            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edt_otp.getText().toString().length() < 1) {
                        displaySnackBar("Enter OTP value");
                        edt_otp.requestFocus();
                    } else {
                        popupDialog.dismiss();
                        if (otpValue.equals(edt_otp.getText().toString())) {
                            HomeInnerActivity.this.showProgressDialog(getResources().getString(R.string.loading));
                            registerJson = homeFragmentController.getRegisterJson_UpdatePhone(registerJson, number);
                            jsonRequestController.sendRequest(HomeInnerActivity.this, registerJson, EXHIBITIONREGISTER_URL);
                        } else {
                            displaySnackBar("Wrong OTP");
                        }
                    }
                }
            });
            final TextViewPret tv_resend = (TextViewPret) view.findViewById(R.id.tv_resend);
            tv_resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_resend.setClickable(false);
                    tv_resend.setTextColor(Color.LTGRAY);

                    showProgressDialog(getResources().getString(R.string.loading));
                    JSONObject otpObject = homeFragmentController.getOTPVerificationJson(number);
                    jsonRequestController.sendRequest(HomeInnerActivity.this, otpObject, EXHIBITIONREGISTEROTP_URL);
                }
            });
        }
    }

    private void setupOTPReceiver(){
        IncomingSms.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                try {
                    if(edittextPret!=null)
                        edittextPret.setText(messageText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showRegisterPopup() {

        final Dialog popupDialog = new Dialog(HomeInnerActivity.this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.popup_register, null);

        popupDialog.setCanceledOnTouchOutside(true);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        popupDialog.setContentView(view);
        popupDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) popupDialog.getWindow().getAttributes();
        popupDialog.getWindow().setAttributes(params);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();

        final EdittextPret edt_phone = (EdittextPret) view.findViewById(R.id.edt_phone);
        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });

        TextViewPret tv_privacy = (TextViewPret) view.findViewById(R.id.tv_privacy);
        tv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NavigationItemsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra("fragment", PRIVACY_FRAGMENT);
                startActivity(intent);
            }
        });

        ButtonPret btn_send = (ButtonPret) view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regexStr = "^[789]\\d{9}$";
                number = edt_phone.getText().toString();
                if(number.length()<10 || number.length()>13 || number.matches(regexStr)==false  ) {
                    edt_phone.setError("Invalid phone number!");
                }
                else{
                    popupDialog.dismiss();
                    setupOTPReceiver();
                    showProgressDialog(getResources().getString(R.string.loading));
                    JSONObject otpObject = homeFragmentController.getOTPVerificationJson(number);
                    jsonRequestController.sendRequest(HomeInnerActivity.this, otpObject, EXHIBITIONREGISTEROTP_URL);
                }
            }
        });
    }

    @Override
    public void interestInvoke(int trendingId, int pageId) {
    }

    @Override
    public void openTrendingArticle(TrendingItems trendingItems, String prePage) {
        if(trendingItems.getBanner()){
            openNext(trendingItems, prePage);
        }else {
            Intent intent = new Intent(HomeInnerActivity.this, TrendingArticleActivity.class);
            intent.putExtra(Constant.PARCEL_KEY, trendingItems);
            intent.putExtra(Constant.PRE_PAGE_KEY, Constant.TRENDINGPAGE);
            if(trendingItems.getPagetypeid().equals(ARTICLEPAGE))
                startActivityForResult(intent, TRENDINGARTICLE_FRAGMENT);
            else if(trendingItems.getPagetypeid().equals(GIVEAWAYARTICLEPAGE))
                startActivity(intent);
        }
    }

    public void openExhibitionDetails(TrendingItems trendingItems){
        Intent intent = new Intent(HomeInnerActivity.this, ExhibitionDetailsActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, trendingItems);
        intent.putExtra(Constant.PRE_PAGE_KEY, Constant.EXHIBITIONPAGE);
        startActivityForResult(intent, EXHIBITION_DETAILS);
    }

    private void openNext(TrendingItems trendingItems, String prePage){
        String pageid = trendingItems.getPagetypeid();
        Intent intent;
        switch (pageid){
            case ARTICLEPAGE:
                intent = new Intent(HomeInnerActivity.this, TrendingArticleActivity.class);
                intent.putExtra(Constant.PARCEL_KEY, trendingItems);
                startActivityForResult(intent, TRENDINGARTICLE_FRAGMENT);
                break;
            case GIVEAWAYARTICLEPAGE:
                intent = new Intent(HomeInnerActivity.this, TrendingArticleActivity.class);
                intent.putExtra(Constant.PARCEL_KEY, trendingItems);
                startActivity(intent);
                break;
            case EXARTICLEPAGE:
                intent = new Intent(HomeInnerActivity.this, ExhibitionDetailsActivity.class);
                intent.putExtra(Constant.PARCEL_KEY, trendingItems);
                intent.putExtra(Constant.PRE_PAGE_KEY, STORELISTINGPAGE);
                startActivityForResult(intent, EXHIBITION_DETAILS);
                break;
            case MULTISTOREPAGE:
                intent = new Intent(HomeInnerActivity.this, MultistoreActivity.class);
                intent.putExtra(Constant.ID_KEY, trendingItems.getId());//TODO : check id
                intent.putExtra(Constant.PRE_PAGE_KEY, prePage);
                startActivity(intent);
                break;
            case STOREDETAILSPAGE:
                StoreListModel storeListModel =  new StoreListModel();
                storeListModel.setId(trendingItems.getId());
                intent = new Intent(HomeInnerActivity.this, StoreDetailsActivity.class);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra(Constant.PARCEL_KEY, storeListModel);
                startActivity(intent);
                break;
            default: break;
        }
    }

    public void showSortScreem() {

        final Dialog popupDialog = new Dialog(HomeInnerActivity.this);
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.popup_ex_sort, null);
        ButtonPret btn_apply = (ButtonPret) view.findViewById(R.id.btn_apply);
        final RadioGroup radioGroup_sortby = (RadioGroup) view.findViewById(R.id.radioGroup);
        final RadioGroup radioGroup_orderby = (RadioGroup) view.findViewById(R.id.radioGroup1);

        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popupDialog.setContentView(view);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        popupDialog.show();

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });
    }

    @Override
    public void onClicked(int position, ArrayList<String> mImagearray) {
        ArrayList<String> imageModels1 = mImagearray;
        Intent intent = new Intent(context, FullscreenGalleryActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, imageModels1);
        intent.putExtra(Constant.PRE_PAGE_KEY, Integer.parseInt(Constant.HOMEPAGE));
        intent.putExtra(Constant.POSITION_KEY, position);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                int status = Integer.parseInt(data.getStringExtra(PARCEL_KEY));
                String storeId = data.getStringExtra(ID_KEY);
                switch (requestCode) {
                    case TRENDINGARTICLE_FRAGMENT:
                        try {
                            trendingFragment.updateLikeStatus(status, storeId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case EXHIBITION_DETAILS:
                        try {
                            exhibitionFragment.updateLikeStatus(status, storeId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
