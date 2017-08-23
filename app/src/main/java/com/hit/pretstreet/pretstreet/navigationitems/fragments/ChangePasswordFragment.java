package com.hit.pretstreet.pretstreet.navigationitems.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigationitems.controllers.NavItemsController;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 7/12/2017.
 */

public class ChangePasswordFragment  extends AbstractBaseFragment<WelcomeActivity> {

    @BindView(R.id.edt_currentpass) EdittextPret edt_currentpass;
    @BindView(R.id.edt_newpass) EdittextPret edt_newpass;
    @BindView(R.id.edt_confirmnew) EdittextPret edt_confirmnew;

    NavItemsController navItemsController;
    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_changepassword, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        navItemsController = new NavItemsController(getActivity());
    }

    @OnClick(R.id.btn_changepass)
    public void onUpdatePassPressed() {
        navItemsController.validateUpdatePassFields(edt_currentpass, edt_newpass,
                edt_confirmnew);
    }

    public void onValidationError(EdittextPret editText, String message){
        editText.setError(message);
    }
}