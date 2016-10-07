package com.hit.pretstreet.pretstreet.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.hit.pretstreet.pretstreet.ui.HomeActivity;
import com.hit.pretstreet.pretstreet.ui.SelectLocation;
import com.hit.pretstreet.pretstreet.ui.StoreLocationMapScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Jesal on 05-Sep-16.
 */
public class StoreDetailFragment extends Fragment implements View.OnClickListener {
    private ImageView img_icon_menu, img_search, img_filter, img_address, img_photo, img_call, img_map,
            img_upload_pic, img_sale, img_new_arrival, img_notification;
    private TextView txt_location, txt_store_name, txt_storename, txt_address, txt_website,
            txt_website_value, txt_information, txt_timing, txt_opentime, txt_closetime,
            txt_close_on1, txt_close_on2, txt_folleowercount, img_follow_unfollow;
    private RelativeLayout rl_pic, rl_location_search, rl_header;
    private LinearLayout ll_scroll_photo;

    private ArrayList<ProductImageItem> returnObjimage;
    private ProductImageItem productImageItem;
    private DisplayMetrics dMetric;
    private String id, name, phone1, phone2, phone3, href;

    private Bitmap bitmap;
    private String encodedImage;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private int StarRating, rateValue = 0;
    private String rateTitle = "";
    Dialog popupDialog;
    private Typeface font, fontM;
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.store_detail_screen, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        Bundle bundle = this.getArguments();
        id = bundle.getString("cat_id");
        name = bundle.getString("cat_name");
        Constant.hide_keyboard(getActivity());
        img_icon_menu = (ImageView) rootView.findViewById(R.id.img_icon_menu);
        //img_share = (ImageView) rootView.findViewById(R.id.img_share);
        img_search = (ImageView) rootView.findViewById(R.id.img_search);
        img_filter = (ImageView) rootView.findViewById(R.id.img_filter);
        img_photo = (ImageView) rootView.findViewById(R.id.img_photo);
        img_call = (ImageView) rootView.findViewById(R.id.img_call);
        img_sale = (ImageView) rootView.findViewById(R.id.img_sale);
        img_new_arrival = (ImageView) rootView.findViewById(R.id.img_new_arrival);
        img_map = (ImageView) rootView.findViewById(R.id.img_map);
        img_notification = (ImageView) rootView.findViewById(R.id.img_notification);

        rl_pic = (RelativeLayout) rootView.findViewById(R.id.rl_pic);
        rl_location_search = (RelativeLayout) rootView.findViewById(R.id.rl_location_search);
        rl_header = (RelativeLayout) rootView.findViewById(R.id.rl_header);
        ll_scroll_photo = (LinearLayout) rootView.findViewById(R.id.ll_scroll_photo);

        img_upload_pic = (ImageView) rootView.findViewById(R.id.img_upload_pic);
        img_address = (ImageView) rootView.findViewById(R.id.img_address);

        txt_location = (TextView) rootView.findViewById(R.id.txt_location);
        txt_store_name = (TextView) rootView.findViewById(R.id.txt_store_name);
        txt_storename = (TextView) rootView.findViewById(R.id.txt_storename);
        txt_address = (TextView) rootView.findViewById(R.id.txt_address);
        txt_website = (TextView) rootView.findViewById(R.id.txt_website);
        txt_website_value = (TextView) rootView.findViewById(R.id.txt_website_value);
        txt_information = (TextView) rootView.findViewById(R.id.txt_information);
        txt_timing = (TextView) rootView.findViewById(R.id.txt_timing);
        txt_opentime = (TextView) rootView.findViewById(R.id.txt_opentime);
        txt_closetime = (TextView) rootView.findViewById(R.id.txt_closetime);
        txt_close_on1 = (TextView) rootView.findViewById(R.id.txt_close_on1);
        txt_close_on2 = (TextView) rootView.findViewById(R.id.txt_close_on2);
        txt_folleowercount = (TextView) rootView.findViewById(R.id.txt_folleowercount);
        img_follow_unfollow = (TextView) rootView.findViewById(R.id.img_follow_unfollow);
        rl_header.bringToFront();

        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
        fontM = Typeface.createFromAsset(getActivity().getAssets(), "Merriweather Light.ttf");
        txt_location.setTypeface(font);
        txt_store_name.setTypeface(font);
        txt_folleowercount.setTypeface(font);
        img_follow_unfollow.setTypeface(font);
        txt_storename.setTypeface(font);
        txt_address.setTypeface(fontM);
        txt_website.setTypeface(fontM);
        txt_website_value.setTypeface(fontM);
        txt_information.setTypeface(font);
        txt_information.setPaintFlags(txt_information.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_timing.setTypeface(font);
        txt_opentime.setTypeface(font);
        txt_closetime.setTypeface(font);
        txt_close_on1.setTypeface(font);
        txt_close_on2.setTypeface(font);

        dMetric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dMetric);

        txt_store_name.setText(name);
        img_icon_menu.setOnClickListener(this);
        img_address.setOnClickListener(this);
        txt_location.setOnClickListener(this);
        img_search.setOnClickListener(this);
        img_filter.setOnClickListener(this);
        img_call.setOnClickListener(this);
        img_upload_pic.setOnClickListener(this);
        img_follow_unfollow.setOnClickListener(this);
        img_notification.setOnClickListener(this);

        //fetchRatings();
        getStoreDetails();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        txt_location.setText(PreferenceServices.getInstance().getCurrentLocation());
    }

    private void getStoreDetails() {
        String urlJson = Constant.FASHION_API + "route=store_view&store_id=" + id + "&user_id=" + PreferenceServices.getInstance().geUsertId();
        showpDialog();
        Log.e("Store Details: ", urlJson);
        final ArrayList<LinkedHashMap<String, String>> list = new ArrayList<>();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.e("Volley", response.toString());
                boolean responseSuccess = false;
                String strsuccess;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equals("true")) {
                        responseSuccess = true;
                        JSONArray jsonArray = new JSONArray(response.getString("store"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            LinkedHashMap<String, String> item = new LinkedHashMap<>();
                            item.put("id", jsonObject.getString("id"));
                            item.put("name", jsonObject.getString("name"));
                            item.put("href", jsonObject.getString("href"));
                            item.put("thumb", jsonObject.getString("thumb"));
                            item.put("website_link", jsonObject.getString("website_link"));
                            item.put("gender", jsonObject.getString("gender"));
                            item.put("sale", jsonObject.getString("sale"));
                            item.put("arrival", jsonObject.getString("arrival"));
                            item.put("phone1", jsonObject.getString("phone1"));
                            item.put("phone2", jsonObject.getString("phone2"));
                            item.put("phone3", jsonObject.getString("phone3"));
                            item.put("open", jsonObject.getString("open"));
                            item.put("close", jsonObject.getString("close"));
                            item.put("map", jsonObject.getString("map"));
                            item.put("latitude", jsonObject.getString("latitude"));
                            item.put("longitude", jsonObject.getString("longitude"));
                            item.put("address", jsonObject.getString("address"));
                            item.put("closed", jsonObject.getString("closed"));
                            item.put("closed_date", jsonObject.getString("closed_date"));
                            item.put("reason", jsonObject.getString("reason"));
                            item.put("rating_count", jsonObject.getString("rating_count"));
                            item.put("wishlist", jsonObject.getString("wishlist"));
                            item.put("follow_count", jsonObject.getString("follow_count"));
                            JSONArray jsonArrayImage = new JSONArray(jsonObject.getString("image_collection"));
                            returnObjimage = new ArrayList<ProductImageItem>();
                            String imageUrl;
                            for (int j = 0; j < jsonArrayImage.length(); j++) {
                                imageUrl = jsonArrayImage.getString(j);
                                productImageItem = new ProductImageItem();
                                productImageItem.setImage(imageUrl);
                                returnObjimage.add(productImageItem);
                            }
                            list.add(item);
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
                        Toast.makeText(getActivity(), "Server not responding Please try again...", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            href = list.get(i).get("href");
                            phone1 = list.get(i).get("phone1");
                            phone2 = list.get(i).get("phone2");
                            phone3 = list.get(i).get("phone3");

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            int rlWidth = dMetric.widthPixels;
                            int rlHeight = (int) ((dMetric.heightPixels) * 0.4);
                            layoutParams.width = rlWidth;
                            layoutParams.height = rlHeight;
                            rl_pic.setLayoutParams(layoutParams);
                            Glide.with(getActivity())
                                    .load(list.get(i).get("thumb"))
                                    .into(img_photo);

                            txt_storename.setText(list.get(i).get("name"));
                            txt_address.setText(list.get(i).get("address"));
                            txt_opentime.setText(list.get(i).get("open"));
                            txt_closetime.setText(list.get(i).get("close"));
                            txt_website_value.setText(list.get(i).get("website_link"));

                            String strFollowCount = list.get(i).get("follow_count");
                            if (strFollowCount.length() >= 4) {
                                String convertedCountK = strFollowCount.substring(0, strFollowCount.length() - 3);
                                if (convertedCountK.length() >= 4) {
                                    String convertedCount = convertedCountK.substring(0, convertedCountK.length() - 3);
                                    txt_folleowercount.setText(Html.fromHtml(convertedCount + "<sup>M</sup>"));
                                } else {
                                    txt_folleowercount.setText(Html.fromHtml(convertedCountK + "<sup>K</sup>"));
                                }
                            } else {
                                txt_folleowercount.setText(Html.fromHtml(strFollowCount));
                            }

                            if (list.get(i).get("wishlist").equalsIgnoreCase("notin")) {
                                img_follow_unfollow.setText("FOLLOW");
                            } else {
                                img_follow_unfollow.setText("UNFOLLOW");
                            }

                            if (list.get(i).get("sale").equalsIgnoreCase("Yes"))
                                img_sale.setVisibility(View.VISIBLE);
                            else
                                img_sale.setVisibility(View.INVISIBLE);

                            if (list.get(i).get("arrival").equalsIgnoreCase("Yes"))
                                img_new_arrival.setVisibility(View.VISIBLE);
                            else
                                img_new_arrival.setVisibility(View.INVISIBLE);


                            if (list.get(i).get("closed").equalsIgnoreCase(""))
                                txt_close_on1.setVisibility(View.GONE);
                            else
                                txt_close_on1.setText("* " + list.get(i).get("closed") + " Closed");

                            String closeDate = list.get(i).get("closed_date").equalsIgnoreCase("") ? "" : list.get(i).get("closed_date");
                            String closeReason = list.get(i).get("reason").equalsIgnoreCase("") ? "" : "on " + list.get(i).get("reason");
                            if (closeDate.equalsIgnoreCase("") && closeReason.equalsIgnoreCase(""))
                                txt_close_on2.setVisibility(View.GONE);
                            else
                                txt_close_on2.setText(closeDate + " " + "Close " + closeReason);

                            final int finalI = i;
                            img_map.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (list.get(finalI).get("latitude").equalsIgnoreCase("") ||
                                            list.get(finalI).get("longitude").equalsIgnoreCase("")) {
                                        Toast.makeText(getActivity(), "Lat Long not Found", Toast.LENGTH_SHORT).show();
                                    } else {
                                        clickLogTracking("map", list.get(finalI).get("id"));
                                        Intent intent = new Intent(getActivity(), StoreLocationMapScreen.class);
                                        Bundle b = new Bundle();
                                        b.putString("name", name);
                                        b.putDouble("lat", Double.parseDouble(list.get(finalI).get("latitude")));
                                        b.putDouble("long", Double.parseDouble(list.get(finalI).get("longitude")));
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                }
                            });

                            for (int j = 0; j < returnObjimage.size(); j++) {
                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.row_hori_scroll, null);
                                ImageView img_photo = (ImageView) view.findViewById(R.id.img_photo);
                                TextView txt_cat = (TextView) view.findViewById(R.id.txt_cat);
                                txt_cat.setVisibility(View.GONE);
                                Glide.with(getActivity())
                                        .load(returnObjimage.get(j).getImage())
                                        .into(img_photo);
                                ll_scroll_photo.addView(view);
                            }
                        }
                    }
                } else {
                    getFragmentManager().popBackStack();
                    Toast.makeText(getActivity(), "Server not responding Please try again...", Toast.LENGTH_SHORT).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                getFragmentManager().popBackStack();
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {

            case R.id.img_icon_menu:
                // startActivity(new Intent(getActivity(), HomeActivity.class));
                break;

            case R.id.img_address:
                clickLogTracking("address", id);
                showAddress();
                break;

            case R.id.img_follow_unfollow:
                String str = img_follow_unfollow.getText().toString();
                if (str.equalsIgnoreCase("FOLLOW")) {
                    addToFollowers(id);
                } else {
                    removeFromFollowers(id);
                }
                break;

            case R.id.txt_location:
                startActivity(new Intent(getActivity(), SelectLocation.class).putExtra("location", "default"));
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

            case R.id.img_call:
                if (phone1.equalsIgnoreCase("") && phone2.equalsIgnoreCase("") && phone3.equalsIgnoreCase(""))
                    Toast.makeText(getActivity(), "Number not Found", Toast.LENGTH_SHORT).show();
                else {
                    clickLogTracking("call", id);
                    showPopupPhoneNumber();
                }
                break;

            case R.id.img_upload_pic:
                //selectImage();
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

            default:
                break;
        }
    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Check this store on Prestreet App");
        share.putExtra(Intent.EXTRA_TEXT, "Check below store on Pretstreet App:- " + href + "\n\nStore details:-"
                + "\n" + name + "\n" + txt_address.getText().toString());
        startActivity(Intent.createChooser(share, "Share store details with.."));
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //intent.setType("image");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //img_upload_pic.setImageBitmap(bitmap);  //convert bitmap to base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        //Cursor cursor = getActivity().managedQuery(selectedImageUri, projection, null, null, null);
        Cursor cursor = getActivity().getContentResolver().query(selectedImageUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(column_index);
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        //img_upload_pic.setImageBitmap(bm);
        //convert bitmap to base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void showPopupPhoneNumber() {
        final Dialog popupDialog = new Dialog(getActivity());
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.popup_phone_number, null);
        TextView txt_cat = (TextView) view.findViewById(R.id.txt_cat);
        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        img_close.setVisibility(View.VISIBLE);
        RelativeLayout rl_phone1 = (RelativeLayout) view.findViewById(R.id.rl_phone1);
        RelativeLayout rl_phone2 = (RelativeLayout) view.findViewById(R.id.rl_phone2);
        RelativeLayout rl_phone3 = (RelativeLayout) view.findViewById(R.id.rl_phone3);
        TextView txt_phone1 = (TextView) view.findViewById(R.id.txt_phone1);
        TextView txt_phone2 = (TextView) view.findViewById(R.id.txt_phone2);
        TextView txt_phone3 = (TextView) view.findViewById(R.id.txt_phone3);
        txt_cat.setTypeface(fontM);
        txt_phone1.setTypeface(fontM);
        txt_phone2.setTypeface(fontM);
        txt_phone3.setTypeface(fontM);
        txt_cat.setText(txt_storename.getText().toString());
        txt_phone1.setText(phone1);
        txt_phone2.setText(phone2);
        txt_phone3.setText(phone3);
        if (phone1.equalsIgnoreCase(""))
            rl_phone1.setVisibility(View.GONE);
        if (phone2.equalsIgnoreCase(""))
            rl_phone2.setVisibility(View.GONE);
        if (phone3.equalsIgnoreCase(""))
            rl_phone3.setVisibility(View.GONE);

        popupDialog.setCanceledOnTouchOutside(true);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.popup_bundle);
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

        rl_phone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPhoneListener phoneListener = new MyPhoneListener();
                TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                try {
                    String uri = "tel:" + phone1;
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                    startActivity(dialIntent);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Call Failed", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        rl_phone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPhoneListener phoneListener = new MyPhoneListener();
                TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                try {
                    String uri = "tel:" + phone1;
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                    startActivity(dialIntent);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Call Failed", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        rl_phone3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPhoneListener phoneListener = new MyPhoneListener();
                TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                try {
                    String uri = "tel:" + phone1;
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                    startActivity(dialIntent);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Call Failed", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public void showAddress() {
        final Dialog popupDialog = new Dialog(getActivity());
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.popup_phone_number, null);
        TextView txt_cat = (TextView) view.findViewById(R.id.txt_cat);
        TextView txt_close = (TextView) view.findViewById(R.id.txt_close);
        TextView txt_address1 = (TextView) view.findViewById(R.id.txt_address);
        RelativeLayout rl_phone1 = (RelativeLayout) view.findViewById(R.id.rl_phone1);
        RelativeLayout rl_phone2 = (RelativeLayout) view.findViewById(R.id.rl_phone2);
        RelativeLayout rl_phone3 = (RelativeLayout) view.findViewById(R.id.rl_phone3);
        rl_phone1.setVisibility(View.GONE);
        rl_phone2.setVisibility(View.GONE);
        rl_phone3.setVisibility(View.GONE);
        txt_close.setVisibility(View.VISIBLE);
        txt_address1.setVisibility(View.VISIBLE);
        txt_cat.setTypeface(fontM);
        txt_close.setTypeface(fontM);
        txt_address1.setTypeface(fontM);
        txt_cat.setText(txt_storename.getText().toString());
        txt_address1.setText(txt_address.getText().toString());
        popupDialog.setCanceledOnTouchOutside(true);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });
    }

    private void removeFromFollowers(String id) {
        //http://doctronics.co.in/fashionapp/fashion_api.php?route=remove_follow&store_id=31&user_id=3
        String urlJsonObj = Constant.FASHION_API + "route=remove_follow&store_id=" + id + "&user_id=" + PreferenceServices.getInstance().geUsertId();
        showpDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String strsuccess, message;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equalsIgnoreCase("true")) {
                        message = response.getString("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        img_follow_unfollow.setText("FOLLOW");
                        int count = Integer.parseInt(txt_folleowercount.getText().toString());
                        txt_folleowercount.setText(--count + "");
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
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
        PretStreet.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void addToFollowers(String id) {
        //http://doctronics.co.in/fashionapp/fashion_api.php?route=save_follow&store_id=31&user_id=3
        String urlJsonObj = Constant.FASHION_API + "route=save_follow&store_id=" + id + "&user_id=" + PreferenceServices.getInstance().geUsertId();
        showpDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String strsuccess, message;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equalsIgnoreCase("true")) {
                        message = response.getString("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        img_follow_unfollow.setText("UNFOLLOW");
                        int count = Integer.parseInt(txt_folleowercount.getText().toString());
                        txt_folleowercount.setText(++count + "");
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
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
        PretStreet.getInstance().addToRequestQueue(jsonObjReq);
    }

    private class MyPhoneListener extends PhoneStateListener {

        private boolean onCall = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Toast.makeText(getActivity(), incomingNumber + " " + "Call you", Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // one call exists that is dialing, active, or on hold
                    Toast.makeText(getActivity(), "onCall", Toast.LENGTH_LONG).show();
                    //because user answers the incoming call
                    onCall = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // in initialization of the class and at the end of phone call
                    // detect flag from CALL_STATE_OFFHOOK
                    if (onCall == true) {
                        Toast.makeText(getActivity(), "Restart app after call", Toast.LENGTH_LONG).show();
                        // restart our application
                        Intent restart = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
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

    public class ProductImageItem {
        String image;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
