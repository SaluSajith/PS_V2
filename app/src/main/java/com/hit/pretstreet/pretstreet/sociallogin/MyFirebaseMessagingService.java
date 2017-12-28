package com.hit.pretstreet.pretstreet.sociallogin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.helpers.DatabaseHelper;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.NotificationUtils;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String TAG = "FCM";
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0 && remoteMessage.getNotification() != null) {
            try {
                Log.d("FCM_service", "Received");
                sendNotification(remoteMessage.getNotification().getTitle(),
                        remoteMessage.getNotification().getBody(), remoteMessage);
                savedata(remoteMessage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
/*
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e("FCM", "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e("FCM", "Data Payload: " + remoteMessage.getData().toString());

            try {
                savedata(remoteMessage);
                sendNotification(remoteMessage.getNotification().getTitle(),
                        remoteMessage.getNotification().getBody(), remoteMessage);
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e("FCM", "Exception: " + e.getMessage());
            }
        }*/
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * @param messageBody FCM message body received.
     */

    private void sendNotification(String title, String messageBody, RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("id", remoteMessage.getData().get("id"));
        intent.putExtra("title", remoteMessage.getData().get("title"));
        intent.putExtra("body", remoteMessage.getData().get("body"));
        intent.putExtra("share", remoteMessage.getData().get("share"));
        intent.putExtra("image_url", remoteMessage.getData().get("image_url"));
        intent.putExtra("image", remoteMessage.getData().get("image"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications",
                    NotificationManager.IMPORTANCE_MAX);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Bitmap image = null;
        try {
            URL url = new URL(remoteMessage.getData().get("image_url"));
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            System.out.println(e);
        } finally {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            //Bitmap mask1 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.splash);
            builder = builder
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(0, builder.build());
        }
    }


    private void savedata(RemoteMessage remoteMessage){
        try {
            TrendingItems trendingItems = new TrendingItems();
            trendingItems.setId(remoteMessage.getData().get("id"));
            trendingItems.setTitle(remoteMessage.getNotification().getTitle());
            trendingItems.setArticle(remoteMessage.getNotification().getBody());
            trendingItems.setShareUrl(remoteMessage.getData().get("share"));

            trendingItems.setLogoImage(remoteMessage.getNotification().getIcon());
            ArrayList arrayList = new ArrayList();
            arrayList.add(remoteMessage.getData().get("image"));
            trendingItems.setImagearray(arrayList);

            DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
            databaseHelper.saveNotif(trendingItems);

            int size = PreferenceServices.getInstance().getNotifCOunt();
            PreferenceServices.getInstance().updateNotif(size + 1);

            Intent i = new Intent("RECEIVE_NOTIFICATION");
            sendBroadcast(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Constant.PUSH_NOTIFICATION);
            pushNotification.putExtra("body", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("body");
            //boolean isBackground = data.getBoolean("is_background");
            boolean isBackground = false;
            String imageUrl = data.getString("image_url");
            String timestamp = "";
            //JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            //Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Constant.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), WelcomeActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message,
                                         String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message,
                                                     String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}