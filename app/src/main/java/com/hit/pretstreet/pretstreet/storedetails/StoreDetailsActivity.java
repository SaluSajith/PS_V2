package com.hit.pretstreet.pretstreet.storedetails;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.view.menu.ShowableListMenu;
import android.support.v7.widget.ForwardingListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.helpers.ShadowTransformer;
import com.hit.pretstreet.pretstreet.location.StoreLocationMapScreen;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.storedetails.adapters.CardFragmentPagerAdapter;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.storedetails.adapters.GalleryAdapter;
import com.hit.pretstreet.pretstreet.storedetails.controllers.StoreDetailsController;
import com.hit.pretstreet.pretstreet.storedetails.interfaces.ImageClickCallback;
import com.hit.pretstreet.pretstreet.storedetails.model.StoreDetailsModel;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers.SubCategoryController;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.BOOK_APPOINTMENT_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.CALLEDLINK;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.CALLLINK;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.REPORT_ERROR_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRACK_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.VIEWADDRESSLINK;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.VIEWONMAPLINK;

public class StoreDetailsActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, ImageClickCallback {
    JsonRequestController jsonRequestController;
    SubCategoryController subCategoryController;
    StoreDetailsController storeDetailsController;

    private static final int ABOUTDESIGNER_FRAGMENT = 101;

    @BindView(R.id.tv_product) TextViewPret tv_product;
    @BindView(R.id.tv_about) TextViewPret tv_about;
    @BindView(R.id.tv_imgsrc) TextViewPret tv_imgsrc;
    @BindView(R.id.tv_book_app) TextViewPret tv_book_app;
    @BindView(R.id.tv_about_heading) TextViewPret tv_about_heading;
    @BindView(R.id.tv_heading_hrs) TextViewPret tv_heading_hrs;
    @BindView(R.id.tv_testimonials_heading) TextViewPret tv_testimonials_heading;

    @BindView(R.id.tv_time) TextViewPret tv_time;
    @BindView(R.id.tv_storename) TextViewPret tv_storename;
    @BindView(R.id.tv_location) TextViewPret tv_location;
    @BindView(R.id.tv_openstatus) TextViewPret tv_openstatus;
    @BindView(R.id.tv_folowerscount) TextViewPret tv_folowerscount;
    @BindView(R.id.tv_openinghrs) TextViewPret tv_openinghrs;
    @BindView(R.id.tv_reportError) TextViewPret tv_reportError;
    @BindView(R.id.tv_heading_photos) TextViewPret tv_heading_photos;

    @BindView(R.id.rv_images) RecyclerView rv_images;
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.btn_follow) ButtonPret btn_follow;

    @BindView(R.id.iv_sale) ImageView iv_sale;
    @BindView(R.id.iv_offer) ImageView iv_offer;
    @BindView(R.id.iv_new) ImageView iv_new;

    @BindView(R.id.ll_call) LinearLayout ll_call;
    @BindView(R.id.ll_address) LinearLayout ll_address;
    @BindView(R.id.ll_getdirec) LinearLayout ll_getdirec;

    String mStoreId, shareUrl;
    Dialog popupDialog;
    private StoreDetailsModel storeDetailsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storedetails);
        /* Intent intent = getIntent();
        final String cheeseName = intent.getStringExtra(EXTRA_NAME);*/

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initUi();
    }

    @Override
    protected void setUpController() {
        subCategoryController = new SubCategoryController(this);
        jsonRequestController = new JsonRequestController(this);
        storeDetailsController = new StoreDetailsController(this);
    }

    private void initUi(){
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        tv_reportError.setVisibility(View.VISIBLE);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager
                (3, LinearLayoutManager.VERTICAL);
        rv_images.setLayoutManager(staggeredGridLayoutManager);
        rv_images.setNestedScrollingEnabled(false);

        StoreListModel storeListModel = (StoreListModel)getIntent()
                .getSerializableExtra(Constant.PARCEL_KEY);
        String pagekey = getIntent().getStringExtra(Constant.PRE_PAGE_KEY);
        String clickid = getIntent().getStringExtra(Constant.CLICKTYPE_KEY);
        mStoreId = storeListModel.getId();
        setupCollapsingHeader("", "");
        getShopDetails(mStoreId, pagekey, clickid);

        SpannableString content = new SpannableString("Testimonials");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_testimonials_heading.setText(content);

    }

    private void getShopDetails(String storeId, String pageId, String clickid){
        JSONObject resultJson = storeDetailsController.getShopDetailsJson(storeId, pageId, clickid);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, Constant.STOREDETAILS_URL);
    }

    @OnClick(R.id.tv_reportError)
    public void onReportPressed() {
        showReportErrorPopup();
    }

    @OnClick(R.id.tv_about)
    public void aboutShop(){
        Intent intent = new Intent(getApplicationContext(), NavigationItemsActivity.class);
        intent.putExtra(PRE_PAGE_KEY, Constant.HOMEPAGE);
        intent.putExtra("fragment", ABOUTDESIGNER_FRAGMENT);
        intent.putExtra("NAME", storeDetailsModel.getStoreName());
        intent.putExtra(PARCEL_KEY, storeDetailsModel.getDescription());
        startActivity(intent);
    }

    @OnClick(R.id.btn_follow)
    public void onFollowPressed() {
        JSONObject resultJson = subCategoryController.updateFollowCount(mStoreId,
                getIntent().getStringExtra(Constant.PRE_PAGE_KEY), Constant.FOLLOWLINK);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, Constant.UPDATEFOLLOWSTATUS_URL);
    }

    private void setupDetailsPage(StoreDetailsModel storeDetailsModel){
        try {
            setupCollapsingHeader(storeDetailsModel.getStoreName(), storeDetailsModel.getBaseImage());
            tv_storename.setText(storeDetailsModel.getStoreName());
            tv_location.setText(storeDetailsModel.getAreaCity());
            tv_openstatus.setText(storeDetailsModel.getOpenStatus() == true ? "Closed" : "Open now");
            tv_folowerscount.setText(storeDetailsModel.getFollowingCount() + " followers");
            tv_about.setText(storeDetailsModel.getAbout());
            tv_time.setText(" - " + storeDetailsModel.getTimingToday());
            String sourceString = "<b>" + "Product: " + "</b> " + storeDetailsModel.getProducts();
            tv_product.setText(Html.fromHtml(sourceString));
            sourceString = "<b>" + "Image Source: " + "</b> " + storeDetailsModel.getImageSource();
            tv_imgsrc.setText(Html.fromHtml(sourceString));

            btn_follow.setText(storeDetailsModel.getFollowingStatus() == true ? "Follow" : "Unfollow");
            tv_book_app.setVisibility(storeDetailsModel.getAppointmentFlag() == true ? View.VISIBLE : View.GONE);
            iv_sale.setVisibility(storeDetailsModel.getFlags().contains("0") == true ? View.VISIBLE : View.INVISIBLE);
            iv_offer.setVisibility(storeDetailsModel.getFlags().contains("1") == true ? View.VISIBLE : View.INVISIBLE);
            iv_new.setVisibility(storeDetailsModel.getFlags().contains("2") == true ? View.VISIBLE : View.INVISIBLE);
            tv_heading_photos.setVisibility(storeDetailsModel.getArrayListImages().size() > 0 ? View.VISIBLE : View.GONE);

            ArrayList arrayListTimings = storeDetailsModel.getArrayListTimings();
            StringBuilder strTiming = new StringBuilder();
            for (Object timing : arrayListTimings) {
                strTiming.append(timing + "<br/>" + "<br/>");
            }
            sourceString = strTiming.toString();
            tv_openinghrs.setText(Html.fromHtml(sourceString));
            if (storeDetailsModel.getArrayListTesti().size() > 0)
                setupTestimonials(storeDetailsModel);
            else {
                viewPager.setVisibility(View.GONE);
                tv_testimonials_heading.setVisibility(View.GONE);
            }
            setupGallery(storeDetailsModel.getArrayListImages());
            shareUrl = storeDetailsModel.getShare();

            tv_about.setVisibility(storeDetailsModel.getDescription().length() > 0 ? View.VISIBLE : View.GONE);
            tv_about_heading.setVisibility(storeDetailsModel.getDescription().length() > 0 ? View.VISIBLE : View.GONE);
            tv_imgsrc.setVisibility(storeDetailsModel.getImageSource().length() > 0 ? View.VISIBLE : View.GONE);
            tv_product.setVisibility(storeDetailsModel.getProducts().length() > 0 ? View.VISIBLE : View.GONE);
            tv_time.setVisibility(storeDetailsModel.getTimingToday().length() > 0 ? View.VISIBLE : View.GONE);
            tv_heading_hrs.setVisibility(storeDetailsModel.getArrayListTimings().size() > 0 ? View.VISIBLE : View.GONE);
        }catch (Exception e){}
    }

    public void showReportErrorPopup() {

        popupDialog = new Dialog(StoreDetailsActivity.this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.popup_reporterror, null);

        popupDialog.setCanceledOnTouchOutside(true);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        popupDialog.setContentView(view);
        popupDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) popupDialog.getWindow().getAttributes();
        popupDialog.getWindow().setAttributes(params);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();

        final EdittextPret edt_remarks = (EdittextPret) view.findViewById(R.id.edt_remarks);
        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });

        edt_remarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_remarks.setError(null);
            }
        });

        ButtonPret btn_send = (ButtonPret) view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_remarks.setError("");
                if(edt_remarks.getText().toString().trim().length()==0){
                    edt_remarks.setError("Field cannot be blank!");
                }
                else {
                    popupDialog.dismiss();
                    reportError(edt_remarks.getText().toString());
                }
            }
        });
    }

    public void showBookPopup() {

        popupDialog = new Dialog(StoreDetailsActivity.this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.popup_bookappointment, null);

        popupDialog.setCanceledOnTouchOutside(true);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        popupDialog.setContentView(view);
        popupDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) popupDialog.getWindow().getAttributes();
        popupDialog.getWindow().setAttributes(params);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();

        final EdittextPret edt_remarks = (EdittextPret) view.findViewById(R.id.edt_remarks);
        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });

        final EdittextPret edt_date = (EdittextPret) view.findViewById(R.id.edt_date);
        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_date.setError(null);
                final Calendar c = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(StoreDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                edt_date.setText(dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + year);

                            }
                        }, c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
                c.add(Calendar.DATE, 1);
                dpd.getDatePicker().setMinDate(c.getTimeInMillis());
                c.add(Calendar.YEAR, 1);
                dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
                dpd.show();
            }
        });

        final EdittextPret edt_time = (EdittextPret) view.findViewById(R.id.edt_time);
        edt_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_time.setError(null);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(StoreDetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        edt_time.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//true : Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        ButtonPret btn_send = (ButtonPret) view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_date.getText().toString().trim().length()==0){
                    edt_date.setError("Select date");
                }
                else if(edt_time.getText().toString().trim().length()==0){
                    edt_time.setError("Select time");
                }
                else{
                    popupDialog.dismiss();
                    bookAppointment(edt_date.getText().toString(),
                            edt_time.getText().toString(),
                            edt_remarks.getText().toString());
                }
            }
        });
    }

    private void bookAppointment(String date, String time, String remarks){
        JSONObject resultJson = storeDetailsController.getBookAppoJson(date+" "+time, mStoreId, remarks);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, BOOK_APPOINTMENT_URL);
    }


    private void reportError(String remarks){
        JSONObject resultJson = storeDetailsController.getReportErrorJson(mStoreId, remarks);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, REPORT_ERROR_URL);
    }

    private void logTracking(String clicktypeid){
        JSONObject resultJson = storeDetailsController.getLogTrackJson(clicktypeid, "",
                getIntent().getStringExtra(Constant.PRE_PAGE_KEY), mStoreId);
        jsonRequestController.sendRequest(this, resultJson, TRACK_URL);
    }

    private void setupCollapsingHeader(String title, String image){

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(title);
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent)); // transperent color = #00000000
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        loadBackdrop(image);
    }

    private void setupTestimonials(StoreDetailsModel storeDetailsModel){
        CardFragmentPagerAdapter pagerAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(), dpToPixels(1, this),
                storeDetailsModel.getArrayListTesti());
        ShadowTransformer fragmentCardShadowTransformer = new ShadowTransformer(viewPager, pagerAdapter);
        fragmentCardShadowTransformer.enableScaling(true);

        viewPager.setAdapter(pagerAdapter);
        //viewPager.setCurrentItem(1);
        viewPager.setPageTransformer(false, fragmentCardShadowTransformer);
        viewPager.setOffscreenPageLimit(3);
    }

    private void setupGallery(ArrayList<String> arrayListImages){
        GalleryAdapter storeList_recyclerAdapter = new GalleryAdapter(StoreDetailsActivity.this, arrayListImages);
        rv_images.setAdapter(storeList_recyclerAdapter);
    }

    private void loadBackdrop(String imageUrl) {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(imageUrl).asBitmap().placeholder(R.drawable.default_banner).fitCenter().into(imageView);
    }

    public void showPopupPhoneNumber(StoreDetailsModel storeDetailsModel) {
        final Dialog popupDialog = new Dialog(StoreDetailsActivity.this);
        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View view = li.inflate(R.layout.popup_phone_number, null);
        TextViewPret txt_cat = (TextViewPret) view.findViewById(R.id.txt_cat);
        RelativeLayout rl_phone1 = (RelativeLayout) view.findViewById(R.id.rl_phone1);
        RelativeLayout rl_phone2 = (RelativeLayout) view.findViewById(R.id.rl_phone2);
        RelativeLayout rl_phone3 = (RelativeLayout) view.findViewById(R.id.rl_phone3);

        TextViewPret txt_phone1 = (TextViewPret) view.findViewById(R.id.txt_phone1);
        TextViewPret txt_phone2 = (TextViewPret) view.findViewById(R.id.txt_phone2);
        TextViewPret txt_phone3 = (TextViewPret) view.findViewById(R.id.txt_phone3);

        final ArrayList<String> arrayList = storeDetailsModel.getPhone();
        txt_cat.setText(storeDetailsModel.getStoreName());

        switch (arrayList.size()){
            case 1:
                txt_phone1.setText(arrayList.get(0));
                rl_phone2.setVisibility(View.GONE);
                rl_phone3.setVisibility(View.GONE);
                break;
            case 2:
                txt_phone1.setText(arrayList.get(0));
                txt_phone2.setText(arrayList.get(1));
                rl_phone3.setVisibility(View.GONE);
                break;
            case 3:
                txt_phone1.setText(arrayList.get(0));
                txt_phone3.setText(arrayList.get(1));
                txt_phone2.setText(arrayList.get(2));
                break;
            default:
                break;
        }

        popupDialog.setCanceledOnTouchOutside(true);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popupDialog.setContentView(view);
        popupDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) popupDialog.getWindow().getAttributes();
        popupDialog.getWindow().setAttributes(params);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();

        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });

        rl_phone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone(arrayList.get(0));
            }
        });

        rl_phone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone(arrayList.get(1));
            }
        });

        rl_phone3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone(arrayList.get(2));
            }
        });

        logTracking(CALLLINK);
    }

    private void dialPhone(String phone){
        logTracking(CALLEDLINK);
        MyPhoneListener phoneListener = new MyPhoneListener();
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        try {
            String uri = "tel:" + phone;
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
            startActivity(dialIntent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Call Failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private class MyPhoneListener extends PhoneStateListener {
        private boolean onCall = false;
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    displaySnackBar(incomingNumber + " " + "Call you");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    displaySnackBar("onCall");
                    onCall = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (onCall == true) {
                        displaySnackBar("Restart app after call");
                        // restart our application
                        Intent restart = getApplicationContext().getPackageManager().
                                getLaunchIntentForPackage(getApplicationContext().getPackageName());
                        restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(restart);
                        onCall = false;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void showAddress(StoreDetailsModel storeDetailsModel) {
        final Dialog popupDialog = new Dialog(StoreDetailsActivity.this);
        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.popup_phone_number, null);
        TextViewPret txt_cat = (TextViewPret) view.findViewById(R.id.txt_cat);
        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        TextViewPret txt_address1 = (TextViewPret) view.findViewById(R.id.txt_address);
        RelativeLayout rl_phone1 = (RelativeLayout) view.findViewById(R.id.rl_phone1);
        RelativeLayout rl_phone2 = (RelativeLayout) view.findViewById(R.id.rl_phone2);
        RelativeLayout rl_phone3 = (RelativeLayout) view.findViewById(R.id.rl_phone3);
        rl_phone1.setVisibility(View.GONE);
        rl_phone2.setVisibility(View.GONE);
        rl_phone3.setVisibility(View.GONE);
        txt_address1.setVisibility(View.VISIBLE);
        txt_cat.setText(storeDetailsModel.getStoreName());
        txt_address1.setText(storeDetailsModel.getAddress());
        popupDialog.setCanceledOnTouchOutside(true);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        logTracking(VIEWADDRESSLINK);
    }

    private void showLocation(StoreDetailsModel storeDetailsModel){
        if (storeDetailsModel.getLatitude().equalsIgnoreCase("") ||
                storeDetailsModel.getLongitude().equalsIgnoreCase("")) {
            displaySnackBar("Location not found");
        } else {
            Intent intent = new Intent(getApplicationContext(), StoreLocationMapScreen.class);
            Bundle b = new Bundle();
            b.putString("name", storeDetailsModel.getStoreName());
            b.putString("address", storeDetailsModel.getAddress());
            b.putDouble("lat", Double.parseDouble(storeDetailsModel.getLatitude()));
            b.putDouble("long", Double.parseDouble(storeDetailsModel.getLongitude()));
            intent.putExtras(b);
            startActivity(intent);
        }
        logTracking(VIEWONMAPLINK);
    }

    private void handleResponse(JSONObject response){
        try {
            String url = response.getString("URL");
            //displaySnackBar(response.getString("CustomerMessage"));
            switch (url){
                case Constant.STOREDETAILS_URL:
                    StoreDetailsModel storeDetailsModel = storeDetailsController.getStoreData(response);
                    this.storeDetailsModel = storeDetailsModel;
                    setupDetailsPage(storeDetailsModel);
                    break;
                case BOOK_APPOINTMENT_URL:
                    displaySnackBar(response.getString("CustomerMessage"));
                case Constant.UPDATEFOLLOWSTATUS_URL:
                    btn_follow.setText(response.getJSONObject("Data").getInt("FollowingStatus") == 0 ? "Follow" : "Unfollow");
                    break;
                case REPORT_ERROR_URL:
                    displaySnackBar(response.getString("CustomerMessage"));
                    break;
                default: break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_share:
                this.shareUrl(shareUrl);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
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

    @Override
    public void onClicked(int position, ArrayList<String> imageModels) {

        ArrayList<String> imageModels1 = imageModels;
        Intent intent = new Intent(StoreDetailsActivity.this, FullscreenGalleryActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, imageModels1);
        intent.putExtra(Constant.PRE_PAGE_KEY, Integer.parseInt(Constant.STORELISTINGPAGE));
        intent.putExtra(Constant.POSITION_KEY, position);
        startActivity(intent);

    }

    @OnClick(R.id.ll_call)
    public void onCallPressed() {
        ArrayList jsonArray = storeDetailsModel.getPhone();
        if (jsonArray.size()==0)
            displaySnackBar("Number not Found");
        else {
            showPopupPhoneNumber(storeDetailsModel);
        }
    }


    @OnClick(R.id.ll_address)
    public void onAddressPressed() {
        showAddress(storeDetailsModel);
    }


    @OnClick(R.id.ll_getdirec)
    public void onDirePressed() {
        showLocation(storeDetailsModel);
    }


    @OnClick(R.id.tv_book_app)
    public void onBookPressed() {
        showBookPopup();
    }

}