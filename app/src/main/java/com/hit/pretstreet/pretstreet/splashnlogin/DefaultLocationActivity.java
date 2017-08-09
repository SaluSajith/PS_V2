package com.hit.pretstreet.pretstreet.splashnlogin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.helpers.DatabaseHelper;
import com.hit.pretstreet.pretstreet.core.helpers.GPSTracker;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.PermissionResult;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DefaultLocationActivity extends AbstractBaseAppCompatActivity {

    @BindView(R.id.img_close) ImageView img_close;
    @BindView(R.id.edt_search) EdittextPret edt_search;
    @BindView(R.id.list_places) ListView placeList;

    private double lat1, long1;
    private DatabaseHelper helper;
    private String currentLocation, strSearch, strType, PageType, latitude, longitude;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String TYPE_COUNTRIES = "&components=country:in|country:ae";
    private static final String TYPE_SENSOR = "&types=(cities)";//,sublocality
    String types = "cities|sublocality";
    private static final String TYPE_CITIES = "&sensor=false&types=(cities)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_location_screen);
        init();
    }

    private void init(){
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        helper = new DatabaseHelper(getApplicationContext());
        edt_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strSearch = s.toString();
                new doInBackground().execute();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

        });
    }
    @Override
    protected void setUpController() {
    }

    private class doInBackground extends AsyncTask<Void, Void, Boolean> {
        ArrayList<String> resultList;

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Retrieve the autocomplete results.
            resultList = autocomplete(strSearch);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (resultList==null) {
                displaySnackBar("Please try after sometime");
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, resultList);
                placeList.setAdapter(adapter);
                placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String str = resultList.get(i).toString();
                        getLocationFromAddress(DefaultLocationActivity.this, str);
                        placeList.setAdapter(null);
                    }
                });
            }

        }
    }

    public void getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address != null) {
                Address location = address.get(0);
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                Log.e("Address: ", latitude + ", " + longitude + " " + strAddress);
                helper.saveLocation(strAddress);
                String parts[] = new String[0];
                if (strAddress.contains(",")) {
                    parts= strAddress.split(",");
                    Log.e("Address", parts[0]);
                }
                displaySnackBar("Location set to " + parts[0]);
                PreferenceServices.instance().saveCurrentLocation(parts[0]+"");
                PreferenceServices.instance().saveLatitute(latitude + "");
                PreferenceServices.instance().saveLongitute(longitude + "");
                finish();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + Constant.API_KEY_BROWSER);
            sb.append(TYPE_COUNTRIES);//|country:ae");
            //sb.append("&sensor=false&types=(cities)");
            sb.append("&sensor=false");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            URL url = new URL(sb.toString());
            //System.out.println("URL: " + url);
            Log.e("URL: ", url + "");
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
        }
        return resultList;
    }

    @OnClick(R.id.img_close)
    public void onClosePressed() {
        this.finish();
    }

    @OnClick(R.id.rl_auto_detect)
    public void onAutoDetectPressed() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            askCompactPermission(Manifest.permission.ACCESS_FINE_LOCATION, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    getLocation();
                }

                @Override
                public void permissionDenied() {

                }
            });
        }
    }

    private void getLocation() {
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            displaySnackBar("Please wait while fetching your location..");
            lat1 = gps.getLatitude();
            long1 = gps.getLongitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list;
            try {
                list = geocoder.getFromLocation(lat1, long1, 2);
                if (list.isEmpty()) {
                    displaySnackBar("Please try again");
                } else {
                    Address location = list.get(1);
                    currentLocation = location.getSubLocality();
                    //currentLocation = location.getAddressLine(0) + ", " + location.getAddressLine(1);
                    displaySnackBar( "Location set to " + currentLocation);
                    PreferenceServices.instance().saveCurrentLocation(currentLocation);
                    PreferenceServices.instance().saveLatitute(lat1 + "");
                    PreferenceServices.instance().saveLongitute(long1 + "");
                    this.finish();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            gps.showSettingsAlert();
        }
    }

}