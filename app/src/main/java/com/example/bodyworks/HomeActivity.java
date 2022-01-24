package com.example.bodyworks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.pm.PermissionInfoCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity   {
    @BindView(R.id.stepstxt)
    TextView stepTxt;
    @BindView(R.id.totalCaloriesTxt)
    TextView totalCaloriesTxt;
    @BindView(R.id.totalStepTxt)
    TextView totalStepTxt;
    @BindView(R.id.resetBtn)
    ImageView resetBtn;

    @BindView(R.id.homeBtn)
    ImageView homeBtn;
    @BindView(R.id.addBtn)
    ImageView addBtn;
    @BindView(R.id.planBtn)
    ImageView planBtn;
    @BindView(R.id.profileBtn)
    ImageView profileBtn;
    @BindView(R.id.notificationBtn)
    ImageView notificationBtn;

    private Sensor stepSensor;
    private SensorManager sensorManager;
    private Sensor stepDetector;
    private boolean isCounterSensoerPresent,isDectorSensoerPresent;
    private int steps;
    private Integer totalSteps =0;
    private double preMagnitute = 0;
    private Integer stepCounter = 0;

    PackageManager packageManager;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ButterKnife.bind(this);
        navigate();

        //check useror not
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() == null){

        }

        //set sensors
        sensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);


        try { Sensor accelerometterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            SensorEventListener stepDetector = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    if (sensorEvent != null){
                        float x_acceleration = sensorEvent.values[0];
                        float y_acceleration = sensorEvent.values[1];
                        float z_acceleration = sensorEvent.values[2];

                        double magnitutte = Math.sqrt(x_acceleration * y_acceleration * z_acceleration);
                        double magnituteDelta = magnitutte - preMagnitute;
                        preMagnitute = magnitutte;

                        if (magnituteDelta > 4){
                            stepCounter++;
                            totalSteps++;
                        }

                        stepTxt.setText(stepCounter.toString());
                        totalStepTxt.setText(totalSteps.toString());
                        totalCaloriesTxt.setText(String.valueOf(totalSteps * 0.04));
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };
            sensorManager.registerListener(stepDetector,accelerometterSensor,SensorManager.SENSOR_DELAY_NORMAL);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //reset steps
        resetBtn.setOnClickListener(view -> stepCounter = 0);
    }


    private void navigate() {
        profileBtn.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this,ProfileAcivity.class)));
        addBtn.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this,AddWorkoutActivity.class)));
        planBtn.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this,ShaduleActivity.class)));
        notificationBtn.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this,NotificationActivity.class)));
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}