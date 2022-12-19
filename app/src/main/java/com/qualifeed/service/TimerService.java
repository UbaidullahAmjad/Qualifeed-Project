package com.qualifeed.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

public class TimerService extends Service {
    private Handler customHandler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    long secs,mins;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }



    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
         /*   binding.tvTimer.setText(String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));*/

            Intent timerIntent = new Intent("timer_action");
            timerIntent.putExtra("timer",String.format("%02d", mins) + ":"
                    + String.format("%02d", secs) );
            sendBroadcast(timerIntent);


            customHandler.postDelayed(this, 0);
        }

    };


    @Override
    public void onDestroy() {
        super.onDestroy();

        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
    }
}
