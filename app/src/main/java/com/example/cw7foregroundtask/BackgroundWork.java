package com.example.cw7foregroundtask;

import android.util.Log;

public class BackgroundWork {
    public void doStuff(){
        int sum =0;
        for (int i = 0; i < 100000; i++){
            if (i % 10000 == 0)
                Log.d("Progress", i + "");
            for (int j = 0; j < 100000; j++){
                sum += j;
            }
        }
    }
}
