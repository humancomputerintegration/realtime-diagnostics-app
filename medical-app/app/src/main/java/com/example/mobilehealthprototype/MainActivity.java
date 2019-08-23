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
    boolean enable = true;
    boolean outbreak_enabled = false;

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
    }

    public void checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                enable = true;
            }else{
                enable = false;
            }
        }
    }

    public void setUpInterface(){
        Button diagnose = (Button) findViewById(R.id.diagnosisButton);
        diagnose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
                AlertDialog.Builder bd = buildWarning(R.string.permissions_warning_title,
                                                                    R.string.permissions_warning_message,
                                                                                    R.string.button_close);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        bd.show();
                    }
                }
                if (enable) {
                    startActivity(new Intent(MainActivity.this, PatientInfoActivity.class));

                }
            }
        });

        //TODO Actually configure this "OUTBREAK" button & activities
        Button outbreak = (Button) findViewById(R.id.outbreakButton);
        if(!outbreak_enabled){
            outbreak.setEnabled(false); //delete this line once we're ready to create an outbreak screen
            outbreak.setBackgroundColor(getResources().getColor(R.color.disabled_gray));
        }else{
            outbreak.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, OutbreakScreens.class);
                    startActivity(i);
                }
            });
        }

    }

    public AlertDialog.Builder buildWarning(int title_id, int message_id, int pb){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title_id);
        builder.setMessage(message_id);
        builder.setPositiveButton(pb, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        return builder;
    }
}
