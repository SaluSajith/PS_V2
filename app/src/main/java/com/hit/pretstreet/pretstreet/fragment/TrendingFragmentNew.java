package com.hit.pretstreet.pretstreet.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.Items.TrendingItems;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.customview.CircularImageView;
import com.hit.pretstreet.pretstreet.customview.DividerDecoration;
import com.hit.pretstreet.pretstreet.interfaces.ZoomedViewListener;
import com.hit.pretstreet.pretstreet.ui.SelectLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by HIT on 22-02-2017.
 */

public class TrendingFragmentNew extends Fragment implements View.OnClickListener, ZoomedViewListener{

    private String mainCAtId, name, image;
    private ProgressDialog pDialog;
    private RecyclerView rv_trending;
    ArrayList<TrendingItems> list;
    TrendingAdapter adapter;

    private FrameLayout  ll_header;
    private NestedScrollView nsv_header;
    private ImageView img_icon_menu, img_notification, img_filter, img_search, img_expand,img_back;
    private TextView txt_location, txt_cat_name, txt_recent;
    private boolean loading = true, first = true;
    int visibleItemCount;
    int offset=0, pagecount =0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trending_screen_new, null);

        Bundle bundle = this.getArguments();
        mainCAtId = bundle.getString("main_cat_id");
        name = bundle.getString("main_cat_name");
        image = bundle.getString("image");
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        list = new ArrayList<>();

        ll_header= (FrameLayout) view.findViewById(R.id.ll_header);
        rv_trending = (RecyclerView) view.findViewById(R.id.rv_trending);
        nsv_header = (NestedScrollView) view.findViewById(R.id.nsv_header);

        img_icon_menu = (ImageView) view.findViewById(R.id.img_icon_menu);
        img_notification = (ImageView) view.findViewById(R.id.img_notification);
        img_filter = (ImageView) view.findViewById(R.id.img_filter);
        img_search = (ImageView) view.findViewById(R.id.img_search);
        img_expand = (ImageView) view.findViewById(R.id.img_expand);
        img_back = (ImageView) view.findViewById(R.id.img_back);

        txt_location = (TextView) view.findViewById(R.id.txt_location);
        txt_recent = (TextView) view.findViewById(R.id.txt_recent);
        txt_cat_name = (TextView) view.findViewById(R.id.txt_cat_name);
        txt_location.setTypeface(font);
        txt_recent.setTypeface(font);
        txt_cat_name.setTypeface(font);
        txt_cat_name.setText(name);

        txt_location.setOnClickListener(this);
        img_icon_menu.setOnClickListener(this);
        img_notification.setOnClickListener(this);
        img_search.setOnClickListener(this);
        img_filter.setOnClickListener(this);
        img_expand.setOnClickListener(this);
        img_back.setOnClickListener(this);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) txt_cat_name.getLayoutParams();
        int margin = (int) getResources().getDimension(R.dimen.text_margintop);
        lp.setMargins(10, margin, 0, 0);
        txt_cat_name.setLayoutParams(lp);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_trending.setLayoutManager(mLayoutManager);
        rv_trending.addItemDecoration(new DividerDecoration(getActivity(), getResources().getColor(R.color.trending_grey), 5.0f));
        rv_trending.setNestedScrollingEnabled(false);

        nsv_header.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if (scrollY > oldScrollY) {
                        visibleItemCount = mLayoutManager.getChildCount();

                        int visibleItemCount = mLayoutManager.getChildCount();
                        final int lastItem = mLayoutManager.findFirstVisibleItemPosition() + visibleItemCount;

                        if (lastItem == mLayoutManager.getItemCount() ) {
                            if(!loading)
                                if(offset<pagecount)
                                    getData();
                        }

                        /*if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                            Log.i("hitserver", "BOTTOM SCROLL");
                            if(!requestCalled)
                                getStoreList_distancewise(LLSelectedID, false);
                        }*/

                    }
                }
            }
        });

        ll_header.bringToFront();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        txt_location.setText(PreferenceServices.getInstance().getCurrentLocation());
        getData();
    }

    private void getData() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL ;

            URL = Constant.TRENDING_API + "ftc";
            loading = true;

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("UserId", PreferenceServices.getInstance().geUsertId());
            jsonBody.put("Limit", Constant.LIMIT);
            jsonBody.put("Offset", ++offset);
            jsonBody.put("ApiKey", Constant.API);
            final String requestBody = jsonBody.toString();
            Log.d("URL", URL+"offset "+offset);
            if(first) {
                showpDialog();
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Trending_api_response", String.valueOf(response));
                            try {
                                JSONObject jsonObject =  new JSONObject(response);
                                boolean responseSuccess = false;
                                String strsuccess;
                                try {
                                    strsuccess = jsonObject.getString("Status");
                                    pagecount = jsonObject.getInt("PageCount");
                                    if (strsuccess.equals("1")) {
                                        responseSuccess = true;
                                        JSONArray jsonArray = null;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                            jsonArray = jsonObject.getJSONArray("TrendingContent");

                                            TrendingItems item;
                                            if (list == null)
                                                list = new ArrayList<>();
                                            /*else
                                                list.clear();*/
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject trendingContent = jsonArray.getJSONObject(i);
                                                item = new TrendingItems();
                                                item.setId(trendingContent.getString("tcId"));
                                                item.setStoreLink(trendingContent.getString("Storelink"));
                                                item.setLogoImage(trendingContent.getString("LogoImage"));
                                                item.setTitle(trendingContent.getString("Title"));
                                                item.setArticle(trendingContent.getString("Article"));
                                                item.setLike((trendingContent.getInt("CustomerLike")+""));
                                                item.setStoreName(trendingContent.getString("Storename"));

                                                JSONArray jsonImagearray = trendingContent.getJSONArray("ImageArray");
                                                ArrayList imagearray = new ArrayList();
                                                for(int j=0;j<jsonImagearray.length();j++) {
                                                    imagearray.add(jsonImagearray.get(j));
                                                }
                                                item.setImagearray(imagearray);

                                                try {
                                                    DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa");
                                                    DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                                                    String inputDateStr = trendingContent.getString("ArticleDate");
                                                    Date date = inputFormat.parse(inputDateStr);
                                                    String outputDateStr = outputFormat.format(date);
                                                    item.setArticledate(outputDateStr);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                list.add(item);
                                            }
                                        }
                                        loading = false;
                                    } else {
                                        //Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                        responseSuccess = false;
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                    responseSuccess = false;
                                }
                                if (responseSuccess && list.size()>0) {
                                    //if (adapter == null) {

                                    adapter = new TrendingAdapter(getActivity(), list);
                                    rv_trending.setAdapter(adapter);
                                    // } else
                                    //     adapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(first)
                                first = false;
                            hidepDialog();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Trending_api_error", error.toString());
                    if(first)
                        first = false;
                    hidepDialog();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();

        }
        /*stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(stringRequest, Constant.tag_json_obj);*/
    }

    private void showpDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
            pDialog.setContentView(R.layout.progress_activity);
            pDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        Bundle bundle;
        switch (viewId) {

            case R.id.img_expand:
                startActivity(new Intent(getActivity(), SelectLocation.class).putExtra("location", "default"));
                break;

            case R.id.txt_location:
                startActivity(new Intent(getActivity(), SelectLocation.class).putExtra("location", "default"));
                break;

            case R.id.img_notification:
                Fragment f3 = new FollowersFragment();
                Bundle b3 = new Bundle();
                b3.putString("screen", "notifications");
                f3.setArguments(b3);
                FragmentTransaction t3 = getFragmentManager().beginTransaction();
                t3.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t3.add(R.id.frame_container, f3);
                t3.addToBackStack(null);
                t3.commit();
                break;

            case R.id.img_search:
                Fragment f1 = new SearchFragment();
                FragmentTransaction t1 = getFragmentManager().beginTransaction();
                t1.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t1.add(R.id.frame_container, f1);
                t1.addToBackStack(null);
                t1.commit();
                break;

            case R.id.img_back:
                getActivity().onBackPressed();
                break;

            default:
                break;
        }
    }

    @Override
    public void onClicked(int position, ArrayList<StoreDetailFragment.ProductImageItem> mImagearray) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("images", mImagearray);
        bundle.putInt("position", position);
        bundle.putInt("countvisibility", 0);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");
    }

    private class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder>{

        Context context;
        ArrayList<TrendingItems> list;
        int button01pos = 0;
        ArticlePagerAdapter mAdapter;
        int dotsCount = 0;

        public TrendingAdapter(Context context, ArrayList<TrendingItems> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(viewType, parent, false), viewType);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            TrendingItems trendingItems = list.get(position);
            holder.txt_date.setText(trendingItems.getArticledate());
            holder.txt_title.setText(trendingItems.getTitle());
            holder.txt_description.setText(trendingItems.getArticle());

            if(trendingItems.getImagearray().size()==0){
                holder.iv_banner.setVisibility(View.VISIBLE);
                holder.iv_banner.setImageResource(R.mipmap.ic_launcher);
            }
            else {
                holder.iv_banner.setVisibility(View.INVISIBLE);
                mAdapter = new ArticlePagerAdapter(context, trendingItems.getImagearray());
                holder.article_images.setAdapter(mAdapter);
                holder.article_images.setCurrentItem(0);
                if(trendingItems.getImagearray().size()>1){
                    setUiPageViewController(holder, position);
                }
            }

            String udata = trendingItems.getStoreName();
            SpannableString content = new SpannableString(udata);
            content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
            holder.txt_shopname.setText(content);

            if(!trendingItems.getStoreLink().equals("0")){
                Glide.with(getActivity())
                        .load(trendingItems.getLogoImage())
                        .centerCrop()
                        //.placeholder(R.mipmap.ic_launcher)
                        .into(holder.img_profile);
            }
            else{
                holder.img_profile.setImageResource(R.mipmap.ic_launcher);
                Bitmap bitmap = ((BitmapDrawable)holder.img_profile.getDrawable()).getBitmap();
                holder.img_profile.setImageBitmap(getCircleBitmap(bitmap));
            }

            if(trendingItems.getLike().equals("0")){
                button01pos = 0;
                holder.img_like.setImageResource(R.drawable.grey_heart);
            }
            else{
                button01pos = 1;
                holder.img_like.setImageResource(R.drawable.red_heart);
            }

        }

        private void setUiPageViewController(ViewHolder holder, int position) {

            dotsCount = list.get(position).getImagearray().size();
            holder.dots = new ImageView[dotsCount];
            holder.pager_indicator.removeAllViews();

            for (int i = 0; i < dotsCount; i++) {
                //for (Iterator it = list.iterator(); it.hasNext(); ) {
                holder.dots[i] = new ImageView(getActivity());
                holder.dots[i].setImageDrawable(getResources().getDrawable(R.drawable.image_indicator_unselected));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(4, 0, 4, 0);
                holder.pager_indicator.addView(holder.dots[i], params);
            }
            if(holder.dots.length>0)
                holder.dots[0].setImageDrawable(getResources().getDrawable(R.drawable.image_indicator_selected));
        }

        private Bitmap getCircleBitmap(Bitmap bitmap) {

            Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            Canvas c = new Canvas(circleBitmap);
            c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
            return circleBitmap;
        }

        @Override
        public int getItemCount() {
            return list.size() ;
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.row_trending_data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements
                ViewPager.OnPageChangeListener, View.OnClickListener {

            View view;
            int viewType;
            ImageView img_like, img_share, iv_banner;
            CircularImageView img_profile;
            TextView txt_title, txt_description, txt_shopname, txt_date;
            ViewPager article_images;
            LinearLayout pager_indicator;
            ImageView[] dots;

            public ViewHolder(View itemView, int viewType) {
                super(itemView);

                this.viewType = viewType;
                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
                Typeface fontDesc = Typeface.createFromAsset(getActivity().getAssets(), "Merriweather Light.ttf");

                img_like = (ImageView) itemView.findViewById(R.id.iv_like);
                img_share = (ImageView) itemView.findViewById(R.id.iv_share);
                iv_banner = (ImageView) itemView.findViewById(R.id.iv_banner);
                img_profile = (CircularImageView) itemView.findViewById(R.id.iv_profile);
                txt_description = (TextView) itemView.findViewById(R.id.txt_description);
                txt_title = (TextView) itemView.findViewById(R.id.txt_title);
                txt_shopname = (TextView) itemView.findViewById(R.id.txt_shopname);
                txt_date = (TextView) itemView.findViewById(R.id.txt_date);

                txt_description.setTypeface(fontDesc);
                txt_date.setTypeface(fontDesc);
                txt_title.setTypeface(font);
                txt_shopname.setTypeface(font);

                article_images = (ViewPager) itemView.findViewById(R.id.pager_article);
                article_images.setOnPageChangeListener(this);
                pager_indicator = (LinearLayout) itemView.findViewById(R.id.viewPagerCountDots);

                img_like.setOnClickListener(this);
                img_share.setOnClickListener(this);
                txt_shopname.setOnClickListener(this);
                iv_banner.setOnClickListener(this);

            }

            @Override
            public void onClick(View view) {
                int viewId = view.getId();
                switch (viewId) {
                    case R.id.iv_like:
                        if (button01pos == 0) {
                            img_like.setImageResource(R.drawable.red_heart);
                            button01pos = 1;
                        } else if (button01pos == 1) {
                            img_like.setImageResource(R.drawable.grey_heart);
                            button01pos = 0;
                        }
                        sendButtonStatus(list.get(getAdapterPosition()).getId());
                        break;
                    case R.id.iv_share:
                        shareTextUrl();
                        break;
                    case R.id.txt_shopname:
                        if(!list.get(getAdapterPosition()).getStoreLink().equals("0"))
                            openStoreDetails();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.image_indicator_unselected));
                }
                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.image_indicator_selected));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            private void openStoreDetails(){
                int position = getAdapterPosition();
                clickLogTracking("storeview", list.get(getAdapterPosition()).getStoreLink());
                Fragment f1 = new StoreDetailFragment();
                Bundle b1 = new Bundle();
                b1.putString("cat_id", list.get(getAdapterPosition()).getStoreLink());
                b1.putString("cat_name", "");
                b1.putInt("position", position);
                f1.setArguments(b1);
                FragmentTransaction t1 = getFragmentManager().beginTransaction();
                t1.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t1.add(R.id.frame_container, f1);
//                    t1.replace(R.id.frame_container, f1);
                t1.addToBackStack(null);
                t1.commit();
            }

            private void clickLogTracking(String event, String storeId) {
                String urlJson = Constant.FASHION_API + "route=trackinglogs&action=" + event + "&user_id="
                        + PreferenceServices.getInstance().geUsertId() + "&pid=" + storeId;
                Log.e("URL: ", urlJson);
                final ArrayList<HashMap<String, String>> list = new ArrayList<>();
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.e("Volley", response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Volley", "Error: " + error.getMessage());
                    }
                });
                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
            }

            private void shareTextUrl() {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//        share.putExtra(Intent.EXTRA_SUBJECT, "Check this store on Prestreet App");
//        share.putExtra(Intent.EXTRA_TEXT, "Check below store on Pretstreet App:- " + href + "\n\nStore details:-"
//                + "\n" + name + "\n" + txt_address.getText().toString());
                share.putExtra(Intent.EXTRA_SUBJECT, "PrêtStreet : Your ultimate shopping guide!!!");
                share.putExtra(Intent.EXTRA_TEXT, "Discover the latest talent in Fashion Designers, brands & Jewellers." +
                        " Follow us on PrêtStreet, Your ultimate shopping guide.\n\nhttp://www.pretstreet.com/share.php");
                startActivity(Intent.createChooser(share, "Share with.."));
            }
        }
    }

    private void sendButtonStatus(String tcId){
        try {
            String URL;
                URL = Constant.TRENDING_API + "litc";
            Log.d("URL", URL);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("UserId", PreferenceServices.getInstance().geUsertId());
            jsonBody.put("tcId", tcId);
            jsonBody.put("ApiKey", Constant.API);
            final String requestBody = jsonBody.toString();

            showpDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Like_api_response", String.valueOf(response));
                            try {
                                JSONObject jsonObject =  new JSONObject(response);
                                String strsuccess;
                                try {
                                    strsuccess = jsonObject.getString("Status");
                                    if (strsuccess.equals("1")) {
                                        Log.d("Trending_api_response",strsuccess);
                                        //getTrendingData();
                                    } else {
                                        //Toast.makeText(getActivity(), "Please try after sometime.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            hidepDialog();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hidepDialog();
                    Log.d("Like_api_error", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };

            //requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();

        }
        /*stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(stringRequest, Constant.tag_json_obj);*/
    }

    private class ArticlePagerAdapter extends PagerAdapter{

        private Context mContext;
        private ArrayList<String> mResources;
        private ArrayList<StoreDetailFragment.ProductImageItem > mImagearray;

        public ArticlePagerAdapter(Context mContext, ArrayList<String> mResources) {
            this.mContext = mContext;
            this.mResources = mResources;
            mImagearray = new ArrayList<>();

            StoreDetailFragment.ProductImageItem productImageItem;
            for(int i = 0;i<mResources.size();i++) {
                productImageItem = new StoreDetailFragment.ProductImageItem();
                productImageItem.setImage(mResources.get(i));
                mImagearray.add(productImageItem);
            }
        }

        @Override
        public int getCount() {
            return mResources.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final View itemView = LayoutInflater.from(mContext).inflate(R.layout.image_slider_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
            if((mResources.get(position)).length()==0){
                imageView.setImageResource(R.mipmap.ic_launcher);
            }else {
                Glide.with(mContext)
                        .load(mResources.get(position))
                        .fitCenter()
                        .into(imageView);
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClicked(position, mImagearray);
                }
            });
            container.addView(itemView);

            return itemView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
