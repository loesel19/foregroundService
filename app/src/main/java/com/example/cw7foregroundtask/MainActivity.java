package com.example.cw7foregroundtask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;

public class MainActivity extends AppCompatActivity {

    //Views
    Button btnStart;
    Button btnStop;
    EditText editTime;
    TextView txtClock;
    //class variables
    public final int TIMER_SERVICE_CODE = 1;
    public final String TIMER_ACTION = "TIMER_UPDATE";
    public String TAG = "timeDemo";
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get handles on views
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        editTime = findViewById(R.id.editTime);
        txtClock = findViewById(R.id.txtClock);
        running = false;
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("rec", "received");
                if (intent.getAction().equals("Timer update")){
                    String status = intent.getStringExtra("status");
                    if (status.equals("run")) {
                        int time = intent.getIntExtra("time", 0);
                        if (time < 10){
                            txtClock.setText("Time: 00:0" + time);
                        }else
                            txtClock.setText("Time: 00:" + time);

                    }
                    else{
                        running = false;
                        btnStart.setEnabled(true);
                        btnStop.setEnabled(false);
                        txtClock.setText("Time: 00:00");
                    }
                }
            }
        };
        intentFilter = new IntentFilter("Timer update");
        btnStop.setEnabled(false);
    }
    
    public void startTimer(View v){
        if (running)
            Toast.makeText(this, "Stop or Let First Timer finish", Toast.LENGTH_SHORT).show();
        else {
            Intent i = new Intent(this, TimerService.class);
            String time = editTime.getText().toString();
            if (time.equals(""))
                i.putExtra("Time", 10);
            else {
                i.putExtra("Time", Integer.parseInt(time));
            }
            i.setAction("START_TIMER");
            running = true;
            startForegroundService(i);
        }
        Log.d(TAG,"Started");
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
    }
    public void stopTimer(View v){
        Intent i = new Intent(this, TimerService.class);
        i.setAction("STOP_TIMER");
        running = false;
        startForegroundService(i);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
    }

//    public void doStuff(View v){
//       WorkRequest backgroundWork = new OneTimeWorkRequest.Builder(BackgroundWork.class).build();
//       WorkManager.
//    }

    @Override
    protected void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}