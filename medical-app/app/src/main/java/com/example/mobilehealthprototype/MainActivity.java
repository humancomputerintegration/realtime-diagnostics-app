package com.example.mobilehealthprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureButtons();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("TESTING PERMISSIONS", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }
        //TextView tb = (TextView) findViewById(R.id.introTextBox);
        //tb.setText("WELCOME TO MOBILE HEALTH");
    }

    public void configureButtons(){
        Button diagnose = (Button) findViewById(R.id.diagnosisButton);
        diagnose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PatientInfoActivity.class));
            }
        });

        Button outbreak = (Button) findViewById(R.id.outbreakButton);
        outbreak.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, OutbreakScreens.class));
                startActivity(new Intent(MainActivity.this, PDiagResult.class));
            }
        });

        Button search = (Button) findViewById(R.id.searchDiseaseButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchDisease.class));
            }
        });
    }

    //Probably delete these later
    public void startDiagnosis(View view){
        Intent intent = new Intent(this, PatientInfoActivity.class);
        startActivity(intent);
    }

    public void checkOutbreaks(View view){
        Intent intent = new Intent(this, OutbreakScreens.class);
        startActivity(intent);
    }

}
