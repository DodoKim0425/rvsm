package com.example.rvsm;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG="FirebaseMsgService";
    private String msg,title;
    @Override
    public void onNewToken(@NonNull String token) {//토큰을 새로 만들면 토큰 출력
        super.onNewToken(token);

        Log.d("tag", "Refreshed token!!!!!!!: " + token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        //알람오면 실행되는 함수
        super.onMessageReceived(remoteMessage);
        Log.e(TAG,"onMessageRecived");
        title=remoteMessage.getNotification().getTitle();
        msg=remoteMessage.getNotification().getBody();
        Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0);

        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1,1000})//1초 동안 진동
                ;
        //푸시 알림오면 어떤 모양, 소리로 알림 뜨게 할지
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,mBuilder.build());
        mBuilder.setContentIntent(contentIntent);
    }
}
