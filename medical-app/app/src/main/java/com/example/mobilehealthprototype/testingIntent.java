package com.example.mobilehealthprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class testingIntent extends AppCompatActivity {
    Sex p_sex;
    int p_id, p_age;
    float p_height, p_weight;
    ArrayList<String> patientSymptoms;
    Hashtable<String, String> SympToUmls; //Symptom to UMLS code
    Hashtable<String, Integer> SympToIndex; //Symptom to index
    Hashtable<String, String> UmlsToSYDS; //UMLS to Symptom or Disease
    Hashtable<String, Integer> UmlsToIndex; //UMLS to index (will have duplicate indices)
    Hashtable<Integer, String> IndexToDumls; //Index to a Disease UMLS
    Hashtable<Integer, String> IndexToSumls; //Index to a symptom UMLS

    int disease_index;
    float disease_percentage;

    String hashTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_intent);
        handlePassedIntent();
        String ht2 = hashTest + hashTest;
        String ht3 = ht2 + hashTest;
        String ht4 = hashTest + "abc";
        String ht5 = hashTest + "abcd";
        String ht159 = ht2 + "A*7MM(H-fF^!$sQ_ej9j0nPKtZjNsCt";
        String ht160 = ht2 + "A*7MM(H-fF^!$sQ_ej9j0nPKtZjNsCtM";
        String ht161 = ht2 + "A*7MM(H-fF^!$sQ_ej9j0nPKtZjNsCtM!";
//        sendMessage2("8478686626", hashTest);
//        sendMessage2("8478686626", ht2);
//        sendMessage2("8478686626", ht4);
//        sendMessage2("8478686626", ht5);
        sendMessage2("8478686626", ht159);
        sendMessage2("8478686626", ht160);
//        sendMessage2("8478686626", ht161);
        Log.d("TESTING", Integer.toString(ht159.length()));

//        Log.d("TESTING", "TESTING THE PASSED INTENT");
//        Log.d("TESTING", Integer.toString(disease_index));
//        Log.d("TESTING", IndexToDumls.get(disease_index));
//        Log.d("TESTING", UmlsToSYDS.get(IndexToDumls.get(disease_index)));
//        Log.d("TESTING", Float.toString(disease_percentage));
    }

    public void handlePassedIntent(){
        Intent passedIntent = getIntent();
//        p_sex = (Sex) passedIntent.getSerializableExtra("sex");
//        p_id = passedIntent.getIntExtra("hid", -1);
//        p_age = passedIntent.getIntExtra("age", -1);
//        p_height = passedIntent.getFloatExtra("height",-1);
//        p_weight = passedIntent.getFloatExtra("weight",-1);
//        patientSymptoms = passedIntent.getStringArrayListExtra("patient_symptoms");
//        SympToUmls = new Hashtable<>((HashMap<String,String>)passedIntent.getSerializableExtra("stu"));
//        SympToIndex = new Hashtable<> ((HashMap<String,Integer>) passedIntent.getSerializableExtra("sti"));
//        UmlsToIndex = new Hashtable<> ((HashMap<String, Integer>) passedIntent.getSerializableExtra("uti"));
//        UmlsToSYDS = new Hashtable<> ((HashMap<String, String>) passedIntent.getSerializableExtra("utsd"));
//        IndexToSumls = new Hashtable<> ((HashMap<Integer, String>) passedIntent.getSerializableExtra("its"));
//        IndexToDumls = new Hashtable<> ((HashMap<Integer, String>) passedIntent.getSerializableExtra("itd"));
//        disease_index = passedIntent.getIntExtra("diagnosed_disease", -1);
//        disease_percentage = passedIntent.getFloatExtra("likelihood_of_disease", -1f);
        hashTest = passedIntent.getStringExtra("hashed_string");
    }

    private void sendMessage2(String phone_num, String msg){
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(phone_num, null, msg, null, null);
    }
}
