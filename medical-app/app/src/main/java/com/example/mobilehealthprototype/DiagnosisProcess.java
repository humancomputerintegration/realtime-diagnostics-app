package com.example.mobilehealthprototype;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.ajithvgiri.searchdialog.OnSearchItemSelected;
import com.ajithvgiri.searchdialog.SearchListItem;
import com.ajithvgiri.searchdialog.SearchableDialog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

//TODO Refractor the code mess here
public class DiagnosisProcess extends AppCompatActivity {
    Intent passedIntent;
    Sex p_sex;
    int p_id, p_age;
    float p_height, p_weight;

    List<SearchListItem> allDiseases = new ArrayList<>();
    ArrayList<String> patientSymptoms;
    //TODO remove the amount of diciontary look ups we have to 4
    Hashtable<String, String> SympToUmls; //Symptom to UMLS code
    Hashtable<String, Integer> SympToIndex; //Symptom to index
    Hashtable<String, String> UmlsToSYDS; //UMLS to Symptom or Disease
    Hashtable<String, Integer> UmlsToIndex; //UMLS to index (will have duplicate indices)
    Hashtable<Integer, String> IndexToDumls; //Index to a Disease UMLS
    Hashtable<Integer, String> IndexToSumls; //Index to a symptom UMLS
    Hashtable<String, Integer> DiseaseToIndex = new Hashtable<String, Integer>();

    int ncols, nrows;
    float[][] wm, symptom_vector, disease_vector;

    SearchableDialog sd;
    Button allWrongDiagnose;
    Button[] arrButtons = new Button[7];

    //probable diseases & their respective correlation matrices
    float[] top5floats = new float[5];
    int[] top5DiseaseIndex = new int[5];

    String diagnosedDisease = null;
    float ddProb = -1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_process);
        handlePassedIntent();
        loadDiseases("DiseaseList.csv");
        findNumRowsCols("Dis_Sym_30.csv");
        wm = loadWeightMatrix("Dis_Sym_30.csv", 1, 1);
        symptom_vector = loadSymptoms();
        disease_vector = matrixMultiply(wm, symptom_vector, nrows,ncols, ncols,1); //preliminary multiplication of the weight matrix to symptom vector

        float[] temp = flattenMatrix(disease_vector, disease_vector.length);
        ArrayList<String> killme = probableDiseases(temp, 0.1f);

        generateTop5(temp);
        normalizeDiseaseVector();

        setUpInterface();
    }

    //Layout Functions
    public void setUpInterface(){
        sd = new SearchableDialog(DiagnosisProcess.this, allDiseases,"Disease Search");
        sd.setOnItemSelected(new OnSearchItemSelected(){
            public void onClick(int position, SearchListItem searchListItem){
                diagnosedDisease = searchListItem.getTitle();
                if(diagnosedDisease != null){
                    String t = diagnosedDisease + " - (Click this button again to change the disease)";
                    allWrongDiagnose.setText(t);
                }
            }
        });

        //Set up the Button option Views
        LinearLayout ll;
        ll = findViewById(R.id.diagnose_button_layout);
        for (int i = 0; i < 5; i++) {
            Button nbut = new Button(this);
            String umlsd = IndexToDumls.get(top5DiseaseIndex[i]);
            String bmsg = umlsd + " (" + UmlsToSYDS.get(umlsd) + ")" +  " - " + Float.toString(top5floats[i]);
            nbut.setText(bmsg);
            nbut.setOnClickListener(new DiseaseClickListener(UmlsToSYDS.get(umlsd), top5floats[i], i));
            arrButtons[i] = nbut;
            ll.addView(nbut);
        }

        allWrongDiagnose = new Button(this);
        allWrongDiagnose.setText("All of the above are incorrect diagnoses - I have a better one");
        allWrongDiagnose.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                sd.show();
                setOtherButtonsBgCol(5, arrButtons, Color.GRAY, Color.GREEN);
            }
        });
        arrButtons[5] = allWrongDiagnose;
        ll.addView(allWrongDiagnose);

        Button allWrongContinue = new Button(this);
        allWrongContinue.setText("All of the above are incorrect but I still need a diagnosis");
        arrButtons[6] = allWrongContinue;
        allWrongContinue.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                diagnosedDisease = null;
                ddProb = -1f;
            }
        });
        ll.addView(allWrongContinue);

        Button nextStep= new Button(this);
        nextStep.setText("CONTINUE");
        nextStep.setBackgroundColor(Color.GREEN);
        nextStep.setTextColor(Color.WHITE);
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(diagnosedDisease == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(DiagnosisProcess.this);
                    builder.setTitle("No disease selected");
                    builder.setMessage("Please select one of the options above");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    Intent intent = new Intent(DiagnosisProcess.this, ConfirmationScreen.class);
                    intent.putExtra("hid", p_id);
                    intent.putExtra("sex", p_sex);
                    intent.putExtra("age", p_age);
                    intent.putExtra("height", p_height);
                    intent.putExtra("weight", p_weight);
                    intent.putExtra("patient_symptoms", patientSymptoms);
                    intent.putExtra("stu", SympToUmls);
                    intent.putExtra("sti", SympToIndex);
                    intent.putExtra("uti", UmlsToIndex);
                    intent.putExtra("utsd", UmlsToSYDS);
                    intent.putExtra("its", IndexToSumls);
                    intent.putExtra("itd", IndexToDumls);
                    intent.putExtra("dti", DiseaseToIndex);
                    intent.putExtra("diagnosed_disease_index", DiseaseToIndex.get(diagnosedDisease));
                    intent.putExtra("likelihood_of_disease", ddProb);
                    intent.putExtra("diagnosed_UMLS", IndexToDumls.get(DiseaseToIndex.get(diagnosedDisease)));
                    intent.putExtra("diagnosed_disease_name", diagnosedDisease);
                    startActivity(intent);
                }
            }
        });
        ll.addView(nextStep);

    }

    public void setOtherButtonsBgCol(int ind, Button[] others, int non_sel_col, int selected_col){
        for(int i = 0; i < others.length ; i++){
            Button temp = others[i];
            if(i != ind){
                temp.setBackgroundColor(non_sel_col);
            }else{
                temp.setBackgroundColor(selected_col);
            }
        }
    }

    //Custom class used to add additional functionality to the thing
    public class DiseaseClickListener implements View.OnClickListener {
        String disease_name;
        float percentage;
        int button_index;

        public DiseaseClickListener(String disease_n, float percentage, int button_index) {
            this.disease_name = disease_n;
            this.percentage = percentage;
            this.button_index = button_index;
        }

        @Override
        public void onClick(View v) {
            diagnosedDisease = disease_name;
            ddProb = percentage;
            setOtherButtonsBgCol(button_index, arrButtons, Color.GRAY, Color.GREEN);
        }
    }

    //TODO Fix this god awful function
    public void generateTop5(float[] results){
        float first, second, third, fourth, fifth;
        int firsti, secondi, thirdi, fourthi, fifthi;
        firsti = secondi = thirdi = fourthi = fifthi = -1;
        first = second = third = fourth = fifth = Integer.MIN_VALUE;
        String[] largest5 = new String[5];
        for(int i = 0; i < results.length; i++){
            if(results[i] > first){
                fifth = fourth;
                fourth = third;
                third = second;
                second = first;
                first = results[i];
                firsti = i;
            }else if(results[i] > second){
                fifth = fourth;
                fourth = third;
                third = second;
                second = results[i];
                secondi = i;
            }else if(results[i] > third){
                fifth = fourth;
                fourth = third;
                third = results[i];
                thirdi = i;
            }else if(results[i] > fourth){
                fifth = fourth;
                fourth = results[i];
                fourthi = i;
            }else if(results[i] > fifth){
                fifth = results[i];
                fifthi = i;
            }
        }
        top5floats[0] = first;
        top5DiseaseIndex[0] = firsti;
        top5floats[1] = second;
        top5DiseaseIndex[1] = secondi;
        top5floats[2] = third;
        top5DiseaseIndex[2] = thirdi;
        top5floats[3] = fourth;
        top5DiseaseIndex[3] = fourthi;
        top5floats[4] = fifth;
        top5DiseaseIndex[4] = fifthi;
    }

    public void normalizeDiseaseVector(){
        float sum = 0;
        int len = top5floats.length;
        for(int i = 0; i < len; i++){
            sum += top5floats[i];
        }
        for(int j =0; j < len; j++){
            top5floats[j] = top5floats[j]/sum;
        }
        Log.d("TESTING", "BEGINNING TO NORMALIZE TOP 5 VECTORS");
        Log.d("Testing", Float.toString(sum));
        Log.d("TESTING", Arrays.toString(top5floats));
    }

    public float[] flattenMatrix(float[][] m, int size){
        float[] temp = new float[size];
        for(int i = 0; i < size; i++){
            temp[i] = m[i][0];
        }
        return temp;
    }

    public ArrayList<String> probableDiseases(float[] target, float threshold){
        ArrayList<String> diseases = new ArrayList<>();
        for(int i = 0; i < target.length; i++){
            if(target[i] > threshold){
                diseases.add(UmlsToSYDS.get(IndexToDumls.get(i)));
            }
        }
        return diseases;
    }

    public float[] dropValues(float[] target, float threshhold){
        ArrayList<Float> results = new ArrayList<Float>();
        float[] newM = null;
        int dropped = 0;
        for(int i = 0; i < target.length; i++){
            if(target[i] < threshhold){
                dropped++;
                results.add(target[i]);
            }
        }

        newM = new float[target.length - dropped];
        int j = 0;
        for(Float f: results){
            newM[j++] = (float) (f != null ? f : -1.0);
        }
        return newM;
    }

    //Make sure you explain why this needs to return a 2 dimension array
    //TODO rename this function to something that doesn't conflict with ListSymptom.java
    public float[][] loadSymptoms(){
        float[][] ph = new float[ncols][1];
        String placeholder;
        for(int i = 0; i < patientSymptoms.size(); i++){
            placeholder = patientSymptoms.get(i);
            int vec_index =UmlsToIndex.get(SympToUmls.get(placeholder));
//            Log.d("TESTING", placeholder);
//            Log.d("TESTING", Integer.toString(vec_index));
            ph[vec_index][0] = 1f;
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
                UmlsToSYDS.put(temp[0], temp[1]);
                IndexToDumls.put(index, temp[0]);
                UmlsToIndex.put(temp[0], index);
                DiseaseToIndex.put(temp[1], index);
                index += 1;
                SearchListItem t = new SearchListItem(0, temp[1]);
                allDiseases.add(t);
            }
            reader.close(); //make sure you close the reader after opening a file
        }catch (IOException e){
            e.printStackTrace();
            Log.d("ERROR", "AN ERROR HAS OCCURRED IN LOADSYMPTOMS");
        }
    }

    //Helper Functions
    public void handlePassedIntent(){
        passedIntent = getIntent();
        p_sex = (Sex) passedIntent.getSerializableExtra("sex");
        p_id = passedIntent.getIntExtra("hid", -1);
        p_age = passedIntent.getIntExtra("age", -1);
        p_height = passedIntent.getFloatExtra("height",-1);
        p_weight = passedIntent.getFloatExtra("weight",-1);
        patientSymptoms = passedIntent.getStringArrayListExtra("patient_symptoms");
        SympToUmls = new Hashtable<> ((HashMap<String,String>)passedIntent.getSerializableExtra("stu"));
        SympToIndex = new Hashtable<> ((HashMap<String,Integer>) passedIntent.getSerializableExtra("sti"));
        UmlsToIndex = new Hashtable<> ((HashMap<String, Integer>) passedIntent.getSerializableExtra("uti"));
        UmlsToSYDS = new Hashtable<> ((HashMap<String, String>) passedIntent.getSerializableExtra("utsd"));
        IndexToSumls = new Hashtable<> ((HashMap<Integer, String>) passedIntent.getSerializableExtra("its"));
        IndexToDumls = new Hashtable<> ((HashMap<Integer, String>) passedIntent.getSerializableExtra("itd"));
    }

    //Due to the structure of the CSV, we need to subtract 1 from the total to get an
    //accurate response
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
            Log.d("TESTING", "ERROR NumRowColMatrixLabels");
            Log.d("TESTING", e.toString());
        }
    }

    //returns null if something went wrong
    public float[][] loadWeightMatrix(String fname, int skip_r, int skip_c) {
        String nl;
        String[] temp = null;
        float[][] weight_matrix = new float[nrows][ncols];
        wm = new float[nrows][ncols];

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
            System.out.println("Error - the dimensions of the matrix do not match");
            return null;
        }

        float[][] mproduct = new float[r1][c2];
        for(int i = 0; i < r1; i++) {
            for (int j = 0; j < c2; j++) {
                for (int k = 0; k < c1; k++) {
                    mproduct[i][j] += fm[i][k] * sm[k][j];
                }
            }
        }
        return mproduct;
    }

    //This causes a decent amount of app slowdown - the screen also go black from how heavy
    // the processing takes
    //but checks if the validated matrix is close to the file
    public boolean validateMatrix(String fname, float[][] sub_matrix){
        String nl;
        String[] temp;
        try {
            InputStreamReader is = new InputStreamReader(getAssets().open(fname));
            BufferedReader reader = new BufferedReader(is);
            nl = reader.readLine(); //read the first line
            int row, col;
            row = col = 0;

            while((nl = reader.readLine()) != null) {
                for(col = 0; col < ncols; col++){
                    temp = nl.split(",");
                    float processed = Float.parseFloat(temp[col+1]);
                    if(Math.abs(processed - sub_matrix[row][col]) > 0.000001){
                        return false;
                    }
                }
                row++;
            }
            reader.close();
            return true;
        } catch (IOException e) {
            Log.d("TESTING", "ERROR validateMatrx");
            Log.d("TESTING", e.toString());
        }
        return true;
    }

}
