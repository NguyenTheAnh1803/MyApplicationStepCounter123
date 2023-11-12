package com.example.myapplicationstepcounter123;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView stepCountTextView;
    private TextView distanceTextView;
    private TextView timeTextView;

    private Button pauseButton;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int stepCount = 0;
    private ProgressBar progressBar;
    private boolean isPaused = false;
    private long timePause = 0;
    private float stepLengthInMeters = 0.762f;

    private long    startTime ;
    private int steoCountTarget = 5000;
    private TextView stepCountTargetTextView ;
    private Handler timerhandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long milis = System.currentTimeMillis()- startTime;
            int seconds = (int) milis/1000;
            int min  = seconds/60;
            seconds = seconds%60;
            timeTextView.setText(String.format(Locale.getDefault(),"Time: %02d:%02d",min,seconds));
            timerhandler.postDelayed(this,1000);
        }
    };
    @Override
    protected void onStop() {
        super.onStop();

        if(stepCounterSensor!= null){
            sensorManager.unregisterListener(this);
            timerhandler.removeCallbacks(timerRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(stepCounterSensor!= null){
            sensorManager.registerListener((SensorEventListener) this,stepCounterSensor,SensorManager.SENSOR_DELAY_NORMAL);
            timerhandler.postDelayed(timerRunnable,0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepCountTextView = findViewById(R.id.stepCountTextView);
        distanceTextView = findViewById(R.id.distaceTextView);
        timeTextView = findViewById(R.id.timeTextView);
        pauseButton = findViewById(R.id.pauseButton);
        stepCountTargetTextView = findViewById(R.id.stepTargetTextView);
        progressBar = findViewById(R.id.progressBar);

        startTime = System.currentTimeMillis();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        progressBar.setMax(steoCountTarget);
        stepCountTargetTextView.setText("Step Goal:"+steoCountTarget);

        if(stepCounterSensor==null){
            stepCountTextView.setText("Step counter not available");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            stepCount = (int) sensorEvent.values[0];
            stepCountTextView.setText("Step Count:"+ stepCount);
            progressBar.setProgress(stepCount);

            if(stepCount>= steoCountTarget){
                stepCountTargetTextView.setText("Step Goal Achieved");
            }
            float distanceInKm =  stepCount*stepLengthInMeters/1000;
            distanceTextView.setText(String.format(Locale.getDefault(),"Distance:%.2f km",distanceInKm));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public void onPausedButtonClicked(View view){
        if(isPaused){
            isPaused = false;
            pauseButton.setText("Pause");
            startTime = System.currentTimeMillis()-timePause;
            timerhandler.postDelayed(timerRunnable,0);
        }else {
            isPaused = true;
            pauseButton.setText("Resume");
            timerhandler.removeCallbacks(timerRunnable);
            timePause = System.currentTimeMillis()-startTime;
        }
    }
}