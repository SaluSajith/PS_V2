package com.hit.pretstreet.pretstreet.sociallogin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.helpers.DatabaseHelper;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;

import java.util.ArrayList;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0 && remoteMessage.getNotification() != null) {
            try {
                Log.d("FCM", "Received");
                sendNotification(remoteMessage.getNotification().getTitle(),
                        remoteMessage.getNotification().getBody());
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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Create and show a simple notification containing the received FCM message.
     * @param messageBody FCM message body received.
     */

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo1)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}