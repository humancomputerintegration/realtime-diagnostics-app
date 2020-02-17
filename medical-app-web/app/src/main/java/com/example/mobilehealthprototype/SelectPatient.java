package com.example.mobilehealthprototype;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ajithvgiri.searchdialog.OnSearchItemSelected;
import com.ajithvgiri.searchdialog.SearchListItem;
import com.ajithvgiri.searchdialog.SearchableDialog;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class SelectPatient extends AppCompatActivity {
    //important properties for the GUI to load in
    List<SearchListItem> patients = new ArrayList<>();
    ArrayList<String> currentPatient = new ArrayList<>();
    ListView patientView;
    ListSymptoms.SymptomAdapter adp;
    SearchableDialog sd;
    Hashtable<String,String> patientList = new Hashtable<>();

    String p_sex, p_id, p_age, p_height, p_weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_patients);
        loadfile("PatientList.csv");
        setUpInterface();
    }

    private void loadfile(String s) {
        String[] temp;
        SearchListItem t;
        try {
            FileInputStream fileInputStream = openFileInput(s);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = bufferedReader.readLine();
            while (line != null) {
                System.out.println(line);
                temp = line.split(",");
                t = new SearchListItem(0, temp[0]);
                patients.add(t);
                patientList.put(temp[0], line);
                System.out.println(t);
                line = bufferedReader.readLine();
            }
            System.out.println(patients);
            bufferedReader.close();
            fileInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO - Implement a Synonym-lookup feature
    public void setUpInterface(){
        //Setting up the search view to look up symptoms
        sd = new SearchableDialog(SelectPatient.this, patients,"Patient Search");
        sd.setOnItemSelected(new OnSearchItemSelected(){
            public void onClick(int position, SearchListItem searchListItem){
                String newSmp = searchListItem.getTitle();
                if(!currentPatient.contains(newSmp)){
                    currentPatient.add(searchListItem.getTitle());
                    ((ListSymptoms.SymptomAdapter) patientView.getAdapter()).notifyDataSetChanged();
                }
            }
        });

        Button selectPatient = findViewById(R.id.select_patient_button);
        CustomButton.changeButtonColor(this, selectPatient, R.color.colorPrimaryDark,3, R.color.colorPrimaryDarkAccent);
        selectPatient.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                sd.show();
            }
        });

        //Sets up the ListView for the patient's current symptoms
        patientView = findViewById(R.id.patient_view);
        adp = new ListSymptoms.SymptomAdapter(this, currentPatient);
        patientView.setAdapter(adp);

        Button diagnose = findViewById(R.id.continue_button);
        CustomButton.changeButtonColor(this, diagnose, R.color.colorPrimary,3, R.color.colorAccent);

        diagnose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectPatient.this, ListSymptoms.class);
                intent.putExtra("mode", 1);
                intent.putExtra("hid", p_id);
                intent.putExtra("sex", p_sex);
                intent.putExtra("age", p_age);
                intent.putExtra("height", p_height);
                intent.putExtra("weight", p_weight);
                startActivity(intent);
            }
        });
    }

    public class SymptomAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();
        private Context context;

        public SymptomAdapter(Context context, ArrayList<String> list) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        @Override
        //necessary, but will never be used
        public long getItemId(int pos) {
            return 0; //just return 0 if your list items do not have an Id variable.
        }

        @Override
        public void notifyDataSetChanged(){
            super.notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_curr_symptom, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.list_item_symptom);
            listItemText.setText(list.get(position));

            //Handle buttons and add onClickListeners
            Button deleteBtn = view.findViewById(R.id.delete_symptom_button);
            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    list.remove(position); //or some other task
                    notifyDataSetChanged();
                }
            });

            return view;
        }
    }

}
