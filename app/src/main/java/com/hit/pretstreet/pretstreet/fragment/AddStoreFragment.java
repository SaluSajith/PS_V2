package com.hit.pretstreet.pretstreet.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.R;

/**
 * Created by Jesal on 05-Sep-16.
 */
public class AddStoreFragment extends Fragment implements View.OnClickListener {
    private ImageView img_icon_menu, img_submit;
    private TextView txt_cat_name;
    private ScrollView scroll;
    private EditText edt_store_name, edt_name, edt_email, edt_location, edt_mobileno, edt_landline, edt_about;
    private Typeface font;
    private String baseImage;
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_store_screen, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        img_icon_menu = (ImageView) rootView.findViewById(R.id.img_icon_menu);
        img_submit = (ImageView) rootView.findViewById(R.id.img_submit);

        txt_cat_name = (TextView) rootView.findViewById(R.id.txt_cat_name);
        scroll = (ScrollView) rootView.findViewById(R.id.scroll);

        edt_store_name = (EditText) rootView.findViewById(R.id.edt_store_name);
        edt_location = (EditText) rootView.findViewById(R.id.edt_location);
        edt_mobileno = (EditText) rootView.findViewById(R.id.edt_mobileno);
        edt_landline = (EditText) rootView.findViewById(R.id.edt_landline);
        edt_name = (EditText) rootView.findViewById(R.id.edt_name);
        edt_about = (EditText) rootView.findViewById(R.id.edt_about);
        edt_email = (EditText) rootView.findViewById(R.id.edt_email);

        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
        txt_cat_name.setText("ADD STORE");
        txt_cat_name.setTypeface(font);
        edt_store_name.setTypeface(font);
        edt_location.setTypeface(font);
        edt_mobileno.setTypeface(font);
        edt_landline.setTypeface(font);
        edt_name.setTypeface(font);
        edt_about.setTypeface(font);
        edt_email.setTypeface(font);

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

        img_icon_menu.setOnClickListener(this);
        img_submit.setOnClickListener(this);

        return rootView;
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
                    Toast.makeText(getActivity(), "Enter Store Name", Toast.LENGTH_SHORT).show();
                    edt_store_name.requestFocus();
                } else if (personName.length() < 2) {
                    Toast.makeText(getActivity(), "Enter Contact Person number", Toast.LENGTH_SHORT).show();
                    edt_name.requestFocus();
                } else if (emailid.length() < 2) {
                    Toast.makeText(getActivity(), "Enter Email id", Toast.LENGTH_SHORT).show();
                    edt_email.requestFocus();
                } else if (!Constant.isValidEmail(emailid)) {
                    Toast.makeText(getActivity(), "Enter valid email id", Toast.LENGTH_SHORT).show();
                    edt_email.requestFocus();
                }/* else if (landline.length() < 2) {
                    Toast.makeText(getActivity(), "Enter Landline number", Toast.LENGTH_SHORT).show();
                    edt_landline.requestFocus();
                }*/ else if (location.length() < 2) {
                    Toast.makeText(getActivity(), "Enter Location", Toast.LENGTH_SHORT).show();
                    edt_location.requestFocus();
                } else if (mobile.length() < 2) {
                    Toast.makeText(getActivity(), "Enter Mobile number", Toast.LENGTH_SHORT).show();
                    edt_mobileno.requestFocus();
                } else if (about.length() < 2) {
                    Toast.makeText(getActivity(), "Enter about store", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
                    }
                    getFragmentManager().popBackStack();
                }
                break;

            default:
                break;
        }
    }

}
