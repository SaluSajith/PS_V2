package com.hit.pretstreet.pretstreet.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.fragment.AccountFragment;
import com.hit.pretstreet.pretstreet.fragment.AddStoreFragment;
import com.hit.pretstreet.pretstreet.fragment.CategoryWiseStoreListFragment;
import com.hit.pretstreet.pretstreet.fragment.FollowersFragment;
import com.hit.pretstreet.pretstreet.fragment.HomeFragment;
import com.hit.pretstreet.pretstreet.fragment.StoreDetailFragment;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by hit on 17/3/16.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_home, btn_account, btn_following, btn_addstore;
    private Typeface font;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        getSupportActionBar().hide();
        PreferenceServices.init(this);
        init();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.e("dm.densityDpi:", dm.densityDpi + "");
        PreferenceServices.instance().saveDeviceSize(dm.densityDpi);

        btn_home.setOnClickListener(this);
        btn_account.setOnClickListener(this);
        btn_following.setOnClickListener(this);
        btn_addstore.setOnClickListener(this);
        if (savedInstanceState == null) {
            displayView(0);
        }
    }

    private void init() {
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_account = (Button) findViewById(R.id.btn_account);
        btn_following = (Button) findViewById(R.id.btn_following);
        btn_addstore = (Button) findViewById(R.id.btn_addstore);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "RedVelvet-Regular.otf");
        btn_home.setTypeface(font);
        btn_account.setTypeface(font);
        btn_following.setTypeface(font);
        btn_addstore.setTypeface(font);
        getFixedImages("loginscreen");
    }

    private void getFixedImages(final String id) {
        String urlJsonObj = Constant.FASHION_API + "route=" + id;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Volley Base Image", response.toString());
                boolean responseSuccess = false;
                String strsuccess, imgUrl = null;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equalsIgnoreCase("true")) {
                        responseSuccess = true;
                        imgUrl = response.getString("url");
                    } else {
                        responseSuccess = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    responseSuccess = false;
                }
                if (responseSuccess) {
                    PreferenceServices.instance().saveBaseImage(imgUrl);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
        } else {
            // error in creating fragment
            Log.e("HomeActivity", "Error in creating fragment");
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof HomeFragment) {
            if (doubleBackToExitPressedOnce)
                finish();
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {

            case R.id.btn_home:
                Fragment preFrag = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                if (preFrag instanceof HomeFragment) {
                } else {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    while (fragmentManager.getBackStackEntryCount() > 0) {
                        Log.e("Stack Count", fragmentManager.getBackStackEntryCount() + "");
                        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                }
                break;

            case R.id.btn_account:
                Fragment f2 = new AccountFragment();
                FragmentTransaction t2 = getSupportFragmentManager().beginTransaction();
                t2.hide(getSupportFragmentManager().findFragmentById(R.id.frame_container));
                t2.add(R.id.frame_container, f2);
                t2.addToBackStack(null);
                t2.commit();
                break;

            case R.id.btn_following:
                Fragment f3 = new FollowersFragment();
                Bundle b3 = new Bundle();
                b3.putString("screen", "followings");
                f3.setArguments(b3);
                FragmentTransaction t3 = getSupportFragmentManager().beginTransaction();
                t3.hide(getSupportFragmentManager().findFragmentById(R.id.frame_container));
                t3.add(R.id.frame_container, f3);
                t3.addToBackStack(null);
                t3.commit();
                break;

            case R.id.btn_addstore:
                Fragment f1 = new AddStoreFragment();
                FragmentTransaction t1 = getSupportFragmentManager().beginTransaction();
                t1.hide(getSupportFragmentManager().findFragmentById(R.id.frame_container));
                t1.add(R.id.frame_container, f1);
                t1.addToBackStack(null);
                t1.commit();
                break;

            default:
                break;
        }
    }
}
