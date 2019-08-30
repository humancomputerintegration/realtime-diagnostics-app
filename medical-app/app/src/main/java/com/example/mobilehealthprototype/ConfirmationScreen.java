package com.example.mobilehealthprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ajithvgiri.searchdialog.SearchListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class ConfirmationScreen extends AppCompatActivity {
    Intent passedIntent;
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

    int STROKE_WIDTH = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_screen);

        handlePassedIntent();
        setUpInterface();
    }

    public void setUpInterface(){
        Button confirmation_button = findViewById(R.id.final_confirmation);
        CustomButton.changeButtonColor(this, confirmation_button,R.color.colorPrimary,
                                                                STROKE_WIDTH, R.color.colorAccent);
        confirmation_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommunicationHandler ch = new CommunicationHandler();
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                for(int i = 0; i < patientSymptoms.size(); i++){
                    tmp.add(UmlsToIndex.get(SympToUmls.get(patientSymptoms.get(i))));
                }

                String toSend = ch.generateRawMessage(p_id, p_sex, p_age, p_height, p_weight, tmp, diagnosed_disease_index);
                sendMessage(R.string.server_number,toSend);

                Intent intent = new Intent(ConfirmationScreen.this, ConfirmInformationSent.class);
                startActivity(intent);
            }
        });

        //These do not need checks because these will always be required
        TextView is = findViewById(R.id.input_s);
        is.setText((p_sex == Sex.MALE)? "male" : "female");

        TextView iage = findViewById(R.id.input_a);
        iage.setText(convertFloat((float) p_age));


        TextView ipid = findViewById(R.id.input_pid);
        String temp = convertFloat((float) p_id);
        ipid.setText(temp);
        if(temp.contains("No")){ipid.setTextColor(getResources().getColor(R.color.gray));}


        TextView ih = findViewById(R.id.input_h);
        temp = convertFloat(p_height);
        ih.setText(temp);
        if(temp.contains("No")) {ih.setTextColor(getResources().getColor(R.color.gray));}

        TextView iw = findViewById(R.id.input_w);
        temp = convertFloat(p_weight);
        iw.setText(convertFloat(p_weight));
        if(temp.contains("No")) {iw.setTextColor(getResources().getColor(R.color.gray));}

        TextView idd = findViewById(R.id.input_dd);
        idd.setText(diagnosed_disease);
    }

    public String convertFloat(float n){
        if(n < 0){
            return "Nothing was inputted";
        }else{
            return Float.toString(n);
        }
    }

    public String constructConfirmationDetails(){
        String spid = Integer.toString(p_id);
        String spage = Integer.toString(p_age);
        String spsex = (p_sex == Sex.MALE) ? "M" : "F";
        String spheight = Float.toString(p_height);
        String spweight = Float.toString(p_weight);

        String sddprob = Float.toString(diagnosed_disease_prob);

        String fp = spid + "\n" + spage + "\n" + spsex + "\n" + spheight + "\n" + spweight + "\n";
        String sp = fp + "Patient Symptoms::" + "\n";
        for(int i = 0; i < patientSymptoms.size(); i++){
            sp = sp + patientSymptoms.get(i) + "\n";
        }
        String tp = sp + "Doctor Diagnosis::" + "\n" + diagnosed_disease + " with probability: " + sddprob;
        return tp;
    }

    public void handlePassedIntent(){
        passedIntent = getIntent();
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

    private void sendMessage(int phone_num_resource, String msg){
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(getString(phone_num_resource), null, msg, null, null);
    }


}
