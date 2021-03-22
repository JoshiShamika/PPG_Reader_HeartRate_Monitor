package com.example.ppgreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class StartPage extends Activity {
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppg_startpage);
        mHandler.postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), PPGLandingPage.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }

}
