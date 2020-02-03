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
    Hashtable<Integer, String> IndexToSymp = new Hashtable<>();

    //since UMLS are unique, you can reuse UmlsToIndex
    Hashtable<String, Integer> UmlsToIndex = new Hashtable<>();
    Hashtable<String, String> DisToUmls = new Hashtable<>();
    Hashtable<Integer,String> IndexToDis = new Hashtable<>();

    int ncols, nrows, next_symp, last_symp;
    float[][] wm, symptom_vector;
    ArrayList<Integer> symptom_list = new ArrayList<>();
    ArrayList<Integer> disease_list = new ArrayList<>();


    //UI Stuff
    int STROKE_WIDTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adaptive_diagnosis);
        handlePassedIntent();
        loadDiseases("DiseaseList.csv");
        findNumRowsCols("Dis_Sym_30.csv");
        wm = new float[nrows][ncols];
        wm = loadWeightMatrix("Dis_Sym_30.csv", 1, 1);

        if ((mode == 1)) {
            for(int i = 0; i < nrows; i++){
                disease_list.add(i);
            }
            for(int j = 0; j < ncols; j++){
                symptom_list.add(j);
            }

            reduceMatrix1();
            symptom_vector = loadPatientSymptoms(patientSymptoms);
            next_symp = getNext_symp(wm, symptom_list, disease_list);
        }

        setUpInterface();
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
               i--;
               ndis--;
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
        SympToUmls = new Hashtable<> ((HashMap<String,String>) passedIntent.getSerializableExtra("stu"));
        IndexToSymp = new Hashtable<> ((HashMap<Integer,String>) passedIntent.getSerializableExtra("its"));
        UmlsToIndex = new Hashtable<> ((HashMap<String, Integer>) passedIntent.getSerializableExtra("uti"));
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
        final String symptom_name = IndexToSymp.get(last_symp);
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
                patientSymptoms.add(IndexToSymp.get(last_symp));
                Intent intent;
                reduceMatrix1();
                next_symp = getNext_symp(wm, symptom_list, disease_list);

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
                intent.putExtra("uti", UmlsToIndex);
                intent.putExtra("its",IndexToSymp);
                intent.putExtra( "symptom_id", next_symp);
                intent.putExtra("dl", disease_list);
                intent.putExtra("sl", symptom_list);
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
                intent.putExtra("uti", UmlsToIndex);
                intent.putExtra("its",IndexToSymp);
                intent.putExtra( "symptom_id", next_symp);
                intent.putExtra("dl", disease_list);
                intent.putExtra("sl", symptom_list);
                startActivity(intent);
            }
        });
    }

    //the patientSymptom needs to be a vector for matrix multi - so it needs to be a 2d matrix
    public float[][] loadPatientSymptoms(ArrayList<String> ps){
        float[][] ph = new float[ncols][1];
        int vector_index;
        for(int i = 0; i < ps.size(); i++){
            vector_index = UmlsToIndex.get(SympToUmls.get(ps.get(i)));
            ph[vector_index][0] = 1f;
        }
        return ph;
    }

    //TODO Add a dictionary that allows for easy look up between symptoms and results
    public void loadDiseases(String fname){ //ArrayList<String>
        try{
            InputStreamReader is = new InputStreamReader(getAssets().open(fname));
            BufferedReader reader = new BufferedReader(is);
            String nl;
            String[] temp;
            nl = reader.readLine(); //skips the heading in the csv
            int index = 0;
            while((nl = reader.readLine()) != null){
                temp = nl.split(",");
                DisToUmls.put(temp[1], temp[0]);
                IndexToDis.put(index, temp[1]);
                UmlsToIndex.put(temp[0], index);
                index += 1;
                SearchListItem t = new SearchListItem(0, temp[1]);
                allDiseases.add(t);
            }
            reader.close(); //make sure you close the reader after opening a file
        }catch (IOException e){
            e.printStackTrace();
            Log.d("ERROR", "AN ERROR HAS OCCURRED IN LOADDISEASES");
        }
    }

    //Due to the structure of the CSV, we need to subtract 1 from the total to get an accurate pair
    public void findNumRowsCols(String fname){
        String nl;
        int row_count, col_count;
        row_count = col_count = 0;

        try {
            InputStreamReader is = new InputStreamReader(getAssets().open(fname));
            BufferedReader reader = new BufferedReader(is);
            nl = reader.readLine(); //read the first line
            col_count = (nl.split(",")).length - 1;
            while((nl = reader.readLine()) != null) {
                row_count = row_count + 1;
            }
            reader.close();
            ncols = col_count;
            nrows = row_count;
            return;
        } catch (IOException e) {
            Log.d("TESTING", "ERROR findNumRowsCols");
            Log.d("TESTING", e.toString());
        }
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


}
