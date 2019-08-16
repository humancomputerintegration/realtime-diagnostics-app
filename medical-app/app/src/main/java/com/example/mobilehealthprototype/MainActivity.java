package com.example.mobilehealthprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    boolean permissions_granted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermissions();
        setUpInterface();
    }

    public void askPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions_granted = (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED);
        }
    }

    public void setUpInterface(){
        Button diagnose = (Button) findViewById(R.id.diagnosisButton);
        diagnose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = createAlertDialog();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        builder.show();
                    }
                }
                if (permissions_granted) {
                    startActivity(new Intent(MainActivity.this, PatientInfoActivity.class));
                }
            }
        });

        //TODO Actually configure this "OUTBREAK" button & activities
        Button outbreak = (Button) findViewById(R.id.outbreakButton);
        outbreak.setEnabled(false); //delete this line once we're ready to create an outbreak screen
        outbreak.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OutbreakScreens.class));
//                Intent i = new Intent(MainActivity.this, testingIntent.class);
//                startActivity(i);
            }
        });
    }

    public AlertDialog.Builder createAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.permissions_warning_message);
        builder.setTitle(R.string.permissions_warning_title);
        builder.setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        return builder;
    }
}
