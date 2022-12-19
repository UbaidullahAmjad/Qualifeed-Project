package com.qualifeed.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.qualifeed.R;
import com.qualifeed.databinding.ActivitySplashBinding;
import com.qualifeed.utils.DataManager;

public class SplashAct extends AppCompatActivity {
    ActivitySplashBinding binding;
    public static int SPLASH_TIME_OUT = 3000;
    int PERMISSION_ID = 44;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
       if(checkPermissions()) processNextActivity();
       else requestPermissions();


    }


    private void processNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (DataManager.getInstance().getUserData(getApplicationContext()) != null &&
                        DataManager.getInstance().getUserData(getApplicationContext()).result != null &&
                        !DataManager.getInstance().getUserData(getApplicationContext()).result.id.equals("")) {
                    startActivity(new Intent(SplashAct.this, ToDoAct.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashAct.this, LoginAct.class));
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(SplashAct.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED  ) {
            return true;
        }
        return false;
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                SplashAct.this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_ID
        );
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                processNextActivity();
            }
        }
    }


}
