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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


//TODO: - Review https://github.com/ajithvgiri/search-dialog - for documentation
//TODO - Maybe look at the above github to figure out how to do the searchavle dialoge from scratch
//TODO Make sure that the button click animation for "Diagnose is visible"
public class ListSymptoms extends AppCompatActivity {
    List<SearchListItem> allSymptoms = new ArrayList<>();
    ArrayList<String> currentSymptoms = new ArrayList<String>();
    ListView currentSymptomListView;
    SymptomAdapter adp;
    SearchableDialog sd;

    Intent passedIntent;
    Sex p_sex;
    int p_id, p_age;
    float p_height, p_weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_symptoms);

        handlePassedIntent();
        //TODO Make sure you add to the documentation that the CSV needs to be well behaved
        //TODO - Well behaved csv = UMLS code to empty field
        loadSymptoms("SymptomList.csv");
        setUpInterface();


        Button diagnose = (Button) findViewById(R.id.continue_diagnose_button);
        diagnose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListSymptoms.this, SendMessage.class);
                intent.putExtra("hid", p_id);
                intent.putExtra("sex", p_sex);
                intent.putExtra("age", p_age);
                intent.putExtra("height", p_height);
                intent.putExtra("weight", p_weight);
                intent.putExtra("patient_symptoms", currentSymptoms);
                startActivity(intent);
            }
        });

    }

    public void handlePassedIntent(){
        passedIntent = getIntent();
        p_sex = (Sex) passedIntent.getSerializableExtra("sex");
        p_id = passedIntent.getIntExtra("hid", -1);
        p_age = passedIntent.getIntExtra("age", -1);
        p_height = passedIntent.getFloatExtra("height",-1);
        p_weight = passedIntent.getFloatExtra("weight",-1);
    }

    //Loads up all the symptoms from the file into our activity
    public void loadSymptoms(String fname){ //ArrayList<String>
        try{
            InputStreamReader is = new InputStreamReader(getAssets().open(fname));
            BufferedReader r = new BufferedReader(is);
            String nl;
            String[] temp;
            nl = r.readLine(); //skips the heading in the csv
            while((nl = r.readLine()) != null){
                temp = nl.split(",");
                SearchListItem t = new SearchListItem(0, temp[1]);
                allSymptoms.add(t);
            }
        }catch (IOException e){
            e.printStackTrace();
            Log.e("ERROR", "AN ERROR HAS OCCURRED IN LOADSYMPTOMS");
        }
    }

    //TODO - MAKE IT SO THAT THE TEXTBOX IS AUTOMATICALLY IN FOCUS AS YOU LOG IN
    public void setUpInterface(){
        sd = new SearchableDialog(ListSymptoms.this, allSymptoms,"Symptom Search");
        sd.setOnItemSelected(new OnSearchItemSelected(){
            public void onClick(int position, SearchListItem searchListItem){
                String newSmp = searchListItem.getTitle();
                if(!currentSymptoms.contains(newSmp)){
//                    String temp = searchListItem.getTitle();
                    currentSymptoms.add(searchListItem.getTitle());
//                    Log.d("TESTING", "SUCCESSFULLY PROCESSED :: " + temp);
                    ((SymptomAdapter) currentSymptomListView.getAdapter()).notifyDataSetChanged();
                }
            }
        });

        TextView addsymp = (TextView) findViewById(R.id.add_symptom_button);
        addsymp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                sd.show();
//                sd;
            }
        });

        //Sets up the ListView for the patient's current symptoms
        currentSymptomListView = (ListView) findViewById(R.id.currentsymptomlist);
        adp = new SymptomAdapter(this, currentSymptoms);
        currentSymptomListView.setAdapter(adp);
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
        public long getItemId(int pos) {
//            return list.get(pos).getId();
            return 0;
            //just return 0 if your list items do not have an Id variable.
        }

        @Override
        public void notifyDataSetChanged(){
            super.notifyDataSetChanged();
//            Log.d("TESTING", "DATASET CHANGING");
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_curr_symptom, null);
                //my_custom_list_layout
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.list_item_symptom);
            listItemText.setText(list.get(position));

            //Handle buttons and add onClickListeners
            Button deleteBtn = (Button)view.findViewById(R.id.delete_symptom_button);
            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something
                    list.remove(position); //or some other task
                    notifyDataSetChanged();
                }
            });

            return view;
        }
    }

}
