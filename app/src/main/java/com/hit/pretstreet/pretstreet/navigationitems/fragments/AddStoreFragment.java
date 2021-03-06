package com.hit.pretstreet.pretstreet.navigationitems.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.navigationitems.controllers.NavItemsController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.PLACE_PICKER_REQUEST;

/**
 * Created by User on 7/12/2017.
 */

public class AddStoreFragment extends AbstractBaseFragment<NavigationItemsActivity>
        implements GoogleApiClient.OnConnectionFailedListener  {

    @BindView(R.id.edt_storename) EdittextPret edt_storename;
    @BindView(R.id.edt_name) EdittextPret edt_name;
    @BindView(R.id.edt_email) EdittextPret edt_email;
    @BindView(R.id.edt_landline) EdittextPret edt_landline;
    @BindView(R.id.edt_mobile) EdittextPret edt_mobile;
    @BindView(R.id.edt_location) EdittextPret edt_location;
    @BindView(R.id.edt_about) EdittextPret edt_about;

    NavItemsController navItemsController;
    Location location;

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addstore, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        navItemsController = new NavItemsController(getActivity());
        location = new Location("");

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();
        mGoogleApiClient.connect();
    }

    @OnClick(R.id.btn_addstore)
    public void onSubmitPressed() {
        navItemsController.validateAddStoreFields(edt_storename, edt_name,
                edt_email, edt_landline,
                edt_mobile, edt_about,
                edt_location, location);
    }

    @OnClick(R.id.edt_location)
    public void selectLocation(){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            getActivity().startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void onValidationError(EdittextPret editText, String message){
        editText.setError(message);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(edt_location, connectionResult.getErrorMessage() + "", Snackbar.LENGTH_LONG).show();
    }

    public void setLocation(Place place){
        try {
            String placename = String.format("%s", place.getName());
            edt_location.setText(placename);
            location.setLatitude(Double.parseDouble(String.valueOf(place.getLatLng().latitude)));
            location.setLongitude(Double.parseDouble(String.valueOf(place.getLatLng().longitude)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}