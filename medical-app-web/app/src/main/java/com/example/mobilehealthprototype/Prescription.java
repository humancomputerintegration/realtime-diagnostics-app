package com.example.mobilehealthprototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.ajithvgiri.searchdialog.SearchListItem;
import com.ajithvgiri.searchdialog.SearchableDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class Prescription extends AppCompatActivity {
    Intent passedIntent;
    Sex p_sex;
    int p_age, p_pressure, p_temperature, p_pregnancy;
    String p_id,lab_test, lab_result, prescription, dosage;
    float p_height, p_weight;

    int mode;
    int diagnosed_disease_index;
    String diagnosed_disease;
    Float diagnosed_disease_prob;

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

    int ncols, nrows;
    float[][] wm, symptom_vector, disease_vector;

    SearchableDialog sd;
    Button allWrongAlternative;
    Button allWrongContinue;
    Button[] arrButtons = new Button[6]; //todo - change 6 to 7 once we start implementing adaptive diagnosis
    //the reason why it is is 6 is to prevent bug crashes

    //result of disease matrix multiplication
    DiseaseProb[] mm_output;

    //probable diseases & their respective correlation matrices
    DiseaseProb[] top5Diseases = new DiseaseProb[5];
    String diagnosedDisease = null;
    float ddProb = -1f;

    //UI Stuff
    int STROKE_WIDTH = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        System.out.println("before pass:");
        System.out.println(patientSymptoms);
        handlePassedIntent();
        System.out.println("after pass:");
        System.out.println(patientSymptoms);
        setUpInterface();
    }

    public void setUpInterface(){

        EditText text_pres   = (EditText)findViewById(R.id.prescription_input);
        EditText text_dosage   = (EditText)findViewById(R.id.dosage_input);
        lab_test = text_pres.getText().toString();
        lab_result = text_dosage.getText().toString();

        Button next = findViewById(R.id.next_prescription);
        CustomButton.changeButtonColor(this, next, R.color.colorPrimary, STROKE_WIDTH, R.color.colorAccent);
        CustomButton.changeButtonText(this, next, R.color.white);
        next.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent = new Intent(Prescription.this, ConfirmationScreen.class);
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
                intent.putExtra("diagnosed_disease_index", diagnosed_disease_index);
                intent.putExtra("likelihood_of_disease", diagnosed_disease_prob);
//                intent.putExtra("diagnosed_UMLS", DisToUmls.get(diagnosedDisease));
                intent.putExtra("diagnosed_disease_name", diagnosed_disease);
                intent.putExtra("lab_test", lab_test);
                intent.putExtra("lab_result", lab_result);
                intent.putExtra("prescription", prescription);
                intent.putExtra("dosage", dosage);
                System.out.println("Going to comfirmation");
                startActivity(intent);
            }
        });

        return;
    }

    public void handlePassedIntent(){
        passedIntent = getIntent();
        p_sex = (Sex) passedIntent.getSerializableExtra("sex");
        p_id = passedIntent.getStringExtra("hid");
        p_age = passedIntent.getIntExtra("age", -1);
        p_height = passedIntent.getFloatExtra("height",-1);
        p_weight = passedIntent.getFloatExtra("weight",-1);
        p_temperature = passedIntent.getIntExtra("temperature", 0);
        p_pressure = passedIntent.getIntExtra("pressure", 0);
        p_pregnancy = passedIntent.getIntExtra("pregnancy", 0);
        patientSymptoms = passedIntent.getStringArrayListExtra("patient_symptoms");
        lab_test = passedIntent.getStringExtra("lab_test");
        lab_result = passedIntent.getStringExtra("lab_result");

        SympToUmls = new Hashtable<> ((HashMap<String,String>) passedIntent.getSerializableExtra("stu"));
        UmlsToSymp = new Hashtable<>((HashMap<String,String>) passedIntent.getSerializableExtra("uts"));
        IndexToUmls_s = new Hashtable<>((HashMap<Integer, String>) passedIntent.getSerializableExtra("itus"));
        UmlsToIndex_s = new Hashtable<>((HashMap<String, Integer>) passedIntent.getSerializableExtra("utis"));

        DisToUmls = new Hashtable<> ((HashMap<String,String>) passedIntent.getSerializableExtra("dtu"));
        UmlsToDis = new Hashtable<>((HashMap<String,String>) passedIntent.getSerializableExtra("utd"));
        IndexToUmls_d = new Hashtable<>((HashMap<Integer, String>) passedIntent.getSerializableExtra("itud"));
        UmlsToIndex_d = new Hashtable<>((HashMap<String, Integer>) passedIntent.getSerializableExtra("utid"));

        diagnosed_disease = passedIntent.getStringExtra("diagnosed_disease_name");
        diagnosed_disease_prob = passedIntent.getFloatExtra("likelihood_of_disease", -1f);
        diagnosed_disease_index = passedIntent.getIntExtra("diagnosed_disease_index", -1);
    }

}
