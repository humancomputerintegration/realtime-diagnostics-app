package com.example.mobilehealthprototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class PatientInfoActivity extends AppCompatActivity {
    Sex p_sex = null;
    int p_age, p_pressure, p_temperature, p_pregnancy;
    String p_id;
    float p_weight, p_height;
    boolean complete = false;

//    TODO - Sex Age - mandatory
//    TODO - Height, Weight, ID - Optional


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        setUpInterface();
    }

    //Helper Functions
    public void warnError(int input_id, int header_id){
        TextView header = findViewById(header_id);
        String orig = header.getText().toString();
        String mod_orig = (orig.contains("*")) ? orig : orig + "*";
        header.setText(mod_orig);
        header.setTextColor(getResources().getColor(R.color.errorColor));
//        if(input_id > 0){
//            EditText input = findViewById(input_id);
//            input.setBackgroundColor(getResources().getColor(R.color.transparentRed));
//        }
    }

    public void removeError(int input_id, int header_id){
        TextView header = findViewById(header_id);
        String orig = header.getText().toString();
        String mod_orig = (orig.contains("*")) ? orig.substring(0, orig.length()-1) : orig;
        header.setText(mod_orig);
        header.setTextColor(getResources().getColor(R.color.black));
    }

    public float checkValue(int id1, int id2, boolean required){
        String parsed = ((TextView) findViewById(id1)).getText().toString();
        if(parsed == null || parsed.trim().equals("")){
            if(required){ warnError(id1, id2); }
            return -1f;
        }else{
            removeError(id1, id2);
        }
        return Float.parseFloat(parsed);
    }

    //Function that creates the logic & handling behind UI of the activity
    public void setUpInterface(){
        setupRadioButtions();

        Button next_step = findViewById(R.id.next_step_button);
        CustomButton.changeButtonColor(this, next_step, R.color.colorPrimary, 3, R.color.colorAccent);

        next_step.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                EditText mEdit   = (EditText)findViewById(R.id.pid_input);
                p_id = mEdit.getText().toString();
                p_age = (int) checkValue(R.id.age_input, R.id.age_header, true);
                p_height = checkValue(R.id.height_input, R.id.height_header, false);
                p_weight = checkValue(R.id.weight_input, R.id.weight_header, false);
                if(p_sex == null){  warnError(0, R.id.sex_option_header); }
                complete = (p_age >= 0) & (p_sex != null);
                //(p_id > 0) & (p_age > 0) & (p_sex != null) &(p_height > 0) & (p_weight > 0); //original version

                String spid = p_id;
                String spage = Integer.toString(p_age);
                String sptemperature = Integer.toString(p_temperature);
                String sppressure = Integer.toString(p_pressure);
                String sppregnancy = Integer.toString(p_pregnancy);
                String spsex = (p_sex == Sex.MALE) ? "M" : "F";
                String spheight = Float.toString(p_height);
                String spweight = Float.toString(p_weight);

                String toSave = spid + "," + spage + "," + spsex + "," + spheight + "," + spweight +  "," + sptemperature + "," + sppressure + "," + sppregnancy + "\n";
                saveFile(toSave);
                if(complete) {
                    Intent intent = new Intent(PatientInfoActivity.this, ListSymptoms.class);
                    intent.putExtra("hid", p_id);
                    intent.putExtra("sex", p_sex);
                    intent.putExtra("age", p_age);
                    intent.putExtra("height", p_height);
                    intent.putExtra("weight", p_weight);
                    intent.putExtra("temperature", p_temperature);
                    intent.putExtra("pressure", p_pressure);
                    intent.putExtra("pregnancy", p_pregnancy);
                    startActivity(intent);
                }
                //TODO:removed alertdialog because Pedro thinks its bad design (will comment in case people need it for future implementations)
                // else{
                //AlertDialog.Builder wn = buildWarning(R.string.warning_title, R.string.warning_message, R.string.close);
                //wn.show();
                //}
            }
        });
    }

    private void setupRadioButtions() {
        RadioButton moption = findViewById(R.id.Male_option);
        RadioButton foption = findViewById(R.id.Female_option);

        View.OnClickListener onRBClick = new View.OnClickListener() {
            public void onClick(View view){
                boolean checked = ((RadioButton) view).isChecked();
                switch(view.getId()) {
                    case R.id.Male_option:
                        if (checked)
                            p_sex = Sex.MALE;
                        break;
                    case R.id.Female_option:
                        if (checked)
                            p_sex = Sex.FEMALE;
                        break;
                }
            }
        };

        moption.setOnClickListener(onRBClick);
        foption.setOnClickListener(onRBClick);

        RadioButton feveroption = findViewById(R.id.fever_option);
        RadioButton normToption = findViewById(R.id.normT_option);

        View.OnClickListener onRBClick_T = new View.OnClickListener() {
            public void onClick(View view){
                boolean checked = ((RadioButton) view).isChecked();
                switch(view.getId()) {
                    case R.id.fever_option:
                        if (checked)
                            p_temperature = 1;
                        break;
                    case R.id.normT_option:
                        if (checked)
                            p_temperature = 0;
                        break;
                }
            }
        };
        feveroption.setOnClickListener(onRBClick_T);
        normToption.setOnClickListener(onRBClick_T);

        RadioButton pregoption = findViewById(R.id.yes_option);
        RadioButton nopregoption = findViewById(R.id.no_option);
        View.OnClickListener onRBClick_Preg = new View.OnClickListener() {
            public void onClick(View view){
                boolean checked = ((RadioButton) view).isChecked();
                switch(view.getId()) {
                    case R.id.yes_option:
                        if (checked)
                            p_pregnancy = 1;
                        break;
                    case R.id.no_option:
                        if (checked)
                            p_pregnancy = 0;
                        break;
                }
            }
        };
        pregoption.setOnClickListener(onRBClick_Preg);
        nopregoption.setOnClickListener(onRBClick_Preg);

        RadioButton hpoption = findViewById(R.id.high_option);
        RadioButton lpoption = findViewById(R.id.low_option);
        RadioButton normPoption = findViewById(R.id.normP_option);
        View.OnClickListener onRBClick_P = new View.OnClickListener() {
            public void onClick(View view){
                boolean checked = ((RadioButton) view).isChecked();
                switch(view.getId()) {
                    case R.id.high_option:
                        if (checked)
                            p_pressure = 1;
                        break;
                    case R.id.low_option:
                        if (checked)
                            p_pressure = -1;
                        break;
                    case R.id.normP_option:
                        if (checked)
                            p_pressure = 0;
                        break;
                }
            }
        };
        hpoption.setOnClickListener(onRBClick_P);
        lpoption.setOnClickListener(onRBClick_P);
        normPoption.setOnClickListener(onRBClick_P);
    }

    private void saveFile(String toSave) {
        try {
            FileOutputStream fileOutputStream = openFileOutput("PatientList.csv", MODE_APPEND);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(fileOutputStream));
            writer.write(toSave);
            writer.flush();
            writer.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fileInputStream = openFileInput("PatientList.csv");
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

//    public AlertDialog.Builder buildWarning(int title_id, int message_id, int pb){
//        AlertDialog.Builder builder = new AlertDialog.Builder(PatientInfoActivity.this);
//        builder.setTitle(title_id);
//        builder.setMessage(message_id);
//        builder.setPositiveButton(pb, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                //do nothing
//            }
//        });
//        return builder;
//    }

}
