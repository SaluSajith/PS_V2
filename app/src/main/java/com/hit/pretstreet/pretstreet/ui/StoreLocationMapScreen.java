package com.hit.pretstreet.pretstreet.ui;

import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by hit on 22/3/16.
 */
public class StoreLocationMapScreen extends FragmentActivity {
    private ImageView img_back;
    private TextView txt_cat;
    private Typeface font;
    private double latitute, longitute;
    private FrameLayout mapContainer;
    private MapFragment mapFragment;
    private GoogleMap mMap;
    private String storeLocation;//, currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_location_map_screen);
        mapContainer = (FrameLayout) findViewById(R.id.mapContainer);
        img_back = (ImageView) findViewById(R.id.img_back);
        txt_cat = (TextView) findViewById(R.id.txt_cat);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Merriweather Light.ttf");
        txt_cat.setTypeface(font);
        Bundle b = getIntent().getExtras();
        String address = b.getString("address");
        latitute = b.getDouble("lat");
        longitute = b.getDouble("long");
        txt_cat.setText(address);

        /*currentLocation = PreferenceServices.getInstance().getCurrentLocation();
        Log.e("Current location:", currentLocation);*/

        FragmentManager fm = getFragmentManager();
        mapFragment = MapFragment.newInstance();
        fm.beginTransaction().replace(R.id.mapContainer, mapFragment).commit();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                addMarks(mMap);
                //getLineTrace();
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreLocationMapScreen.this.finish();
            }
        });
    }

    private void addMarks(GoogleMap googleMap) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                LatLng latLng = new LatLng(latitute, longitute);
                builder.include(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(storeLocation);
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
                googleMap.addMarker(markerOptions);
            }

            //current location code
            /*if (i == 1) {
                LatLng latLng = new LatLng(Double.parseDouble(PreferenceServices.getInstance().getLatitute()),
                        Double.parseDouble(PreferenceServices.getInstance().getLongitute()));
                builder.include(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(currentLocation);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
                googleMap.addMarker(markerOptions);
            }*/
        }
        LatLngBounds bounds = builder.build();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitute, longitute), 15));
    }

    private void getLineTrace() {
        // Instantiates a new Polyline object and adds points to define a rectangle
        PolylineOptions rectOptions = new PolylineOptions();
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                rectOptions.add(new LatLng(latitute, longitute))
                        .color(Color.argb(60, 0, 0, 255));
            }
            if (i == 1) {
                rectOptions.add(new LatLng(Double.parseDouble(PreferenceServices.getInstance().getLatitute()),
                        Double.parseDouble(PreferenceServices.getInstance().getLongitute())))
                        .color(Color.argb(60, 0, 0, 255));
            }
        }
        mMap.addPolyline(rectOptions);
    }
}
