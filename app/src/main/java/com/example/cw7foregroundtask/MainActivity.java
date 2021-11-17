package com.example.cw7foregroundtask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    //Views
    Button btnStart;
    Button btnStop;
    EditText editTime;
    //class variables
    public final int TIMER_SERVICE_CODE = 1;
    public final String TIMER_ACTION = "TIMER_UPDATE";
    public String TAG = "timeDemo";
    BroadcastReceiver receiver;
    IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get handles on views
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        editTime = findViewById(R.id.editTime);
    }
    
    public void startTimer(View v){
        Intent i = new Intent(this, TimerService.class);
        String time = editTime.getText().toString();
        if (time.equals(""))
        i.putExtra("Time", 10);
        else {
            i.putExtra("Time", Integer.parseInt(time));
        }
        i.setAction("START_TIMER");
        startForegroundService(i);
        Log.d(TAG,"Started");
    }
    public void stopTimer(View v){
        Intent i = new Intent(this, TimerService.class);
        i.setAction("STOP_TIMER");
        startForegroundService(i);
    }
}