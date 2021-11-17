package com.example.cw7foregroundtask;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class TimerService extends Service {

    //class variables
    public String TAG = "timeDemo";
    public final String CHANNEL_ID="Timer Channel";
    public final String TIMER_ACTION = "TIMER_UPDATE";
    int startTime;

    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "created service");
        createNotificationChannel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "started command");
        Notification notification = buildNotification("Timer", "this is a simple countdown");
        startForeground(1,notification);
        if (intent.getAction() != null && intent.getAction().equals("START_TIMER")){
            startTime = intent.getIntExtra("Time",10);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < startTime; i++){
                        Notification n = buildNotification("Timer", startTime-i + "");
                        startForeground(1, n);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Notification n = buildNotification("Timer", "Timer Finished!");
                    startForeground(1, n);
                }
            });

            t.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public Notification buildNotification(String title, String content){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        return notification;
    }

    public void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
