package com.hit.pretstreet.pretstreet.navigationitems.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.navigationitems.controllers.NavItemsController;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 7/12/2017.
 */

public class ContactUsFragment extends AbstractBaseFragment<WelcomeActivity> {

    int selected_fragment = 0;
    private static final int CONTACTUS_FRAGMENT = 4;
    private static final int FEEDBACK_FRAGMENT = 5;
    NavItemsController navItemsController;

    @BindView(R.id.edt_data) EdittextPret edt_data;

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactus, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        String myValue = this.getArguments().getString(Constant.ID_KEY);
        navItemsController = new NavItemsController(getActivity());

        switch (myValue){
            case CONTACTUS_FRAGMENT+"":
                selected_fragment = CONTACTUS_FRAGMENT;
                break;
            case FEEDBACK_FRAGMENT+"":
                selected_fragment = FEEDBACK_FRAGMENT;
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.btn_submit)
    public void onSubmitPressed() {
        navItemsController.validateFields(edt_data, selected_fragment);
    }

    public void onValidationError(EdittextPret editText, String message){
        editText.setError(message);
    }
}