package com.hit.pretstreet.pretstreet.location;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
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
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;

public class StoreLocationMapScreen extends FragmentActivity {

    private double latitute, longitute;
    private MapFragment mapFragment;
    private GoogleMap mMap;
    private String storeName;

    @BindView(R.id.img_back) AppCompatImageView img_back;
    @BindView(R.id.btn_getdir) ButtonPret btn_getdir;
    @BindView(R.id.txt_cat) TextViewPret txt_cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_location_map_screen);
        ButterKnife.bind(this);

        //get data from intent
        if (getIntent() != null) {
            if (getIntent().getExtras() != null && getIntent().getExtras()
                    .containsKey("lat")) {
                Bundle b = getIntent().getExtras();
                final String address = b.getString("address");
                storeName = b.getString("name");
                latitute = b.getDouble("lat");
                longitute = b.getDouble("long");
                txt_cat.setText(address);
            }
        }
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

                /** Passing store location and user location to Google Map*/
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

    /** Locate store by putting a marker*/
    private void addMarks(GoogleMap googleMap) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng latLng = new LatLng(latitute, longitute);
        builder.include(latLng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(storeName);
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitute, longitute), 15));
    }
}
