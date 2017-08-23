package com.hit.pretstreet.pretstreet.navigationitems.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.CircularImageView;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.navigationitems.controllers.NavItemsController;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.storedetails.view.SlideshowDialogFragment;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 7/14/2017.
 */

public class AccountFragment extends AbstractBaseFragment<WelcomeActivity> {

    @BindView(R.id.civ_profile) CircularImageView civ_profile;
    @BindView(R.id.edt_fname) EdittextPret edt_fname;
    @BindView(R.id.edt_lname) EdittextPret edt_lname;
    @BindView(R.id.edt_email) EdittextPret edt_email;
    @BindView(R.id.edt_dob) EdittextPret edt_dob;
    @BindView(R.id.edt_mobile) EdittextPret edt_mobile;

    NavItemsController navItemsController;
    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myaccount, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        navItemsController = new NavItemsController(getActivity());

        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(getActivity());
        LoginSession loginSession = sharedPreferencesHelper.getUserDetails();
        edt_fname.setText(loginSession.getFname());
        edt_lname.setText(loginSession.getLname());
        edt_email.setText(loginSession.getEmail());
        edt_dob.setText(loginSession.getDob());
        edt_mobile.setText(loginSession.getMobile());

        Glide.with(getActivity())
                .load(loginSession.getProfile_pic())
                .centerCrop()
                .placeholder(R.drawable.profilepic)
                .into(civ_profile);
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
                edt_email, edt_dob,
                edt_mobile);
    }

    @OnClick(R.id.btn_changepass)
    public void onChangePasswordClick() {
        ((NavigationItemsActivity) getActivity()).openUpdatePassword();
    }

    public void onValidationError(EdittextPret editText, String message){
        editText.setError(message);
    }

}