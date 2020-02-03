package com.example.mobilehealthprototype;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

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

    //Reads a key from a file
    private static String readKeyFile(String fname) throws IOException {
        String strKeyPEM = "";
        Context context = null;
        AssetManager am = context.getAssets();
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
        String publicKeyPEM = key;
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        // the X509EncodedKeySpec line below is a little suspicious - may cause bugs in the future do more research into it
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
        return pubKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encryptMessage(String raw_msg){
        Cipher cipher = null;
        String pubKeyString = null;
        try {
            pubKeyString = readKeyFile(String.valueOf(R.string.pubkeyfilename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        RSAPublicKey pubKey = null;
        try {
            pubKey = getPublicKeyFromString(pubKeyString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        try {
            cipher = Cipher.getInstance("RSA/NONE/OAEPPadding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.d("ERROR","error in generating the cipher algorithm");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            Log.d("ERROR","error in generating the cipher padding");
        }

        try {
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        byte[] cipherText = new byte[0];
        try {
            cipherText = cipher.doFinal(raw_msg.getBytes("UTF-8"));
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("ERROR", "cipher: " + new String(cipherText));

    }

}
