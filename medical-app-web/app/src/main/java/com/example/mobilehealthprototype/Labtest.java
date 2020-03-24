package com.example.mobilehealthprototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.ajithvgiri.searchdialog.SearchListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class Labtest extends AppCompatActivity {
    Intent passedIntent;
    Sex p_sex;
    int p_age, p_pressure, p_temperature, p_pregnancy;
    String p_id,lab_test, lab_result;
    float p_height, p_weight;

    int mode;

    List<SearchListItem> allDiseases = new ArrayList<>();
    ArrayList<String> patientSymptoms;

    Hashtable<String, String> SympToUmls= new Hashtable<String, String>();
    Hashtable<String, String> UmlsToSymp= new Hashtable<String, String>();
    Hashtable<Integer, String> IndexToUmls_s = new Hashtable<Integer, String>();
    Hashtable<String, Integer> UmlsToIndex_s = new Hashtable<String, Integer>();
    Hashtable<String, Integer> UmlsToIndex_d = new Hashtable<String, Integer>();
    Hashtable<Integer, String> IndexToUmls_d = new Hashtable<Integer, String>();
    Hashtable<String, String> DisToUmls = new Hashtable<String, String>();
    Hashtable<String,String> UmlsToDis = new Hashtable<String,String>();

    int ncols=0, nrows=0, next_symp=0, last_symp=0;
    float[][] wm, symptom_vector;
    String symptom_name;
    ArrayList<Integer> symptom_list = new ArrayList<>();
    ArrayList<Integer> disease_list = new ArrayList<>();


    int STROKE_WIDTH = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_test);

        System.out.println("before pass:");
        System.out.println(patientSymptoms);
        handlePassedIntent();
        System.out.println("after pass:");
        System.out.println(patientSymptoms);
        setUpInterface();
    }

    public void setUpInterface(){

        EditText text_test   = (EditText)findViewById(R.id.lab_test_input);
        EditText text_result   = (EditText)findViewById(R.id.lab_result_input);
        lab_test = text_test.getText().toString();
        lab_result = text_result.getText().toString();

        Button next = findViewById(R.id.next_lab);
        CustomButton.changeButtonColor(this, next, R.color.colorPrimary, STROKE_WIDTH, R.color.colorAccent);
        CustomButton.changeButtonText(this, next, R.color.white);
        next.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent = new Intent(Labtest.this, DiagnosisResult.class);
                intent.putExtra("mode", 1);
                intent.putExtra("hid", p_id);
                intent.putExtra("sex", p_sex);
                intent.putExtra("age", p_age);
                intent.putExtra("height", p_height);
                intent.putExtra("weight", p_weight);
                intent.putExtra("temperature", p_temperature);
                intent.putExtra("pressure", p_pressure);
                intent.putExtra("pregnancy", p_pregnancy);
                intent.putExtra("patient_symptoms", patientSymptoms);
                intent.putExtra("stu", SympToUmls);
                intent.putExtra("uts", UmlsToSymp);
                intent.putExtra("dtu", DisToUmls);
                intent.putExtra("utd", UmlsToDis);
                intent.putExtra("itud", IndexToUmls_d);
                intent.putExtra("itus", IndexToUmls_s);
                intent.putExtra("utid", UmlsToIndex_d);
                intent.putExtra("utis", UmlsToIndex_s);
                intent.putExtra( "symptom_id", next_symp);
                intent.putExtra("dl", disease_list);
                intent.putExtra("sl", symptom_list);
                intent.putExtra("ncols", ncols);
                intent.putExtra("nrows", nrows);
                intent.putExtra("lab_test", lab_test);
                intent.putExtra("lab_result", lab_result);
                startActivity(intent);
            }
        });

        return;
    }

    public void handlePassedIntent(){
        passedIntent = getIntent();
        mode = passedIntent.getIntExtra("mode", -1);
        if(mode == 1){
            p_sex = (Sex) passedIntent.getSerializableExtra("sex");
            p_id = passedIntent.getStringExtra("hid");
            p_age = passedIntent.getIntExtra("age", -1);
            p_height = passedIntent.getFloatExtra("height",-1);
            p_weight = passedIntent.getFloatExtra("weight",-1);
            p_temperature = passedIntent.getIntExtra("temperature", 0);
            p_pressure = passedIntent.getIntExtra("pressure", 0);
            p_pregnancy = passedIntent.getIntExtra("pregnancy", 0);
            patientSymptoms = passedIntent.getStringArrayListExtra("patient_symptoms");
            SympToUmls = new Hashtable<> ((HashMap<String,String>) passedIntent.getSerializableExtra("stu"));
            UmlsToSymp = new Hashtable<>((HashMap<String,String>) passedIntent.getSerializableExtra("uts"));
            IndexToUmls_s = new Hashtable<>((HashMap<Integer, String>) passedIntent.getSerializableExtra("itus"));
            UmlsToIndex_s = new Hashtable<>((HashMap<String, Integer>) passedIntent.getSerializableExtra("utis"));

            DisToUmls = new Hashtable<> ((HashMap<String,String>) passedIntent.getSerializableExtra("dtu"));
            UmlsToDis = new Hashtable<>((HashMap<String,String>) passedIntent.getSerializableExtra("utd"));
            IndexToUmls_d = new Hashtable<>((HashMap<Integer, String>) passedIntent.getSerializableExtra("itud"));
            UmlsToIndex_d = new Hashtable<>((HashMap<String, Integer>) passedIntent.getSerializableExtra("utid"));

        }else if(mode == 2){
            p_sex = (Sex) passedIntent.getSerializableExtra("sex");
            p_id = passedIntent.getStringExtra("hid");
            p_age = passedIntent.getIntExtra("age", -1);
            p_height = passedIntent.getFloatExtra("height",-1);
            p_weight = passedIntent.getFloatExtra("weight",-1);
            p_temperature = passedIntent.getIntExtra("temperature", 0);
            p_pressure = passedIntent.getIntExtra("pressure", 0);
            p_pregnancy = passedIntent.getIntExtra("pregnancy", 0);
            patientSymptoms = passedIntent.getStringArrayListExtra("patient_symptoms");
            SympToUmls = new Hashtable<> ((HashMap<String,String>) passedIntent.getSerializableExtra("stu"));
            UmlsToSymp = new Hashtable<>((HashMap<String,String>) passedIntent.getSerializableExtra("uts"));
            IndexToUmls_s = new Hashtable<>((HashMap<Integer, String>) passedIntent.getSerializableExtra("itus"));
            UmlsToIndex_s = new Hashtable<>((HashMap<String, Integer>) passedIntent.getSerializableExtra("utis"));

            DisToUmls = new Hashtable<> ((HashMap<String,String>) passedIntent.getSerializableExtra("dtu"));
            UmlsToDis = new Hashtable<>((HashMap<String,String>) passedIntent.getSerializableExtra("utd"));
            IndexToUmls_d = new Hashtable<>((HashMap<Integer, String>) passedIntent.getSerializableExtra("itud"));
            UmlsToIndex_d = new Hashtable<>((HashMap<String, Integer>) passedIntent.getSerializableExtra("utid"));
        }
    }

}
