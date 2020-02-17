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
    ArrayList<String> patientSymptoms;

    String pinfo;
    String psymp = "";
    String diagnosis= "";
    String diagnosis_code= "UMLS";
    float diangosis_probability;

    int disease_index;
    float disease_percentage;
    String disease_umls;
    String disease_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        handlePassedIntent();

        Log.d("TESTING", alToString(patientSymptoms));
        pinfo = sexToString(p_sex) + "\nID = " + p_id + "\nAGE = "  + p_age + "\nHEIGHT=" + p_height + "m\nWEIGHT=" + p_weight +"kg";
        sendMessage("3122410651",pinfo);

        for (int i =0; i < patientSymptoms.size(); i++){
            psymp= (patientSymptoms.get(i)) + ", " + psymp;
        }
        sendMessage("3122410651",psymp);



        String dtemp = "Disease index = " + disease_index + "(" + disease_name + ")";
        dtemp = dtemp + "-- probability = " + Float.toString(disease_percentage);
        sendMessage("3122410651", dtemp);
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
        patientSymptoms = passedIntent.getStringArrayListExtra("patient_symptoms");

        disease_index = passedIntent.getIntExtra("diagnosed_disease", -1);
        disease_percentage = passedIntent.getFloatExtra("likelihood_of_disease", -1f);
        disease_umls = passedIntent.getStringExtra("diagnosed_UMLS");
        disease_name = passedIntent.getStringExtra("diagnosed_disease_name");
    }

    private void sendMessage(String phone_num, String msg){
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(phone_num, null, msg, null, null);
    }
}
