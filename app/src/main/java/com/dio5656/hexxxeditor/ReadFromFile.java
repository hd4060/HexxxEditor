package com.dio5656.hexxxeditor;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dio5656.hexxxeditor.MainActivity.columnSetCount;
import static com.dio5656.hexxxeditor.MainActivity.column_size;
import static com.dio5656.hexxxeditor.MainActivity.customColumns;
import static com.dio5656.hexxxeditor.MainActivity.customEditTextArrayList;
import static com.dio5656.hexxxeditor.MainActivity.customTextViewArrayList;
import static com.dio5656.hexxxeditor.MainActivity.edittextcount;
import static com.dio5656.hexxxeditor.MainActivity.Encoding;
import static com.dio5656.hexxxeditor.MainActivity.hexToString;
import static com.dio5656.hexxxeditor.MainActivity.myUri;
import static com.dio5656.hexxxeditor.MainActivity.nextchar;
import static com.dio5656.hexxxeditor.MainActivity.params;
import static com.dio5656.hexxxeditor.MainActivity.readdone;
//import static com.dio5656.hexxxeditor.MainActivity.resetValues;
import static com.dio5656.hexxxeditor.MainActivity.savehashMap;
import static com.dio5656.hexxxeditor.MainActivity.textviewcount;
import static com.dio5656.hexxxeditor.MainActivity.readsize;
import static com.dio5656.hexxxeditor.MainActivity.skip;
import static com.dio5656.hexxxeditor.MainActivity.count;
import static com.dio5656.hexxxeditor.MainActivity.firsttime;


public class ReadFromFile extends AppCompatActivity {
    Context context;
    Activity activity;

    public ReadFromFile(Context context, Activity activity){
        this.context=context;
        this.activity=activity;
        firsttime=true;

    }
    public void start () {
if (firsttime && Encoding.equals("Ansi")) {
        column_size= column_size*3/2;
        firsttime=false;
    }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        System.out.println("column_size based on screen ratio="+column_size);
            System.out.println("reeeeead first line");
          //  ExecutorService executorService = Executors.newSingleThreadExecutor();
            //  executorService.execute(() -> {
          //  System.out.println("inside executeeeeeeeee");
            activity.runOnUiThread(() -> {
                   /* new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Main Activity", "Running in background thread");
*/
                          /*  runOnUiThread(new Runnable() {
                                @Override
                                public void run() {*/
                Log.d("Main Activity", "reeeeead start on ui thread");
                System.out.println("reeeeead start on ui thread");
                TableRow new_row;
                ContentResolver contentResolver = context.getContentResolver();
                try (InputStream inputStream = contentResolver.openInputStream(myUri);
                     InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);//for utf8
                     BufferedInputStream bufferedInputStreamForAnsi = new BufferedInputStream(inputStream); //-- old one
                     BufferedReader  bufferedInputStreamForUtf8 = new BufferedReader(inputStreamReader);
                )
                {



                    String line = "";
                    String stringpattern = "[0-9a-f]?[0-9a-f]";
                    Pattern pattern = Pattern.compile(stringpattern, Pattern.CASE_INSENSITIVE);
                    System.out.println("skip=" + skip);
                    if (Encoding.equals("Ansi")) {
                        bufferedInputStreamForAnsi.skip(skip);
                        nextchar = bufferedInputStreamForAnsi.read();
                    } else { //utf8
                        bufferedInputStreamForUtf8.skip(skip);
                        nextchar = bufferedInputStreamForUtf8.read();
                    }
                    System.out.println("nextchar in read method=" + nextchar);
                    if (nextchar == -1) {
                        readdone = true;
                    } else {
                        readdone = false;
                    }

                    //create rows
                    while (nextchar != -1 && count < readsize) {
                        System.out.println("column_size="+column_size);

                        new_row = new TableRow(context);
                        System.out.println("aaaaaaaaaaaaaaaaaa");
                        new_row.setLayoutParams(params);
                        new_row.setGravity(Gravity.CENTER);
                        //create columns of column_size
                        for (int i = 0; i < column_size; i++) {
                           if (nextchar == -1) {
                                readdone = true;
                                break;
                            }

                            CustomEditText edittext = new CustomEditText(context);
                            if (Encoding.equals("Ansi")) {
                                edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)}); //uncomm
                                edittext.setText(Integer.toHexString(nextchar));// convert to ansi hex
                            } else {//utf8
                                edittext.setText(utf8ToHex(String.valueOf((char) nextchar)));
                            }
                            edittext.setTextSize(15f);
                            line += (char) nextchar;
                            edittext.setAddress(edittextcount);
                            customEditTextArrayList.add(edittext);
                            edittextcount++;
                            edittext.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }

                                public void afterTextChanged(Editable s) {

                                    // put in hashmap on if its hex value

                                    if (Encoding.equals("Ansi")) {
                                        Matcher matcher = pattern.matcher(s.toString());
                                        if (matcher.matches()) {
                                            CustomTextView customTextView = customTextViewArrayList.get((int) edittext.getAddress());
                                            customTextView.setText(hexToString(edittext.getText().toString()));
                                            savehashMap.put(String.valueOf(edittext.getAddress()), edittext.getText().toString());
                                            //    Toast.makeText(MainActivity.this, "Edited value: " + edittext.getText(), Toast.LENGTH_SHORT).show();
                                            //    Toast.makeText(MainActivity.this, "Edited value adress: " + edittext.getAddress(), Toast.LENGTH_SHORT).show();
                                        } else
                                            //clear the text if not a hex value
                                            s.clear();

                                    } else {//utf8
                                        CustomTextView customTextView = customTextViewArrayList.get((int) edittext.getAddress());
                                        customTextView.setText(String.valueOf(hexToChar(edittext.getText().toString())));
                                        savehashMap.put(String.valueOf(edittext.getAddress()), edittext.getText().toString());
                                        //    Toast.makeText(MainActivity.this, "Edited value: " + edittext.getText(), Toast.LENGTH_SHORT).show();
                                        //    Toast.makeText(MainActivity.this, "Edited value adress: " + edittext.getAddress(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            new_row.addView(edittext);
                            if (Encoding.equals("Ansi")) {
                                nextchar = bufferedInputStreamForAnsi.read();
                            } else {
                                nextchar = bufferedInputStreamForUtf8.read();
                            }
                            if (nextchar == -1) {
                                readdone = true;
                            }
                            skip++;
                            count++;

                            //set column size for overflow
                            if ((i>=3) && columnSetCount <10  &&!customColumns &&!Encoding.equals("Ansi")) {
                                columnSetCount++;
                                int hexlenght=0;
                                String aa="";
                                for (int co = 0;co<i+1;co++) {
                                    hexlenght+=  customEditTextArrayList.get((int) edittextcount-1-co).getText().toString().length();
                                    aa+=customEditTextArrayList.get((int) edittextcount-1-co).getText().toString();
                                }
                                System.out.println("hhhhexlenght="+hexlenght);
                                System.out.println("iiiii="+i);
                                System.out.println("aa="+aa);
                                if (hexlenght>=column_size*3){

                                    System.out.println("hexlenght="+hexlenght);
                                    System.out.println("i="+i);
                                    System.out.println("exeeeeeeeeeeeecccccccccccc");
                                    column_size -= 1;
                                    new_row.removeAllViews();
                                    resetValuesAndReadFromStart();
                                    return;
                                 }

                            }
                        }
                        System.out.println("line.length()=" + line.length());

                        //add textview to rows
                        for (int i = 0; i < line.length(); i++) {
                            System.out.println("sssssssssss");
                            CustomTextView textView = new CustomTextView(context);
                            System.out.println("asdddddddddddd");
                            textView.setTextSize(17f);
                            textView.setTypeface(Typeface.DEFAULT_BOLD);
                            textView.setPadding(2, 0, 3, 0);
                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            textView.setBackground(AppCompatResources.getDrawable(context,R.drawable.twostrokes));
                            System.out.println("1111aaaaaaaaaaa");
                            textView.setText(line.substring(i, i + 1));
                            textView.setAddress(textviewcount);
                            customTextViewArrayList.add(textView);
                            textviewcount++;

                            System.out.println("bbbbbbb");

                            new_row.addView(textView);
                        }
                        System.out.println("ccccccccccc");
                        TableLayout tableLayout = activity.findViewById(R.id.mytable);
                        tableLayout.addView(new_row, params);
                        line = "";

                    }
                    if (nextchar == -1) {
                        readdone = true;
                    } else {
                        readdone = false;
                    }
                    count = 0;

                } catch (Exception e) {
                    System.out.println(e);
                }

            });


            // executorService.shutdown();
           // readsize=100;

    }

    public static String utf8ToHex(String utf8String) {
        if (utf8String == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }
        byte[] bytes = utf8String.getBytes(StandardCharsets.UTF_8);
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : bytes) {
            hexStringBuilder.append(String.format("%02X", b));
        }
        return hexStringBuilder.toString();
    }



    public static char hexToChar(String hex) {
        if (hex == null ||
                hex.length() % 2 != 0) {
            return ' ';
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }

        String decodedString = new String(bytes, StandardCharsets.UTF_8);

        if (decodedString.length() != 1) {
            return ' ';
        }

        return decodedString.charAt(0);
    }
    public   void  resetValuesAndReadFromStart() {
        readdone=false;
        count=0;
        nextchar=1;
        skip=0;
        edittextcount=0;
        textviewcount=0;
        customEditTextArrayList.clear();
        customTextViewArrayList.clear();
        // savehashMap.clear();
        System.out.println("abc");
        TableLayout tableLayout=activity.findViewById(R.id.mytable);
        System.out.println("ddd");
        tableLayout.removeAllViews();
          ReadFromFile readFromFile1 = new ReadFromFile(context,activity);
        System.out.println("ee");
        readFromFile1.start();
        System.out.println("def");
    }
}
