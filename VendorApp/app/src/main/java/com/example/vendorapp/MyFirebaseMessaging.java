package com.example.vendorapp;

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

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.vendorapp.Client.OrderDetails;
import com.example.vendorapp.Client.OrderDetails2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private static final String NOTIFICATION_CHANNEL_ID = "MY_NOTIFICATION_CHANNEL_ID";//required for android 0 and above
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebasUser;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //all notifications received here

        firebaseAuth = FirebaseAuth.getInstance();
        firebasUser = firebaseAuth.getCurrentUser();

        //get data from notification
        String notificationType = remoteMessage.getData().get("notificationType");
        if (notificationType.equals("NewOrder")) {
            String buyerUid = remoteMessage.getData().get("buyerUid");
            String sellerUid = remoteMessage.getData().get("sellerUid");
            String orderID = remoteMessage.getData().get("orderId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle");
            String notificationMessage = remoteMessage.getData().get("notificationMessage");

            if (firebasUser != null && firebaseAuth.getUid().equals(sellerUid)) {
                showNotification(orderID, sellerUid, buyerUid, notificationTitle, notificationMessage, notificationType);
            }
        }
        if (notificationType.equals("OrderStatusChanged")) {
            String buyerUid = remoteMessage.getData().get("buyerUid");
            String sellerUid = remoteMessage.getData().get("sellerUid");
            String orderId = remoteMessage.getData().get("orderId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle");
            String notificationMessage = remoteMessage.getData().get("notificationMessage");

            if (firebasUser != null && firebaseAuth.getUid().equals(buyerUid)) {
                showNotification(orderId, sellerUid, buyerUid, notificationTitle, notificationMessage, notificationType);
            }
        }
    }

    private void showNotification(String orderId, String sellerUid, String buyerUid, String notificationTitle, String notificationMessage,
                                  String notificationType) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //generate random id for notification
        int notificationID = new Random().nextInt(3000);

        //check if android version is Oreo/O or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNotificationChannel(notificationManager);
        }

        Intent intent = null;
        if (notificationType.equals("OrderStatusChanged")) {
            intent = new Intent(this, OrderDetails2.class);

            intent.putExtra("orderID", orderId);
            intent.putExtra("shopID", sellerUid);
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);

        } else if (notificationType.equals("NewOrder")) {
            intent = new Intent(this, OrderDetails.class);

            intent.putExtra("orderID", orderId);
            intent.putExtra("orderBy", buyerUid);
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeicon = BitmapFactory.decodeResource(getResources(), R.drawable.cart);

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder xer = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        xer.setLargeIcon(largeicon)
                .setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSound(notificationSoundUri)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationID, xer.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupNotificationChannel(NotificationManager notificationManager) {
        CharSequence channelName = "XerxesCodes Rocks";
        String channelDescription = "Channel Description here";

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}