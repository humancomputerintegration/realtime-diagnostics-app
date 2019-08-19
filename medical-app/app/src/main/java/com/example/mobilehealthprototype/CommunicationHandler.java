package com.example.mobilehealthprototype;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


//TODO: Encryption Protocol
//Handles communnication protocol between the app & server -- needs to include encryption protocol
public class CommunicationHandler {

    //generates a compressed string of the information into the server
    public String generateHashID(int p_id){
        String pre_processed_id = Integer.toString(p_id);
        String hashedPID= null;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(pre_processed_id.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < bytes.length; i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashedPID = sb.toString();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return hashedPID;
    }

    public String generateRawMessage(int p_id, Sex p_sex, int p_age, float p_height, float p_weight,
                                     ArrayList<Integer> patientSymptomsIndex, int diagnosedDiseaseIndex){
        String spsex = (p_sex == Sex.MALE) ? "M" : "F";
        String spheight = Float.toString(p_height);
        String spweight = Float.toString(p_weight);

        String fh = p_id + ";" + spsex + ";" + p_age + ";" + spheight + ";" + spweight + ";";

        String symps = "";
        for(int i = 0; i < patientSymptomsIndex.size(); i++){
            symps = (i < patientSymptomsIndex.size() - 1) ? symps + patientSymptomsIndex.get(i) + ","
                                                            : symps + patientSymptomsIndex.get(i);
        }

        String sh = fh + symps + ";" + diagnosedDiseaseIndex;
        Log.d("TESTING", sh);
        return sh;
    }

}
