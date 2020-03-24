package com.example.mobilehealthprototype;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ajithvgiri.searchdialog.SearchListItem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class ConfirmationScreen extends AppCompatActivity {
    Intent passedIntent;
    Sex p_sex;
    int p_age, p_pressure, p_temperature, p_pregnancy;
    String p_id,lab_test, lab_result, prescription, dosage;
    float p_height, p_weight;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_screen);

        handlePassedIntent();
        final String summary = constructConfirmationDetails();
        TextView text;
        text = findViewById(R.id.textView5);
        text.setMovementMethod(ScrollingMovementMethod.getInstance());
        text.setText(summary);

        Button confirmation_button = (Button) findViewById(R.id.final_confirmation);
        confirmation_button.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view) {
                CommunicationHandler ch = new CommunicationHandler();
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                for(int i = 0; i < patientSymptoms.size(); i++){
                    tmp.add(UmlsToIndex_s.get(SympToUmls.get(patientSymptoms.get(i))));
                    //new Integer(UmlsToIndex.get(SympToUmls.get(patientSymptoms.get(i))))
                }

                String toSend = ch.generateRawMessage(p_id, p_sex, p_age, p_height, p_weight, tmp, diagnosed_disease_index, lab_test, lab_result, prescription, dosage);
                String encToSend = null;
                byte[] K = null;
                try {
                    K = readKeyFile(getString(R.string.pfilename));
//                    Log.d("TESTING", "successfully read key ----");
//                    Log.d("TESTING", Arrays.toString(K));
//                    Log.d("TESTING", Base64.encodeToString(K, Base64.DEFAULT));
                } catch (IOException e){
                    e.printStackTrace();
                }
                encToSend = ch.encrypt_p(toSend, K);
//                Log.d("TESTING", "---- printing encrypted message (I hope) ----");
//                Log.d("TESTING", encToSend);//
//                String decrypted = ch.decrypt_p(encToSend, K);
//                Log.d("TESTING", "---- printing decrypted message (I hope) ----");
//                Log.d("TESTING", decrypted);
                sendMessage(getString(R.string.server_number),encToSend);
                saveFile(summary);
                Intent restart = new Intent(ConfirmationScreen.this, MainActivity.class);
                startActivity(restart);
            }
        });


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

    private void readFile() {
        try {
            FileInputStream fileInputStream = openFileInput("cache_text");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = bufferedReader.readLine();
            while (line != null) {
                System.out.println(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            fileInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile(String toSend) {
        String seperater = "------------\n";
        SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy-MM-dd   HH:mm");
        Date curDate =  new Date(System.currentTimeMillis());
        String   currentDate   =   formatter.format(curDate) + "\n";
        try {
            FileOutputStream fileOutputStream = openFileOutput("cache_text", MODE_APPEND);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(fileOutputStream));
            writer.write(seperater + currentDate + toSend+"\n");
            writer.flush();
            writer.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public String constructConfirmationDetails(){
        String spid = p_id;
        String spage = Integer.toString(p_age);
        String spsex = (p_sex == Sex.MALE) ? "M" : "F";
        String spheight = Float.toString(p_height);
        String spweight = Float.toString(p_weight);

        String sddprob = Float.toString(diagnosed_disease_prob);

        String fp = "Patient id:" + spid + "\n" + "Patient age:" + spage + "\n" + "Patient Sex" + spsex + "\n";
        String sp = fp + "Patient Symptoms::" + "\n" + "Lab test:" + lab_test + "\n" + "Prescription:" + prescription + ',' + dosage + "\n";
        for(int i = 0; i < patientSymptoms.size(); i++){
            sp = sp + patientSymptoms.get(i) + "\n";
        }
        String tp = sp + "Doctor Diagnosis::" + "\n" + diagnosed_disease + "\n" + "with probability: " + sddprob;
        return tp;
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
        prescription = passedIntent.getStringExtra("prescription");
        dosage = passedIntent.getStringExtra("dosage");

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

    private void sendMessage(String phone_num, String msg){
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(phone_num, null, msg, null, null);
    }


}
