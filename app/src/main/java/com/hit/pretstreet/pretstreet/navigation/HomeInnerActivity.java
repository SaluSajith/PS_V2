package com.hit.pretstreet.pretstreet.navigation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.customview.monthPicker.RackMonthPicker;
import com.hit.pretstreet.pretstreet.core.customview.monthPicker.listener.DateMonthDialogListener;
import com.hit.pretstreet.pretstreet.core.customview.monthPicker.listener.OnCancelMonthDialogListener;
import com.hit.pretstreet.pretstreet.core.helpers.IncomingSms;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
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
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.SmsListener;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.ARTICLEPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.CLICKTYPE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXARTICLEPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXARTICLEREGISTER;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITIONLIKE_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITIONREGISTEROTP_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITIONREGISTER_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITIONSEARCH;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITION_DETAILS;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITION_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITION_REQUEST;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITION_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXUNLIKELINK;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.GIVEAWAYARTICLEPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.GIVEAWAY_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.GIVEAWAY_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.MULTISTOREPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRIVACY_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.STOREDETAILSPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.STORELISTINGPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDINGARTICLE_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDINGLIKE_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDINGPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDING_FRAGMENT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDING_URL;
/**
 * Parent activity to display Exhibition fragment,
 * Trending, Giveaway fragment
 **/
public class HomeInnerActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, TrendingHolderInvoke, ZoomedViewListener {

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
    @BindView(R.id.iv_search)ImageView iv_search;
    @BindView(R.id.tv_cat_name) TextViewPret tv_cat_name;
    @BindView(R.id.nsv_header)AppBarLayout nsv_header;

    private String number;
    private JSONObject registerJson;
    private EdittextPret edittextPret;
    private String mMonth = "", mYear = "";

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView iv_menu = toolbar.findViewById(R.id.iv_back);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iv_search.setVisibility(View.GONE);
        iv_filter.setVisibility(View.GONE);
        nsv_header.bringToFront();

        Intent intent = getIntent();
        int fragmentId = intent.getIntExtra("fragment", 0);
        setupFragment(fragmentId, false);
    }

    private void showMonthPicker(){
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = calendar.get(Calendar.MONTH);

        new RackMonthPicker(this)
                .setLocale(Locale.ENGLISH)
                .setSelectedMonth(thisMonth)
                .setSelectedYear(thisYear)
                .setColorTheme(R.color.yellow_indicator)
                .setPositiveButton(new DateMonthDialogListener() {
                    @Override
                    public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                        mMonth = month + "";
                        mYear = year + "";
                        setupFragment(EXHIBITION_FRAGMENT, false);
                    }
                })
                .setNegativeButton(new OnCancelMonthDialogListener() {
                    @Override
                    public void onCancel(AlertDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @OnClick(R.id.iv_filter)
    public void filterResult(){
        showMonthPicker();
        //showSortScreem();
    }

    @OnClick(R.id.iv_search)
    public void searchExhibitions(){
        startActivityForResult(new Intent(HomeInnerActivity.this,
                ExhibitionSearchActivity.class), EXHIBITION_REQUEST);
    }

    /**To change headername, filter button visibility and all as per the selected fragment
     * @param fragmentId id of the fragment
     * @param b shows addtobackstack boolean*/
    private void setupFragment(int fragmentId, boolean b){
        switch (fragmentId){
            case TRENDING_FRAGMENT:
                first = true;//to handle pagination
                tv_cat_name.setText("Trending");
                iv_header.setImageResource(R.drawable.header_yellow);
                trendingFragment = new TrendingFragment();
                trendingCallback = trendingFragment; //Callback to handle response : to show datain the corresponding fragments
                changeFragment(trendingFragment, b);
                break;
            case GIVEAWAY_FRAGMENT:
                first = true;//to handle pagination
                tv_cat_name.setText("Giveaway");
                iv_header.setImageResource(R.drawable.header_yellow);
                giveawayFragment = new GiveawayFragment();
                trendingCallback = giveawayFragment; //Callback to handle response : to show datain the corresponding fragments
                changeFragment(giveawayFragment, b);
                break;
            case EXHIBITION_FRAGMENT:
                first = true; //to handle pagination
                tv_cat_name.setText("Exhibition");
                iv_filter.setVisibility(View.VISIBLE);
                iv_search.setVisibility(View.VISIBLE);
                iv_header.setImageResource(R.drawable.header_yellow);
                exhibitionFragment = new ExhibitionFragment();
                trendingCallback = exhibitionFragment; //Callback to handle response : to show datain the corresponding fragments
                changeFragment(exhibitionFragment, b);
                break;
            case TRENDINGARTICLE_FRAGMENT:
                first = true;
                break;
            default:
                break;
        }
    }

    /** Call Trending Listing URL*/
    public void getTrendinglist(int offset){
        JSONObject resultJson = homeFragmentController.getTrendinglistJson(offset,
                getIntent().getStringExtra(Constant.PRE_PAGE_KEY),
                getIntent().getStringExtra(Constant.CLICKTYPE_KEY));
        if(first)
            this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, TRENDING_URL);
    }

    /** Call Giveaway Listing URL*/
    public void getGiveawaylist(int offset){
        JSONObject resultJson = homeFragmentController.getGiveawaylistJson(offset,
                getIntent().getStringExtra(Constant.PRE_PAGE_KEY),
                getIntent().getStringExtra(Constant.CLICKTYPE_KEY));
        if(first)
            this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, GIVEAWAY_URL);
    }

    /**Call Exhibition Listing URL*/
    public void getExhibitionlist(int offset){
        JSONObject resultJson = homeFragmentController.getExhibitionlistJson(offset,
                getIntent().getStringExtra(Constant.PRE_PAGE_KEY),
                getIntent().getStringExtra(Constant.CLICKTYPE_KEY), mMonth, mYear);
        if(first)
            this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, EXHIBITION_URL);
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
                case TRENDING_URL:
                    first = false;
                    requestCalled = false;
                    ArrayList<TrendingItems> trendingItemses = homeFragmentController.getTrendingList(response);
                    trendingCallback.bindData(trendingItemses, "");
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
                    trendingCallback.bindData(giveawayItemses, "");
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
                    trendingCallback.bindData(exHItemses, response.getString("CustomerMessage"));
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
        displaySnackBar(error);
        /**Show no data available message*/
        ArrayList<TrendingItems> giveawayItemses = new ArrayList<>();
        trendingCallback.bindData(giveawayItemses, error);
    }

    @Override
    public void loadStoreDetails(int position, StoreListModel storeListModel) {
        /**To open multistore an d store details page on button clicks in listing pages*/
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

    /**Sharing page with other contacts*/
    @Override
    public void shareurl(String text) {
        this.shareUrl(text);
    }

    /**LIKE button onclick
     * @param pageId passing pageid for tracking purpose
     * @param trendingId which indicates the trending or exhibition id*/
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
        /**Register fir exhibition*/
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        LoginSession loginSession = sharedPreferencesHelper.getUserDetails();
        String phone = loginSession.getMobile();
        registerJson = homeFragmentController.getExhibitionRegisterJson(EXARTICLEREGISTER,
                Id + "", getIntent().getStringExtra(PRE_PAGE_KEY), phone);
        if(phone.trim().length()==0||phone.equalsIgnoreCase("null")){
            /**Registering using phone number
             If number is there in the account we will take it otherwise gives popup*/
            showRegisterPopup();
        }
        else {
            HomeInnerActivity.this.showProgressDialog(getResources().getString(R.string.loading));
            jsonRequestController.sendRequest(this, registerJson, EXHIBITIONREGISTER_URL);
        }
    }

    /**OTP screen to enter the OTP value*/
    private void showOTPScreem(final String otpValue) {

        if(!popupDialog.isShowing()) {
            popupDialog.setCanceledOnTouchOutside(false);
            popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = li.inflate(R.layout.popup_otp_screen, null);
            ImageView img_close = view.findViewById(R.id.img_close);
            final EdittextPret edt_otp = view.findViewById(R.id.edt_otp);
            edittextPret = edt_otp;
            ButtonPret btn_send = view.findViewById(R.id.btn_send);

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
            final TextViewPret tv_resend = view.findViewById(R.id.tv_resend);
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
    /**OTP receiver broadcast to update thw OTP value in edittext*/
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

    /**If the User is not registered with his phone number
     * Mainly used in exhibition page*/
    public void showRegisterPopup() {
        final Dialog popupDialog = new Dialog(HomeInnerActivity.this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.popup_register, null);

        popupDialog.setCanceledOnTouchOutside(true);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        popupDialog.setContentView(view);
        popupDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = popupDialog.getWindow().getAttributes();
        popupDialog.getWindow().setAttributes(params);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();

        final EdittextPret edt_phone = view.findViewById(R.id.edt_phone);
        ImageView img_close = view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });

        TextViewPret tv_privacy = view.findViewById(R.id.tv_privacy);
        tv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NavigationItemsActivity.class);
                intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
                intent.putExtra("fragment", PRIVACY_FRAGMENT);
                startActivity(intent);
            }
        });

        ButtonPret btn_send = view.findViewById(R.id.btn_send);
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

    /**To receive click events from child fragments
     * @param trendingItems userdefined object having all the needed ids and params
     * @param prePage passing prepageid - mainly using for tracking purpose*/
    @Override
    public void openTrendingArticle(TrendingItems trendingItems, String prePage) {
        if(trendingItems.getBanner()){
            openNext(trendingItems, prePage);
        }else {
            Intent intent = new Intent(HomeInnerActivity.this, TrendingArticleActivity.class);
            intent.putExtra(Constant.PARCEL_KEY, trendingItems);
            intent.putExtra(Constant.PRE_PAGE_KEY, TRENDINGPAGE);
            if(trendingItems.getPagetypeid().equals(ARTICLEPAGE))
                startActivityForResult(intent, TRENDINGARTICLE_FRAGMENT);
            else if(trendingItems.getPagetypeid().equals(GIVEAWAYARTICLEPAGE))
                startActivity(intent);
        }
    }

    /**To receive click events from child fragments
     * @param trendingItems userdefined object having all the needed ids and params*/
    public void openExhibitionDetails(TrendingItems trendingItems){
        Intent intent = new Intent(HomeInnerActivity.this, ExhibitionDetailsActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, trendingItems);
        intent.putExtra(Constant.PRE_PAGE_KEY, Constant.EXHIBITIONPAGE);
        startActivityForResult(intent, EXHIBITION_DETAILS);
    }

    /**To receive click events from child fragments
     * @param trendingItems userdefined object having all the needed ids and params
     * @param prePage passing prepageid - mainly using for tracking purpose*/
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
        ButtonPret btn_apply = view.findViewById(R.id.btn_apply);
        final RadioGroup radioGroup_sortby = view.findViewById(R.id.radioGroup);
        final RadioGroup radioGroup_orderby = view.findViewById(R.id.radioGroup1);

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
        /**Passing images to show images as a swipable gallery*/
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
        /**To update LIKE and REGISTER button ststus from
         * Trending Details page into Trending listing and Exhibition listing*/
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
                    case EXHIBITION_REQUEST:
                        TrendingItems trendingItems = new TrendingItems();
                        trendingItems.setId(storeId);
                        trendingItems.setPagetypeid(EXARTICLEPAGE);

                        openNext(trendingItems, EXHIBITIONSEARCH);
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
