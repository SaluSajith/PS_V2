package com.hit.pretstreet.pretstreet.splashnlogin;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import org.json.JSONObject;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.CHECKIP_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.INSTALLREFERRERKEY;

public class DeepLinkActivity extends AppCompatActivity implements ApiListenerInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceServices.init(this);
        Intent intent = getIntent();

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            try {
                String valueOne = uri.getQueryParameter("share");
                String id = uri.getQueryParameter("id");
                Log.e("DeepLinkActivity TAG", "data saved "+ valueOne + id);
                PreferenceServices.getInstance().setIdQueryparam(id);
                PreferenceServices.getInstance().setShareQueryparam(valueOne);
                PreferenceServices.getInstance().setTypeQueryparam(INSTALLREFERRERKEY);

                /*Log.e("DeepLinkActivity TAG", "data saved "+ uri.getQueryParameter("utm_campaign"));
                Log.e("DeepLinkActivity TAG", "data saved "+ uri.getQueryParameter("utm_source"));
                Log.e("DeepLinkActivity TAG", "data saved "+ uri.getQueryParameter("utm_medium"));
                Log.e("DeepLinkActivity TAG", "data saved "+ uri.getQueryParameter("utm_term"));
                Log.e("DeepLinkActivity TAG", "data saved "+ uri.getQueryParameter("utm_content")); */
                String referrer = "share=" +valueOne+
                        "&id="+id+
                        "&utm_campaign=" + uri.getQueryParameter("utm_campaign")+
                        "&utm_source=" + uri.getQueryParameter("utm_source")+
                        "&utm_medium=" + uri.getQueryParameter("utm_medium")+
                        "&utm_term=" + uri.getQueryParameter("utm_term")+
                        "&utm_content=" + uri.getQueryParameter("utm_content");
                PreferenceServices.getInstance().setUTMQueryparam(referrer); //utm param*/

                JsonRequestController jsonRequestController = new JsonRequestController(this);
                jsonRequestController.sendRequest(this, new JSONObject(), CHECKIP_URL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (PreferenceServices.getInstance().geUsertId().equalsIgnoreCase("")) {
            startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
        } else {
            if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("")
                    || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("")) {
                startActivity(new Intent(getApplicationContext(), DefaultLocationActivity.class));
            } else {
                intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            finish();
        }

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData data) {
                        if (data == null) {
                            Log.e(Constant.TAG, "getInvitation: no data");
                            return;
                        }
                        // Get the deep link
                        Uri deepLink = data.getLink();
                        //Intent intent1 = data.getUpdateAppIntent(getApplicationContext());

                        Log.e(Constant.TAG, "deepLink "+deepLink);

                        // Extract invite
                        FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(data);
                        if (invite != null) {
                            String invitationId = invite.getInvitationId();
                            // handle invite
                            Log.e(Constant.TAG, "handle invite "+invitationId);
                        }
                        // Handle the deep link
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(Constant.TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        /*if (AppInviteReferral.hasReferral(intent)) {
            processReferralIntent(intent);
        }*/
    }
    private void processReferralIntent(Intent intent) {
        // Extract referral information from the intent
        String invitationId = AppInviteReferral.getInvitationId(intent);
        String deepLink = AppInviteReferral.getDeepLink(intent);
        Log.d(Constant.TAG, "Found Referral: " + invitationId + ":" + deepLink);

    }

    @Override
    public void onResponse(JSONObject response) {
    }

    @Override
    public void onError(String error) {

    }
}
