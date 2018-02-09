package com.hit.pretstreet.pretstreet.navigationitems.fragments;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.navigationitems.controllers.NavItemsController;
import com.hit.pretstreet.pretstreet.navigationitems.interfaces.ContentBindingInterface;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.SOCIALLOGIN;

/**
 * Created by User on 7/14/2017.
 */

public class AccountFragment extends AbstractBaseFragment<WelcomeActivity>
        implements ContentBindingInterface{

    @BindView(R.id.civ_profile)AppCompatImageView civ_profile;
    @BindView(R.id.edt_fname) EdittextPret edt_fname;
    @BindView(R.id.edt_lname) EdittextPret edt_lname;
    @BindView(R.id.edt_email) EdittextPret edt_email;
    @BindView(R.id.edt_dob) EdittextPret edt_dob;
    @BindView(R.id.edt_mobile) EdittextPret edt_mobile;
    @BindView(R.id.btn_changepass) ButtonPret btn_changepass;

    NavItemsController navItemsController;
    Bitmap bitmap = null;

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myaccount, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        PreferenceServices.init(getHostActivity());
        navItemsController = new NavItemsController(getActivity());

        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(getActivity());
        LoginSession loginSession = sharedPreferencesHelper.getUserDetails();
        edt_fname.setText(loginSession.getFname());
        edt_lname.setText(loginSession.getLname());
        edt_email.setText(loginSession.getEmail());
        edt_dob.setText(loginSession.getDob());
        edt_mobile.setText(loginSession.getMobile());
        if(PreferenceServices.instance().getLoginType().equals(SOCIALLOGIN))
            btn_changepass.setVisibility(View.GONE);

        Glide.with(getActivity()).load(loginSession.getProfile_pic()).asBitmap()
                .placeholder(R.drawable.profilepic)
                .centerCrop().into(new BitmapImageViewTarget(civ_profile) {
            @Override
            protected void setResource(Bitmap resource) {
                bitmap = resource;
                civ_profile.setImageDrawable(getRoundedBitmap(resource));
            }
        });
    }

    private RoundedBitmapDrawable getRoundedBitmap(Bitmap resource){
        RoundedBitmapDrawable circularBitmapDrawable =
                RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
        circularBitmapDrawable.setCircular(true);
        return circularBitmapDrawable;
    }

    @OnClick(R.id.edt_dob)
    public void selectDOB(){
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        edt_dob.setText(dayOfMonth + "-"
                                + (monthOfYear + 1) + "-" + year);

                    }
                }, c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
        c.add(Calendar.YEAR, -100);
        dpd.getDatePicker().setMinDate(c.getTimeInMillis());
        dpd.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        dpd.show();
    }

    @OnClick(R.id.btn_submit)
    public void onSubmitPressed() {
        navItemsController.validateRegisterFields(edt_fname, edt_lname,
                edt_email, edt_dob, edt_mobile, bitmap);
    }

    @OnClick(R.id.civ_profile)
    public void onChooseImage() {
        ((NavigationItemsActivity) getActivity()).chooseProfileImage();
    }

    @OnClick(R.id.btn_changepass)
    public void onChangePasswordClick() {
        ((NavigationItemsActivity) getActivity()).openUpdatePassword();
    }

    public void onValidationError(EdittextPret editText, String message){
        editText.setError(message);
    }

    @Override
    public void updateImage(Bitmap bitmap) {
        this.bitmap = bitmap;
        civ_profile.setImageDrawable(getRoundedBitmap(bitmap));
    }
}