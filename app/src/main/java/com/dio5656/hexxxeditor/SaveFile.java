package com.dio5656.hexxxeditor;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.TreeMap;


import static com.dio5656.hexxxeditor.MainActivity.customEditTextArrayList;
import static com.dio5656.hexxxeditor.MainActivity.Encoding;
import static com.dio5656.hexxxeditor.MainActivity.hexStringToByteArray;
import static com.dio5656.hexxxeditor.MainActivity.myUri;
import static com.dio5656.hexxxeditor.MainActivity.savehashMap;


public class SaveFile {
    Context context;
    Activity activity;
    ReadFromFile readFromFile;
    public SaveFile(Context context, Activity activity) {
        this.context=context;
        this.activity=activity;
        readFromFile = new ReadFromFile(context,activity);
    }
    public void start (View view) {

        try {
            TreeMap<String, String> sortedMap = new TreeMap<>((key1, key2) -> {
                Integer intKey1 = Integer.parseInt(key1);
                Integer intKey2 = Integer.parseInt(key2);
                return intKey1.compareTo(intKey2);
            });
            sortedMap.putAll(savehashMap);
            System.out.println("savehashmap"+savehashMap);
            System.out.println("sortedmap"+sortedMap);
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStreamForAnsi = contentResolver.openInputStream(myUri);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStreamForAnsi);
            BufferedReader inputStreamForUtf8 = new BufferedReader(inputStreamReader);
            OutputStream outputStreamForAnsi = contentResolver.openOutputStream(myUri, "rw"); //{ // "wa" for write/append mode
            OutputStreamWriter outputStreamWriter= new OutputStreamWriter(outputStreamForAnsi, StandardCharsets.UTF_8);
            BufferedWriter outputStreamForUtf8 = new BufferedWriter(outputStreamWriter);


            byte[] byteBufferForAnsi = new byte[1024];
            char [] charBufferForUtf8 = new char[1024];
            int bytesRead;
            String valuechanged;

            // Copy the original content up to the offset
            String oldaddress="-5";
            int offset=0;
            int i =0;
            for (String address : sortedMap.keySet()) {

                System.out.println("adress+"+i+"= "+address);
            }
            //get the modified values
            for (String address : sortedMap.keySet()) {
                valuechanged=  sortedMap.get(address); //get hex value
                byte[] byteArray;
                if (Encoding.equals("Ansi")) {
                     byteArray = hexStringToByteArray(valuechanged);
                } else {
                     byteArray = utf8HexStringToByteArray(valuechanged);
                }
                System.out.println("bytearray in char"+ new String(byteArray, StandardCharsets.UTF_8));

                //this works when multiple chars are edited
                if (!oldaddress.equals("-5"))
                {
                    System.out.println("22222222222");
                    System.out.println("address="+address);
                    System.out.println("oldaddress="+oldaddress);
                    offset= Integer.valueOf(address) - Integer.valueOf(oldaddress)-1;
                    if (Encoding.equals("Ansi"))
                        inputStreamForAnsi.skip(1);
                    //calc amount of bytes edittext in offset
                    int realoffset=0;
                    for (int b=0;b<offset;b++) {
                        realoffset+=customEditTextArrayList.get(b).getText().toString().length()/2;
                    }

                    //inputStream.skip(1);
                    System.out.println("offset="+offset);
                    System.out.println("realoffset="+realoffset);
                    if (Encoding.equals("Ansi")) {
                        bytesRead = inputStreamForAnsi.read(byteBufferForAnsi,0, offset);
                    } else {
                        bytesRead = inputStreamForUtf8.read(charBufferForUtf8, 0, offset);
                    }
                    System.out.println("buffer="+charBufferForUtf8);
                    System.out.println("bytesRead="+bytesRead);
                    if ( bytesRead !=-1) {
                        if (Encoding.equals("Ansi")) {
                            outputStreamForAnsi.write(byteBufferForAnsi, 0,offset);
                            outputStreamForAnsi.write(byteArray);
                        }else {
                        outputStreamForUtf8.write(String.valueOf(charBufferForUtf8),0,offset);
                        outputStreamForUtf8.write(new String(byteArray, StandardCharsets.UTF_8));
                        bytesRead = inputStreamForUtf8.read();
                        String charRead = String.valueOf((char) bytesRead);
                        System.out.println("charRead2222="+charRead);
                        System.out.println("charRead.getBytes().length"+charRead.getBytes().length);
                        System.out.println("valuechanged.length()/2="+valuechanged.length()/2);
                        if (charRead.getBytes().length >1 &&valuechanged.length()/2==1 ) {
                            outputStreamForUtf8.write(0);
                        }
                        }
                        oldaddress=address;
                        continue;
                    }
                }
                //this works the first time edit is made
                System.out.println("11111111111");
                //calc amount of bytes edittext in offset
                int realadress=0;
                for (int b=0;b<Integer.valueOf(address);b++) {
                    realadress+=customEditTextArrayList.get(b).getText().toString().length()/2;
                }
                if (Encoding.equals("Ansi")) {
                    bytesRead = inputStreamForAnsi.read(byteBufferForAnsi,0,  Integer.valueOf(address));
                } else {
                    bytesRead = inputStreamForUtf8.read(charBufferForUtf8, 0, Integer.parseInt(address));
                }
                System.out.println("adress="+address);
                System.out.println("realadress="+realadress);
                if( bytesRead!=-1) {

                    System.out.println("bytesRead="+bytesRead);
                    System.out.println("buffer="+ Arrays.toString(charBufferForUtf8));
                    System.out.println("buffertostring"+charBufferForUtf8.toString());
                    System.out.println("String.valueOf(buffer)"+String.valueOf(charBufferForUtf8));
                    if (Encoding.equals("Ansi")) {
                        outputStreamForAnsi.write(byteBufferForAnsi, 0, bytesRead);
                        outputStreamForAnsi.write(byteArray);
                    } else {

                        outputStreamForUtf8.write(charBufferForUtf8, 0, Integer.parseInt(address));
                        System.out.println("byyyyte=" + new String(byteArray, StandardCharsets.UTF_8));
                        outputStreamForUtf8.write(new String(byteArray, StandardCharsets.UTF_8), 0, new String(byteArray, StandardCharsets.UTF_8).length());
                        bytesRead = inputStreamForUtf8.read();
                        String charRead = String.valueOf((char) bytesRead);
                        System.out.println("charRead" + charRead);
                        if (charRead.getBytes().length > 1 && valuechanged.length() / 2 == 1) {
                            System.out.println("charRead.getBytes().length" + charRead.getBytes().length);
                            outputStreamForUtf8.write(0);
                        }
                    }
                    oldaddress = address;
                }
            }
            inputStreamForUtf8.close();
            outputStreamForUtf8.close();
            savehashMap.clear();
            sortedMap.clear();
            Snackbar.make(view, "File is Saved.", Snackbar.LENGTH_LONG).show();

        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error writing to file "+e, Toast.LENGTH_SHORT).show();
        }

    }

    public static byte[] utf8HexStringToByteArray(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string");
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }

        return bytes;
    }
}
