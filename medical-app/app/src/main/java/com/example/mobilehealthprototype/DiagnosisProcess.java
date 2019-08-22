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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class DiagnosisProcess extends AppCompatActivity {
    Intent passedIntent;
    Sex p_sex;
    int p_id, p_age;
    float p_height, p_weight;

    List<SearchListItem> allDiseases = new ArrayList<>();
    ArrayList<String> patientSymptoms;

    Hashtable<String, String> SympToUmls= new Hashtable<String, String>();
    Hashtable<Integer, String> IndexToSymp = new Hashtable<>();

    //since UMLS are unique, you can reuse UmlsToIndex
    Hashtable<String, Integer> UmlsToIndex = new Hashtable<>();
    Hashtable<String, String> DisToUmls = new Hashtable<>();
    Hashtable<Integer,String> IndexToDis = new Hashtable<>();

    int ncols, nrows;
    float[][] wm, symptom_vector, disease_vector;

    SearchableDialog sd;
    Button allWrongDiagnose;
    Button[] arrButtons = new Button[7];

    //result of disease matrix multiplication
    DiseaseProb[] mm_output;

    //probable diseases & their respective correlation matrices
    DiseaseProb[] top5Diseases = new DiseaseProb[5];
    String diagnosedDisease = null;
    float ddProb = -1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_process);
        handlePassedIntent();
        loadDiseases("DiseaseList.csv");
        findNumRowsCols("Dis_Sym_30.csv");
        wm = new float[nrows][ncols];
        wm = loadWeightMatrix("Dis_Sym_30.csv", 1, 1);
        symptom_vector = loadPatientSymptoms(patientSymptoms);
        //preliminary multiplication of the weight matrix to symptom vector
        disease_vector = matrixMultiply(wm, symptom_vector, nrows,ncols, ncols,1);
        mm_output = convertDiseaseVector(disease_vector, disease_vector.length);


        top5Diseases = generateTop5(mm_output); // not generating properly
        normalizeDiseaseVector(top5Diseases);

        setUpInterface();
    }

    public void handlePassedIntent(){
        passedIntent = getIntent();
        p_sex = (Sex) passedIntent.getSerializableExtra("sex");
        p_id = passedIntent.getIntExtra("hid", -1);
        p_age = passedIntent.getIntExtra("age", -1);
        p_height = passedIntent.getFloatExtra("height",-1);
        p_weight = passedIntent.getFloatExtra("weight",-1);
        patientSymptoms = passedIntent.getStringArrayListExtra("patient_symptoms");
        SympToUmls = new Hashtable<> ((HashMap<String,String>)passedIntent.getSerializableExtra("stu"));
        IndexToSymp = new Hashtable<> ((HashMap<Integer,String>) passedIntent.getSerializableExtra("its"));
        UmlsToIndex = new Hashtable<> ((HashMap<String, Integer>) passedIntent.getSerializableExtra("uti"));
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
            String umlsd = top5Diseases[i].getUmls();
            String dname = top5Diseases[i].getDisease();
            Float tprob = top5Diseases[i].getProb();
            String bmsg = dname + " (" + umlsd + ")" +  " - " + tprob;
            nbut.setText(bmsg);
            nbut.setOnClickListener(new DiseaseClickListener(umlsd, tprob, UmlsToIndex.get(umlsd)));
            arrButtons[i] = nbut;
            ll.addView(nbut);
        }

        //This button does not work correctly - todo - fix later
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
                    intent.putExtra("its", IndexToSymp);
                    intent.putExtra("uti", UmlsToIndex);
                    intent.putExtra("dtu", DisToUmls);
                    intent.putExtra("itd", IndexToDis);
                    intent.putExtra("diagnosed_disease_index", UmlsToIndex.get(DisToUmls.get(diagnosedDisease)));
                    intent.putExtra("likelihood_of_disease", ddProb);
                    intent.putExtra("diagnosed_UMLS", DisToUmls.get(diagnosedDisease));
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

    //Custom class used to add additional functionality to this activity
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

    //THIS ISN'T SORTING THE LIST
    //TODO - MAKE SURE THAT THIS PROPERLY SORTS STUFF - IT DOESN'T RIGHT NOW
    public DiseaseProb[] generateTop5(DiseaseProb[] results){
        //Sort the list
        Log.d("TESTING", Arrays.toString(results));
        DiseaseProb[] top;
        Arrays.sort(results); // this isn't working
        //get the top 5 results
        top = Arrays.copyOfRange(results, 0, 5);
        Log.d("TESTING", Arrays.toString(results));
        Log.d("TESTING", Arrays.toString(top));

        return top;
    }

    //Flattens a 1D vector to an array of DiseaseProbs
    public DiseaseProb[] convertDiseaseVector(float[][] m, int size){
        DiseaseProb[] temp = new DiseaseProb[size];
        for(int i = 0; i < size; i++){
            temp[i] = new DiseaseProb(DisToUmls.get(IndexToDis.get(i)),IndexToDis.get(i), m[i][0]);
        }
        return temp;
    }

    public void normalizeDiseaseVector(DiseaseProb[] dp){
        float sum = 0;
        int len = dp.length;

        for(int i =0; i < len; i++){
            sum += dp[i].getProb();
        }

        for(int j =0; j < len; j++){
            float tprob = dp[j].getProb();
            dp[j].setProb(tprob/sum);
        }
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

    //This causes a decent amount of app slowdown - the screen also go black from how heavy
    // the processing takes
    //but checks if the validated matrix is close to the file
    //TODO: Maybe delete - not likely to use this during run time
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
