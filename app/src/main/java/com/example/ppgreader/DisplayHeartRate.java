package com.example.ppgreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


public class DisplayHeartRate extends Activity {

    int HeartRateforDisplay;


    TextView showHeartRate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppg_show);
        showHeartRate = (TextView) this.findViewById(R.id.HRR);



        Bundle bundle = getIntent().getExtras();
        //check the value of heartrate passed from previous class and set it to the text in ppg_show layout
        if (bundle != null) {
            HeartRateforDisplay = bundle.getInt("beats_per_min");
            showHeartRate.setText(String.valueOf(HeartRateforDisplay));
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(DisplayHeartRate.this, PPGLandingPage.class);
        startActivity(i);
        finish();
    }
}


