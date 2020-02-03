package com.example.mobilehealthprototype;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//Handles communication protocol between the app & server -- needs to include encryption protocol
public class CommunicationHandler extends AppCompatActivity {

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

    //Reads a key from a file
    private String readKeyFile(Context cn, String fname) throws IOException {
        String strKeyPEM = "";
        AssetManager am = cn.getAssets();
        InputStreamReader is = new InputStreamReader(am.open(fname));
        BufferedReader br = new BufferedReader(is);
        String line;
        while ((line = br.readLine()) != null) {
            strKeyPEM += line + "\n";
        }
        br.close();
        return strKeyPEM;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static RSAPublicKey getPublicKeyFromString(String key) throws IOException, GeneralSecurityException {
        String publicKeyPEM = key; //TODO: HERE THE KEY IS NULL FOR SOME REASON
        //TODO FIXED THE NULL KEY PROBLEM

        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        Log.d("TESTING", publicKeyPEM);

//        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        byte[] encoded = publicKeyPEM.getBytes("UTF-8"); //TODO:: PROBLEM LINE HERE
        //TODO - this SEEMS to fix the problem

        KeyFactory kf = KeyFactory.getInstance("RSA");
        // the X509EncodedKeySpec line below is a little suspicious - may cause bugs in the future do more research into it
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
        return pubKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encryptMessage(Context cn, String rawMsg){
        Cipher cipher = null;
        String pubKeyString = null;

        try {
            pubKeyString = readKeyFile(cn, cn.getString(R.string.pubkeyfilename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("TESTING", pubKeyString);

        RSAPublicKey pubKey = null;
        try {
            pubKey = getPublicKeyFromString(pubKeyString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        //TODO problem lines
        byte[] cipherText = new byte[0];
        try {
            cipher = Cipher.getInstance("RSA/NONE/OAEPPadding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            cipherText = cipher.doFinal(rawMsg.getBytes("UTF-8"));
        }catch(Exception e){
            return null;
        }

        Log.d("TESTING", "cipher= " + new String(cipherText));
        return new String(cipherText);
    }

    public void sendEncryptedMessage(Context cn, String phone_num, String msg){
        SmsManager sm = SmsManager.getDefault();
        String enc_msg = null;
        CommunicationHandler ch = new CommunicationHandler();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            enc_msg = ch.encryptMessage(cn, msg);
        }
        Log.d("TESTING", msg);

        if(enc_msg == null){
            Log.d("TESTING", "did not successfully encrypt message");
        }
        if(enc_msg == null){
            Log.d("TESTING", "enc_msg is null!");
        }
        Log.d("TESTING", enc_msg);

        sm.sendTextMessage(phone_num, null, enc_msg, null, null);
    }

}
