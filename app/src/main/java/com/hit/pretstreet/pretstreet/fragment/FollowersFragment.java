package com.hit.pretstreet.pretstreet.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;

import android.widget.RelativeLayout.LayoutParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jesal on 05-Sep-16.
 */
public class FollowersFragment extends Fragment implements View.OnClickListener {
    private ImageView img_icon_menu;
    private TextView txt_cat_name, line;
    private RelativeLayout rl;
    private ListView list_notification;
    private FollowersListAdapter followersListAdapter;
    private NotificationListAdapter notificationListAdapter;
    private String screen;
    private Typeface font;
    private ProgressDialog pDialog;
    JsonObjectRequest jsonObjReqfollowers, jsonObjReqnotification;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_screen, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        img_icon_menu = (ImageView) rootView.findViewById(R.id.img_icon_menu);
        rl = (RelativeLayout) rootView.findViewById(R.id.rl);
        rl.bringToFront();

        txt_cat_name = (TextView) rootView.findViewById(R.id.txt_cat_name);
        line = (TextView) rootView.findViewById(R.id.line);
        list_notification = (ListView) rootView.findViewById(R.id.list_followers);

        img_icon_menu.setOnClickListener(this);

        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
        txt_cat_name.setTypeface(font);

        Bundle bundle = this.getArguments();
        screen = bundle.getString("screen");
        if (screen.equalsIgnoreCase("followings")) {
            txt_cat_name.setText("FOLLOWING");
            getFollowers();
        } else {
            txt_cat_name.setText("NOTIFICATION");
            getNotification();
        }
        return rootView;
    }

    private void getFollowers() {
        String urlJson = Constant.FASHION_API + "route=follow_list&user_id=" + PreferenceServices.getInstance().geUsertId();
        showpDialog();
        final ArrayList<HashMap<String, String>> list = new ArrayList<>();
        jsonObjReqfollowers = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.e("Volley", response.toString());
                boolean responseSuccess = false;
                String strsuccess, category;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equals("true")) {
                        responseSuccess = true;
                        JSONArray jsonArray = new JSONArray(response.getString("whilist"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("cat_name", jsonObject.getString("cat_name"));
                            hashMap.put("id", jsonObject.getString("id"));
                            hashMap.put("name", jsonObject.getString("name"));
                            hashMap.put("small_image", jsonObject.getString("small_image"));
                            list.add(hashMap);
                        }
                    } else {
                        responseSuccess = false;
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    responseSuccess = false;
                }
                if (responseSuccess) {
                    if (list.isEmpty()) {
                        Toast.makeText(getActivity(), "You have no Followers", Toast.LENGTH_SHORT).show();
                    } else {
                        followersListAdapter = new FollowersListAdapter(getActivity(), R.layout.row_followers1, list);
                        list_notification.setAdapter(followersListAdapter);
                        line.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getActivity(), "Server not responding Please try again...", Toast.LENGTH_SHORT).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                hidepDialog();
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjReqfollowers.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReqfollowers, Constant.tag_json_obj);
    }

    private void getNotification() {
        String urlJson = Constant.FASHION_API + "route=generalnotification&storeid=1";
        showpDialog();
        final ArrayList<HashMap<String, String>> list = new ArrayList<>();
        jsonObjReqnotification = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.e("Volley", response.toString());
                boolean responseSuccess = false;
                String strsuccess, category;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equals("true")) {
                        responseSuccess = true;
                        JSONArray jsonArray = new JSONArray(response.getString("notification"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("title", jsonObject.getString("title"));
                            list.add(hashMap);
                        }
                    } else {
                        responseSuccess = false;
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    responseSuccess = false;
                }
                if (responseSuccess) {
                    if (list.isEmpty()) {
                        Toast.makeText(getActivity(), "You have no Notifications", Toast.LENGTH_SHORT).show();
                    } else {
                        line.setVisibility(View.GONE);
                        notificationListAdapter = new NotificationListAdapter(getActivity(), R.layout.row_notification, list);
                        list_notification.setAdapter(notificationListAdapter);
                    }
                } else {
                    Toast.makeText(getActivity(), "Server not responding Please try again...", Toast.LENGTH_SHORT).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                hidepDialog();
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjReqnotification.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReqnotification, Constant.tag_json_obj);
    }

    public class FollowersListAdapter extends BaseAdapter {
        private Context context;
        int layoutResourceId;
        private ArrayList<HashMap<String, String>> mItems;

        public FollowersListAdapter(Context context, int layoutResourceId, ArrayList<HashMap<String, String>> data) {
            this.context = context;
            this.mItems = data;
            this.layoutResourceId = layoutResourceId;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ImageView img_photo, img_unfollw;
            TextView txt_name, txt_cat_name;
            LinearLayout rel;
            LayoutInflater inflater = LayoutInflater.from(context);
            if (position % 2 == 0) {
                convertView = inflater.inflate(R.layout.row_followers1, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.row_followers2, parent, false);
            }
            img_photo = (ImageView) convertView.findViewById(R.id.img_photo);
            img_unfollw = (ImageView) convertView.findViewById(R.id.img_unfollw);
            txt_cat_name = (TextView) convertView.findViewById(R.id.txt_cat_name);
            txt_name = (TextView) convertView.findViewById(R.id.txt_name);
            rel = (LinearLayout) convertView.findViewById(R.id.rel);
            txt_name.setTypeface(font);
            txt_cat_name.setTypeface(font);

            txt_cat_name.setText(mItems.get(position).get("cat_name"));
            txt_name.setText(mItems.get(position).get("name"));
            /*if (position == 0) {
                LinearLayout.LayoutParams relativeParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                relativeParams.setMargins(0, 100, 0, 0);
                img_photo.setLayoutParams(relativeParams);
                txt_name.setLayoutParams(relativeParams);
            }*/

            Glide.with(getActivity()).load(mItems.get(position).get("small_image")).asBitmap()
                    .into(new BitmapImageViewTarget(img_photo) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            Bitmap mask;
                            if (position % 2 == 0) {
                                mask = BitmapFactory.decodeResource(getResources(), R.drawable.followers_img_2);
                            } else {
                                mask = BitmapFactory.decodeResource(getResources(), R.drawable.followers_img_1);
                            }
                            Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas mCanvas = new Canvas(result);
                            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                            mCanvas.drawBitmap(resource, 0, 0, null);
                            mCanvas.drawBitmap(mask, 0, 0, paint);
                            paint.setXfermode(null);
                            img_photo.setImageBitmap(result);
                            img_photo.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                    });

            img_unfollw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unFollowStore(mItems.get(position).get("id"), position);
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment f1 = new StoreDetailFragment();
                    Bundle b1 = new Bundle();
                    b1.putString("cat_id", mItems.get(position).get("id"));
                    b1.putString("cat_name", mItems.get(position).get("name"));
                    f1.setArguments(b1);
                    FragmentTransaction t1 = getFragmentManager().beginTransaction();
                    t1.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                    t1.add(R.id.frame_container, f1);
                    //t1.replace(R.id.frame_container, f1);
                    t1.addToBackStack(null);
                    t1.commit();
                }
            });
            return convertView;
        }

        private void unFollowStore(String id, final int pos) {
            //http://doctronics.co.in/fashionapp/fashion_api.php?route=remove_follow&store_id=31&user_id=3
            String urlJsonObj = Constant.FASHION_API + "route=remove_follow&store_id=" + id + "&user_id=" + PreferenceServices.getInstance().geUsertId();
            Log.e("", urlJsonObj);
            showpDialog();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("Volley", response.toString());
                    String strsuccess, message;
                    try {
                        strsuccess = response.getString("success");
                        if (strsuccess.equalsIgnoreCase("true")) {
                            message = response.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            mItems.remove(pos);
                            notifyDataSetChanged();
                        } else {
                            message = response.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    hidepDialog();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Volley", "Error: " + error.getMessage());
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    hidepDialog();
                }
            });
            PretStreet.getInstance().addToRequestQueue(jsonObjReq);
        }
    }

    public class NotificationListAdapter extends BaseAdapter {
        private Context context;
        int layoutResourceId;
        private ArrayList<HashMap<String, String>> mItems;

        public NotificationListAdapter(Context context, int layoutResourceId, ArrayList<HashMap<String, String>> data) {
            this.context = context;
            this.mItems = data;
            this.layoutResourceId = layoutResourceId;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class RecordHolder {
            ImageView img_photo;
            TextView txt_store_discount;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final RecordHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.row_notification, parent, false);
                viewHolder = new RecordHolder();
                viewHolder.img_photo = (ImageView) convertView.findViewById(R.id.img_photo);
                viewHolder.txt_store_discount = (TextView) convertView.findViewById(R.id.txt_store_discount);
                viewHolder.txt_store_discount.setTypeface(font);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RecordHolder) convertView.getTag();
            }
            viewHolder.txt_store_discount.setText(mItems.get(position).get("title"));
            if (position == 0) {
                //commented because of issue by nitin
                /*LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));*/

                LinearLayout.LayoutParams relativeParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);

                relativeParams.setMargins(0, 100, 0, 0);
                viewHolder.txt_store_discount.setLayoutParams(relativeParams);
            }
            return convertView;
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
        switch (viewId) {

            case R.id.img_icon_menu:
                // startActivity(new Intent(getActivity(), HomeActivity.class));
                break;

            default:
                break;
        }

    }
}


