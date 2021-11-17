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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class TimerService extends Service {

    //class variables
    public String TAG = "timeDemo";
    public final String CHANNEL_ID="Timer Channel";
    public final String TIMER_ACTION = "TIMER_UPDATE";
    int startTime;
    boolean cancelTimer;

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
        cancelTimer = false;
        if (intent.getAction() != null && intent.getAction().equals("START_TIMER")){
            startTime = intent.getIntExtra("Time",10);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < startTime; i++){
                        if (cancelTimer)
                            break;
                        Notification n;
                        if (startTime - i < 10){
                            n = buildNotification("Timer", "00:0" + (startTime-i) + "");
                        }else
                            n = buildNotification("Timer", "00:" + (startTime - i));
                        startForeground(1, n);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent timeIntent = new Intent(getApplicationContext(), MainActivity.class);
                        timeIntent.setAction("Timer update");
                        timeIntent.putExtra("time", startTime - i);
                        timeIntent.putExtra("status", "run");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(timeIntent);
                    }
                    Notification n = buildNotification("Timer", "Timer Finished!");
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.setAction("Timer update");
                    i.putExtra("status", "finished");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                    startForeground(1, n);
                }
            });

            t.start();
        }
        else{
            cancelTimer = true;
            stopSelf();
        }
        return START_NOT_STICKY;
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
