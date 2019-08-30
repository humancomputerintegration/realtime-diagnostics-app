package com.example.mobilehealthprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

//Purely Aesthetic
public class ConfirmInformationSent extends AppCompatActivity {

    TextView x;
    TextView y;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_information_sent);

        x = findViewById(R.id.confirmation_header);
        x.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                next();
            }
        }, 4000);




    }

    public void next(){

        x.setVisibility(View.INVISIBLE);
        y = findViewById(R.id.done);
        y.setVisibility(View.VISIBLE);

        try {
            Thread.sleep(2382);
        }catch (Exception e){
            e.printStackTrace();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent backToHome = new Intent(ConfirmInformationSent.this, MainActivity.class);
                startActivity(backToHome);
            }
        }, 4000);

    }
}
