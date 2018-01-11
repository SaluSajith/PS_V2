package com.hit.pretstreet.pretstreet.sociallogin;

import android.annotation.SuppressLint;
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

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.helpers.DatabaseHelper;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.NOTIF_BODY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.NOTIF_ID;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.NOTIF_IMAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.NOTIF_SHARE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.NOTIF_TITLE;
/**
 * Service to receive Notification data
 *
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
    private Uri defaultSoundUri;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0){ //&& remoteMessage.getNotification() != null) {
            defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            try {
                PreferenceServices.init(this);
                sendNotification(remoteMessage);
                savedata(remoteMessage);
                Intent i = new Intent("RECEIVE_NOTIFICATION");
                sendBroadcast(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * @param remoteMessage FCM message body received.
     */
    private void sendNotification(RemoteMessage remoteMessage) throws FileNotFoundException {

        PendingIntent pendingIntent = setupPendingIntent(remoteMessage);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications",
                    NotificationManager.IMPORTANCE_MAX);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = null;
        if(!remoteMessage.getData().containsKey(NOTIF_IMAGE)){
            builder = getBuilderWithoutImage(remoteMessage, pendingIntent);
        }
        else {
            Bitmap image;
            try {
                URL url = new URL(remoteMessage.getData().get(NOTIF_IMAGE));
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                builder = getBuilderWithImage(remoteMessage, pendingIntent, image);
            } catch (FileNotFoundException e) {
                builder = getBuilderWithoutImage(remoteMessage, pendingIntent);
            } catch (IOException e) {
                builder = getBuilderWithoutImage(remoteMessage, pendingIntent);
            }
        }
        System.out.println("notify");
        notificationManager.notify(0, builder.build());
    }

    /**
     * Setup pending intent for notification on click
     * Will open Welcome activity
     * @param remoteMessage FCM message body received.
     */
    private PendingIntent setupPendingIntent(RemoteMessage remoteMessage){
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(NOTIF_ID, remoteMessage.getData().get(NOTIF_ID));
        intent.putExtra(NOTIF_TITLE, remoteMessage.getData().get(NOTIF_TITLE));
        intent.putExtra(NOTIF_BODY, remoteMessage.getData().get(NOTIF_BODY));
        intent.putExtra(NOTIF_SHARE, remoteMessage.getData().get(NOTIF_SHARE));
        intent.putExtra(NOTIF_IMAGE, remoteMessage.getData().get(NOTIF_IMAGE));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        return  pendingIntent;
    }

    /**
     * Setup builder without image
     * @param remoteMessage FCM message body received.
     * @param pendingIntent Data bundle
     */
    private NotificationCompat.Builder getBuilderWithoutImage(RemoteMessage remoteMessage, PendingIntent pendingIntent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder = builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getData().get(NOTIF_TITLE))
                .setContentText(remoteMessage.getData().get(NOTIF_BODY))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        System.out.println("getBuilderWithoutImage");
        return builder;
    }

    /**
     * Setup builder with image
     * @param remoteMessage FCM message body received.
     * @param pendingIntent Data bundle
     */
    private NotificationCompat.Builder getBuilderWithImage(RemoteMessage remoteMessage, PendingIntent pendingIntent, Bitmap image){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder = builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                        .setBigContentTitle(remoteMessage.getData().get(NOTIF_TITLE)))
                .setContentTitle(remoteMessage.getData().get(NOTIF_TITLE))
                .setContentText(remoteMessage.getData().get(NOTIF_BODY))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        System.out.println("getBuilderWithImage");

        return builder;
    }

    /**
     * Save notification list
     * @param remoteMessage FCM message body received.
     */
    private void savedata(RemoteMessage remoteMessage){
        try {
            TrendingItems trendingItems = new TrendingItems();
            trendingItems.setId(remoteMessage.getData().get(NOTIF_ID));
            trendingItems.setTitle(remoteMessage.getData().get(NOTIF_TITLE));
            trendingItems.setArticle(remoteMessage.getData().get(NOTIF_BODY));
            trendingItems.setShareUrl(remoteMessage.getData().get(NOTIF_SHARE));
            if(remoteMessage.getData().containsKey(NOTIF_IMAGE)) {
                trendingItems.setLogoImage(remoteMessage.getData().get(NOTIF_IMAGE));
                ArrayList arrayList = new ArrayList();
                arrayList.add(remoteMessage.getData().get(NOTIF_IMAGE));
                trendingItems.setImagearray(arrayList);
            }

            DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
            databaseHelper.saveNotif(trendingItems);

            int size = PreferenceServices.getInstance().getNotifCOunt();
            PreferenceServices.getInstance().updateNotif(size + 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}