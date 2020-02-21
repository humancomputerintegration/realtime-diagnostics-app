package com.example.mobilehealthprototype;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.cryptomator.siv.SivMode;
import org.cryptomator.siv.UnauthenticCiphertextException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
//import java.util.Base64;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

//Handles communication protocol between the app & server -- needs to include encryption protocol
public class CommunicationHandler {

    //generates a compressed string of the information into the server
    // TODO DELETE
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
    // TODO DELETE
    private String readKeyFileCH(Context c, String fname) throws IOException {
        String strKeyPEM = "";
        Log.d("TESTING", "READING KEY FILE");
        BufferedReader br = null;
        try{
            InputStreamReader is = new InputStreamReader(c.getAssets().open(fname));
            br = new BufferedReader(is);
        }catch (IOException e){
            Log.d("TESTING", "Error with loading in file");
            e.printStackTrace();
        }

        if(br == null){ Log.d("TESTING","failed to read file successfully"); }

        String line;
        while ((line = br.readLine()) != null) {
            strKeyPEM += line + "\n";
            Log.d("TESTING", line);
        }
        br.close();
        Log.d("TESTING", strKeyPEM);
        Log.d("TESTING", "Was nothing printed??");
        return strKeyPEM;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    // TODO DELETE
    private static RSAPublicKey getPublicKeyFromString(String key) throws IOException, GeneralSecurityException {
        String publicKeyPEM = key;
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("\n","");
        byte[] encoded = Base64.decode(publicKeyPEM, Base64.DEFAULT);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        // the X509EncodedKeySpec line below is a little suspicious - may cause bugs in the future do more research into it
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
        return pubKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    // TODO DELETE
    public static String encryptMessage(String pubKeyString, String raw_msg){
        RSAPublicKey pubKey = null;
        try {
            pubKey = getPublicKeyFromString(pubKeyString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        byte[] cipherText = new byte[0];
        String cipherTextComm = null;
        Cipher cipher = null;
        try{
            cipher = Cipher.getInstance("RSA/NONE/OAEPPadding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            cipherText = cipher.doFinal(raw_msg.getBytes("UTF-8"));
            cipherTextComm = Base64.encodeToString(cipherText, Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d("TESTING", "cipher= " + new String(cipherText));
//        return new String(cipherText);
        return new String(cipherTextComm);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encryptMessageSIV(byte[] key, byte[] mackey, String raw_msg){
        byte[] cipherText = new byte[0];
        SivMode AES_SIV = new SivMode();
        cipherText = AES_SIV.encrypt(key, mackey, raw_msg.getBytes());
        String output = Base64.encodeToString(cipherText, Base64.DEFAULT);
        return output;
    }

    // input to decrypt should be a base64 encoded string
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decryptMessageSIV(byte[] key, byte[] mackey, String cipher){
        byte[] cipher_b = Base64.decode(cipher, Base64.DEFAULT);
        byte[] plaintext = new byte[0];
        SivMode AES_SIV = new SivMode();
        try {
            plaintext = AES_SIV.decrypt(key, mackey, cipher_b);
        } catch (UnauthenticCiphertextException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        String output = plaintext.toString();
//                Base64.getEncoder().encodeToString(plaintext);
        return output;
    }

    public static void testEncryptDecrypt(byte[] key, byte[] mackey, String raw_msg){
        SivMode AES_SIV = new SivMode();
//        Log.d("TESTING", raw_msg);
        Log.d("TESTING", "hello world");
        byte[] encrypted = AES_SIV.encrypt(key, mackey, "hello world".getBytes());
        Log.d("TESTING", encrypted.toString());
        byte[] decrypted = new byte[0];
        try {
            decrypted = AES_SIV.decrypt(key, mackey, encrypted);
            Log.d("TESTING", "OIEWJROWIEJR");
            Log.d("TESTING", decrypted.toString());
        } catch (UnauthenticCiphertextException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return;
    }

    private byte[] xor_bytes(byte[] a, byte[] b){
        int lena = a.length;
        int lenb = b.length;
        int min_len = Math.min(lena, lenb);
        byte[] result = new byte[min_len];
        for(int i =0; i < min_len; i++){
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }

    public String encrypt_p(String msg, byte[] key){
        byte[] ciphertext = xor_bytes(msg.getBytes(), key);
        String output = Base64.encodeToString(ciphertext, android.util.Base64.DEFAULT);
        return output;
    }

    public String decrypt_p(String ctxt, byte[] key){
        byte[] plaintext = xor_bytes(android.util.Base64.decode(ctxt, android.util.Base64.DEFAULT), key);
        String output = new String(plaintext);
        return output;
    }

    public String[] splitMessage(String msg, int parts, String tag){
        String[] split_msg = new String[parts];
        int slen = msg.length();
        int substring_len = slen/parts;

        for(int i = 0; i < parts; i++){
            if(i == (parts - 1)){
                split_msg[i] = tag + i + msg.substring(i * substring_len, slen);
            }else{
                split_msg[i] = tag + i + msg.substring(i * substring_len, (i+1)*substring_len);
            }
        }
        return split_msg;
    }

}
