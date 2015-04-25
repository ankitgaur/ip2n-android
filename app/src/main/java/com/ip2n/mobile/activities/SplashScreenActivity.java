package com.ip2n.mobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.ip2n.mobile.R;



public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences mSharedPrefs;


        mSharedPrefs = getSharedPreferences("NIGERIA_PLEDGE", 0);
        if (mSharedPrefs.getString("NIGERIA_LOGIN_KEY", null) != null) {
            Intent i = new Intent(this, NigeriaTimelineActivity.class);

            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);


        } else {
            Intent i = new Intent(this, NigeriaLoginActivity.class);

            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }
        setContentView(R.layout.activity_splash_screen);


    }





}
