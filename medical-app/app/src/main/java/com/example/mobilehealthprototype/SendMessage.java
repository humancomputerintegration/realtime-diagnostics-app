package com.example.mobilehealthprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;

public class SendMessage extends AppCompatActivity {

    Intent passedIntent;
    Sex p_sex;
    int p_id, p_age;
    float p_height, p_weight;
    ArrayList<String> symptoms;

    String pinfo;
    String psymp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        handlePassedIntent();

        Log.d("TESTING", alToString(symptoms));
        pinfo = sexToString(p_sex) + "\nID = " + p_id + "\nAGE = "  + p_age + "\nHEIGHT=" + p_height + "m\nWEIGHT=" + p_weight +"kg";
        sendMessage("8478686626",pinfo);

        for (int i =0; i < symptoms.size(); i++){
            psymp= (symptoms.get(i)) + ", " + psymp;
        }
        sendMessage("8478686626",psymp);
    }

    public String sexToString(Sex s){
        if(s == Sex.MALE){
            return "MALE";
        }else{
            return "FEMALE";
        }
    }

    public String alToString(ArrayList<String> al){
        String base = "";
        for(int i = 0; i < al.size(); i++){
            base = base + al.get(i) + " ";
        }
        return base;
    }

    public void handlePassedIntent(){
        passedIntent = getIntent();
        p_sex = (Sex) passedIntent.getSerializableExtra("sex");
        p_id = passedIntent.getIntExtra("hid", -1);
        p_age = passedIntent.getIntExtra("age", -1);
        p_height = passedIntent.getFloatExtra("height",-1);
        p_weight = passedIntent.getFloatExtra("weight",-1);
        symptoms = passedIntent.getStringArrayListExtra("patient_symptoms");
    }

    private void sendMessage(String phone_num, String msg){
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(phone_num, null, msg, null, null);
    }
}
