package com.example.mobilehealthprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class DiseasePrediction extends AppCompatActivity {

    Sex p_sex;
    int p_id, p_age;
    float p_height, p_weight;

    int diagnosed_disease_index;
    String diagnosed_disease;
    Float diagnosed_disease_prob;

    List<SearchListItem> allDiseases = new ArrayList<>();
    ArrayList<String> patientSymptoms;

    Hashtable<String, String> SympToUmls= new Hashtable<String, String>();
    Hashtable<Integer, String> IndexToSymp = new Hashtable<>();
    Hashtable<String, Integer> UmlsToIndex = new Hashtable<>();
    Hashtable<String, String> DisToUmls = new Hashtable<>();
    Hashtable<Integer,String> IndexToDis = new Hashtable<>();
    Hashtable<String, String> DisPred1 = new Hashtable<>();
    Hashtable<String, String> DisPred2= new Hashtable<>();
    Hashtable<String, String> DisPred3 = new Hashtable<>();
    Hashtable<String, String> drug = new Hashtable<String, String>();

    int STROKE_WIDTH = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_prediction);

        handlePassedIntent();
        loadPrediction("diseaseCorrelation.csv");
        loadDrug("DrugRecommendation.csv");

        setUpInterface();
    }

    public void loadPrediction(String fname){ //ArrayList<String>
        try{
            InputStreamReader is = new InputStreamReader(getAssets().open(fname));
            BufferedReader reader = new BufferedReader(is);
            String nl;
            String[] temp;
            nl = reader.readLine(); //skips the heading in the csv
            nl = reader.readLine();
            while((nl = reader.readLine()) != null){
                temp = nl.split(",");
                DisPred1.put(temp[0], temp[1]);
                DisPred2.put(temp[0], temp[2]);
                DisPred3.put(temp[0], temp[3]);
            }
            reader.close(); //make sure you close the reader after opening a file
        }catch (IOException e){
            e.printStackTrace();
            Log.d("ERROR", "AN ERROR HAS OCCURRED IN LOAD PREDICTION");
        }
    }
    public void loadDrug(String fname){ //ArrayList<String>
        try{
            InputStreamReader is = new InputStreamReader(getAssets().open(fname));
            BufferedReader reader = new BufferedReader(is);
            String nl;
            String[] temp;
            nl = reader.readLine(); //skips the heading in the csv
            while((nl = reader.readLine()) != null){
                temp = nl.split(",");
                String dis = temp[0];
                String drug_reco = "";
                for(int i = 1; i < temp.length - 1; i++){
                    drug_reco = drug_reco + temp[i] + ", ";
                }
                drug_reco += temp[temp.length-1];
                drug.put(dis, drug_reco);
            }
            reader.close(); //make sure you close the reader after opening a file
        }catch (IOException e){
            e.printStackTrace();
            Log.d("ERROR", "AN ERROR HAS OCCURRED IN LOADDRUG");
        }
    }

    @SuppressLint("ResourceAsColor")
    public void setUpInterface(){
        LinearLayout ll;
        ll = findViewById(R.id.disease_prediction_layout);
        ArrayList<String> pred = new ArrayList<>();
        String drug_recommendation;

        String code = DisToUmls.get(diagnosed_disease);
        if(DisPred1.containsKey(code)){
            pred.add(DisPred1.get(code));
            pred.add(DisPred2.get(code));
            pred.add(DisPred3.get(code));

            Button nbut = CustomButton.createButton(this, R.drawable.rounded_button, "Patients like you may also have following diseases:",
                    R.color.noSelection, STROKE_WIDTH,
                    R.color.noSelectionAccent);
            nbut.setTextColor(R.color.selectionTextColor);
            CustomButton.changeButtonColor(this, nbut, R.color.colorPrimary, STROKE_WIDTH, R.color.colorAccent);
            ll.addView(nbut);
            for (int i = 0; i < 3; i++) {

                String possibleDisease = pred.get(i);
                nbut = CustomButton.createButton(this, R.drawable.rounded_button, possibleDisease,
                        R.color.noSelection, STROKE_WIDTH,
                        R.color.noSelectionAccent);

                nbut.setTransformationMethod(null);

                ll.addView(nbut);
            }

        }
        else{
            String possibleDisease = "We do not have enough data to predict possible diseases for you right now";
            Button nbut = CustomButton.createButton(this, R.drawable.rounded_button, possibleDisease,
                    R.color.colorPrimary, STROKE_WIDTH,
                    R.color.colorAccent);

            ll.addView(nbut);
        }
        if(drug.containsKey(code)){
            drug_recommendation = drug.get(code);
            Button nbut = CustomButton.createButton(this, R.drawable.rounded_button, "Patients like you are taking following drugs:",
                    R.color.noSelection, STROKE_WIDTH,
                    R.color.noSelectionAccent);
            CustomButton.changeButtonColor(this, nbut, R.color.colorPrimary, STROKE_WIDTH, R.color.colorAccent);
            nbut.setTextColor(R.color.noSelectionTextColor);
            ll.addView(nbut);

            nbut = CustomButton.createButton(this, R.drawable.rounded_button, drug_recommendation,
                    R.color.noSelection, STROKE_WIDTH,
                    R.color.noSelectionAccent);
            nbut.setTransformationMethod(null);
            ll.addView(nbut);
        } else{
            drug_recommendation = "We do not have enough data to suggest drugs for you right now";
            Button nbut = CustomButton.createButton(this, R.drawable.rounded_button, drug_recommendation,
                    R.color.colorPrimary, STROKE_WIDTH,
                    R.color.colorAccent);

            ll.addView(nbut);
        }

        Button back = findViewById(R.id.go_back);
        CustomButton.changeButtonColor(this, back, R.color.colorPrimary, STROKE_WIDTH, R.color.colorAccent);
        CustomButton.changeButtonText(this, back, R.color.white);
        back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent backwards = new Intent(DiseasePrediction.this, DiagnosisResult.class);
                backwards.putExtra("mode", 2);
                backwards.putExtra("hid", p_id);
                backwards.putExtra("sex", p_sex);
                backwards.putExtra("age", p_age);
                backwards.putExtra("height", p_height);
                backwards.putExtra("weight", p_weight);
                backwards.putExtra("patient_symptoms", patientSymptoms);
                backwards.putExtra("stu", SympToUmls);
                backwards.putExtra("its", IndexToSymp);
                backwards.putExtra("uti", UmlsToIndex);
                backwards.putExtra("dtu", DisToUmls);
                backwards.putExtra("itd", IndexToDis);
                startActivity(backwards);
            }
        });

        Button sendToServ = findViewById(R.id.send_to_server);
        CustomButton.changeButtonColor(this, sendToServ, R.color.colorPrimary, STROKE_WIDTH, R.color.colorAccent);
        CustomButton.changeButtonText(this, sendToServ, R.color.white);
        sendToServ.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                CommunicationHandler ch = new CommunicationHandler();
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                for(int i = 0; i < patientSymptoms.size(); i++){
                    tmp.add(UmlsToIndex.get(SympToUmls.get(patientSymptoms.get(i))));
                    //new Integer(UmlsToIndex.get(SympToUmls.get(patientSymptoms.get(i))))
                }

                String toSend = ch.generateRawMessage(p_id, p_sex, p_age, p_height, p_weight, tmp, diagnosed_disease_index);
                String encToSend = null;
                String testEncryption = "test message";
                byte[] K = null;
                try {
                    K = readKeyFile(getString(R.string.kfilename));
//                    Log.d("TESTING", Arrays.toString(K));
//                    Log.d("TESTING", Base64.encodeToString(K, Base64.DEFAULT));
                } catch (IOException e){
                    e.printStackTrace();
                }

//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////                    encToSend = ch.encryptMessage(pubkey, toSend);
//                    encToSend = ch.encryptMessage(pubkey, testEncryption);
//                }
//                Log.d("TESTING", "length of the encrypted message= " + Integer.toString(encToSend.length()));
//                sendMessage("8478686626", encToSend.substring(0,60)); //TODO
//                sendMessage("8478686626", encToSend.substring(60,121)); //TODO
//                sendMessage("8478686626", encToSend.substring(121,160)); //TODO

//                Log.d("TESTING", encToSend);
//                Log.d("TESTING", "LENGTH OF ENCRYTPED MESSAGE " + Integer.toString(encToSend.length()));
//                String subset = encToSend.substring(0,70);
//                sendMessage("8478686626", subset); //TODO
//                sendMessage("8478686626", encToSend); //TODO
                sendMessage(getString(R.string.server_number),toSend); //Check if this is working later

                Intent sendToServ = new Intent(DiseasePrediction.this, ConfirmationScreen.class);
                startActivity(sendToServ);
                Intent restart = new Intent(DiseasePrediction.this, MainActivity.class);
                startActivity(restart);
            }
        });
        return;
    }

    private byte[] readKeyFile(String fname) throws IOException {
        String strKeyPEM = "";
        BufferedReader br = null;
        try{
            InputStreamReader is = new InputStreamReader(getAssets().open(fname));
            br = new BufferedReader(is);
        }catch (IOException e){
            Log.d("TESTING", "Error with loading in file");
            e.printStackTrace();
        }

        String line;
        while ((line = br.readLine()) != null) {
            strKeyPEM += strKeyPEM + line;
        }
        br.close();
        byte[] byteform = Base64.decode(strKeyPEM, Base64.DEFAULT);
        return byteform;
    }

    public void handlePassedIntent(){
        Intent passedIntent = getIntent();
        p_sex = (Sex) passedIntent.getSerializableExtra("sex");
        p_id = passedIntent.getIntExtra("hid", -1);
        p_age = passedIntent.getIntExtra("age", -1);
        p_height = passedIntent.getFloatExtra("height",-1);
        p_weight = passedIntent.getFloatExtra("weight",-1);
        patientSymptoms = passedIntent.getStringArrayListExtra("patient_symptoms");

        SympToUmls = new Hashtable<>((HashMap<String, String>) passedIntent.getSerializableExtra("stu"));
        IndexToSymp = new Hashtable<>((HashMap<Integer, String>) passedIntent.getSerializableExtra("its"));
        UmlsToIndex = new Hashtable<>((HashMap<String, Integer>) passedIntent.getSerializableExtra("uti"));
        DisToUmls = new Hashtable<>((HashMap<String, String>) passedIntent.getSerializableExtra("dtu"));
        IndexToDis = new Hashtable<>((HashMap<Integer, String>) passedIntent.getSerializableExtra("itd"));

        diagnosed_disease = passedIntent.getStringExtra("diagnosed_disease_name");
        diagnosed_disease_prob = passedIntent.getFloatExtra("likelihood_of_disease", -1f);
        diagnosed_disease_index = passedIntent.getIntExtra("diagnosed_disease_index", -1);
    }

    private void sendMessage(String phone_num, String msg){
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(phone_num, null, msg, null, null);
    }
}
