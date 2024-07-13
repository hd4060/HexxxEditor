package com.dio5656.hexxxeditor;

import android.app.Activity;
import android.content.Context;
import android.view.View;


import static com.dio5656.hexxxeditor.MainActivity.customEditTextArrayList;
import static com.dio5656.hexxxeditor.MainActivity.customTextViewArrayList;
import static com.dio5656.hexxxeditor.MainActivity.readdone;
import static com.dio5656.hexxxeditor.MainActivity.result;
import static com.dio5656.hexxxeditor.MainActivity.texttofind;

import com.google.android.material.snackbar.Snackbar;

public class FindNext {
    Context context;
    Activity activity;
    ReadFromFile readFromFile;
    public FindNext (Context context, Activity activity) {
        this.context=context;
        this.activity=activity;
        readFromFile = new ReadFromFile(context,activity);
    }
    // searches with String value in textviews
    public void start (View view) {

            int skipcharsread=0;
            CustomEditText customEditTexthighlighted = (CustomEditText) activity.getCurrentFocus();

            int addresstoskip = (int) customEditTexthighlighted.getAddress();
            skipcharsread+=addresstoskip+1;
            // System.out.println("highliiight"+customEditTexthighlighted.getAddress());
            int firsttry=0;
            boolean found =false;
            // System.out.println("readdone="+readdone);
            while (!readdone || firsttry==0) {
                if (found) break;
                firsttry++;
                // System.out.println("loooooooooooooo");
                // System.out.println("readdone="+readdone);
                // System.out.println("firsttry="+firsttry);
                result="";
                // readsize = 500;
                readFromFile.start();
                // System.out.println("file is read");

                while (skipcharsread < customTextViewArrayList.size()) {
                    // System.out.println("looooooooop");
                    result += customTextViewArrayList.get(skipcharsread).getText().toString();

                    if (result.contains(texttofind)
                    ) {
                        found=true;
                        // System.out.println("yeeeeeeeeeeeeees");
                        // System.out.println("result.lenght="+result.length());
                        // System.out.println("texttofind.lenght="+texttofind.length());
                        // System.out.println("customTextViewArrayList.size()="+customTextViewArrayList.size());
                        // System.out.println("skipcharsread="+skipcharsread);
                        // System.out.println("foooooooooooooound=" + result);
                        result = "";
                        CustomEditText customEditText = customEditTextArrayList.get(skipcharsread-texttofind.length()+1);
                        customEditText.requestFocus();
                        customEditText.setSelection(0, customEditText.getText().toString().length());
                        Snackbar.make(view, "Found", Snackbar.LENGTH_SHORT).show();
                        break;
                    }
                    // System.out.println("result=" + result);
                    skipcharsread++;

                }

            }
            if (readdone && !found) {
                Snackbar.make(view, "No more" + "\"" + texttofind + "\"" + " to find", Snackbar.LENGTH_SHORT).show();

            }
    }
}
