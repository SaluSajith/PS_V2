package com.hit.pretstreet.pretstreet.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hit.pretstreet.pretstreet.Items.CategoryItem;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.ui.SelectLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jesal on 28-Sep-16.
 */

public class TwoSubCategoryFragment extends Fragment implements View.OnClickListener {

    private ImageView img_icon_menu, img_notification, img_search, img_filter;
    private TextView txt_Mcat_name;
    private RelativeLayout rl_location_search;
    private LinearLayout ll_brands;
    public TextView txt_location;
    private String mainCAtId, name, savedCaTList = "";
    int deviceSize;
    private Typeface font;
    private ProgressDialog pDialog;
    private JsonObjectRequest jsonObjReq;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.three_sub_category_screen, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
        Bundle bundle = this.getArguments();
        mainCAtId = bundle.getString("main_cat_id");
        name = bundle.getString("main_cat_name");
        img_icon_menu = (ImageView) rootView.findViewById(R.id.img_icon_menu);
        img_notification = (ImageView) rootView.findViewById(R.id.img_notification);
        img_search = (ImageView) rootView.findViewById(R.id.img_search);
        img_filter = (ImageView) rootView.findViewById(R.id.img_filter);

        txt_location = (TextView) rootView.findViewById(R.id.txt_location);
        txt_Mcat_name = (TextView) rootView.findViewById(R.id.txt_cat_name);

        ll_brands = (LinearLayout) rootView.findViewById(R.id.ll_brands);
        rl_location_search = (RelativeLayout) rootView.findViewById(R.id.rl_location_search);

        txt_Mcat_name.setText(name);
        txt_location.setTypeface(font);
        txt_Mcat_name.setTypeface(font);
        rl_location_search.bringToFront();

        txt_location.setOnClickListener(this);
        img_icon_menu.setOnClickListener(this);
        img_notification.setOnClickListener(this);
        img_search.setOnClickListener(this);
        img_filter.setOnClickListener(this);

        deviceSize = PreferenceServices.getInstance().getDeviceSize();

        savedCaTList = PreferenceServices.getInstance().getThedesignerscatlist();
        if (savedCaTList.length() > 1) {
            final ArrayList<CategoryItem> list = new ArrayList<>();
            JSONObject response = null;
            try {
                response = new JSONObject(savedCaTList);
                JSONArray jsonArray = new JSONArray(response.getString("sub_cat"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CategoryItem item = new CategoryItem();
                    item.setId(jsonObject.getString("sub_cat_id"));
                    item.setCat_name(jsonObject.getString("name"));
                    item.setImage(jsonObject.getString("image"));
                    list.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ll_brands.removeAllViews();
            for (int i = 0; i < list.size(); i++) {
                LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view;
                if (i % 2 == 0) {
                    view = infl.inflate(R.layout.row_two_sub_cat_list1, null);
                } else {
                    view = infl.inflate(R.layout.row_two_sub_cat_list2, null);
                }
                RelativeLayout rl_dd = (RelativeLayout) view.findViewById(R.id.rl_dd);
                TextView txt_cat_name = (TextView) view.findViewById(R.id.txt_cat_name);
                final ImageView mImageView = (ImageView) view.findViewById(R.id.img_cat_image);
                txt_cat_name.setMaxLines(1);
                txt_cat_name.setTypeface(font);

                LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                if (deviceSize == DisplayMetrics.DENSITY_LOW) {//TODO: 120
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -25, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -30, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_MEDIUM) {//TODO: 160
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -30, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -37, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_TV) {//TODO: 213
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -40, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -55, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_HIGH) {//TODO: 240
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -45, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -55, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_XHIGH) {//TODO: 320
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -60, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -75, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_420) {//TODO: 420
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -75, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -95, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_XXHIGH) {//TODO: 480
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -85, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -110, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_560) {//TODO: 560
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -115, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -145, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_XXXHIGH) {//TODO: 640
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -115, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -150, 0, 0);
                    }
                } else {
                    //default device
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else
                            relativeParams.setMargins(0, -90, 0, 0);
                    } else {
                        relativeParams.setMargins(0, -100, 0, 0);
                    }
                }
                rl_dd.setLayoutParams(relativeParams);
                rl_dd.requestLayout();

                txt_cat_name.setText(list.get(i).getCat_name());

                DisplayMetrics displaymetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                final int dwidth = displaymetrics.widthPixels;
                final int dheight = (int) ((displaymetrics.heightPixels) * 0.5);

                final int finalI = i;
                Glide.with(getActivity()).load(list.get(i).getImage()).asBitmap()
                        .into(new BitmapImageViewTarget(mImageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                int width = resource.getWidth();
                                int height = resource.getHeight();
                                float scaleWidth = ((float) dwidth) / width;
                                float scaleHeight = ((float) dheight) / height;
                                Matrix matrix = new Matrix();
                                matrix.postScale(scaleWidth, scaleHeight);
                                Bitmap resizedBitmap = Bitmap.createBitmap(resource, 0, 0, width, height, matrix, false);

                                Bitmap mask;
                                if (finalI % 2 == 0) {
                                    mask = BitmapFactory.decodeResource(getResources(), R.drawable.twocat1);
                                } else {
                                    mask = BitmapFactory.decodeResource(getResources(), R.drawable.twocat2);
                                }
                                Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
                                Canvas mCanvas = new Canvas(result);
                                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                                //mCanvas.drawBitmap(resource, 0, 0, null);
                                mCanvas.drawBitmap(resizedBitmap, 0, 0, null);
                                mCanvas.drawBitmap(mask, 0, 0, paint);
                                paint.setXfermode(null);
                                mImageView.setImageBitmap(result);
                                mImageView.setScaleType(ImageView.ScaleType.CENTER);
                            }
                        });

                final int finalI1 = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment f1 = new CategoryWiseStoreListFragment();
                        Bundle b1 = new Bundle();
                        b1.putString("main_cat_id", mainCAtId);
                        b1.putString("sub_cat_id", list.get(finalI1).getId());
                        b1.putSerializable("cat_list", list);
                        b1.putString("isFrom", "");
                        b1.putString("main_cat_name", name);
                        f1.setArguments(b1);
                        FragmentTransaction t1 = getFragmentManager().beginTransaction();
                        t1.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                        t1.add(R.id.frame_container, f1);
                        t1.addToBackStack(null);
                        t1.commit();
                    }
                });
                ll_brands.addView(view);
            }
        }
        getCategoryList();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        jsonObjReq.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        txt_location.setText(PreferenceServices.getInstance().getCurrentLocation());
    }

    private void getCategoryList() {
        String urlJson = Constant.FASHION_API + "route=designer&parent_id=" + mainCAtId;
        if (savedCaTList.length() > 1) {
        } else {
            showpDialog();
        }
        Log.e("UYrl", urlJson);
        final ArrayList<CategoryItem> list = new ArrayList<>();
        jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                boolean responseSuccess = false;
                String strsuccess, msg = null;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equals("true")) {
                        responseSuccess = true;
                        JSONArray jsonArray = new JSONArray(response.getString("sub_cat"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CategoryItem item = new CategoryItem();
                            item.setId(jsonObject.getString("sub_cat_id"));
                            item.setCat_name(jsonObject.getString("name"));
                            item.setImage(jsonObject.getString("image"));
                            list.add(item);
                        }
                    } else {
                        responseSuccess = false;
                        msg = response.getString("sub_cat");
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    responseSuccess = false;
                }
                if (responseSuccess) {
                    PreferenceServices.instance().saveThedesignerscatlist(response.toString());
                    if (response.toString().equalsIgnoreCase(savedCaTList)) {
                    } else {
                        ll_brands.removeAllViews();
                        for (int i = 0; i < list.size(); i++) {
                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view;
                            if (i % 2 == 0) {
                                view = inflater.inflate(R.layout.row_two_sub_cat_list1, null);
                            } else {
                                view = inflater.inflate(R.layout.row_two_sub_cat_list2, null);
                            }
                            RelativeLayout rl_dd = (RelativeLayout) view.findViewById(R.id.rl_dd);
                            TextView txt_cat_name = (TextView) view.findViewById(R.id.txt_cat_name);
                            final ImageView mImageView = (ImageView) view.findViewById(R.id.img_cat_image);
                            txt_cat_name.setMaxLines(1);
                            txt_cat_name.setTypeface(font);

                            LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(
                                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));

                            if (deviceSize == DisplayMetrics.DENSITY_LOW) {//TODO: 120
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -25, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -30, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_MEDIUM) {//TODO: 160
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -30, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -37, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_TV) {//TODO: 213
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -40, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -55, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_HIGH) {//TODO: 240
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -45, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -55, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_XHIGH) {//TODO: 320
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -60, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -75, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_420) {//TODO: 420
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -75, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -95, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_XXHIGH) {//TODO: 480
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -85, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -110, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_560) {//TODO: 560
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -115, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -145, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_XXXHIGH) {//TODO: 640
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -115, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -150, 0, 0);
                                }
                            } else {
                                //default device
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else
                                        relativeParams.setMargins(0, -90, 0, 0);
                                } else {
                                    relativeParams.setMargins(0, -100, 0, 0);
                                }
                            }
                            rl_dd.setLayoutParams(relativeParams);
                            rl_dd.requestLayout();

                            txt_cat_name.setText(list.get(i).getCat_name());

                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                            final int dwidth = displaymetrics.widthPixels;
                            final int dheight = (int) ((displaymetrics.heightPixels) * 0.5);

                            final int finalI = i;
                            Glide.with(getActivity()).load(list.get(i).getImage()).asBitmap()
                                    .into(new BitmapImageViewTarget(mImageView) {
                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            int width = resource.getWidth();
                                            int height = resource.getHeight();
                                            float scaleWidth = ((float) dwidth) / width;
                                            float scaleHeight = ((float) dheight) / height;
                                            Matrix matrix = new Matrix();
                                            matrix.postScale(scaleWidth, scaleHeight);
                                            Bitmap resizedBitmap = Bitmap.createBitmap(resource, 0, 0, width, height, matrix, false);

                                            Bitmap mask;
                                            if (finalI % 2 == 0) {
                                                mask = BitmapFactory.decodeResource(getResources(), R.drawable.twocat1);
                                            } else {
                                                mask = BitmapFactory.decodeResource(getResources(), R.drawable.twocat2);
                                            }
                                            Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
                                            Canvas mCanvas = new Canvas(result);
                                            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                                            //mCanvas.drawBitmap(resource, 0, 0, null);
                                            mCanvas.drawBitmap(resizedBitmap, 0, 0, null);
                                            mCanvas.drawBitmap(mask, 0, 0, paint);
                                            paint.setXfermode(null);
                                            mImageView.setImageBitmap(result);
                                            mImageView.setScaleType(ImageView.ScaleType.CENTER);
                                        }
                                    });

                            final int finalI1 = i;
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Fragment f1 = new CategoryWiseStoreListFragment();
                                    Bundle b1 = new Bundle();
                                    b1.putString("main_cat_id", mainCAtId);
                                    b1.putString("sub_cat_id", list.get(finalI1).getId());
                                    b1.putSerializable("cat_list", list);
                                    b1.putString("isFrom", "");
                                    b1.putString("main_cat_name", name);
                                    f1.setArguments(b1);
                                    FragmentTransaction t1 = getFragmentManager().beginTransaction();
                                    t1.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                                    t1.add(R.id.frame_container, f1);
                                    t1.addToBackStack(null);
                                    t1.commit();
                                }
                            });
                            ll_brands.addView(view);
                        }
                    }
                } else {
                    if (savedCaTList.length() > 1) {
                    } else {
                        Toast.makeText(getActivity(), /*msg*/"No Stores Found", Toast.LENGTH_SHORT).show();
                    }
                }
                if (savedCaTList.length() > 1) {
                } else {
                    hidepDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Volley", "Error: " + error.getMessage());
                if (savedCaTList.length() > 1) {
                } else {
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
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
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
}
