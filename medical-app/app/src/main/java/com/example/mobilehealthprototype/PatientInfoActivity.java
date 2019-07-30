package com.example.mobilehealthprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

enum Sex
{
    MALE, FEMALE;
}

public class PatientInfoActivity extends AppCompatActivity {

    Sex p_sex = null;
    int p_id, p_age;
    float p_weight, p_height;
    int enteredFields;
    final int REQUIRED = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        RadioButton moption = (RadioButton) findViewById(R.id.Male_option);
        RadioButton foption = (RadioButton) findViewById(R.id.Female_option);

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
    }

    //Checks if a given editText entry is empty
    // and modifies the corresponding header
    //TODO : FIX THE UNINTENDED BEHAVIOR ON MULTIPLE NO ENTRY CLICKS
    public Boolean checkEmpty(String pstr, int id2){
        if(pstr == null || pstr.trim().equals("")){
            TextView header = findViewById(id2);
            String og = header.getText().toString();
            String new_header = og + " (please enter a value)";
            header.setText(new_header);
            header.setTextColor(Color.RED);
            return true;
        }
        return false;
    }

    //Returns the Int inside of an edit textfield
    //as a side effect, it also increases the value of the enteredFields by 1
    //if it is not empty
    public int idToInt(int id, int id2){
        String parsed = ((TextView) findViewById(id)).getText().toString();
        if(checkEmpty(parsed, id2)){
            return -1;
        }
        return Integer.parseInt(parsed);
    }

    public float idToFloat(int id, int id2){
        String parsed = ((TextView) findViewById(id)).getText().toString();
        if(checkEmpty(parsed, id2)){
            return -1f;
        }
        return Float.parseFloat(parsed);
    }

    public void onDiagnoseClicked(View view){
        p_id = idToInt(R.id.health_id_input, R.id.hid_header);
        p_age = idToInt(R.id.age_input, R.id.age_header);
        p_height = idToFloat(R.id.height_input, R.id.height_header);
        p_weight = idToFloat(R.id.weight_input, R.id.weight_header);
        //TODO : MAKE THIS LOOK LESS UGLY
        if(p_sex != null){
            enteredFields = enteredFields + 1;
        }

        if(p_age > 0){
            enteredFields = enteredFields + 1;
        }

        if(p_height > 0){
            enteredFields = enteredFields + 1;
        }

        if(p_weight > 0){
            enteredFields = enteredFields + 1;
        }

        if(p_id > 0){
            enteredFields = enteredFields + 1;
        }

        Intent intent = new Intent(this, ListSymptoms.class);
        intent.putExtra("hid", p_id);
        intent.putExtra("sex", p_sex);
        intent.putExtra("age", p_age);
        intent.putExtra("height", p_height);
        intent.putExtra("weight", p_weight);

        if(enteredFields == REQUIRED){
            startActivity(intent);
        }else{
            Log.d("TESTING","IT DID NOT PASS THIS CHECK");
        }
    }
}
