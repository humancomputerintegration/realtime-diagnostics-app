package com.example.mobilehealthprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.ajithvgiri.searchdialog.SearchListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class DiseaseWebPreview extends AppCompatActivity {

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

    int STROKE_WIDTH = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_web_preview);

        handlePassedIntent();
        setUpInterface();

        //TODO: These should not be naked like this, but I'm running out of time
        String sk = diagnosed_disease.replace(' ', '+');
        String host = "https://www.bing.com/search?q=";
        String addr = host+sk;

//        WebSearcher ws = new WebSearcher(sk, host, endpoint);
//        ws.SearchInternet(diagnosed_disease);

        WebView wv= findViewById(R.id.disease_webviewer);
        wv.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                Log.d("TESTING", "WebPage is done loading");
            }
        });
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setJavaScriptEnabled(true);

        wv.loadUrl(addr);
    }

    public void setUpInterface(){


        Button back = findViewById(R.id.go_back);
        CustomButton.changeButtonColor(this, back, R.color.colorPrimary, STROKE_WIDTH, R.color.colorAccent);
        CustomButton.changeButtonText(this, back, R.color.white);
        back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent backwards = new Intent(DiseaseWebPreview.this, DiagnosisProcess.class);
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
//                sendMessage(getString(R.string.server_number),toSend); //Check if this is working later
                Log.d("TESTING", toSend);
                Intent sendToServ = new Intent(DiseaseWebPreview.this, ConfirmationScreen.class);
                startActivity(sendToServ);
            }
        });
        return;
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
