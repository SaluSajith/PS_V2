package com.hit.pretstreet.pretstreet.navigation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.location.StoreLocationMapScreen;
import com.hit.pretstreet.pretstreet.navigation.controllers.DetailsPageController;
import com.hit.pretstreet.pretstreet.navigation.controllers.HomeFragmentController;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;
import com.hit.pretstreet.pretstreet.storedetails.adapters.GalleryAdapter;
import com.hit.pretstreet.pretstreet.storedetails.interfaces.ImageClickCallback;
import com.hit.pretstreet.pretstreet.storedetails.model.StoreDetailsModel;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers.SubCategoryController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXARTICLEREGISTER;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITIONLIKE_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITIONREGISTER_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
/**
 * Exhibition Details page
 * @ features : view, call, gallery, register option
 **/
public class ExhibitionDetailsActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, ImageClickCallback {

    JsonRequestController jsonRequestController;
    HomeFragmentController homeFragmentController;
    DetailsPageController detailsPageController;
    SubCategoryController subCategoryController;

    @BindView(R.id.tv_product) TextViewPret tv_product;
    @BindView(R.id.tv_about) TextViewPret tv_about;
    @BindView(R.id.tv_about_heading) TextViewPret tv_about_heading;
    @BindView(R.id.tv_heading_hrs) TextViewPret tv_heading_hrs;
    @BindView(R.id.tv_imgsrc) TextViewPret tv_imgsrc;

    @BindView(R.id.tv_storename) TextViewPret tv_storename;
    @BindView(R.id.tv_location) TextViewPret tv_location;
    @BindView(R.id.tv_openstatus) TextViewPret tv_openstatus;
    @BindView(R.id.tv_time) TextViewPret tv_time;
    @BindView(R.id.tv_openinghrs) TextViewPret tv_openinghrs;
    @BindView(R.id.tv_book_app) TextViewPret tv_book_app;
    @BindView(R.id.tv_heading_photos) TextViewPret tv_photos_heading;
    @BindView(R.id.tv_testimonials_heading) TextViewPret tv_testimonials_heading;
    @BindView(R.id.tv_folowerscount) TextViewPret tv_folowerscount;

    @BindView(R.id.rv_images) RecyclerView rv_images;
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.btn_follow) ButtonPret btn_follow;

    @BindView(R.id.iv_sale) AppCompatImageView iv_sale;
    @BindView(R.id.iv_offer) AppCompatImageView iv_offer;
    @BindView(R.id.iv_new) AppCompatImageView iv_new;
    @BindView(R.id.ib_like)AppCompatImageView ib_like;

    @BindView(R.id.ll_call) LinearLayout ll_call;
    @BindView(R.id.ll_address) LinearLayout ll_address;
    @BindView(R.id.ll_getdirec) LinearLayout ll_getdirec;

    String mStoreId;
    Context context;
    private StoreDetailsModel exhibitionDetailsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibition_details);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUi();
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
        detailsPageController = new DetailsPageController(this);
        homeFragmentController = new HomeFragmentController(this);
        subCategoryController = new SubCategoryController(this);
    }

    private void initUi(){
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        context = getApplicationContext();
        exhibitionDetailsModel = new StoreDetailsModel();

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager
                (3, LinearLayoutManager.VERTICAL);
        rv_images.setLayoutManager(staggeredGridLayoutManager);

        TrendingItems trendingItems = (TrendingItems)getIntent()
                .getSerializableExtra(Constant.PARCEL_KEY);
        String pagekey = trendingItems.getPagetypeid();
        String clicktype = trendingItems.getClicktype();
        mStoreId = trendingItems.getId();
        getShopDetails(mStoreId, clicktype, pagekey);
        setVisibility();
    }

    private void setVisibility(){
        iv_new.setVisibility(View.GONE);
        iv_offer.setVisibility(View.GONE);
        iv_sale.setVisibility(View.GONE);
        btn_follow.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        tv_testimonials_heading.setVisibility(View.GONE);
        tv_folowerscount.setVisibility(View.GONE);
        tv_about.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
    }

    @OnClick(R.id.ib_like)
    public void onLikePressed() {
        JSONObject resultJson = homeFragmentController.getTrendinglikeJson(mStoreId ,
                getIntent().getStringExtra(Constant.PRE_PAGE_KEY));
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, EXHIBITIONLIKE_URL);
    }

    private void getShopDetails(String storeId, String clicktype, String pagekey){
        JSONObject resultJson = detailsPageController.getExhibitionArticle(pagekey, clicktype, storeId);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, Constant.EXHIBITIONARTICLE_URL);
    }

    @OnClick(R.id.btn_follow)
    public void onFollowPressed() {
        JSONObject resultJson = subCategoryController.updateFollowCount(mStoreId,
                getIntent().getStringExtra(Constant.PRE_PAGE_KEY), Constant.FOLLOWLINK);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, Constant.UPDATEFOLLOWSTATUS_URL);
    }

    private void setupDetailsPage(StoreDetailsModel exhibitionDetailsModel){
        try {
            tv_about_heading.setVisibility(exhibitionDetailsModel.getAbout().trim().length() > 0 ? View.VISIBLE : View.GONE);
            tv_about.setVisibility(exhibitionDetailsModel.getAbout().trim().length() > 0 ? View.VISIBLE : View.GONE);
            tv_imgsrc.setVisibility(exhibitionDetailsModel.getImageSource().length() > 0 ? View.VISIBLE : View.GONE);
            tv_product.setVisibility(exhibitionDetailsModel.getProducts().length() > 0 ? View.VISIBLE : View.GONE);
            tv_time.setVisibility(exhibitionDetailsModel.getTimingToday().length() > 0 ? View.VISIBLE : View.GONE);
            tv_heading_hrs.setVisibility(exhibitionDetailsModel.getArrayListTimings().size() > 0 ? View.VISIBLE : View.GONE);
            tv_photos_heading.setVisibility(exhibitionDetailsModel.getArrayListImages().size() > 0 ? View.VISIBLE : View.GONE);

            setupCollapsingHeader(exhibitionDetailsModel.getStoreName(), exhibitionDetailsModel.getBaseImage());
            setRegisterVisibility(exhibitionDetailsModel.getRegisterStatus());
            tv_storename.setText(exhibitionDetailsModel.getStoreName());
            tv_location.setText(exhibitionDetailsModel.getAreaCity());
            tv_openstatus.setText(exhibitionDetailsModel.getOpenStatus() == false ? "Closed" : "Open now");
            tv_about.setText(exhibitionDetailsModel.getAbout());
            tv_time.setText(" - " + exhibitionDetailsModel.getTimingToday());
            String sourceString = "<b>" + "Product: " + "</b> " + exhibitionDetailsModel.getProducts();
            tv_product.setText(Html.fromHtml(sourceString));
            sourceString = "<b>" + "Image Source: " + "</b> " + exhibitionDetailsModel.getImageSource();
            //tv_product.setText(tv_product.getText()+"/n"+exhibitionDetailsModel.get);
            tv_imgsrc.setText(Html.fromHtml(sourceString));

            ArrayList arrayListTimings = exhibitionDetailsModel.getArrayListTimings();
            StringBuilder strTiming = new StringBuilder();
            for (Object timing : arrayListTimings) {
                strTiming.append(timing + "<br/>" + "<br/>");
            }
            String sourceStringTime = strTiming.toString();
            if(sourceStringTime.trim().length()==0)
                tv_openinghrs.setVisibility(View.GONE);
            else
                tv_openinghrs.setText(Html.fromHtml(sourceStringTime));
            setupGallery(exhibitionDetailsModel.getArrayListImages());
        }catch (Exception e){}
    }

    private void setRegisterVisibility(String flag){
        if(flag.contains("0")){
            tv_book_app.setClickable(true);
            tv_book_app.setText("Register");
            tv_book_app.setVisibility(View.VISIBLE);
            tv_book_app.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
            tv_book_app.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
        }
        else if(flag.contains("1")){
            tv_book_app.setClickable(false);
            tv_book_app.setText("Registered");
            tv_book_app.setVisibility(View.VISIBLE);
            tv_book_app.setTextColor(ContextCompat.getColor(context, R.color.white));
            tv_book_app.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
        }
        else{
            tv_book_app.setEnabled(false);
            tv_book_app.setVisibility(View.GONE);
        }
    }

    private void setupCollapsingHeader(String title, String img){

        CollapsingToolbarLayout collapsingToolbar =
                findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(title);
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(context, R.color.transparent)); // transperent color = #00000000
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(context, R.color.white));
        loadBackdrop(img);
    }

    private void setupGallery(ArrayList<String> arrayListImages){
        if(arrayListImages.size()==0){
            tv_photos_heading.setVisibility(View.GONE);
        }
        GalleryAdapter storeList_recyclerAdapter = new GalleryAdapter(ExhibitionDetailsActivity.this, arrayListImages);
        rv_images.setAdapter(storeList_recyclerAdapter);
    }

    private void loadBackdrop(String imageUrl) {
        final AppCompatImageView imageView = findViewById(R.id.backdrop);
        Glide.with(context).load(imageUrl).asBitmap().fitCenter().placeholder(R.drawable.default_banner)
                .diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into(imageView);
    }

    public void showPopupPhoneNumber(StoreDetailsModel storeDetailsModel) {
        if (storeDetailsModel.getPhone().size()==0) {
            displaySnackBar("Number not found");
        } else {
            final Dialog popupDialog = new Dialog(ExhibitionDetailsActivity.this);
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") final View view = li.inflate(R.layout.popup_phone_number, null);
            TextViewPret txt_cat = view.findViewById(R.id.txt_cat);
            RelativeLayout rl_phone1 = view.findViewById(R.id.rl_phone1);
            RelativeLayout rl_phone2 = view.findViewById(R.id.rl_phone2);
            RelativeLayout rl_phone3 = view.findViewById(R.id.rl_phone3);

            TextViewPret txt_phone1 = view.findViewById(R.id.txt_phone1);
            TextViewPret txt_phone2 = view.findViewById(R.id.txt_phone2);
            TextViewPret txt_phone3 = view.findViewById(R.id.txt_phone3);

            final ArrayList<String> arrayList = storeDetailsModel.getPhone();
            txt_cat.setText(storeDetailsModel.getStoreName());

            switch (arrayList.size()) {
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
            WindowManager.LayoutParams params = popupDialog.getWindow().getAttributes();
            popupDialog.getWindow().setAttributes(params);
            popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupDialog.show();

            AppCompatImageView img_close = view.findViewById(R.id.img_close);
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
        }
    }

    private void dialPhone(String phone){
        if (phone.equalsIgnoreCase("null") ||
                phone.equalsIgnoreCase("")) {
            displaySnackBar("Number not found");
        } else {
            MyPhoneListener phoneListener = new MyPhoneListener();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            try {
                String uri = "tel:" + phone;
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                startActivity(dialIntent);
            } catch (Exception e) {
                Toast.makeText(context, "Call Failed", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
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
                        Intent restart = context.getPackageManager().
                                getLaunchIntentForPackage(context.getPackageName());
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
        if (storeDetailsModel.getAddress().equalsIgnoreCase("null") ||
                storeDetailsModel.getAddress().equalsIgnoreCase("")) {
            displaySnackBar("Address not found");
        } else {
            final Dialog popupDialog = new Dialog(ExhibitionDetailsActivity.this);
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = li.inflate(R.layout.popup_phone_number, null);
            TextViewPret txt_cat = view.findViewById(R.id.txt_cat);
            AppCompatImageView img_close = view.findViewById(R.id.img_close);
            TextViewPret txt_address1 = view.findViewById(R.id.txt_address);
            RelativeLayout rl_phone1 = view.findViewById(R.id.rl_phone1);
            RelativeLayout rl_phone2 = view.findViewById(R.id.rl_phone2);
            RelativeLayout rl_phone3 = view.findViewById(R.id.rl_phone3);
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
        }
    }

    private void showLocation(StoreDetailsModel storeDetailsModel){
        if (storeDetailsModel.getLatitude().equalsIgnoreCase("") ||
                storeDetailsModel.getLongitude().equalsIgnoreCase("")) {
            displaySnackBar("Location not found");
        } else {
            try {
                Intent intent = new Intent(context, StoreLocationMapScreen.class);
                Bundle b = new Bundle();
                b.putString("name", storeDetailsModel.getStoreName());
                b.putString("address", storeDetailsModel.getAddress());
                b.putDouble("lat", Double.parseDouble(storeDetailsModel.getLatitude()));
                b.putDouble("long", Double.parseDouble(storeDetailsModel.getLongitude()));
                intent.putExtras(b);
                startActivity(intent);
            }catch (Exception e){}
        }
    }

    @OnClick(R.id.tv_book_app)
    public void onRegisterPressed() {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        LoginSession loginSession = sharedPreferencesHelper.getUserDetails();
        String phone = loginSession.getMobile();
        if(phone.trim().length()==0||phone.equalsIgnoreCase("null")){
            showRegisterPopup();
        }
        else {
            this.showProgressDialog(getResources().getString(R.string.loading));
            JSONObject resultJson = homeFragmentController.getExhibitionRegisterJson(EXARTICLEREGISTER,
                    mStoreId + "", getIntent().getStringExtra(PRE_PAGE_KEY), phone);
            jsonRequestController.sendRequest(this, resultJson, EXHIBITIONREGISTER_URL);
        }
    }

    public void showRegisterPopup() {

        final Dialog popupDialog = new Dialog(ExhibitionDetailsActivity.this);
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
        AppCompatImageView img_close = view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });

        ButtonPret btn_send = view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regexStr = "^[789]\\d{9}$";
                String number = edt_phone.getText().toString();
                if(edt_phone.getText().toString().length()<10 || number.length()>13 || number.matches(regexStr)==false  ) {
                    edt_phone.setError("Invalid phone number!");
                }
                else{
                    popupDialog.dismiss();
                    ExhibitionDetailsActivity.this.showProgressDialog(getResources().getString(R.string.loading));
                    JSONObject resultJson = homeFragmentController.getExhibitionRegisterJson(EXARTICLEREGISTER,
                            mStoreId + "", getIntent().getStringExtra(PRE_PAGE_KEY), number);
                    jsonRequestController.sendRequest(ExhibitionDetailsActivity.this, resultJson, EXHIBITIONREGISTER_URL);
                }
            }
        });
    }

    /**Handling response corresponding to the URL
     * @param response response corresponding to each URL - here I am appending the URL itself
     *                 to the response so that I will be able to handle each response seperately*/
    private void handleResponse(JSONObject response){
        try {
            String url = response.getString("URL");
            //displaySnackBar(response.getString("CustomerMessage"));
            switch (url){
                case Constant.EXHIBITIONARTICLE_URL:
                    exhibitionDetailsModel = detailsPageController.getExhibitionData(response);
                    ib_like.setTag(exhibitionDetailsModel.getFollowingStatus() == false ? R.drawable.grey_heart : R.drawable.red_heart);
                    ib_like.setImageResource(exhibitionDetailsModel.getFollowingStatus() == false ? R.drawable.grey_heart : R.drawable.red_heart);
                    setupDetailsPage(exhibitionDetailsModel);
                    break;
                case EXHIBITIONLIKE_URL:
                    JSONObject object = response.getJSONObject("Data");
                    exhibitionDetailsModel.setFollowingStatus(object.getInt("LikeStatus")== 0 ? false : true);
                    ib_like.setTag(object.getInt("LikeStatus") == 1 ? R.drawable.red_heart : R.drawable.grey_heart);
                    ib_like.setImageResource(object.getInt("LikeStatus") == 1 ? R.drawable.red_heart : R.drawable.grey_heart);
                    break;
                case EXHIBITIONREGISTER_URL:
                    object = response.getJSONObject("Data");
                    displaySnackBar(response.getString("CustomerMessage"));
                    if(object.getInt("LikeStatus")==1){
                        tv_book_app.setEnabled(false);
                        tv_book_app.setText("Registered");
                        tv_book_app.setTextColor(ContextCompat.getColor(context, R.color.white));
                        tv_book_app.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
                    }
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
                shareUrl(exhibitionDetailsModel.getShare());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putString(ID_KEY, exhibitionDetailsModel.getId());
        int likeStatus = exhibitionDetailsModel.getFollowingStatus() == true ? 1 : 0;
        b.putString(PARCEL_KEY, likeStatus+"");
        intent.putExtras(b);
        setResult(RESULT_OK, intent);
        finish();
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
        Intent intent = new Intent(ExhibitionDetailsActivity.this, FullscreenGalleryActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, imageModels1);
        intent.putExtra(Constant.PRE_PAGE_KEY, Integer.parseInt(Constant.STORELISTINGPAGE));
        intent.putExtra(Constant.POSITION_KEY, position);
        startActivity(intent);

    }

    @OnClick(R.id.ll_call)
    public void onCallPressed() {
        try {
            ArrayList jsonArray = exhibitionDetailsModel.getPhone();
            if (jsonArray.size() == 0)
                displaySnackBar("Number not found");
            else
                showPopupPhoneNumber(exhibitionDetailsModel);
        }catch (Exception e){}
    }

    @OnClick(R.id.ll_address)
    public void onAddressPressed() {
        showAddress(exhibitionDetailsModel);
    }

    @OnClick(R.id.ll_getdirec)
    public void onDirePressed() {
        showLocation(exhibitionDetailsModel);
    }

}