package com.hit.pretstreet.pretstreet.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.ui.SelectLocation;
import com.hit.pretstreet.pretstreet.ui.WebViewActivity;
import com.hit.pretstreet.pretstreet.ui.WelcomeScreen;

/**
 * Created by Jesal on 09-Sep-16.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {
    private TextView txt_cat_name;
    private ScrollView scroll;
    private String baseImage;
    private ImageView img_your_location, img_edit_profile, img_about, img_tems, img_privacy_policy,
            img_licenses, img_rate_us, img_feedback, img_contact, img_logout, img_change_password, img_last;
    private Typeface font;
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account_screen, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");

        txt_cat_name = (TextView) rootView.findViewById(R.id.txt_cat_name);
        scroll = (ScrollView) rootView.findViewById(R.id.scroll);

        img_your_location = (ImageView) rootView.findViewById(R.id.img_your_location);
        img_edit_profile = (ImageView) rootView.findViewById(R.id.img_edit_profile);
        img_about = (ImageView) rootView.findViewById(R.id.img_about);
        img_tems = (ImageView) rootView.findViewById(R.id.img_tems);
        img_privacy_policy = (ImageView) rootView.findViewById(R.id.img_privacy_policy);
        img_licenses = (ImageView) rootView.findViewById(R.id.img_licenses);
        img_rate_us = (ImageView) rootView.findViewById(R.id.img_rate_us);
        img_feedback = (ImageView) rootView.findViewById(R.id.img_feedback);
        img_contact = (ImageView) rootView.findViewById(R.id.img_contact);
        img_logout = (ImageView) rootView.findViewById(R.id.img_logout);
        img_change_password = (ImageView) rootView.findViewById(R.id.img_change_password);
        img_last = (ImageView) rootView.findViewById(R.id.img_last);

        if (PreferenceServices.getInstance().getLoginType().equalsIgnoreCase("Social")) {
            img_change_password.setVisibility(View.GONE);
            img_last.setVisibility(View.GONE);
        } else {
            img_change_password.setVisibility(View.VISIBLE);
            img_last.setVisibility(View.INVISIBLE);
        }

        txt_cat_name.setTypeface(font);
        txt_cat_name.setText("ACCOUNT");
        img_your_location.setOnClickListener(this);
        img_edit_profile.setOnClickListener(this);
        img_about.setOnClickListener(this);
        img_tems.setOnClickListener(this);
        img_privacy_policy.setOnClickListener(this);
        img_licenses.setOnClickListener(this);
        img_rate_us.setOnClickListener(this);
        img_feedback.setOnClickListener(this);
        img_contact.setOnClickListener(this);
        img_logout.setOnClickListener(this);
        img_change_password.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.img_your_location:
                startActivity(new Intent(getActivity(), SelectLocation.class).putExtra("location", "not"));
                break;

            case R.id.img_about:
                /*Fragment f1 = new About_TnCFragment();
                Bundle b1 = new Bundle();
                b1.putString("screen", "aboutus");
                f1.setArguments(b1);
                FragmentTransaction t1 = getFragmentManager().beginTransaction();
                t1.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t1.add(R.id.frame_container, f1);
                t1.addToBackStack(null);
                t1.commit();*/
                startActivity(new Intent(getActivity(), WebViewActivity.class));
                break;

            case R.id.img_edit_profile:
                Fragment f2 = new MyProfileFragment();
                FragmentTransaction t2 = getFragmentManager().beginTransaction();
                t2.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t2.add(R.id.frame_container, f2);
                t2.addToBackStack(null);
                t2.commit();
                break;

            case R.id.img_tems:
                Fragment f3 = new About_TnCFragment();
                Bundle b3 = new Bundle();
                b3.putString("screen", "terms");
                f3.setArguments(b3);
                FragmentTransaction t3 = getFragmentManager().beginTransaction();
                t3.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t3.add(R.id.frame_container, f3);
                t3.addToBackStack(null);
                t3.commit();
                break;

            case R.id.img_change_password:
                Fragment f4 = new ChangePasswordFragment();
                Bundle b4 = new Bundle();
                f4.setArguments(b4);
                FragmentTransaction t4 = getFragmentManager().beginTransaction();
                t4.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t4.add(R.id.frame_container, f4);
                t4.addToBackStack(null);
                t4.commit();
                break;

            case R.id.img_privacy_policy:
                Fragment f5 = new About_TnCFragment();
                Bundle b5 = new Bundle();
                b5.putString("screen", "privacy");
                f5.setArguments(b5);
                FragmentTransaction t5 = getFragmentManager().beginTransaction();
                t5.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t5.add(R.id.frame_container, f5);
                t5.addToBackStack(null);
                t5.commit();
                break;

            case R.id.img_contact:
                Fragment f6 = new ContactUsFragment();
                Bundle b6 = new Bundle();
                b6.putString("screen", "contact");
                f6.setArguments(b6);
                FragmentTransaction t6 = getFragmentManager().beginTransaction();
                t6.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t6.add(R.id.frame_container, f6);
                t6.addToBackStack(null);
                t6.commit();
                break;

            case R.id.img_feedback:
                Fragment f7 = new ContactUsFragment();
                Bundle b7 = new Bundle();
                b7.putString("screen", "feedback");
                f7.setArguments(b7);
                FragmentTransaction t7 = getFragmentManager().beginTransaction();
                t7.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t7.add(R.id.frame_container, f7);
                t7.addToBackStack(null);
                t7.commit();
                break;

            case R.id.img_licenses:
                Fragment f8 = new About_TnCFragment();
                Bundle b8 = new Bundle();
                b8.putString("screen", "license");
                f8.setArguments(b8);
                FragmentTransaction t8 = getFragmentManager().beginTransaction();
                t8.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t8.add(R.id.frame_container, f8);
                t8.addToBackStack(null);
                t8.commit();
                break;

            case R.id.img_rate_us:
                final String appPackageName = getActivity().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;

            case R.id.img_logout:
                PreferenceServices.instance().saveUserId("");
                PreferenceServices.instance().saveUserName("");
                PreferenceServices.instance().saveCurrentLocation("");
                PreferenceServices.instance().saveLatitute("");
                PreferenceServices.instance().saveLongitute("");
                startActivity(new Intent(getActivity(), WelcomeScreen.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                break;

            default:
                break;
        }
    }
}
