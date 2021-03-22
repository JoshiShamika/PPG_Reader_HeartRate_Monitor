package com.example.ppgreader;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ppgreader.Math.Fft;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.appcompat.app.AlertDialog;

import static java.lang.Math.ceil;


public class RecordPPG extends Activity {
    private static final String TAG = "Reading PPG";
    private static PowerManager.WakeLock wakeLock = null;
    private static final AtomicBoolean processing = new AtomicBoolean(false);

    private static Camera camera = null;
    private static int video_capture= 101;
    private Uri videoUri=null;

    private SurfaceView sview = null;
    private static SurfaceHolder sholder = null;

    public int No_of_Beats=0;


    public double AvgBeatsPerMin=0;





    private ProgressBar progbar1;
    public int inc=0;


    private static long startTime = 0;
    private double freq;

//Array list for storing red and green intensity data from processing frame by frame
    public ArrayList<Double> GreenIntensityList=new ArrayList<Double>();
    public ArrayList<Double> RedIntensityList=new ArrayList<Double>();
    // to count everytime intensity value is added to these arraylist
    public int Intensitycounter = 0;


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppg_read);
        Bundle extras = getIntent().getExtras();
        Intent videoIntent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(videoIntent.resolveActivity(getPackageManager())!=null)
        {startActivityForResult(videoIntent,video_capture);}
        //getting surface view from the layout and passing it to holder var
        sview = (SurfaceView) findViewById(R.id.view1);
        sholder = sview.getHolder();
        sholder.addCallback(sCallback);
        sholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


    //keep the device on while the app is running
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "");

        //set progbar1 with the  progress bar from recordppg activity layout XML layout and set it to 0 progress initially
        progbar1 = (ProgressBar)findViewById(R.id.progress_bar1);
        progbar1.setProgress(0);
    }

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) result = size;
                }
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==video_capture && resultCode==RESULT_OK){
//video saved at videoUri
            videoUri=data.getData();

        }
    }


    private Camera.PreviewCallback pCallback = new Camera.PreviewCallback() {


        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {

            if (data == null) throw new NullPointerException();
            //set surfaceview preview size as camera size to pass for frame analysis
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();
            int width = size.width;
            int height = size.height;


            if (!processing.compareAndSet(false, true)) return;


            double GreenIntensityfromRBG;
            double RedIntensityfromRBG;
            double BlueIntensityfromRBG;
            // call processing methods to process live camera recording frames by frame to fetch RBG intensities from the from each frame.

            RedIntensityfromRBG = Processing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 1);
            //for heart rate computation, we don't really use blue intensity data
            BlueIntensityfromRBG = Processing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 2);
            GreenIntensityfromRBG = Processing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 3);
            // adding red and green intensity list from intensities in RGB on each frame
            GreenIntensityList.add(GreenIntensityfromRBG);
            RedIntensityList.add(RedIntensityfromRBG);
            //update counter
            ++Intensitycounter;


            // if average red is not null update the progress
            if(RedIntensityfromRBG!=0){

                progbar1.setProgress(inc++/34);}


            processing.set(false);




            //Reset parameters and progress bar if the Red from RBG average is less than 200
            if (RedIntensityfromRBG < 200) {
                inc = 0;
                Intensitycounter = 0;
                progbar1.setProgress(inc);
                processing.set(false);
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 30) {
                //convert the red and green list to array  once data is read for 30 sec
                Double[] GreenArray = GreenIntensityList.toArray(new Double[GreenIntensityList.size()]);
                Double[] RedArray = RedIntensityList.toArray(new Double[RedIntensityList.size()]);

                freq = (Intensitycounter / totalTimeInSecs);
                //apply fourier transform to the red and green list array and update the ceiling values of (resulting frequesncies *60)
                double HRPeakGreen = Fft.FFT(GreenArray, Intensitycounter, freq);
                double Green_beatpmin = (int) ceil(HRPeakGreen * 60);
                double HRPeakRed = Fft.FFT(RedArray, Intensitycounter, freq);
                double Red_beatpmin = (int) ceil(HRPeakRed * 60);

                // checking the range of red and green BPM to see if they are in range 45-200
                if ((Green_beatpmin > 45 || Green_beatpmin < 200)&&(Red_beatpmin > 45 || Red_beatpmin < 200)) {


                        AvgBeatsPerMin = (Green_beatpmin + Red_beatpmin) / 2;
                    }
                else if((Green_beatpmin > 45 || Green_beatpmin < 200)) {
                        AvgBeatsPerMin = Green_beatpmin;
                    }

                else if ((Red_beatpmin > 45 || Red_beatpmin < 200)) {
                    AvgBeatsPerMin = Red_beatpmin;
                }
                //If issue with the reading found reset progress bar and update with message that measurement failed
                 else if (AvgBeatsPerMin < 45 || AvgBeatsPerMin > 200) {
                    inc = 0;
                    progbar1.setProgress(inc);
                    //pop up message in case issue with read
                    Toast.makeText(getApplicationContext(), "Issue with PPG read!", Toast.LENGTH_SHORT).show();

                    startTime = System.currentTimeMillis();
                    Intensitycounter = 0;
                    processing.set(false);
                    return;
                }
                // we have our heartrates here but we pass them to display page for display
                No_of_Beats = (int) AvgBeatsPerMin;
            }
            // if able to calculate no. of beats per minute display them on displayHeartRate page
            if (No_of_Beats != 0) {
                Intent i = new Intent(RecordPPG.this, DisplayHeartRate.class);
                i.putExtra("beats_per_min", No_of_Beats);

                startActivity(i);
                finish();
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    @Override
    public void onResume() {
        super.onResume();

        wakeLock.acquire();

        camera = Camera.open();

        camera.setDisplayOrientation(90);

        startTime = System.currentTimeMillis();
    }




    private  SurfaceHolder.Callback sCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                //display the camera view in surface view
                camera.setPreviewDisplay(sholder);
                camera.setPreviewCallback(pCallback);
            } catch (Throwable t) {


            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }

            camera.setParameters(parameters);
            camera.startPreview();
        }


        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Do you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {

                        RecordPPG.super.onBackPressed();
                        finish();
                        System.exit(0);
                    }
                }).create().show();
    }



}
