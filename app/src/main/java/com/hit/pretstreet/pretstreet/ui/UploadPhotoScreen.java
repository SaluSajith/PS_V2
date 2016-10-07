package com.hit.pretstreet.pretstreet.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hit.pretstreet.pretstreet.CircleImageView;
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.ActivityManagePermission;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.PermissionResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by hit on 15/3/16.
 */
public class UploadPhotoScreen extends ActivityManagePermission implements View.OnClickListener {

    private Button btn_continue;
    private CircleImageView img_upload_photo;
    private TextView txt_skip, txt_upload_profile_pic;
    private RelativeLayout rl_profile_pic;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Bitmap bitmap;
    private String encodedImage = "";
    private Typeface font;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_photo_screen);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        init();
        txt_skip.setOnClickListener(this);
        rl_profile_pic.setOnClickListener(this);
        btn_continue.setOnClickListener(this);
    }

    private void init() {
        img_upload_photo = (CircleImageView) findViewById(R.id.img_upload_photo);
        btn_continue = (Button) findViewById(R.id.img_continue);
        txt_skip = (TextView) findViewById(R.id.txt_skip);
        rl_profile_pic = (RelativeLayout) findViewById(R.id.rl_profile_pic);
        txt_upload_profile_pic = (TextView) findViewById(R.id.txt_upload_profile_pic);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "RedVelvet-Regular.otf");
        txt_skip.setTypeface(font);
        txt_upload_profile_pic.setTypeface(font);
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

            case R.id.txt_skip:
                if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("")
                        || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("")) {
                    startActivity(new Intent(getApplicationContext(), DefaultLocation.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
                this.finish();
                break;

            case R.id.rl_profile_pic:
                selectImage();
                break;

            case R.id.img_continue:
                if (encodedImage.equalsIgnoreCase("")) {
                    Toast.makeText(UploadPhotoScreen.this, "Please Select an image", Toast.LENGTH_SHORT).show();
                } else {
                    saveProfilePicture();
                }
                break;

            default:
                break;
        }
    }

    private void saveProfilePicture() {
        //Log.e("Encoded: ", encodedImage);
        showpDialog();
        String urlJsonObj = Constant.FASHION_API;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlJsonObj,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject object;
                        boolean responce;
                        Log.e("Volley", response.toString());
                        String msg = null;
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
                            Toast.makeText(UploadPhotoScreen.this, msg, Toast.LENGTH_SHORT).show();
                            if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("")
                                    || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("")) {
                                startActivity(new Intent(getApplicationContext(), DefaultLocation.class));
                            } else {
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            }
                            finish();
                            // HomeActivity.img_profile_pic.setImageBitmap(bitmap);
                        } else {
                            Toast.makeText(UploadPhotoScreen.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UploadPhotoScreen.this, error.toString(), Toast.LENGTH_SHORT).show();
                        hidepDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("route", "check_json");
                params.put("user_id", PreferenceServices.getInstance().geUsertId());
                params.put("file", encodedImage);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadPhotoScreen.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
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
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
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
        img_upload_photo.setImageBitmap(bitmap);
        //convert bitmap to base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        //Cursor cursor = getActivity().managedQuery(selectedImageUri, projection, null, null, null);
        Cursor cursor = getBaseContext().getContentResolver().query(selectedImageUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(column_index);
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
        img_upload_photo.setImageBitmap(bitmap);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
