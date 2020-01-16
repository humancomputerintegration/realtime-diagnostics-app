package com.example.mobilehealthprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ajithvgiri.searchdialog.SearchListItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.StrictMath.abs;

public class AdaptiveDiagnosis extends AppCompatActivity {
    Intent passedIntent;
    Sex p_sex;
    int p_id, p_age;
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


    //UI Stuff
    int STROKE_WIDTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adaptive_diagnosis);
        handlePassedIntent();

        wm = new float[nrows][ncols];
        //wm = loadWeightMatrix("Dis_Sym_30.csv", 1, 1);
        wm = loadWeightMatrix("DiseaseSymptomMatrix_quantitative.csv", 1, 1);


        if(mode==1){
            for(int i = 0; i < nrows; i++){
                disease_list.add(i);
            }
            for(int j = 0; j < ncols; j++){
                symptom_list.add(j);
            }
        }


        symptom_vector = loadPatientSymptoms(patientSymptoms);
        reduceMatrix();

        next_symp = getNext_symp(wm, symptom_list, disease_list);

        setUpInterface();
    }



    public void reduceMatrix() {
        int ndis = disease_list.size(), idx_dis;
        int nsymp = symptom_list.size(), idx_symp;
        float min_weight, max_weight;
        int NpatientSymp = patientSymptoms.size();

        //filter diseases
        int i = 0;
        while(i<ndis){
            idx_dis = disease_list.get(i);
            for(int p=0; p<NpatientSymp; p++){
                idx_symp = UmlsToIndex_s.get(SympToUmls.get(patientSymptoms.get(p)));
                if(wm[idx_dis][idx_symp] == 0.0) {
                    disease_list.remove(i);
                    i--;
                    ndis--;
                }
                break;
            }
            i++;
        }

        //filter symptoms
        for(int idx2 = 0; idx2 < NpatientSymp; idx2++){
            for(int idx = 0; idx < nsymp; idx++){
                if(symptom_list.get(idx) == UmlsToIndex_s.get(SympToUmls.get(patientSymptoms.get(idx2)))){
                    symptom_list.remove(idx);
                    nsymp--;
                }
            }
        }

        int j = 0;
        while(j<nsymp){
            idx_symp = symptom_list.get(j);
            min_weight = 1;
            max_weight = 0;
            for(int idx = 0; idx < ndis; idx++){
                idx_dis = disease_list.get(idx);
                min_weight = min(min_weight, wm[idx_dis][idx_symp]);
                max_weight = max(max_weight, wm[idx_dis][idx_symp]);
            }
            if(min_weight>0 || max_weight == 0){
                symptom_list.remove(j);
                j--;
                nsymp--;
            }
            j++;
        }

    }

    public int getNext_symp(float[][] wm, ArrayList<Integer> symptom_list, ArrayList<Integer> disease_list) {
        int ndis = disease_list.size();
        int nsymp = symptom_list.size();
        int opt_nsymp = ndis/2;
        int crit = ndis, ntrue;
        int next_symp = 0;
        int idx_symp, idx_dis;

        for(int j = 0; j < nsymp; j++) {
            idx_symp = symptom_list.get(j);
            ntrue = 0;
            for (int i = 0; i < ndis; i++) {
                idx_dis = disease_list.get(i);
                if (wm[idx_dis][idx_symp] > 0f) {
                    ntrue++;
                }
            }

            if (abs(ntrue - opt_nsymp) < crit) {
                next_symp = idx_symp;
                crit = abs(ntrue - opt_nsymp);
            }
        }
        return next_symp;
    }


    public void handlePassedIntent(){
        passedIntent = getIntent();
        mode = passedIntent.getIntExtra("mode", -1);
        p_sex = (Sex) passedIntent.getSerializableExtra("sex");
        p_id = passedIntent.getIntExtra("hid", -1);
        p_age = passedIntent.getIntExtra("age", -1);
        p_height = passedIntent.getFloatExtra("height",-1);
        p_weight = passedIntent.getFloatExtra("weight",-1);
        patientSymptoms = passedIntent.getStringArrayListExtra("patient_symptoms");
        nrows = passedIntent.getIntExtra("nrows", 0);
        ncols = passedIntent.getIntExtra("ncols", 0);
        SympToUmls = new Hashtable<> ((HashMap<String,String>) passedIntent.getSerializableExtra("stu"));
        UmlsToSymp = new Hashtable<>((HashMap<String,String>) passedIntent.getSerializableExtra("uts"));
        IndexToUmls_s = new Hashtable<>((HashMap<Integer, String>) passedIntent.getSerializableExtra("itus"));
        UmlsToIndex_s = new Hashtable<>((HashMap<String, Integer>) passedIntent.getSerializableExtra("utis"));

        DisToUmls = new Hashtable<> ((HashMap<String,String>) passedIntent.getSerializableExtra("dtu"));
        UmlsToDis = new Hashtable<>((HashMap<String,String>) passedIntent.getSerializableExtra("utd"));
        IndexToUmls_d = new Hashtable<>((HashMap<Integer, String>) passedIntent.getSerializableExtra("itud"));
        UmlsToIndex_d = new Hashtable<>((HashMap<String, Integer>) passedIntent.getSerializableExtra("utid"));

        if(mode==2){
            last_symp = passedIntent.getIntExtra("symptom_id", 0);
            disease_list = (ArrayList<Integer>) passedIntent.getSerializableExtra("dl");
            symptom_list = (ArrayList<Integer>) passedIntent.getSerializableExtra("sl");
        }
    }

    //Layout Functions
    public void setUpInterface(){
        //Set up the Button option Views for the 5 diseases

        LinearLayout ll;
        ll = findViewById(R.id.adaptive_diagnosis_layout);
        last_symp = next_symp;
        symptom_name = UmlsToSymp.get(IndexToUmls_s.get(last_symp));
        //String symptom_name = "Cough";
        Button nbut = CustomButton.createButton(this, R.drawable.rounded_button, symptom_name,
                R.color.noSelection, STROKE_WIDTH,
                R.color.noSelectionAccent);
        ll.addView(nbut);

        Button yes_but = findViewById(R.id.yes_button);
        CustomButton.changeButtonColor(this, yes_but, R.color.colorPrimary, STROKE_WIDTH, R.color.colorAccent);
        yes_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientSymptoms.add(symptom_name);
                Intent intent;
                reduceMatrix1();
                next_symp = getNext_symp(wm, symptom_list, disease_list);

                System.out.println(disease_list.size());
                if(disease_list.size() <= 1){
                    intent = new Intent(AdaptiveDiagnosis.this, DiagnosisResult.class);
                    intent.putExtra("mode", 1);
                }
                else{
                    intent = new Intent(AdaptiveDiagnosis.this, AdaptiveDiagnosis.class);
                    intent.putExtra("mode", 2);
                }

                intent.putExtra("hid", p_id);
                intent.putExtra("sex", p_sex);
                intent.putExtra("age", p_age);
                intent.putExtra("height", p_height);
                intent.putExtra("weight", p_weight);
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
                startActivity(intent);
            }
        });

        Button no_but = findViewById(R.id.no_button);
        CustomButton.changeButtonColor(this, no_but, R.color.colorPrimary, STROKE_WIDTH, R.color.colorAccent);
        no_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                reduceMatrix0();
                next_symp = getNext_symp(wm, symptom_list, disease_list);

                System.out.println(disease_list.size());
                if(disease_list.size() <= 1){
                    intent = new Intent(AdaptiveDiagnosis.this, DiagnosisResult.class);
                    intent.putExtra("mode", 1);
                }
                else{
                    intent = new Intent(AdaptiveDiagnosis.this, AdaptiveDiagnosis.class);
                    intent.putExtra("mode", 2);
                }
                intent.putExtra("hid", p_id);
                intent.putExtra("sex", p_sex);
                intent.putExtra("age", p_age);
                intent.putExtra("height", p_height);
                intent.putExtra("weight", p_weight);
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
                startActivity(intent);
            }
        });
    }

    //the patientSymptom needs to be a vector for matrix multi - so it needs to be a 2d matrix
    public float[][] loadPatientSymptoms(ArrayList<String> ps){
        float[][] ph = new float[ncols][1];
        int vector_index;
        for(int i = 0; i < ps.size(); i++){
            vector_index = UmlsToIndex_s.get(SympToUmls.get(ps.get(i)));
            ph[vector_index][0] = 1f;
        }
        return ph;
    }


    //returns null if something went wrong
    public float[][] loadWeightMatrix(String fname, int skip_r, int skip_c) {
        String nl;
        String[] temp = null;
        float[][] weight_matrix = new float[nrows][ncols];

        try{
            InputStreamReader is = new InputStreamReader(getAssets().open(fname));
            BufferedReader reader = new BufferedReader(is);
            nl = reader.readLine(); //skip the first line of the CSV


            for(int r = 0; r < nrows; r++){
                nl = reader.readLine();
                temp = nl.split(",");
                for(int c = 0; c < ncols; c++){
                    weight_matrix[r][c] = Float.parseFloat(temp[c + skip_c]);
                }
            }
            reader.close();
            return weight_matrix;
        } catch (IOException e){
            Log.d("TESTING", "Error opening file");
            Log.d("TESTING", e.toString());
            return null;
        }
    }

    public float[][] matrixMultiply(float[][] fm, float[][] sm, int r1, int c1, int r2, int c2){
        if(c1 != r2) {
            Log.d("ERROR", "The dimensions of the matrices are wrong for multiplication");
            return null;
        }

        float[][] matrix_product = new float[r1][c2];
        for(int i = 0; i < r1; i++) {
            for (int j = 0; j < c2; j++) {
                for (int k = 0; k < c1; k++) {
                    matrix_product[i][j] += fm[i][k] * sm[k][j];
                }
            }
        }
        return matrix_product;
    }

    public void reduceMatrix0() {
        int ndis = disease_list.size(), idx_dis;
        int nsymp = symptom_list.size(), idx_symp;
        float min_weight, max_weight;

        int i = 0;
        while(i<ndis){
            idx_dis = disease_list.get(i);
            if(wm[idx_dis][last_symp] != 0) {
                disease_list.remove(i);
                i--;
                ndis--;
                if(ndis==1){
                    return;
                }
            }
            i++;
        }

        for(int idx = 0; idx < nsymp; idx++){
            if(symptom_list.get(idx) == last_symp){
                symptom_list.remove(idx);
                nsymp--;
            }
        }

        int j = 0;
        while(j<nsymp){
            idx_symp = symptom_list.get(j);
            min_weight = 1;
            max_weight = 0;
            for(int idx = 0; idx < ndis; idx++){
                idx_dis = disease_list.get(idx);
                min_weight = min(min_weight, wm[idx_dis][idx_symp]);
                max_weight = max(max_weight, wm[idx_dis][idx_symp]);
            }
            if(min_weight>0 || max_weight == 0){
                symptom_list.remove(j);
                j--;
                nsymp--;
            }
            j++;
        }
    }
    public void reduceMatrix1() {
        int ndis = disease_list.size(), idx_dis;
        int nsymp = symptom_list.size(), idx_symp;
        float min_weight, max_weight;

        int i = 0;
        while(i<ndis){
            idx_dis = disease_list.get(i);
            if(wm[idx_dis][last_symp] == 0) {
                disease_list.remove(i);
                System.out.println("disease list");
                System.out.println(disease_list.size());
                i--;
                ndis--;
                if(ndis==1){
                    return;
                }
            }
            i++;
        }

        for(int idx = 0; idx < nsymp; idx++){
            if(symptom_list.get(idx) == last_symp){
                System.out.println("symptom list");
                System.out.println(symptom_list.size());
                symptom_list.remove(idx);
                nsymp--;
            }
        }

        int j = 0;
        while(j<nsymp){
            idx_symp = symptom_list.get(j);
            min_weight = 1;
            max_weight = 0;
            for(int idx = 0; idx < ndis; idx++){
                idx_dis = disease_list.get(idx);
                min_weight = min(min_weight, wm[idx_dis][idx_symp]);
                max_weight = max(max_weight, wm[idx_dis][idx_symp]);
            }
            if(min_weight>0 || max_weight == 0){
                symptom_list.remove(j);
                j--;
                nsymp--;
            }
            j++;
        }
    }

}
