package com.hit.pretstreet.pretstreet.navigationitems.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 7/17/2017.
 */

public class AboutFragment extends AbstractBaseFragment<WelcomeActivity> {

    private static final int ACCOUNT_FRAGMENT = 0;
    private static final int FOLLOWING_FRAGMENT = 1;
    private static final int ABOUT_FRAGMENT = 2;
    private static final int ADDSTORE_FRAGMENT = 3;
    private static final int CONTACTUS_FRAGMENT = 4;
    private static final int FEEDBACK_FRAGMENT = 5;
    private static final int ABOUTUS_FRAGMENT = 6;
    private static final int PRIVACY_FRAGMENT = 7;
    private static final int TERMS_FRAGMENT = 8;

    ButtonClickCallback buttonClickCallback;

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        try {
            buttonClickCallback = (ButtonClickCallback) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement onViewSelected");
        }
    }

    @OnClick(R.id.iv_rateus)
    public void onRateusPressed() {
        rateUs();
    }

    @OnClick(R.id.iv_contact)
    public void onContactusPressed() {
        buttonClickCallback.buttonClick(CONTACTUS_FRAGMENT);
    }

    @OnClick(R.id.iv_feedback)
    public void onFeedbackPressed() {
        buttonClickCallback.buttonClick(FEEDBACK_FRAGMENT);
    }

    @OnClick(R.id.iv_aboutus)
    public void onAboutusPressed() {
        buttonClickCallback.buttonClick(ABOUTUS_FRAGMENT);
    }

    @OnClick(R.id.iv_privacy)
    public void onPrivacyPolicyPressed() {
        buttonClickCallback.buttonClick(PRIVACY_FRAGMENT);
    }


    @OnClick(R.id.iv_terms)
    public void onTermsPressed() {
        buttonClickCallback.buttonClick(TERMS_FRAGMENT);
    }

    @OnClick(R.id.iv_logout)
    public void onLogoutPressed() {
        PreferenceServices.instance().saveUserId("");
        PreferenceServices.instance().saveUserName("");
        PreferenceServices.instance().saveCurrentLocation("");
        PreferenceServices.instance().saveLatitute("");
        PreferenceServices.instance().saveLongitute("");
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(getActivity());
        sharedPreferencesHelper.logoutUser();
        getActivity().startActivity(new Intent(getActivity(), WelcomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
    private void rateUs(){
        final String appPackageName = getActivity().getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
    /*public void logout() {
        final GoogleApiClient mGoogleApiClient ;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() *//* FragmentActivity *//*, getActivity() *//* OnConnectionFailedListener *//*)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if(mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d(TAG, "User Logged out");

                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d(TAG, "Google API Client Connection Suspended");
            }
        });
    }
*/
}