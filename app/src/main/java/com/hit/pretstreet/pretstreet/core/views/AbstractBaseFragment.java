package com.hit.pretstreet.pretstreet.core.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hit.pretstreet.pretstreet.BuildConfig;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.helpers.UIThreadHandler;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.PermissionResult;

import java.util.ArrayList;

/**
 * Created by User on 7/7/2017.
 * Parent fragment
 */

public abstract class AbstractBaseFragment <T extends FragmentActivity> extends Fragment {

    private ProgressDialog pDialog;
    private UIThreadHandler handler;

    private int KEY_PERMISSION = 0;
    private PermissionResult permissionResult;
    private String permissionsAsk[];

    protected abstract View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.handler = new UIThreadHandler();
        return onCreateViewImpl(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG){ }
        else {
            /** Update fragment open event in Google analytics */
            PretStreet pretStreet = (PretStreet) getActivity().getApplication();
            Tracker mTracker = pretStreet.tracker();
            mTracker.set("UserTrack", this.getClass().getSimpleName());
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Fragment")
                    .setAction(mTracker.get("UserTrack"))
                    .setLabel(mTracker.get("UserTrack"))
                    .build());
        }
    }

    /**
     * Destroys the ScanningViewPresenter when the View is destroyed
     */

    protected void runOnUIThread(Runnable action) {
        this.handler.post(action);
    }

    public void showProgressDialog(String mContent) {
        if (!pDialog.isShowing()) {
            pDialog.show();
            pDialog.setContentView(R.layout.loading_view);
            pDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public void destroyDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @SuppressWarnings("unchecked")
    public T getHostActivity() {
        return (T) getActivity();
    }

    public void startActivityAndFinish(Class<?> mActivity) {
        try {
            Intent intent = new Intent(getActivity(), mActivity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**Load glide image in Homepage and Subcat page
 * @param url from where image have to be loaded
 * @param imageView to where image have to be loaded
 * @param mask crop downloaded bitmap to this shape
 * @param placeholder placeholder image while loading
 * */
    public void loadImage(String url, final ImageView imageView, final Bitmap mask, int placeholder){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int dwidth = displaymetrics.widthPixels;
        final int dheight = (int) ((displaymetrics.heightPixels) * 0.45);

        Glide.with(getActivity())
                .load(url).asBitmap()
                .placeholder(placeholder)
                //.centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.dontAnimate()
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        try {
                            int width = resource.getWidth();
                            int height = resource.getHeight();
                            float scaleWidth = ((float) dwidth) / width;
                            float scaleHeight = ((float) dheight) / height;

                            /** Creating matrix for image scaling*/
                            Matrix matrix = new Matrix();
                            if (width > height)  //horizontal image
                                if (scaleHeight > scaleWidth) //scaling image as per the device matrix
                                    matrix.postScale(scaleWidth, scaleWidth);
                                else
                                    matrix.postScale(scaleHeight, scaleHeight);
                            else {   // vertical image
                                if (scaleHeight > scaleWidth)  //scaling image as per the device matrix
                                    matrix.postScale(scaleHeight, scaleHeight);
                                else
                                    matrix.postScale(scaleWidth, scaleWidth);
                            }
                            /** Scaling/Cropping downloaded bitmap to adjust in the trape shape*/
                            //resizing downloaded bitmap as per the scaling factor
                            Bitmap resizedBitmap = Bitmap.createBitmap(resource, 0, 0, width, height, matrix, false);
                            //creating bitmap as per the mask
                            Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas mCanvas = new Canvas(result);
                            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                            mCanvas.drawBitmap(resizedBitmap, 0, 0, null);
                            mCanvas.drawBitmap(mask, 0, 0, paint);//draw image as per the mask
                            paint.setXfermode(null);
                            imageView.setImageBitmap(result);
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                        } catch (Exception e){
                            e.printStackTrace();
                        } catch (OutOfMemoryError e){
                            e.printStackTrace();
                        }
                    }
                });
    }


    @Deprecated
    public AbstractBaseFragment askPermission(String permission) {
        this.permissionsAsk = new String[]{permission};
        return AbstractBaseFragment.this;
    }
    @Deprecated
    public AbstractBaseFragment askPermissions(String permissions[]) {
        this.permissionsAsk = permissions;
        return AbstractBaseFragment.this;
    }

    public AbstractBaseFragment setPermissionResult(PermissionResult permissionResult) {
        this.permissionResult = permissionResult;
        return AbstractBaseFragment.this;
    }

    public AbstractBaseFragment requestPermission(int keyPermission) {
        KEY_PERMISSION = keyPermission;
        internalRequestPermission(permissionsAsk);
        return AbstractBaseFragment.this;
    }


    public boolean isPermissionGranted(Context context, String permission) {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) ||
                (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }


    private void internalRequestPermission(String[] permissionAsk) {
        String arrayPermissionNotGranted[];
        ArrayList<String> permissionsNotGranted = new ArrayList<>();

        for (int i = 0; i < permissionAsk.length; i++) {
            if (!isPermissionGranted(getActivity(), permissionAsk[i])) {
                permissionsNotGranted.add(permissionAsk[i]);
            }
        }

        if (permissionsNotGranted.isEmpty()) {
            if (permissionResult != null)
                permissionResult.permissionGranted();

        } else {
            arrayPermissionNotGranted = new String[permissionsNotGranted.size()];
            arrayPermissionNotGranted = permissionsNotGranted.toArray(arrayPermissionNotGranted);
            requestPermissions(arrayPermissionNotGranted, KEY_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == KEY_PERMISSION) {
            boolean granted = true;

            for (int grantResult : grantResults) {
                if (!(grantResults.length > 0 && grantResult == PackageManager.PERMISSION_GRANTED))
                    granted = false;
            }
            if (permissionResult != null) {
                if (granted) {
                    permissionResult.permissionGranted();
                } else {
                    permissionResult.permissionDenied();
                }
            } else {
                Log.e("ManagePermissions", "permissionResult callback was null");
            }
        }
    }

    public void askCompactPermission(String permission, PermissionResult permissionResult) {
        KEY_PERMISSION = 200;
        permissionsAsk = new String[]{permission};
        this.permissionResult = permissionResult;
        internalRequestPermission(permissionsAsk);
    }

    public void askCompactPermissions(String permissions[], PermissionResult permissionResult) {
        KEY_PERMISSION = 200;
        permissionsAsk = permissions;
        this.permissionResult = permissionResult;
        internalRequestPermission(permissionsAsk);
    }
}
