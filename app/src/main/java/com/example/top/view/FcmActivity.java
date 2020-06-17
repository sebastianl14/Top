package com.example.top.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.top.R;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FcmActivity extends AppCompatActivity {

    private static final String SP_TOPICS = "sharedPreferencesTopics";

    @BindView(R.id.spTopics)
    Spinner spTopics;
    @BindView(R.id.tvTopics)
    TextView tvTopics;

    private Set<String> topicsSet;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm);
        ButterKnife.bind(this);

        configSharedPreferences();

//        Intent intent = new Intent(this, NotificacionLocalActivity.class);
//        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Uri sonidoPorDefecto = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        Notification.Builder notificationBuilder = new Notification.Builder(this)
//                .setSmallIcon(R.drawable.ic_notificacion)
//                .setContentTitle("Hola!")
//                .setContentText("Bienvenido")
//                .setAutoCancel(true)
//                .setSound(sonidoPorDefecto)
//                .setContentIntent(pendingIntent);
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelId = getString(R.string.normal_channel_id);
//            String channelName = getString(R.string.normal_channel_name);
//
//            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName,
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationChannel.enableVibration(true);
//            notificationChannel.setVibrationPattern(new long[]{100, 200, 200, 50});
//
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//            notificationBuilder.setChannelId(channelId);
//        }
//
//        if (notificationManager != null) {
//            notificationManager.notify("", 0, notificationBuilder.build());
//        }

    }

    private void configSharedPreferences() {
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        topicsSet = sharedPreferences.getStringSet(SP_TOPICS, new HashSet<>());
        mostrarTopics();
    }

    private void mostrarTopics() {
        tvTopics.setText(topicsSet.toString());
    }

    @OnClick({R.id.btnSuscribir, R.id.btnDesuscribir})
    public void onViewClicked(View view) {

        String topic = getResources().getStringArray(R.array.topicsValues)[spTopics.getSelectedItemPosition()];

        switch (view.getId()) {
            case R.id.btnSuscribir:
                if (!topicsSet.contains(topic)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(topic);
                    topicsSet.add(topic);
                    guardarSharedPreferences();
                }
                break;
            case R.id.btnDesuscribir:
                if (topicsSet.contains(topic)) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                    topicsSet.remove(topic);
                    guardarSharedPreferences();
                }
                break;
        }
    }

    private void guardarSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putStringSet(SP_TOPICS, topicsSet);
        editor.apply();

        mostrarTopics();
    }
}
