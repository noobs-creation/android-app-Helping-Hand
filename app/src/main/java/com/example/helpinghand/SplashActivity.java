package com.example.helpinghand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences, s;
    Boolean firstTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //counter for FIR
        s = getSharedPreferences("FIRnumber", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s.edit();
        editor.putInt("counter", 1);
        editor.apply();
        //counter for FIR

        sharedPreferences = getSharedPreferences("counterForSplash", Context.MODE_PRIVATE);

        firstTime = sharedPreferences.getBoolean("flag", true);


        if(firstTime){

            //hiding title bar
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            //making activity fullscreen
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_splash);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    firstTime = false;
                    editor.putBoolean("flag", firstTime);
                    editor.apply();

                    startActivity(new Intent(SplashActivity.this, AuthenticationActivity.class));
                    finish();
                }
            },2121);
        } else {
            startActivity(new Intent(SplashActivity.this, AuthenticationActivity.class));
            finish();
        }
    }
}
