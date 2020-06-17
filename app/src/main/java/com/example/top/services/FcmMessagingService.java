package com.example.top.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.top.R;
import com.example.top.view.NotificacionLocalActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FcmMessagingService extends FirebaseMessagingService {


    private static final String DESCUENTO = "descuento";

    public FcmMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0 && remoteMessage.getNotification() != null){
            sendNotification(remoteMessage);
        }

    }

    private void sendNotification(RemoteMessage remoteMessage) {
        float desc = Float.valueOf(remoteMessage.getData().get(DESCUENTO));

        Intent intent = new Intent(this, NotificacionLocalActivity.class);
        intent.putExtra(DESCUENTO, desc);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Uri sonidoPorDefecto = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_notificacion)
                .setContentTitle(remoteMessage.getNotification().getTitle())//("Hola!")
                .setContentText(remoteMessage.getNotification().getBody()) //("Bienvenido")
                .setAutoCancel(true)
                .setSound(sonidoPorDefecto)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(desc > .4 ?
                    ContextCompat.getColor(getApplicationContext(), R.color.color_primary) :
                    ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            String channelId = desc < .10 ? getString(R.string.low_channel_id) : getString(R.string.normal_channel_id);
            String channelName = desc < .10? getString(R.string.low_channel_name) : getString(R.string.normal_channel_name);

            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 200, 50});

            if (notificationManager != null){
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notificationBuilder.setChannelId(channelId);
        }

        if (notificationManager != null){
            notificationManager.notify("", 0, notificationBuilder.build());
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        Log.d("New Token", token);
    }
}
