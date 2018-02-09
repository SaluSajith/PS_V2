package com.hit.pretstreet.pretstreet.splashnlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.helpers.DatabaseHelper;
import com.hit.pretstreet.pretstreet.core.helpers.LocationTracker;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LocCallbackInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

import static com.hit.pretstreet.pretstreet.core.utils.Constant.GETLOCATION_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.REQUEST_APP_SETTINGS;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.REQUEST_CHECK_SETTINGS_GPS;
import static com.hit.pretstreet.pretstreet.core.utils.PreferenceServices.currentloc;
import static com.hit.pretstreet.pretstreet.core.utils.PreferenceServices.dropdownloc;
/**
 * Location Select page which uses the
 * class LocationTracker for getting precise location
 **/
public class DefaultLocationActivity extends
        AbstractBaseAppCompatActivity implements ApiListenerInterface, LocCallbackInterface{

    @BindView(R.id.img_close) AppCompatImageView img_close;
    @BindView(R.id.edt_search) EdittextPret edt_search;
    @BindView(R.id.list_places) ListView placeList;

    private double lat1, long1;
    private DatabaseHelper helper;
    private String currentLocation, latitude, longitude;

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String TYPE_COUNTRIES = "&components=country:in|country:ae";
    static String types = "regions";
    private static final String TYPE_CITIES = "&sensor=false&types=(" + types + ")";

    JsonRequestController jsonRequestController;
    String strSearch;
    LocationTracker locationTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_location);
        init();
    }

    private void init(){
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        helper = new DatabaseHelper(getApplicationContext());
        locationTracker = new LocationTracker(DefaultLocationActivity.this);
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
        jsonRequestController = new JsonRequestController(this);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            Log.d("response", response+"");
            this.showProgressDialog(getResources().getString(R.string.loading));
            String url = response.getString("URL");

            if(url.contains("address")) {
                String Status = response.getString("status");
                if (Status.equalsIgnoreCase("OK")) {
                    JSONArray results = response.getJSONArray("results");
                    JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    Address address = new Address(null);
                    address.setLatitude(Double.parseDouble(location.getString("lat")));
                    address.setLongitude(Double.parseDouble(location.getString("lng")));

                    JSONArray address_components = results.getJSONObject(0).getJSONArray("address_components");
                    for (int i = 0; i < address_components.length(); i++) {
                        if (address_components.get(i).toString().contains("locality"))
                            address.setLocality(address_components.getJSONObject(i).getString("short_name"));
                        /*if (address_components.get(i).toString().contains("sublocality_level_1")) {
                            sublocality = address_components.getJSONObject(i).getString("short_name");
                        }*/
                    }
                    saveNContinue(address, strSearch);
                }
            } else if(url.contains("latlng")){
                String Status = response.getString("status");
                if (Status.equalsIgnoreCase("OK")) {
                    JSONArray results = response.getJSONArray("results");
                    JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    Address address = new Address(null);
                    String sublocality = "";
                    address.setLatitude(Double.parseDouble(location.getString("lat")));
                    address.setLongitude(Double.parseDouble(location.getString("lng")));

                    JSONArray address_components = results.getJSONObject(0).getJSONArray("address_components");
                    for (int i = 0; i < address_components.length(); i++) {
                        try {
                            JSONObject zero2 = address_components.getJSONObject(i);
                            JSONArray mtypes = zero2.getJSONArray("types");
                            if (mtypes.toString().contains("sublocality_level_1")) {
                                sublocality = zero2.getString("long_name");
                            }
                            if (mtypes.toString().contains("locality"))
                                address.setLocality(zero2.getString("long_name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    saveNContinue(address, sublocality+"");
                } else{
                    displaySnackBar("Location not found!! Please try again");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            this.hideDialog();
            displaySnackBar("Location not found! Please try again");
        }
        hideDialog();
    }

    @Override
    public void onError(String error) {
        hideDialog();
        displaySnackBar(error);
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
                displaySnackBar("Please check your internet connection!");
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, resultList);
                placeList.setAdapter(adapter);
                placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String str = resultList.get(i);
                        getLocationFromAddress(DefaultLocationActivity.this, str);
                        placeList.setAdapter(null);
                    }
                });
            }
        }
    }

    public void getLocationFromAddress(Context context, String strAddress) {
        this.showProgressDialog(getResources().getString(R.string.loading));
        strSearch = strAddress;
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address != null) {
                Address location = address.get(0);
                Log.e("Address: ", address+"  1");
                saveNContinue(location, strAddress);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            try {
                String query = URLEncoder.encode(strAddress, "utf-8");
                String URL = GETLOCATION_URL + "&address="+query;
                showProgressDialog(getResources().getString(R.string.loading));
                jsonRequestController.sendRequestGoogle(DefaultLocationActivity.this, jsonObject, URL);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveNContinue(Address location, String strAddress){
        try {
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
            Log.e("Address: ", latitude + ", " + longitude + " " +location);
            helper. saveLocation(strAddress);
            String parts[];
            String place;
            if (strAddress.contains(",")) {
                parts= strAddress.split(",");
                place = parts[0];
                Log.e("Address", parts[0]);
            }
            else place = strAddress;
            displaySnackBar("Location set to " + place);
            try{
                String locality = location.getLocality();
                if(!locality.equalsIgnoreCase("null")) {
                    if (place.equals(locality))
                        PreferenceServices.instance().saveCurrentLocation(place);
                    else
                        PreferenceServices.instance().saveCurrentLocation(place + ", " + locality);
                }
            }catch (Exception e){
                PreferenceServices.instance().saveCurrentLocation(place);
                e.printStackTrace();
            }

            PreferenceServices.instance().saveLatitute(latitude + "");
            PreferenceServices.instance().saveLongitute(longitude + "");
            PreferenceServices.instance().saveLocationType(dropdownloc);
            this.hideDialog();
            finish();
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + Constant.API_KEY_BROWSER);
            sb.append(TYPE_COUNTRIES);
            sb.append(TYPE_CITIES);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            URL url = new URL(sb.toString());
            Log.e("URL: ", url + "");
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
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
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            resultList = new ArrayList<>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                if(predsJsonArray.getJSONObject(i).toString().contains("administrative_area_level_1")||
                        predsJsonArray.getJSONObject(i).toString().contains("country"));
                else
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
        if (locationTracker.canGetLocation()) {
            //displaySnackBar("Please wait while fetching your location..");

            if(locationTracker.getLatitude()==0 && locationTracker.getLongitude()==0) {
                showProgressDialog(getResources().getString(R.string.loading));
                lat1 = 0;
                long1 = 0;
                locationTracker.getMyLocation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(lat1 == 0 && long1 == 0){
                            hideDialog();
                            displaySnackBar("Unable to find the location! Please check your location settings.");
                        }
                    }
                }, 10000);
            } else
                getLocation();
        } else
            locationTracker.checkForLocationSettings();
    }

    public void getLocation() {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list;
            try {
                list = geocoder.getFromLocation(lat1, long1, 2);
                if (list.isEmpty()) {
                    //displaySnackBar("Please try again");
                    getLatlng();
                } else {
                    Address location = list.get(1);
                    currentLocation = location.getSubLocality();
                    //displaySnackBar( "Location set to " + currentLocation);
                    try{
                        String locality = location.getLocality();
                        if(!locality.equalsIgnoreCase("null")) {
                            if (currentLocation.equals(locality))
                                PreferenceServices.instance().saveCurrentLocation(currentLocation);
                            else
                                PreferenceServices.instance().saveCurrentLocation(currentLocation + ", " + locality);
                        }
                    }catch (Exception e){
                        PreferenceServices.instance().saveCurrentLocation(currentLocation);
                        e.printStackTrace();
                    }
                    PreferenceServices.instance().saveLatitute(lat1 + "");
                    PreferenceServices.instance().saveLongitute(long1 + "");
                    PreferenceServices.instance().saveLocationType(currentloc);
                    this.finish();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            } catch (IOException e) {
                e.printStackTrace();
                getLatlng();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLatlng(){
        try {
            JSONObject jsonObject = new JSONObject();
            showProgressDialog(getResources().getString(R.string.loading));
            String URL = GETLOCATION_URL + "&latlng=" + lat1 + "," + long1;
            jsonRequestController.sendRequestGoogle(DefaultLocationActivity.this, jsonObject, URL);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        locationTracker.removeUpdates();
    }

    private void showSnackbar(String result){
        final Snackbar snackBar = Snackbar.make(findViewById(R.id.txt_location), result, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction("Enable", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });
        snackBar.show();
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, REQUEST_APP_SETTINGS);
    }

    @Override
    public void setLoc(Location location) {
        hideDialog();
        lat1 = location.getLatitude();
        long1 = location.getLongitude();
        getLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            locationTracker.checkForLocationSettings();
        }
        else{
            showSnackbar("Pretstreet needs permission to access this feature");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        locationTracker.checkPermissions();
                        break;
                    case Activity.RESULT_CANCELED:
                        locationTracker.checkForLocationSettings();
                        break;
                }
                break;
        }
    }
}