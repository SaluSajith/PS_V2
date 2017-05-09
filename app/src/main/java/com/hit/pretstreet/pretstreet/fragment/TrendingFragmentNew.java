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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.Items.TrendingItems;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.customview.DividerDecoration;
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

/**
 * Created by HIT on 22-02-2017.
 */

public class TrendingFragmentNew extends Fragment implements View.OnClickListener{

    private String mainCAtId, name, image;
    private ProgressDialog pDialog;
    private RecyclerView rv_trending;
    ArrayList<TrendingItems> list;
    TrendingAdapter adapter;

    private LinearLayout ll_category, ll_header;
    private HorizontalScrollView hsv_category;
    private ImageView img_icon_menu, img_notification, img_filter, img_search, img_header;
    private TextView txt_location, txt_cat_name;

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

        ll_header= (LinearLayout) view.findViewById(R.id.ll_header);
        rv_trending = (RecyclerView) view.findViewById(R.id.rv_trending);
        hsv_category = (HorizontalScrollView) view.findViewById(R.id.hsv_category);
        ll_category = (LinearLayout) view.findViewById(R.id.ll_category);

        img_icon_menu = (ImageView) view.findViewById(R.id.img_icon_menu);
        img_notification = (ImageView) view.findViewById(R.id.img_notification);
        img_filter = (ImageView) view.findViewById(R.id.img_filter);
        img_search = (ImageView) view.findViewById(R.id.img_search);
        img_header = (ImageView) view.findViewById(R.id.img);

        txt_location = (TextView) view.findViewById(R.id.txt_location);
        txt_cat_name = (TextView) view.findViewById(R.id.txt_cat_name);
        txt_location.setTypeface(font);
        txt_cat_name.setTypeface(font);
        txt_cat_name.setText(name);

        txt_location.setOnClickListener(this);
        img_icon_menu.setOnClickListener(this);
        img_notification.setOnClickListener(this);
        img_search.setOnClickListener(this);
        img_filter.setOnClickListener(this);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) txt_cat_name.getLayoutParams();
        lp.setMargins(10, 40, 0, 0);
        txt_cat_name.setLayoutParams(lp);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        rv_trending.setLayoutManager(layoutManager);
        rv_trending.addItemDecoration(new DividerDecoration(getActivity(), getResources().getColor(R.color.yellow), 5.0f));

        list = new ArrayList<>();

        ll_header.bringToFront();

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        txt_location.setText(PreferenceServices.getInstance().getCurrentLocation());
        getTrendingData();
    }

    private void getTrendingData() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = Constant.TRENDING_API + "ftc";
            Log.e("URL", URL);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("UserId", PreferenceServices.getInstance().geUsertId());
            jsonBody.put("Limit", Constant.LIMIT);
            jsonBody.put("Offset", 1);
            jsonBody.put("ApiKey", Constant.API);
            final String requestBody = jsonBody.toString();

            showpDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("Trending_api_response", String.valueOf(response));
                            try {
                                JSONObject jsonObject =  new JSONObject(response);
                                boolean responseSuccess = false;
                                String strsuccess;
                                try {
                                    strsuccess = jsonObject.getString("Status");
                                    if (strsuccess.equals("1")) {
                                        Log.e("Trending_api_response",strsuccess);
                                        responseSuccess = true;
                                        JSONArray jsonArray = null;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                            jsonArray = jsonObject.getJSONArray("TrendingContent");
                                            TrendingItems item;
                                            if (list == null)
                                                list = new ArrayList<>();
                                            else
                                                list.clear();
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject trendingContent = jsonArray.getJSONObject(i);
                                                item = new TrendingItems();
                                                item.setId(trendingContent.getString("tcId"));
                                                item.setStoreLink(trendingContent.getString("Storelink"));
                                                item.setLogoImage(trendingContent.getString("LogoImage"));
                                                item.setTitle(trendingContent.getString("Title"));
                                                item.setArticle(trendingContent.getString("Article"));
                                                item.setLike(trendingContent.getString("CustomerLike"));
                                                item.setStoreName(trendingContent.getString("Storename"));

                                                ArrayList imagearray = new ArrayList();
                                                JSONArray jsonImagearray = trendingContent.getJSONArray("ImageArray");
                                                for(int j=0;j<jsonImagearray.length();j++) {
                                                    imagearray.add(jsonImagearray.get(j));
                                                }
                                                item.setImagearray(imagearray);
                                                try {
                                                DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aaa");
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
                                    } else {
                                        //Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                        responseSuccess = false;
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                    responseSuccess = false;
                                }
                                if (responseSuccess && list.size()>0) {
                                    if (adapter == null) {
                                        adapter = new TrendingAdapter(getActivity(), list);
                                        rv_trending.setAdapter(adapter);
                                    } else
                                        adapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            hidepDialog();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Trending_api_error", error.toString());
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

            case R.id.img_icon_menu:
                //startActivity(new Intent(getActivity(), HomeActivity.class));
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

            case R.id.img_filter:
                Fragment f2 = new FilterFragment();
                bundle = new Bundle();
                bundle.putString("mainCatId", mainCAtId);
                f2.setArguments(bundle);
                FragmentTransaction t2 = getFragmentManager().beginTransaction();
                t2.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t2.add(R.id.frame_container, f2);
                t2.addToBackStack(null);
                t2.commit();
                break;

            default:
                break;
        }
    }

    private class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder> {

        Context context;
        ArrayList<TrendingItems> list;
        int button01pos = 0;
        TrendingItems trendingItems;

        public TrendingAdapter(Context context, ArrayList<TrendingItems> list) {
            this.context = context;
            this.list = list;
            System.out.println("this.list.size "+this.list.size());
            trendingItems = new TrendingItems();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(viewType, parent, false), viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

               /* LinkedHashMap<String, String> trendItem = list.get(position - 1);
                Glide.with(getActivity()).load(trendItem.get("image")).centerCrop().into(holder.img_banner);
                holder.txtTitle.setText(trendItem.get("name"));
                holder.txtDescription.setText(trendItem.get("desc"));*/
            //LinkedHashMap<String, String> trendItem = list.get(position - 1);

            TrendingItems trendingItems = list.get(position);

            Glide.with(getActivity())
                    .load("http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg")
                    .centerCrop().into(holder.iv_banner);
            holder.txt_date.setText(trendingItems.getArticledate());
            holder.txt_title.setText(trendingItems.getTitle());
            holder.txt_description.setText(trendingItems.getArticle());

            String udata = trendingItems.getStoreName();
            SpannableString content = new SpannableString(udata);
            content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
            holder.txt_shopname.setText(content);

            if(!trendingItems.getStoreLink().equals("0")){
                Glide.with(getActivity())
                        .load(trendingItems.getLogoImage())
                        .centerCrop().into(holder.img_profile);
                /*Bitmap bitmap = ((BitmapDrawable)holder.img_profile.getDrawable()).getBitmap();
                holder.img_profile.setImageBitmap(getCircleBitmap(bitmap));*/
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
            //return 3;
            return list.size() ;
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.row_trending_data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            View view;
            int viewType;
            ImageView iv_banner;
            ImageView img_like, img_share, img_profile;

            TextView txt_title, txt_description, txt_shopname, txt_date;


            public ViewHolder(View itemView, int viewType) {
                super(itemView);

                view = itemView;
                this.viewType = viewType;
                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
                Typeface fontDesc = Typeface.createFromAsset(getActivity().getAssets(), "Merriweather Light.ttf");

                iv_banner = (ImageView) itemView.findViewById(R.id.iv_banner);
                img_like = (ImageView) itemView.findViewById(R.id.iv_like);
                img_share = (ImageView) itemView.findViewById(R.id.iv_share);
                img_profile = (ImageView) itemView.findViewById(R.id.iv_profile);
                txt_description = (TextView) itemView.findViewById(R.id.txt_description);
                txt_title = (TextView) itemView.findViewById(R.id.txt_title);
                txt_shopname = (TextView) itemView.findViewById(R.id.txt_shopname);
                txt_date = (TextView) itemView.findViewById(R.id.txt_date);

                txt_description.setTypeface(fontDesc);
                txt_date.setTypeface(fontDesc);
                txt_title.setTypeface(font);
                txt_shopname.setTypeface(font);

                img_like.setOnClickListener(this);
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
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void sendButtonStatus(String tcId){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = Constant.TRENDING_API + "litc";
            Log.e("URL", URL);
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
                            Log.e("Like_api_response", String.valueOf(response));
                            try {
                                JSONObject jsonObject =  new JSONObject(response);
                                String strsuccess;
                                try {
                                    strsuccess = jsonObject.getString("Status");
                                    if (strsuccess.equals("1")) {
                                        Log.e("Trending_api_response",strsuccess);
                                        getTrendingData();
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
                    Log.e("Like_api_error", error.toString());
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
