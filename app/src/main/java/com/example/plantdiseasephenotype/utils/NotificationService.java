package com.example.plantdiseasephenotype.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.example.plantdiseasephenotype.R;
import com.example.plantdiseasephenotype.activities.ImageDetailActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import static com.example.plantdiseasephenotype.utils.App.FCM_CHANNEL_ID;

public class NotificationService extends FirebaseMessagingService {


    public static final String TAG = "MyTag";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String postId = remoteMessage.getData().get("postId");

        Log.i("Msg", postId);

        Intent intent = new Intent(getApplicationContext(), ImageDetailActivity.class);
        intent.putExtra("postId", postId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, FCM_CHANNEL_ID)
                .setSmallIcon(R.drawable.email_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setColor(Color.BLUE)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1002, notification);


        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "onMessageReceived: Data Size: " + remoteMessage.getData().size());

            for (String key : remoteMessage.getData().keySet()) {
                Log.d(TAG, "onMessageReceived Key: " + key + " Data: " + remoteMessage.getData().get(key));
            }

            Log.d(TAG, "onMessageReceived: Data: " + remoteMessage.getData().toString());
        }

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.d(TAG, "onDeletedMessages: called");
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken: called");
        //upload this token on the app server
    }
}