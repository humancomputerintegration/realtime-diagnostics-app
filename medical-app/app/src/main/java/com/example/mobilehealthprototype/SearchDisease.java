package com.example.mobilehealthprototype;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.HashMap;


public class SearchDisease extends AppCompatActivity {
    List<SearchListItem> allDiseases = new ArrayList<>();
    String diseaseChoice;
    SearchableDialog sd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_disease);

        loadDiseases("DiseaseList.csv");
        setUpInterface();
        //loadDiseaseSymptoms("Disease_Symptom");

    }


    //Loads up all the diseases from the file into our activity
    public void loadDiseases(String fname){ //ArrayList<String>
        try{
            InputStreamReader is = new InputStreamReader(getAssets().open(fname));
            BufferedReader r = new BufferedReader(is);
            String nl;
            String[] temp;
            nl = r.readLine(); //skips the heading in the csv
            while((nl = r.readLine()) != null){
                temp = nl.split(",");
                SearchListItem t = new SearchListItem(0, temp[1]);
                allDiseases.add(t);
            }
        }catch (IOException e){
            e.printStackTrace();
            Log.e("ERROR", "AN ERROR HAS OCCURRED IN LOADDISEASES");
        }
    }

    public void loadDiseaseSymptoms(String fName){
        //File file = new File(fName);
        //Scanner scan = new Scanner(file);
        try {
            DataInputStream textFileStream = new DataInputStream(getAssets().open(String.format(fName)));
            Scanner scan = new Scanner(textFileStream);
            HashMap<String, ArrayList> map = new HashMap<String, ArrayList>();

            //reads file into initial arrayList
            ArrayList<String> raw_elements = new ArrayList<String>();
            String sequence = "";
            while (scan.hasNext()) {
                sequence = scan.next();
                raw_elements.add(sequence);
            }

            //processes raw elements into usable arrayList
            ArrayList<String> processed_elements = new ArrayList<String>();
            int j = 0;
            int k = 0;
            for (String s : raw_elements) {

                String element = s;
                if (s.contains("UMLS")) {

                    try {
                        while (!(raw_elements.get(k + 1).contains("UMLS")) && !(raw_elements.get(k + 1).matches("[0-9]+"))) //while the next elements does not have UMLS
                        {
                            element += (" ") + (raw_elements.get(k + 1));
                            k++;
                        }
                        processed_elements.add(element);
                    } catch (Exception e) {
                    }
                } else if (s.matches("[0-9]+")) {
                    processed_elements.add(s);
                }

                j++;
                k = j;
            }


            for (int i = 0; i < processed_elements.size(); i++) {
                int n = i;
                String disease = "";
                ArrayList<String> symptoms = new ArrayList<String>();

                if (processed_elements.get(i).matches("[0-9]+")) {
                    disease = processed_elements.get(i - 1);
                    try {
                        while (!processed_elements.get(n + 1).matches("[0-9]+")) {
                            symptoms.add(processed_elements.get(n + 1).substring(processed_elements.get(n + 1).lastIndexOf("_") + 1));
                            n++;
                        }
                    } catch (Exception e) {
                    }
                    symptoms.remove(symptoms.size() - 1);
                }
                map.put(disease, symptoms);
            }

            //prints out symptoms for the entered disease
            for (String key : map.keySet()) {
                if (key.contains(diseaseChoice)) {
                    displaySymptoms(map.get(key));
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
            Log.e("ERROR", "AN ERROR HAS OCCURRED IN LOADDISEASESYMPTOMS");
        }
    }


    public void setUpInterface(){
        sd = new SearchableDialog(SearchDisease.this, allDiseases,"Disease Search");
        sd.setOnItemSelected(new OnSearchItemSelected(){
            public void onClick(int position, SearchListItem searchListItem){
                String newDisease = searchListItem.getTitle();
                diseaseChoice = newDisease;
                displayDiseaseInfo();
            }
        });

        TextView addDisease = (TextView) findViewById(R.id.enter_disease_button);
        addDisease.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                sd.show();
            }
        });
    }

    public void displayDiseaseInfo(){
        TextView diseaseTextView = (TextView) findViewById(R.id.diseaseChoice);
        diseaseTextView.setText(diseaseChoice);

        loadDiseaseSymptoms("Disease_Symptom");;

    }

    public void displaySymptoms(ArrayList<String> symp) {
        ListView symptomListView = (ListView) findViewById(R.id.symptoms_list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                symp);
        symptomListView.setAdapter(arrayAdapter);

        View b = findViewById(R.id.compare_button);
        b.setVisibility(View.VISIBLE);
    }


}
