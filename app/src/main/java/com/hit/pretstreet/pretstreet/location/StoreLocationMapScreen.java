package com.hit.pretstreet.pretstreet.location;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;

import java.util.Locale;

public class StoreLocationMapScreen extends FragmentActivity {
    private ImageView img_back;
    private ButtonPret btn_getdir;
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
        setContentView(R.layout.activity_store_location_map_screen);

        mapContainer = (FrameLayout) findViewById(R.id.mapContainer);
        img_back = (ImageView) findViewById(R.id.img_back);
        txt_cat = (TextView) findViewById(R.id.txt_cat);
        btn_getdir = (ButtonPret) findViewById(R.id.btn_getdir);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Merriweather Light.ttf");
        txt_cat.setTypeface(font);
        Bundle b = getIntent().getExtras();
        final String address = b.getString("address");
        latitute = b.getDouble("lat");
        longitute = b.getDouble("long");
        txt_cat.setText(address);
        /* currentLocation = PreferenceServices.getInstance().getCurrentLocation();
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

        btn_getdir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps/dir?api=1&saddr="+
                                Double.parseDouble(PreferenceServices.getInstance().getLatitute())+","+
                                        Double.parseDouble(PreferenceServices.getInstance().getLongitute())
                                +"&daddr="+ latitute + ","+ longitute ));
                                                       // +"&daddr=" + address));
                startActivity(intent);
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
           /* if (i == 1) {
                LatLng latLng = new LatLng(Double.parseDouble(PreferenceServices.getInstance().getLatitute()),
                        Double.parseDouble(PreferenceServices.getInstance().getLongitute()));
                builder.include(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("I am here");
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
                googleMap.addMarker(markerOptions);
            }*/
        }
        LatLngBounds bounds = builder.build();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitute, longitute), 15));
    }

    private void getLineTrace() {
        // Instantiates a new Polyline object and adds points to define a rectangle
        /*PolylineOptions rectOptions = new PolylineOptions();
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
        mMap.addPolyline(rectOptions);*/
        /*Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(Double.parseDouble(PreferenceServices.getInstance().getLatitute()),
                        Double.parseDouble(PreferenceServices.getInstance().getLongitute())), new LatLng(latitute, longitute))*/
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(Double.parseDouble(PreferenceServices.getInstance().getLatitute()),
                        Double.parseDouble(PreferenceServices.getInstance().getLongitute())), new LatLng(latitute, longitute))
                .width(5)
                .color(Color.RED));
    }
}
