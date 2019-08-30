package com.example.mobilehealthprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class InformationSent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_sent);

        TextView x = findViewById(R.id.confirmation_header);
        try {
            Thread.sleep(3782);
        }catch (Exception e){
            e.printStackTrace();
        }

        x.setVisibility(View.VISIBLE);
        try {
            Thread.sleep(2341);
        }catch (Exception e){
            e.printStackTrace();
        }

        Intent backToHome = new Intent(InformationSent.this, MainActivity.class);
        startActivity(backToHome);
    }
}
