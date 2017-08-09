package com.hit.pretstreet.pretstreet;

import android.app.Application;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by User on 20/07/2017.
 */
public class PretStreet extends MultiDexApplication {

    public static final String TAG = PretStreet.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static PretStreet mInstance;
    // private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized PretStreet getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static String getDeviceId() {
        String mDeviceId = "";
        mDeviceId = Settings.Secure.getString(mInstance.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return mDeviceId;
    }
}
