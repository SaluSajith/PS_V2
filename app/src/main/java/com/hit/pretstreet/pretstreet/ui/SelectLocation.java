package com.hit.pretstreet.pretstreet.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.GPSTracker;
import com.hit.pretstreet.pretstreet.Items.DatabaseHelper;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.ActivityManagePermission;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.PermissionResult;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by hit on 28/3/16.
 */
public class SelectLocation extends ActivityManagePermission implements View.OnClickListener {

    private ImageView img_close;
    private Button img_set;
    private TextView txt_location, txt_auto_detect;
    private RelativeLayout rl_auto_detect;
    private EditText edt_search;
    private double lat1, long1;
    private Typeface font;
    private ProgressDialog pDialog;
    private DatabaseHelper helper;
    private LocationListAdapter locationListAdapter;
    private ListView placeList;
    private String currentLocation, strSearch, strType, PageType, latitude, longitude;
    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String TYPE_CITIES = "&sensor=false&types=(cities)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_location_screen);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        PageType = getIntent().getStringExtra("location");
        helper = new DatabaseHelper(getApplicationContext());
        init();

        if (PageType.equalsIgnoreCase("default")) {
            txt_location.setText("Select Location");
        } else {
            txt_location.setText("Preferred Location");
        }

        img_close.setOnClickListener(this);
        rl_auto_detect.setOnClickListener(this);

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
    protected void onResume() {
        super.onResume();
        ArrayList<HashMap<String, String>> list = helper.fetchLocationList();
        if (list.isEmpty()) {
        } else {
            for (int i = 0; i < list.size(); i++) {
                int count = helper.fetchLocationCount();
                Log.e("count:", count + "");
                if (count > 5) {
                    helper.deleteFirstSavedLocationRow();
                } else {
                    Collections.reverse(list);
                    placeList.setVisibility(View.VISIBLE);
                    locationListAdapter = new LocationListAdapter(getApplicationContext(), R.layout.row_search, list);
                    placeList.setAdapter(locationListAdapter);
                    break;
                }
            }
        }
    }

    public class LocationListAdapter extends BaseAdapter {

        private Context context;
        int layoutResourceId;
        private ArrayList<HashMap<String, String>> mItems;

        public LocationListAdapter(Context context, int layoutResourceId, ArrayList<HashMap<String, String>> data) {
            this.context = context;
            this.mItems = data;
            this.layoutResourceId = layoutResourceId;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class RecordHolder {
            TextView txt_loc_name;
            ImageView img_cur_loc;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            RecordHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.row_location, parent, false);
                viewHolder = new RecordHolder();
                viewHolder.img_cur_loc = (ImageView) convertView.findViewById(R.id.img_cur_loc);
                viewHolder.txt_loc_name = (TextView) convertView.findViewById(R.id.txt_loc_name);
                viewHolder.txt_loc_name.setTypeface(font);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RecordHolder) convertView.getTag();
            }

            if (position == 0) {
                viewHolder.img_cur_loc.setVisibility(View.VISIBLE);
            } else {
                viewHolder.img_cur_loc.setVisibility(View.GONE);
            }

            viewHolder.txt_loc_name.setText(mItems.get(position).get("name"));

            /*convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            });*/

            return convertView;
        }

    }

    private void init() {
        img_close = (ImageView) findViewById(R.id.img_close);
        txt_location = (TextView) findViewById(R.id.txt_location);
        txt_auto_detect = (TextView) findViewById(R.id.txt_auto_detect);
        rl_auto_detect = (RelativeLayout) findViewById(R.id.rl_auto_detect);
        edt_search = (EditText) findViewById(R.id.edt_search);
        placeList = (ListView) findViewById(R.id.list_places);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Merriweather Light.ttf");
        txt_location.setTypeface(font);
        txt_auto_detect.setTypeface(font);
        edt_search.setTypeface(font);

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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectLocation.this, android.R.layout.simple_list_item_1, android.R.id.text1, resultList);
            // Setting the adapter
            placeList.setAdapter(adapter);
            placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String str = resultList.get(i).toString();
                    getLocationFromAddress(SelectLocation.this, str);
                    //edt_search.setText(str);
                    placeList.setAdapter(null);
                }
            });
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
                Log.e("Address:old ", latitude + ", " + longitude);
                helper.saveLocation(strAddress);
                Toast.makeText(getApplicationContext(), "Location Changes to " + strAddress, Toast.LENGTH_LONG).show();
                PreferenceServices.instance().saveCurrentLocation(strAddress);
                PreferenceServices.instance().saveLatitute(latitude + "");
                PreferenceServices.instance().saveLongitute(longitude + "");
                finish();
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
            //sb.append(TYPE_CITIES);
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

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.img_close:
                this.finish();
                break;

            case R.id.rl_auto_detect:
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
                break;

            default:
                break;
        }
    }

    private void getLocation() {
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            lat1 = gps.getLatitude();
            long1 = gps.getLongitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list;
            try {
                list = geocoder.getFromLocation(lat1, long1, 2);
                Log.e("LIst: ", list + "");
                if (list.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                } else {
                    Address location = list.get(1);
                    currentLocation = location.getSubLocality();
                    //Log.e("Location: ", currentLocation);
                    Toast.makeText(getApplicationContext(), "Location Changes to " + currentLocation, Toast.LENGTH_LONG).show();
                    PreferenceServices.instance().saveCurrentLocation(currentLocation);
                    PreferenceServices.instance().saveLatitute(lat1 + "");
                    PreferenceServices.instance().saveLongitute(long1 + "");
                    this.finish();
                    //startActivity(new Intent(getApplicationContext(), AfterLoginScreen.class));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            gps.showSettingsAlert();
        }
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
