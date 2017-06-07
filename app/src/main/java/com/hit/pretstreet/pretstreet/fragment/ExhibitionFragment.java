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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
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
import com.hit.pretstreet.pretstreet.ui.StoreLocationMapScreen;

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

/**
 * Created by User on 5/22/2017.
 */

public class ExhibitionFragment extends Fragment implements View.OnClickListener, ZoomedViewListener {

    private String mainCAtId, name, image;
    private ProgressDialog pDialog;
    private RecyclerView rv_trending;
    ArrayList<TrendingItems> list;
    ExhibitionAdapter adapter;

    private FrameLayout ll_header;
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
        txt_cat_name = (TextView) view.findViewById(R.id.txt_cat_name);
        txt_recent = (TextView) view.findViewById(R.id.txt_recent);
        txt_location.setTypeface(font);
        txt_cat_name.setTypeface(font);
        txt_recent.setTypeface(font);
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
        rv_trending.getItemAnimator().setChangeDuration(0);

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
                    }
                }

                /*int position = mLayoutManager.getPosition(v);
                int top = nsv_header.getScrollY(); // Increases when scrolling up ^
                int newTop = (int) (top * .5f);
                mLayoutManager.getChildAt(position).setTranslationY(newTop < 0 ? 0 : newTop);*/
            }
        });
        getData();
        ll_header.bringToFront();

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        txt_location.setText(PreferenceServices.getInstance().getCurrentLocation());
    }

    private void getData() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL ;
            URL = Constant.EXHIBITION_API + "ftc";
            loading = true;

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("UserId", PreferenceServices.getInstance().geUsertId());
            jsonBody.put("Limit",Constant.LIMIT);
            jsonBody.put("Offset", ++offset);
            jsonBody.put("ApiKey", Constant.API);
            final String requestBody = jsonBody.toString();
            Log.d("URL_exh", URL+"offset "+offset);
            if(first) {
                showpDialog();
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Exhibition_api_response", String.valueOf(response));
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
                                            jsonArray = jsonObject.getJSONArray("ExhibitionContent");

                                            TrendingItems item;
                                            if (list == null)
                                                list = new ArrayList<>();
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject trendingContent = jsonArray.getJSONObject(i);
                                                item = new TrendingItems();
                                                item.setId(trendingContent.getString("tcId"));
                                                item.setLatitude(trendingContent.getString("Latitude"));
                                                item.setLongitude(trendingContent.getString("Longitude"));
                                                item.setTitle(trendingContent.getString("Title"));
                                                item.setArticle(trendingContent.getString("Article"));
                                                item.setLike((trendingContent.getInt("CustomerLike")+""));
                                                //item.setStoreName(trendingContent.getString("Storename"));

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
                                if (responseSuccess && list.size()>0 && first) {
                                    adapter = new ExhibitionAdapter(getActivity(), list);
                                    rv_trending.setAdapter(adapter);
                                }
                                else if(responseSuccess && list.size()>0 ){
                                    adapter.notifyDataSetChanged();
                                }
                                else{
                                    Toast.makeText(getActivity(), "No data found!", Toast.LENGTH_SHORT).show();
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

    private class ExhibitionAdapter extends RecyclerView.Adapter<ExhibitionAdapter.ViewHolder>{

        Context context;
        ArrayList<TrendingItems> list;
        int button01pos = 0;

        public ExhibitionAdapter(Context context, ArrayList<TrendingItems> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public ExhibitionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ExhibitionAdapter.ViewHolder(LayoutInflater.from(context).inflate(viewType, parent, false), viewType);
        }

        @Override
        public void onBindViewHolder(final ExhibitionAdapter.ViewHolder holder, int position) {

            TrendingItems trendingItems = list.get(position);
            holder.txt_date.setText(trendingItems.getArticledate());
            holder.txt_heading.setText(trendingItems.getTitle());
            holder.txt_description.setText(trendingItems.getArticle());

            Glide.with(getActivity())
                    .load(trendingItems.getImagearray().get(0))
                    .fitCenter()
                    //.placeholder(R.mipmap.ic_launcher)
                    .into(holder.iv_banner);

            if(trendingItems.getLike().equals("0")){
                button01pos = 0;
                holder.img_like.setImageResource(R.drawable.grey_heart);
            }
            else{
                button01pos = 1;
                holder.img_like.setImageResource(R.drawable.red_heart);
            }

        }

        @Override
        public int getItemCount() {
            return list.size() ;
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.row_exhibitiondata;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements
                View.OnClickListener {

            View view;
            int viewType;
            ImageView img_like, img_share, img_map, iv_banner;
            TextView txt_description, txt_heading, txt_date;
            private int mLastPosition;

            public ViewHolder(View itemView, int viewType) {
                super(itemView);

                this.viewType = viewType;
                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
                Typeface fontDesc = Typeface.createFromAsset(getActivity().getAssets(), "Merriweather Light.ttf");

                img_like = (ImageView) itemView.findViewById(R.id.iv_like);
                img_share = (ImageView) itemView.findViewById(R.id.iv_share);
                img_map = (ImageView) itemView.findViewById(R.id.iv_map);
                iv_banner = (ImageView) itemView.findViewById(R.id.iv_banner);
                txt_description = (TextView) itemView.findViewById(R.id.txt_description);
                txt_heading = (TextView) itemView.findViewById(R.id.txt_heading);
                txt_date = (TextView) itemView.findViewById(R.id.txt_date);

                txt_description.setTypeface(fontDesc);
                txt_date.setTypeface(fontDesc);
                txt_heading.setTypeface(font);

                img_like.setOnClickListener(this);
                img_share.setOnClickListener(this);
                iv_banner.setOnClickListener(this);
                img_map.setOnClickListener(this);

                float initialTranslation = (mLastPosition <= getAdapterPosition() ? 500f : -500f);
                itemView.setTranslationY(initialTranslation);
                itemView.animate()
                        .setInterpolator(new DecelerateInterpolator(1.0f))
                        .translationY(0f)
                        .setDuration(300l)
                        .setListener(null);
                mLastPosition = getAdapterPosition();

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
                    case R.id.iv_map:

                        Log.d("location","location "+ list.get(getAdapterPosition()).getLatitude());
                        Log.d("location","location "+ list.get(getAdapterPosition()).getLongitude());
                        if (list.get(getAdapterPosition()).getLatitude().equals("") ||
                                list.get(getAdapterPosition()).getLongitude().equals("")) {
                            Toast.makeText(getActivity(), "Location not Found", Toast.LENGTH_SHORT).show();
                        } else {
                            clickLogTracking("map",list.get(getAdapterPosition()).getId());
                            Intent i = new Intent(getActivity(), StoreLocationMapScreen.class);
                            Bundle b = new Bundle();
                            b.putString("name", list.get(getAdapterPosition()).getTitle());
                            b.putString("address", list.get(getAdapterPosition()).getArticle());
                            b.putDouble("lat", Double.parseDouble(list.get(getAdapterPosition()).getLatitude()));
                            b.putDouble("long", Double.parseDouble(list.get(getAdapterPosition()).getLongitude()));
                            i.putExtras(b);
                            startActivity(i);
                        }
                        break;
                    case R.id.iv_banner:
                        StoreDetailFragment.ProductImageItem productImageItem = new StoreDetailFragment.ProductImageItem();
                        ArrayList<StoreDetailFragment.ProductImageItem > mImagearray = new ArrayList<>();
                        productImageItem.setImage(list.get(getAdapterPosition()).getImagearray().get(0));
                        mImagearray.add(productImageItem);
                        onClicked(getAdapterPosition(), mImagearray);
                        break;
                    default:
                        break;
                }
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

    private void sendButtonStatus(String tcId){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL;
            URL = Constant.EXHIBITION_API + "litc";
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
                                        Toast.makeText(getActivity(), jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
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

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();

        }

    }
}