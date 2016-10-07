package com.hit.pretstreet.pretstreet.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hit.pretstreet.pretstreet.CircleImageView;
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.FragmentManagePermission;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.PermissionResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Jesal on 05-Sep-16.
 */
public class MyProfileFragment extends FragmentManagePermission implements View.OnClickListener {
    private ImageView img_icon_menu, img_submit;
    private TextView txt_cat;
    private ScrollView scroll;
    private CircleImageView img_photo;
    private EditText edt_name, edt_email, edt_mobile, edt_dob;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Bitmap bitmap;
    private String encodedImage = "", baseImage;
    private Typeface font;
    private ProgressDialog pDialog;
    Calendar myCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_profle_screen, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        img_icon_menu = (ImageView) rootView.findViewById(R.id.img_icon_menu);
        img_photo = (CircleImageView) rootView.findViewById(R.id.img_photo);
        img_submit = (ImageView) rootView.findViewById(R.id.img_submit);
        scroll = (ScrollView) rootView.findViewById(R.id.scroll);
        txt_cat = (TextView) rootView.findViewById(R.id.txt_cat);
        edt_name = (EditText) rootView.findViewById(R.id.edt_name);
        edt_email = (EditText) rootView.findViewById(R.id.edt_email);
        edt_mobile = (EditText) rootView.findViewById(R.id.edt_mobile);
        edt_dob = (EditText) rootView.findViewById(R.id.edt_dob);

        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
        txt_cat.setTypeface(font);
        edt_name.setTypeface(font);
        edt_email.setTypeface(font);
        edt_mobile.setTypeface(font);
        edt_dob.setTypeface(font);

        getUserDetails();
        img_icon_menu.setOnClickListener(this);
        img_photo.setOnClickListener(this);
        img_submit.setOnClickListener(this);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        edt_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        baseImage = PreferenceServices.getInstance().getBaseImage();
        if (baseImage.equalsIgnoreCase("")) {
        } else {
            Glide.with(getActivity())
                    .load(baseImage)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Drawable dr = new BitmapDrawable(resource);
                            scroll.setBackgroundDrawable(dr);
                        }
                    });
        }

        return rootView;
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edt_dob.setText(sdf.format(myCalendar.getTime()));
    }

    private void getUserDetails() {
        //http://doctronics.co.in/fashionapp/fashion_api.php?route=get_customer&user_id=63
        String urlJsonObj = Constant.FASHION_API + "route=get_customer&user_id=" + PreferenceServices.getInstance().geUsertId();
        Log.e("URL: ", urlJsonObj);
        showpDialog();
        final ArrayList<LinkedHashMap<String, String>> list = new ArrayList<>();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Volley", response.toString());
                boolean responseSuccess = false;
                String strsuccess, userId, message;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equalsIgnoreCase("true")) {
                        responseSuccess = true;
                        JSONArray array = response.getJSONArray("user_details");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            LinkedHashMap<String, String> item = new LinkedHashMap<>();
                            item.put("first_name", jsonObject.getString("first_name"));
                            item.put("email", jsonObject.getString("email"));
                            item.put("mobile_no", jsonObject.getString("mobile_no"));
                            item.put("profile_pic", jsonObject.getString("profile_pic"));
                            item.put("dob", jsonObject.getString("dob"));
                            list.add(item);
                        }
                    } else {
                        responseSuccess = false;
                        message = response.getString("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    responseSuccess = false;
                }

                if (responseSuccess) {
                    if (list.isEmpty()) {
                        Toast.makeText(getActivity(), "Server not responding Please try again...", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            Glide.with(getActivity()).load(list.get(i).get("profile_pic")).asBitmap().centerCrop().into(new BitmapImageViewTarget(img_photo) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    img_photo.setImageDrawable(circularBitmapDrawable);
                                    //HomeActivity.img_profile_pic.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                            edt_name.setText(list.get(i).get("first_name"));
                            edt_email.setText(list.get(i).get("email"));
                            edt_mobile.setText(list.get(i).get("mobile_no"));
                            edt_dob.setText(list.get(i).get("dob"));
                        }
                    }
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.img_icon_menu:
                //startActivity(new Intent(this, HomeActivity.class));
                break;

            case R.id.img_photo:
                selectImage();
                break;

            case R.id.img_submit:
                String name = edt_name.getText().toString();
                String mobile = edt_mobile.getText().toString();
                String dob = edt_dob.getText().toString();
                if (name.length() < 2) {
                    Toast.makeText(getActivity(), "Enter your name", Toast.LENGTH_SHORT).show();
                } else if (mobile.length() != 10) {
                    Toast.makeText(getActivity(), "Mobile number should be 10 digits", Toast.LENGTH_SHORT).show();
                } else {
                    saveUserData(name, mobile, dob);
                }
                break;

            default:
                break;
        }
    }

    private void saveUserData(final String name, final String mobile, final String dob) {
        showpDialog();
        //http://52.77.174.143/fashion_api.php?route=edit_profile&user_id=3&fname=Praveench&dob=8/20/1990&mobile_no=&profile=
        final String urlJsonObj = Constant.FASHION_API;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlJsonObj,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject object;
                        Log.e("url:", urlJsonObj);
                        boolean responce;
                        String msg = null;
                        Log.e("Volley", response.toString());
                        try {
                            object = new JSONObject(response);
                            if (object.getString("success").equalsIgnoreCase("true")) {
                                responce = true;
                                msg = object.getString("message");
                            } else {
                                responce = false;
                                msg = object.getString("message");
                            }
                        } catch (JSONException e) {
                            responce = false;
                            e.printStackTrace();
                        }
                        if (responce) {
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                            //HomeActivity.img_profile_pic.setImageBitmap(bitmap);
                            PreferenceServices.instance().saveUserName(name);
                            getFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                        }
                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        hidepDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("route", "edit_profile");
                params.put("user_id", PreferenceServices.getInstance().geUsertId());
                params.put("fname", name);
                params.put("mobile_no", mobile);
                params.put("profile", encodedImage);
                params.put("dob", dob);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_GRANTED) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, REQUEST_CAMERA);
                            } else {
                                askCompactPermission(Manifest.permission.CAMERA, new PermissionResult() {
                                    @Override
                                    public void permissionGranted() {
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(intent, REQUEST_CAMERA);
                                    }

                                    @Override
                                    public void permissionDenied() {

                                    }
                                });
                            }
                        } else if (items[item].equals("Choose from Library")) {
                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                            } else {
                                askCompactPermission(Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionResult() {
                                    @Override
                                    public void permissionGranted() {
                                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                                    }

                                    @Override
                                    public void permissionDenied() {

                                    }
                                });
                            }
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                }

        );
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
        img_photo.setImageBitmap(bitmap);
        //convert bitmap to base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImageUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(column_index);

     /*   int orientation = 0;
        try {
            ExifInterface ex = new ExifInterface(selectedImagePath);
            orientation = ex.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

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

        bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

        //Bitmap bit = BitmapFactory.decodeFile(selectedImagePath, options);
        /*//for rotation of image.
        Matrix m = new Matrix();
        if ((orientation == 3)) {
            m.postRotate(180);
            //m.postScale((float) bit.getWidth(), (float) bit.getHeight());
            Log.e("in orientation", "" + orientation);
            bitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(), m, true);
        } else if (orientation == 6) {
            m.postRotate(90);
            Log.e("in orientation", "" + orientation);
            bitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(), m, true);
        } else if (orientation == 8) {
            m.postRotate(270);
            Log.e("in orientation", "" + orientation);
            bitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(), m, true);
        }*/

        img_photo.setImageBitmap(bitmap);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
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
}
