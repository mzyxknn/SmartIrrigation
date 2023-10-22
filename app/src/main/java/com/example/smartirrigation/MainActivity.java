package com.example.smartirrigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    //SWITCHES
    private Switch water_switch, servo_switch;

    private Button insert, view;

    private DatabaseReference pumpCommand,servoGateCommand;

    private boolean isOpen = false;

    private Handler mHandler = new Handler();
    private boolean doubleBackToExitPressedOnce;

    List<RealtimeHelperClass> dataList;
    DatabaseReference dbReference;
    ValueEventListener event_Listener;
    TextView waterLevel;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    ProgressBar water_progressBar;

    //WATER LEVEL
    int targetWaterProgress = 0;
    int currentWaterProgress = 0;

    //ANIMATOR
    ValueAnimator waterLevelAnimator;

    public MainActivity() {
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Buttons
        insert = findViewById(R.id.insertBttn);
        view = findViewById(R.id.viewBttn);

        waterLevel = findViewById(R.id.realtime_waterLevel);
        water_progressBar = findViewById(R.id.water_level);

        //Storage
        pumpCommand = FirebaseDatabase.getInstance().getReference("WaterPump");
        servoGateCommand = FirebaseDatabase.getInstance().getReference("ServoGate");

        //switch
        water_switch = findViewById(R.id.waterLevelSwitch);
        servo_switch = findViewById(R.id.servoSwitch);

        // Initialize the animators
        waterLevelAnimator = new ValueAnimator();
        waterLevelAnimator.setInterpolator(new LinearInterpolator());
        waterLevelAnimator.setDuration(500); // Animation duration in milliseconds

        waterLevelAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                water_progressBar.setProgress(animatedValue);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Water");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FirebaseDebug", "DataSnapshot: " + snapshot);

                Long realtimeWater = snapshot.child("WaterLevel").getValue(Long.class);
                Log.d("FirebaseDebug", "realtimeWater: " + realtimeWater);
                if (realtimeWater != null) {
                    waterLevel.setText(String.valueOf(realtimeWater) + "%");

                    // Calculate the target progress values
                    targetWaterProgress = realtimeWater.intValue();

                    // Start the animations
                    startProgressAnimation();
                } else {
                    waterLevel.setText("Analyzing Temperature"); // Display a message when data is empty
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        // WATER PUMP



        water_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String pumpState = isChecked ? "Pump_On" : "Pump_Off";
                Log.d("Switch Debug", "water_switch is checked: " + isChecked);
                Log.d("Switch Debug", "Updating Firebase with state: " + pumpState);
                pumpCommand.child("Mode").setValue(pumpState)

                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Firebase Debug", "Pump Status Updated");
                                Toast.makeText(MainActivity.this, "Pump Status Updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firebase Error", "Failed to update Pump Status", e);
                                Toast.makeText(MainActivity.this, "Failed to update Pump Status", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        //WATER SERVO SWITCH


        servo_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String pumpState = isChecked ? "Open_Gate" : "Close_Gate";
                servoGateCommand.child("Mode").setValue(pumpState)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Water Tank Vent Updated!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Failed to update water tank mode", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Set an OnClickListener for the Insert Bttn
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to launch the new activity
                Intent intent = new Intent(MainActivity.this, InsertNumber.class);

                // Start the new activity
                startActivity(intent);
            }
        });

        // Set an OnClickListener for the View Bttn
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to launch the new activity
                Intent intent = new Intent(MainActivity.this, ViewData.class);

                // Start the new activity
                startActivity(intent);
            }
        });





    }

    //=========PROGRESS ANIMATION=============
    private void startProgressAnimation() {
        // Set the current progress values as the starting point for the animations
        currentWaterProgress = water_progressBar.getProgress();

        // Update the target progress values
        waterLevelAnimator.setIntValues(currentWaterProgress, targetWaterProgress);

        // Start the animations
        waterLevelAnimator.start();

    }



    //Handles back press
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy ()
    {
        super.onDestroy();

        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        if (eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }

    @Override
    public void onBackPressed () {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }

}