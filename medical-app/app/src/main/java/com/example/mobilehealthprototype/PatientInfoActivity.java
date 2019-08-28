package com.example.mobilehealthprototype;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class PatientInfoActivity extends AppCompatActivity {
    Sex p_sex = null;
    int p_id, p_age;
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

        Button next_step = findViewById(R.id.next_step_button);
        CustomButton.changeButtonColor(this, next_step, R.color.colorPrimary, 3, R.color.colorAccent);

        next_step.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                p_id = (int) checkValue(R.id.pid_input, R.id.pid_header, false);
                p_age = (int) checkValue(R.id.age_input, R.id.age_header, true);
                p_height = checkValue(R.id.height_input, R.id.height_header, false);
                p_weight = checkValue(R.id.weight_input, R.id.weight_header, false);
                if(p_sex == null){  warnError(0, R.id.sex_option_header); }
                complete = (p_age > 0) & (p_sex != null);
                //(p_id > 0) & (p_age > 0) & (p_sex != null) &(p_height > 0) & (p_weight > 0); //original version

                if(complete) {
                    Intent intent = new Intent(PatientInfoActivity.this, ListSymptoms.class);
                    intent.putExtra("hid", p_id);
                    intent.putExtra("sex", p_sex);
                    intent.putExtra("age", p_age);
                    intent.putExtra("height", p_height);
                    intent.putExtra("weight", p_weight);
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
