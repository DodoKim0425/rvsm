package com.example.rvsm;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG="FirebaseMsgService";
    private String msg,title;
    public static Context context;
    String utoken;
    @Override
    public void onNewToken(@NonNull String token) {//토큰을 새로 만들면 토큰 출력
        super.onNewToken(token);
        Log.d("tag", "Refreshed token!!!!!!!: " + token);
        //utoken=token;
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        //알람오면 실행되는 함수
        super.onMessageReceived(remoteMessage);
        title=remoteMessage.getData().get("title");
        msg=remoteMessage.getData().get("body");
        Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent=new Intent(this,MainActivity.class);

        String channel_id="channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){//오레오 이상일때
            NotificationChannel notificationChannel=new NotificationChannel(channel_id,"important_notification",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri,null);

            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{100,200,300});
            notificationManager.createNotificationChannel(notificationChannel);
            //builder에 채널등록
            notificationBuilder=new NotificationCompat.Builder(this,channel_id);
        }
        else{
            notificationBuilder=new NotificationCompat.Builder(this);
        }

        notificationBuilder
                .setSmallIcon(R.drawable.ic_launcher_foreground)//알람에 표시될 아이콘
                .setContentTitle(title)//알람제목
                .setContentText(msg)//알람 본문
                .setAutoCancel(true)//선택시 알람 자동삭제
                .setSound(uri)//알람소리
                .setVibrate(new long[]{1,1000})//1은wait시간,1000밀리세컨드=1초 동안 진동
                .setShowWhen(true)//알림이 언제 왔는지 표기
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        ;
        //푸시 알림오면 어떤 모양, 소리로 알림 뜨게 할지
        notificationManager.notify(0,notificationBuilder.build());
        notificationBuilder.setContentIntent(contentIntent);
    }
}
