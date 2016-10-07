package com.hit.pretstreet.pretstreet.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.ExpandableHeightGridView;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.ActivityManagePermission;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.PermissionResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by HIT on 13-Apr-16.
 */
public class AddStoreScreen extends ActivityManagePermission implements View.OnClickListener {

    private ImageView img_icon_menu, img_submit;
    private TextView txt_cat;
    private EditText edt_store_name, edt_name, edt_email, edt_location, edt_mobileno, edt_landline, edt_about;
    private Typeface font;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_store_screen);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        img_icon_menu = (ImageView) findViewById(R.id.img_icon_menu);
        img_submit = (ImageView) findViewById(R.id.img_submit);

        txt_cat = (TextView) findViewById(R.id.txt_cat);

        edt_store_name = (EditText) findViewById(R.id.edt_store_name);
        edt_location = (EditText) findViewById(R.id.edt_location);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_mobileno = (EditText) findViewById(R.id.edt_mobileno);
        edt_landline = (EditText) findViewById(R.id.edt_landline);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_about = (EditText) findViewById(R.id.edt_about);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "RedVelvet-Regular.otf");
        txt_cat.setTypeface(font);
        edt_store_name.setTypeface(font);
        edt_location.setTypeface(font);
        edt_mobileno.setTypeface(font);
        edt_landline.setTypeface(font);
        edt_name.setTypeface(font);
        edt_about.setTypeface(font);
        edt_email.setTypeface(font);

        img_icon_menu.setOnClickListener(this);
        img_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.img_icon_menu:
                //this.finish();
                break;

            case R.id.img_submit:
                String storeName = edt_store_name.getText().toString();
                String location = edt_location.getText().toString();
                String mobile = edt_mobileno.getText().toString();
                String landline = edt_landline.getText().toString();
                String personName = edt_name.getText().toString();
                String about = edt_about.getText().toString();
                String emailid = edt_email.getText().toString();

                if (storeName.length() < 2) {
                    Toast.makeText(AddStoreScreen.this, "Enter Store Name", Toast.LENGTH_SHORT).show();
                    edt_store_name.requestFocus();
                } else if (personName.length() < 2) {
                    Toast.makeText(AddStoreScreen.this, "Enter Contact Person number", Toast.LENGTH_SHORT).show();
                    edt_name.requestFocus();
                } else if (emailid.length() < 2) {
                    Toast.makeText(AddStoreScreen.this, "Enter Email id", Toast.LENGTH_SHORT).show();
                    edt_email.requestFocus();
                } else if (!Constant.isValidEmail(emailid)) {
                    Toast.makeText(AddStoreScreen.this, "Enter valid email id", Toast.LENGTH_SHORT).show();
                    edt_email.requestFocus();
                } else if (landline.length() < 2) {
                    Toast.makeText(AddStoreScreen.this, "Enter Landline number", Toast.LENGTH_SHORT).show();
                    edt_landline.requestFocus();
                } else if (location.length() < 2) {
                    Toast.makeText(AddStoreScreen.this, "Enter Location", Toast.LENGTH_SHORT).show();
                    edt_location.requestFocus();
                } else if (mobile.length() < 2) {
                    Toast.makeText(AddStoreScreen.this, "Enter Mobile number", Toast.LENGTH_SHORT).show();
                    edt_mobileno.requestFocus();
                } else if (about.length() < 2) {
                    Toast.makeText(AddStoreScreen.this, "Enter about store", Toast.LENGTH_SHORT).show();
                    edt_about.requestFocus();
                } else {
                    //send email functionality
                    try {
                        String email, subject, message;
                        email = "contact@pretstreet.com";
                        subject = "PretStreet Add Store Details";
                        message = "Store Name: " + storeName + "\n"
                                + "Email id: " + emailid + "\n"
                                + "Store Location: " + location + "\n"
                                + "Mobile No: " + mobile + "\n"
                                + "Landline No: " + landline + "\n"
                                + "Contact Person Name: " + personName + "\n"
                                + "About Store: " + about;
                        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                        this.startActivity(Intent.createChooser(emailIntent, "Sending email..."));
                    } catch (Throwable t) {
                        Toast.makeText(AddStoreScreen.this, "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
                    }
                    finish();
                }
                //getFragmentManager().popBackStack();
                break;

            default:
                break;
        }
    }
}
